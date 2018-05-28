var express = require('express');
var router = express.Router();
var mongoose = require("mongoose");
var User = require("../schemas/user");
var File = require("../schemas/file");
var Session = require("../schemas/session");
var University = require("../schemas/university");
var Course = require("../schemas/course");
var Faculty = require("../schemas/faculty");
var Department = require("../schemas/department");


router.get('/free-text-search', function (req, res) {

    let keywords =  req.query.keyword;

    let numOfCollections = 3;
    let doneCounter = 0;

    let searchResults = {};

    let sendSearchResults = function(){
        // doneCounter = 0;
        res.status(200).json(searchResults);
    };

    User.find({$text: {$search: keywords}}, function (err, results) {
        if(err){
            doneCounter++;
            console.log(err);

        }else{
            searchResults.users = results;
            doneCounter++;
            if(doneCounter == numOfCollections){
                sendSearchResults();
            }
        }
    });

    File.find({$text: {$search: keywords}}, function (err, results) {
        if(err){
            console.log(err);
            doneCounter++;
        }else{
            searchResults.files = results;
            doneCounter++;
            if(doneCounter == numOfCollections){
                sendSearchResults();
            }
        }
    });

    Session.find({$text: {$search: keywords}}, function (err, results) {
        if(err){
            console.log(err);
            doneCounter++;
        }else{
            searchResults.sessions = results;
            doneCounter++;
            if(doneCounter == numOfCollections){
                sendSearchResults();
            }
        }
    });

    // User.find({$text: {$search: keywords}}, function (err, results) {
    //     if(err){
    //         console.log(err);
    //     }else{
    //         doneCounter++;
    //         if(doneCounter == 7){
    //             sendSearchResults();
    //         }
    //     }
    // });

    // User.find({$text: {$search: keywords}}, function (err, results) {
    //     if(err){
    //         console.log(err);
    //     }else{
    //         doneCounter++;
    //         if(doneCounter == 7){
    //             sendSearchResults();
    //         }
    //     }
    // });
    //
    // User.find({$text: {$search: keywords}}, function (err, results) {
    //     if(err){
    //         console.log(err);
    //     }else{
    //         doneCounter++;
    //         if(doneCounter == 7){
    //             sendSearchResults();
    //         }
    //     }
    // });
    //
    // User.find({$text: {$search: keywords}}, function (err, results) {
    //     if(err){
    //         console.log(err);
    //     }else{
    //         doneCounter++;
    //         if(doneCounter == 7){
    //             sendSearchResults();
    //         }
    //     }
    // });
});


module.exports = router;
