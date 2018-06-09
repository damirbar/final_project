var mongoose = require('mongoose');
var MessageSchema = new mongoose.Schema({
    guid: {type: String, default: ""},
    channel_guid: {type: String, default: ""},
    user_guid: {type: String, default: ""},
    content: {type: String, default: ""},
    timestamp: Number
});
module.exports = mongoose.model('Message', MessageSchema);
