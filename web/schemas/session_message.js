var mongoose = require('mongoose');
var SessionMessageSchema = new mongoose.Schema({
    email: String,    // mail of sender
    sid: String ,   // session ID
    type: String,  // question or answer
    rating: {type: Number, default:0}, // like/dislike on this message
    body: Array,
    name: String,
    date:Date
});
module.exports = mongoose.model('Session_Message', SessionMessageSchema);
