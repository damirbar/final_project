var mongoose = require('mongoose');
var bcrypt = require('bcryptjs')
var StudentSchema = new mongoose.Schema({

    first_name: String,
    last_name: String,
    display_name: String,
    mail: { type: String, unique: true },
    about_me: String,
    //facebook_id: String,
    country: String,
    city: String,
    age: Number,
    uni: {},
    gender: String,
    courses: Array,
    photos: Array,
    profile_img: String,
    fs: {},
    grades: Array,
    cred: Number,
    fame: Number,
    msg: Array,
    register_date: Date,
    last_update: Date,
    notifications: Array,
    facebookid:String,
    accessToken:String,
    googleid:String,
    password:String
});

module.exports = mongoose.model('Student', StudentSchema);

module.exports.createStudent=function (newStudent,callback) {
    bcrypt.genSalt(10,function (err,salt) {
        bcrypt.hash(newStudent.password,salt,function (err,hash) {
            newStudent.password=hash;
            newStudent.save(callback);
        });
    });
}
module.exports.comparePasssword=function (candidatePassword,hash,callback) {
    bcrypt.compare(candidatePassword,hash,function (err,isMatch) {
        if(err) throw err;
        callback(null,isMatch);
    })
}
