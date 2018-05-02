var mongoose = require('mongoose');
var MessageSchema = new mongoose.Schema({
    guid: String,
    channel_guid: String,
    user_guid: String,
    content: String,
    timestamp: Number
});
module.exports = mongoose.model('Message', MessageSchema);
