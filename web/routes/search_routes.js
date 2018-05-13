var express = require('express');
var router = express.Router();
var mongoose = require("mongoose");
var User = require("../schemas/user");
const Session = require("../schemas/session");

router.get('/free-text-search', function(req, res){

    console.log('free text search');

    let keywords = req.query.keywords;

    Session.find({$text: {$search: keywords} }, function(err, results){
        if (err) return err;
        console.log(results);
        return res.status(200).json(results);
    });
});

module.exports = router;