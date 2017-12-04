var express = require('express');
var router = express.Router();
var path = require("path");

var SystemFile = require("../schemas/file");

router.post("/file", function (req, res) {
    var file = new SystemFile(req.body);
    file.save(function (err) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('Failed adding File Error 11000' + file.name);
                return res.status(500).send('Failed adding File Error 11000' + file.name);
            }
            if (err.name === 'ValidationError') {
                //ValidationError
                for (field in err.errors) {
                    console.log("Validation Error!");
                    return res.status(500).send("validation Error!");
                }
            }
            // Some other error
            console.log(err);
            return res.status(500).send(err);
        }
        res.send("successfully added " + file.name + "Url:" +file.url + " to db");
        console.log("successfully added " + file.name + "Url:" +file.url + " to db");
    });
});

module.exports = router;