var mongoose = require('mongoose');

var CourseMessageSchema = new mongoose.Schema({
    email: {type: String, default: ""},    // mail of sender
    poster_id: {type: String, required: true},// message poster ID
    cid: {type: String, default: ""} ,   // session ID
    type: {type: String, default: ""},  // question or answer
    body: {type: String, default: ""},
    name: {type: String, default: ""},
    date:{type: Date, default: Date.now()},
    replies: Array,
    parent_id: {type: String, default: ""},
    reply: {type: Boolean, default: false},
}, {usePushEach: true});

module.exports = mongoose.model('Course_Message', CourseMessageSchema);
