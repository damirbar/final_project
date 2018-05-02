var mongoose = require('mongoose');
var ChannelSchema = new mongoose.Schema({
    guid: String,
    name: String
});
module.exports = mongoose.model('Channel', ChannelSchema);
