var express = require("express");
const res = require("express/lib/response");
var router = express.Router();
const mqtt = require("mqtt");


const client = mqtt.connect("mqtt://your server ip");

var date = null;
router.post("/moter", function (req, res, next) {
    res.set("Content-Type", "text/json");
    if (req.body.flag == "on") {
        // MQTT->led : 1
        client.publish("moter", "1");
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth();
        var today = date.getDate();
        var hours =date.getHours();
        var minutes = date.getMinutes();
        var seconds = date.getSeconds();
        date.created_at = new Date(Date.UTC(year, month, today, hours, minutes, seconds));

        res.send(JSON.stringify({ moter: date.created_at }));
    }
});

module.exports = router;//다른 소스코드에서 모듈로 불러오게 하는 함수