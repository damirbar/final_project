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

    let keywords = req.query.keyword;

    let numOfCollections = 6;
    let doneCounter = 0;

    let searchResults = {};

    let sendSearchResults = function () {
        // doneCounter = 0;
        res.status(200).json(searchResults);
    };

    User.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.users = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });

    File.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.files = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });

    Session.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.sessions = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });

    University.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.universities = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });

    // Faculty.find({$text: {$search: keywords}}, function (err, results) {
    //     if (err) {
    //         console.log(err);
    //     }else if(results != []){
    //         searchResults.faculties = results;
    //     }
    //     doneCounter++;
    //     if (doneCounter == numOfCollections) {
    //         sendSearchResults();
    //     }
    // });

    Course.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.courses = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });

    Department.find({$text: {$search: keywords}}, function (err, results) {
        if (err) {
            console.log(err);
        }else if(results != []){
            searchResults.departments = results;
        }
        doneCounter++;
        if (doneCounter == numOfCollections) {
            sendSearchResults();
        }
    });
});


module.exports = router;
