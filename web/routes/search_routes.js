var express = require('express');
var router = express.Router();
var mongoose = require("mongoose");
var User = require("../schemas/user");

router.get('/free-text-search', function(req, res){
    console.log('free text search');
    User.createIndex({name: "text", description: "text"}, function(err){
        if (err) return err;
        return res.status(200).json({message: 'free text search'});
    });
});

module.exports = router;