var express = require('express');
var router = express.Router();
var mongoose = require("mongoose");
var User = require("../schemas/user");
<<<<<<< .merge_file_a14136

router.get('/free-text-search', function(req, res){
    console.log('free text search');
    User.createIndex({name: "text", description: "text"}, function(err){
        if (err) return err;
        return res.status(200).json({message: 'free text search'});
=======
const Session = require("../schemas/session");

router.get('/free-text-search', function(req, res){

    console.log('free text search');

    let keywords = req.query.keywords;

    Session.find({$text: {$search: keywords} }, function(err, results){
        if (err) return err;
        console.log(results);
        return res.status(200).json(results);
>>>>>>> .merge_file_a15968
    });
});

module.exports = router;