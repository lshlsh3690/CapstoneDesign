var express = require("express");
const res = require("express/lib/response");
var router = express.Router();
const mqtt = require("mqtt");


const client = mqtt.connect("mqtt://192.168.55.175");


router.post("/moter", function (req, res, next) {
    res.set("Content-Type", "text/json");
    if (req.body.flag == "on") {
        // MQTT->led : 1
        client.publish("moter", "1");
        res.send(JSON.stringify({ moter:"on" }));
        
    }
});

module.exports = router;//다른 소스코드에서 모듈로 불러오게 하는 함수