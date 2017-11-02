var mongoose = require('mongoose');
var StudentSchema = new mongoose.Schema({
    firstName: String,
    lastName: String,
    displayName: String,
    email: String,
    gender: String,
    age: Number,
    aboutMe: String,
    facebookId: String,
    image: String,
    country: String,
    city: String,
    uni: {},
    courses: Array,
    fs: {},
    grades: Array,
    cred: Number,
    fame: Number,
    msg: Array,
    register_date: Date,
    last_update: Date,
    notifications: Array
});
module.exports = mongoose.model('User', UserSchema);
