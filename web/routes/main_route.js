var express = require('express');
var router = express.Router();
var path = require("path");


router.get("/", function (req, res) {
    console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
    console.log("token = " + req.headers["x-access-token"]);
    if (req.headers["x-access-token"]) {
        console.log("token = " + req.headers["x-access-token"]);
    } else {
        console.log("\t\t\t\t\t\t\t\t\tRequest with no token!");
    }


    res.sendFile(path.join(__dirname + "/../index.html"));
});


module.exports = router;