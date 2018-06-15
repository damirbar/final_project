const socketIOEmitter = require('../tools/socketIO');
const Notification = require('../schemas/notification');
const User = require('../schemas/user');
// const Message = require('../schemas/session_message');
const Course_messages = require('../schemas/course_message');
const Message = require('../schemas/session_message');
const Session = require('../schemas/session');
ObjectID = require('mongodb').ObjectID;


var actionsTypes = {
    0: 'disliked',
    1: 'liked',
    2: 'commented on',
    4: 'replied to',
    5: ''
};

var subjectTypes = {
    'message': Message,
    'session': Session,
    'course message': Course_messages
};


exports.saveAndEmitNotification = function (notification){

    User.findOne({_id:notification.sender_id},{first_name: 1, last_name: 1}, function(err, user){
        if(err) return console.log(err);
        subjectTypes[notification.subject].findOne({_id: notification.subject_id}, {_id: 0, body: 1}, function(err,object){
            if(err) return console.log(err);
            notification.content = `${user.first_name} ${user.last_name} ${actionsTypes[notification.action]} your ${notification.subject}: ${object.body}`;
            notification = new Notification(notification);
            notification.save(function(err, notification){
                if(err) return console.log(err);
                socketIOEmitter.emitEvent(notification.receiver_id, 'newNotification', notification);
            });
        })
    })
};
