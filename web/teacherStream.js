var Teacher = require("./schemas/teacher")

function feedTeachers(dataArr,res)
{

    for (var i=0;i<dataArr.length; i++)
    {
        var myData = new Teacher(dataArr[i]);
        myData.save()
            .catch(function (error) {
                res.status(400).send("unable to save data. Error: " + error);
                console.log("unable to save data");
                return;
            });
        console.log("successfully added " + myData.first_name + " to db");
    }
    res.send("successfully saved teachers to db");
}
module.exports.func=feedTeachers;