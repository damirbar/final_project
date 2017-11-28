var router = require('express').Router();
var path = require("path");
var passport = require('passport');
var expressValidator = require('express-validator');
var Student = require("../schemas/student");


router.get('/login', function (req, res) {
    res.sendFile(path.join(__dirname + "/../login.html"));
});


router.get('/logout', function (req, res) {
    req.logout();
    res.redirect('/');
});


//auth with google

router.get('/google', passport.authenticate('google', {
    scope: ['profile', 'email']
}));

//google callbackURL
router.get('/google/callback', passport.authenticate('google'), function (req, res) {
    //res.redirect('/#/profile/' + req.user.id);
    res.status(200).send(req.user);
});


//auth with facebook

router.get('/facebook', passport.authenticate('facebook', {scope: ['email']}));

//facebook callbackURL
router.get('/facebook/callback', passport.authenticate('facebook'), function (req, res) {
    //res.redirect('/#/profile/' + req.user.id);
    res.status(200).send(req.user);
});

router.post("/auth-login-user-pass",function (req,res){
    Student.findOne({mail: req.query.email}, function (err, student) {
        if (err) return next(err);
        if(req.query.password==student.password || Student.comparePassword(req.query.password,student.password))res.status(200).send({message: "", token: "", student: student});
        //if(Student.comparePassword(req.query.password,student.password)) res.send(student);
    });
});

//local stratagy
router.use(expressValidator());

router.post("/new-student", function (req, res) {
    var fname = req.body.fname;
    var lname = req.body.lname;
    var email = req.body.email;
    // var userName = req.body.userName;
    var password = req.body.password;
    var password_cnfrm = req.body.password_cnfrm;

    req.checkBody("fname", "First Name is required").notEmpty();
    req.checkBody("lname", "Last Name is required").notEmpty();
    req.checkBody("email", "Email is required").notEmpty();
    req.checkBody("email", "Email is not valid").isEmail();
    //req.checkBody("userName", "UserName is required").notEmpty();
    req.checkBody("password", "Password is required").notEmpty();
    req.checkBody("password_cnfrm", "Bouth passwords are required").notEmpty();
    req.checkBody("password_cnfrm", "Passwords do not match").equals(req.body.password);


    var errors = req.validationErrors();

    if (errors) {
        res.status(400).send("erans error");
    }
    else {
        var newStudent = new Student({

            first_name: fname,
            last_name: lname,
            display_name: fname + " " + lname,
            mail: email,
            about_me: '',
            country: '',
            city: '',
            age: 0,
            uni: {},
            gender: '',
            courses: [],
            photos: [],
            // profile_img: profile._json.picture.data.url,
            fs: {},
            grades: [],
            cred: 0,
            fame: 0,
            msg: [],
            register_date: Date.now(),
            last_update: Date.now(),
            notifications: [],
            facebookid: '',
            accessToken: '',
            googleid: '',
            password: password//encrypt

        });
        Student.createStudent(newStudent, function (err, user) {
            if (err) {
                if (err.name === 'MongoError' && err.code === 11000) {
                    // Duplicate username
                    console.log('User ' + Fname + " cannot be added " + email + ' already exists!');
                    return res.status(500).send('User ' + Fname + " cannot be added " + email + ' already exists!');
                }
                if (err.name === 'ValidationError') {
                    //ValidationError
                    var str = "";
                    for (field in err.errors) {
                        console.log("you must provide: " + field + " field");
                        str += "you must provide: " + field + " field  ";
                    }
                    return res.status(500).send(str);
                }
                // Some other error
                console.log(err);
                return res.status(500).send(err);
            }
            console.log(user);
            Student.findOne({mail: email}, function (err, student) {
                if (err) return next(err);
                res.send(student);
            });

        });
    }
});


module.exports = router;
