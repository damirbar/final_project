var mongoose = require('mongoose');

var SessionMessageRatingSchema = new mongoose.Schema({
    sender_id: String,    // the user id of which rated the message
    message_id: String,   // message ID
    type: String, // like or dislike
    date:Date,
});

module.exports = mongoose.model('Session_Message_Rating', SessionMessageRatingSchema);