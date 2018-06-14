var mongoose = require('mongoose');

var SessionMessageSchema = new mongoose.Schema({
    email: {type: String, default: ""},    // mail of sender
    nickname: {type: String, default: "Anon"},
    poster_id: {type: String, required: true},// message poster ID
    sid: {type: String, default: ""} ,   // session ID
    type: {type: String, default: ""},  // question or answer
    reply: {type: Boolean, default: false},  // message or reply
    parent_id: {type: String, default: ""},  // parent message ID
    likes: {type : Number, default: 0},
    dislikes: {type : Number, default: 0},
    likers: [String],
    dislikers: [String],
    body: {type: String, default: ""},
    date:{type: Date, default: Date.now()},
    replies: Array,
    num_of_replies: {type: Number, default: 0}
}, {usePushEach: true});

module.exports = mongoose.model('Session_Message', SessionMessageSchema);
