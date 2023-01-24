const mqtt = require("mqtt");
const client =  mqtt.connect("mqtt://192.168.55.175");//서버 ip
const express = require("express");                 //express 객체란 
const app = express();
const http = require("http");


const TeachableMachine = require("@sashido/teachablemachine-node");

const model = new TeachableMachine({
  modelUrl: "https://teachablemachine.withgoogle.com/models/sfuI_TAU-/"
});

const admin = require('firebase-admin');

let serviceAccount = require('./capstone-86d8f-firebase-adminsdk-l9w7g-8f0617e6d4.json');
var target_token =
    'des07xd6SGyztPqavwNkZt:APA91bGSamQroH69JTZJohJ0YECPivo98YNEeTrukjkzX-oU8pB2MV3_rxKH-4qnOY1_JziwthU-5xlBYfr1rti2Rzz3l6qy1lD0wnGDNVV3XKibNrWQFX4Wa1fu16qcW1I12RfLzhis'

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});



const exp = require("constants");                   //???

const devicesRouter = require("./routes/devices");
const bodyParser = require("body-parser");

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
    client.subscribe("CLASSIFY");
});

//메세지 이벤트 즉 사용자가 어떤 메세지를 전달할때 처리하는 핸들러 
//async()안의 첫번째 인자는 발행된 토픽을 선택한다.
//async()두번째는 메세지의 값

var obj = null;         //이미지는 global variable로 설정해야 갱신이 됩니다
 //분류된 값을 넣습니다.
var CLASSIFIED = false;
client.on("message", async(topic, message)=>{ // {"LED" : "ON"} or {"MOTER" : "ON"}
    if(topic == "JPG")
    {
        obj = message.toString();

        model.classify({
            imageUrl: obj,
          }).then((predictions) => {
            predictions;
            if(CLASSIFIED == true){
                
                if((predictions[0]['score'] > 0.9) && (predictions[0]['class'] != 'WHITE'))
                {   
                    client.publish("moter", "1");
                    console.log(predictions[0]['class']);
                    if(predictions[0]['class'] == 'RED'){
                        let message = {
                            notification: {
                            title: '혈변 감지',
                            body: '수분이 필요해요',
                            },
                            token: target_token,
                        }
                        
                        admin
                            .messaging()
                            .send(message)
                            .then(function (response) {
                            console.log('Successfully sent message: : ', response)
                            })
                            .catch(function (err) {
                                console.log('Error Sending message!!! : ', err)
                            });
                    }
                    else if(predictions[0]['class'] == 'GREEN'){
                        let message = {
                            notification: {
                            title: '녹변 감지',
                            body: '소화 불량입니다.',
                            },
                            token: target_token,
                        }
                        
                        admin
                            .messaging()
                            .send(message)
                            .then(function (response) {
                            console.log('Successfully sent message: : ', response)
                            })
                            .catch(function (err) {
                                console.log('Error Sending message!!! : ', err)
                            });
                    }
                    CLASSIFIED = false;
                }
                else if(predictions[0]['class'] = 'WHITE'){
                    CLASSIFIED = false;
                }
            }
            
        }).catch((e) => {
            console.log("ERROR", e);
        });
        
    }
    else if(topic == "CLASSIFY"){
        obj = JSON.parse(message);  //뒤의 메세지만 parse 함수로 추출함
        console.log(topic);
        CLASSIFIED = true;
    }    
    /*var year = date.getFullYear();
    var month = date.getMonth();
    var today = date.getDate();
    var hours =date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    obj.created_at = new Date(Date.UTC(year, month, today, hours, minutes, seconds));*/
});

app.get("/img", function(req,res,next){
    res.set("content-Type", "text/json");
    model.classify({
        imageUrl: obj,
      }).then((predictions) => {
        //console.log(obj);
        console.log("Predictions:", predictions);
        res.json({
            data : obj , predict : predictions || "no data yet"
        });
        if(CLASSIFIED == true){
            if((predictions[0]['score'] > 0.9) && (predictions[0]['class'] != 'WHITE'))
            {   
                client.publish("moter", "1");
                console.log(predictions[0]['class']);
                CLASSIFIED = false;
            }
            else if(predictions[0]['class'] = 'WHITE'){
                CLASSIFIED = false;
            }
        }
      }).catch((e) => {
        console.log("ERROR", e);
      });
    
    //console.log(obj);//number 1
});

let message = {
    notification: {
      title: '4조',
      body: '스마트 배변 패드',
    },
    token: target_token,
  }

  admin
    .messaging()
    .send(message)
    .then(function (response) {
      console.log('Successfully sent message: : ', response)
    })
    .catch(function (err) {
        console.log('Error Sending message!!! : ', err)
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