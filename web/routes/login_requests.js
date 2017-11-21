var router = require('express').Router();
var path = require("path");
var passport = require('passport');
var expressValidator = require('express-validator');
var Student =require("../schemas/student");

router.get('/login',function (req,res) {
    res.sendFile(path.join(__dirname + "/../login.html"));
});


router.get('/logout',function (req,res) {
   req.logout();
   res.redirect('/');
});


//auth with google

router.get('/google',passport.authenticate('google',{
    scope: ['profile', 'email']
}));

//google callbackURL
router.get('/google/callback',passport.authenticate('google'),function (req,res) {
    //res.send('wellcome ' + req.user.display_name);
    res.redirect('/#/profile/' + req.user.id);
});


//auth with facebook

// router.get('/auth/facebook',passport.authenticate("facebook", {scope: ['email']}));
//router.get('/facebook',passport.authorize('facebook',{ scope : ['email'] }));
router.get('/facebook', passport.authenticate('facebook', { scope:['email']}));

//facebook callbackURL
router.get('/facebook/callback',passport.authenticate('facebook'),function (req,res) {
    //res.send('wellcome ' + req.user.facebook.name);
    res.redirect('/#/profile/' + req.user.id);
});



//local stratagy
router.use(expressValidator());

router.post("/student", function (req, res) {
    var Fname = req.body.Fname;
    var Lname = req.body.Lname;
    var email=req.body.email;
    var userName = req.body.userName;
    var pass1 =req.body.pass1;
    var pass2 = req.body.pass2;

    req.checkBody("Fname","First Name is required").notEmpty();
    req.checkBody("Lname","Last Name is required").notEmpty();
    req.checkBody("email","Email is required").notEmpty();
    req.checkBody("email","Email is not valid").isEmail();
    req.checkBody("userName","UserName is required").notEmpty();
    req.checkBody("pass1","Password is required").notEmpty();
    req.checkBody("pass2","Bouth passwords are required").notEmpty();
    req.checkBody("pass2","Passwords do not match").equals(req.body.pass1);


    var errors = req.validationErrors();

    if(errors)
    {
        //res.render("/student",{
         //   "errors":errors
        //});
    }
    else {
        var newStudent = new Student({

            first_name: Fname,
            last_name: Lname,
            display_name: Fname + " " + Lname,
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
            password: pass1

        });
        Student.createStudent(newStudent,function (err,user) {
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
                res.send('/#/auth/local/' + student._id);
        });

        });
    }
});

router.post('/local', passport.authenticate('local')//,{successRedirect:'/#/',failureRedirect:'/auth/login'}),
   // function (req,res) {
    //    res.send(req.body.email);
    //}
    );

 module.exports = router;
