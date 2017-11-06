var Teacher = require("./schemas/teacher")

function feedTeachers(dataArr,res){
    for (var i=0; i<dataArr.length; i++) {
        var myData= new Teacher(dataArr[i]);
        myData.save()
            .then(function (item){
                res.send("successfully saved item to db");
                console.log("successfully added " +myData.first_name +  " to db");
            })
            .catch(function (error) {
                res.status(400).send("unable to save data");
                console.log("unable to save data");
            });
    }

}
module.exports.func=feedTeachers;