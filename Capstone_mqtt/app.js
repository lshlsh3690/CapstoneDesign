const mqtt = require("mqtt");
const client =  mqtt.connect("mqtt://192.168.55.175");//서버 ip
const express = require("express");                 //express 객체란 
const app = express();
const http = require("http");

const TeachableMachine = require("@sashido/teachablemachine-node");

const model = new TeachableMachine({
  modelUrl: "https://teachablemachine.withgoogle.com/models/tXspLR78v/"
});
  

const exp = require("constants");                   //???

const devicesRouter = require("./routes/devices");
const bodyParser = require("body-parser");

var fs = require('fs');
const { route } = require("./routes/devices");
const router = require("./routes/devices");
const res = require("express/lib/response");
const { send } = require("process");
const { json } = require("express/lib/response");

app.use(express.static(__dirname + "/public"));     //public 디렉토리에 있는 파일에 대한 로드를 허용함
app.use(bodyParser.json());                         //bodyParser.json()은 Content-type데이터를 받아줌 (devices.js에서 전달됨)
app.use(bodyParser.urlencoded({ extended: false }));//application/xxx-form-unicoded 방식의 Content-type을 받는다. ajax의 기본형식

app.use("/devices", devicesRouter);                 //routes/device.js 실행



client.on("connect", ()=>{
    console.log("mqtt connect");                    //아두이노의 connect함수 핸들링
    client.subscribe("JPG");                      //모터라는 토픽을 subscribe로 구독함
});

//메세지 이벤트 즉 사용자가 어떤 메세지를 전달할때 처리하는 핸들러 
//async()안의 첫번째 인자는 발행된 토픽을 선택한다.
//async()두번째는 메세지의 값

var obj = null;//이미지는 global variable로 설정해야 갱신이 됩니다
client.on("message", async(topic, message)=>{ // {"LED" : "ON"} or {"MOTER" : "ON"}
    if(topic == "JPG")
    {
        obj = message.toString();
        /*app.get("/img", function(req,res,next){
            res.set("content-Type", "text/json");
            //res.send(JSON.stringify({data : obj}));
            res.json({
                data: obj || "no image yet"
            });
            //console.log(obj);//number 1 print
        });*/
        //console.log(obj);//number 2 print
    }
    else{
        obj = JSON.parse(message);  //뒤의 메세지만 parse 함수로 추출함
    }    
    /*var year = date.getFullYear();
    var month = date.getMonth();
    var today = date.getDate();
    var hours =date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    obj.created_at = new Date(Date.UTC(year, month, today, hours, minutes, seconds));*/
});

var predictions = null;
app.get("/img", function(req,res,next){
    res.set("content-Type", "text/json");
    /*res.json({
      data: obj || "no image yet"
    });*/
    model.classify({
        imageUrl: obj,
      }).then((predictions) => {
        console.log("Predictions:", predictions);
        res.json({
            data : obj , predict : predictions || "no data yet"
        });
      }).catch((e) => {
        console.log("ERROR", e);
      });
    
    //console.log(obj);//number 1
});


app.set("port", "3000");                    //포트 지정
var server = http.createServer(app);        //서버염

server.listen(3000, (err)=> {               //서버 대기상태
    if(err){
        return console.log(err);
    }else{
        console.log("server ready");
    }
});