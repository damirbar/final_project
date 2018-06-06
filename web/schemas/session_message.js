var mongoose = require('mongoose');

var SessionMessageSchema = new mongoose.Schema({
    email: {type: String, default: ""},    // mail of sender
    sid: {type: String, default: ""} ,   // session ID
    type: {type: String, default: ""},  // question or answer
    likes: {type : Number, default: 0},
    dislikes: {type : Number, default: 0},
    likers: [String],
    dislikers: [String],
    body: {type: String, default: ""},
    name: {type: String, default: ""},
    date:{type: Date, default: Date.now()},
    replies: Array
}, {usePushEach: true});

module.exports = mongoose.model('Session_Message', SessionMessageSchema);
