var mongoose = require('mongoose');
var SessionMessageSchema = new mongoose.Schema({
    type: String, // question or answer
    qaid: String, // question/answer ID
    uid:String, // user ID
    rating: Number,// like/dislike on this message
    body: String
});
module.exports = mongoose.model('Session_Message', SessionMessageSchema);
