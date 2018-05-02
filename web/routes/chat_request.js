var router = require('express').Router();
var path = require("path");
var mongoose = require("mongoose");
var Channel = require("../schemas/Channel");
var Message = require("../schemas/Message");



router.get("/get-channels", function (req, res) {
    var id = req.query.id;
    Channel.find({guid: "money" +
        ""}, function (err, channel) {
        if (err) return next(err);
        console.log("Search result\n:" + channel);

        if (channel instanceof Array) {
            channel.isarray = true;
        } else {
            channel.isarray = false;
        }

        res.json(channel);
    });
});

router.get("/get-messages", function (req, res) {
    var id = req.query.uid;
    Message.find({channel_guid: id +
        ""}, function (err, Message) {
        if (err) return next(err);
        console.log("Search result\n:" + Message);

        if (Message instanceof Array) {
            Message.isarray = true;
        } else {
            Message.isarray = false;
        }

        res.json(Message);
    });
});
router.post("/publish-message", function (req, res) {
    res.status(200).json({message: "ok"});
});



module.exports = router;
