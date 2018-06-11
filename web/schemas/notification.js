var mongoose = require('mongoose');

var NotificationSchema = new mongoose.Schema({
    receiver_id: {type: String, required: true},
    sender_id: {type: String, required: true},
    creation_date: {type: String, default: Date.now()},
    seen: {type: Boolean, default: false},
    type: {type: Number, required: true},
    content: {type: String, required: true}
});

module.exports = mongoose.model('Notification', NotificationSchema);