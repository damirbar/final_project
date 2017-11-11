var express = require("express");
var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
var path = require("path");
var bodyParser = require("body-parser");
var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");


var requests = require('./routes/requests');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

app.use(express.static(path.join(__dirname, 'public')));

var myLessCompiler = require("./less_compiler");
myLessCompiler();


    var mongoDB = 'mongodb://127.0.0.1:27017/main_db';
mongoose.connect(mongoDB, {
     useMongoClient: true}
);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));

Student.find({},{last_name:true,first_name:1,_id:0}, function(err,student){
    if(err) return "error";
    console.log(student);
});

app.use('/', requests);



var feedStudents = function(dataArr) {
    for (var i = 0; i < dataArr.length; ++i) {
        var dat = new Student(dataArr[i]);
        dat.save()
            .then(function(item) {
                console.log("Saved the student " + dat.first_name + " to the DB");
            })
            .catch(function (err) {
                console.log("Couldn't save " + dat.first_name + " to the DB");
            })
    }
};

var studentList = require('./studentsArr');

feedStudents(studentList);


app.listen(3000, function(){
    console.log("listening...");
});
