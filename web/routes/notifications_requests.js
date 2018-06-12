const express = require("express");
const router = express.Router();
const Notification = require("../schemas/notification");

router.post('/read-all-notifications', function(req,res){

    var user_id = req.user_id;

    Notification.update({receiver_id: user_id, seen: false}, {seen: true}, function (err){
       if(err){
           console.log(err);
           return res.status(500).send("Error while reading notifications");
       }
       else{
           console.log("successfully read all notifications");
           return res.status(200).send("successfully read all notifications");
       }
    });
});

router.get('get-user-notifications', function(req, res){
    var user_id = req.query.user_id;
    Notification.find({receiver_id: user_id},function (err, notifications) {
        if(err){
            return res.status(500).json({message: 'error with getting notifications'});
        }
        return res.status(200).json(notifications);
    });
});




module.exprots = router;