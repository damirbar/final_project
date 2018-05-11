var mongoose = require('mongoose');

var SessionMessageSchema = new mongoose.Schema({
    email: String,    // mail of sender
    sid: String ,   // session ID
    type: String,  // question or answer
    ratings: Array,
    likes: {type : Number, default: 0},
    dislikes: {type : Number, default: 0},
    body: Array,
    name: String,
    date:Date,
});
module.exports = mongoose.model('Session_Message', SessionMessageSchema);
