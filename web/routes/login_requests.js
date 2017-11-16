var router = require('express').Router();
var path = require("path");
var passport = require('passport');

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
router.get('/facebook',
    passport.authenticate('facebook', { scope:['user_about_me', 'email']}),
    function(req, res){
    });

//facebook callbackURL
router.get('/facebook/callback',passport.authenticate('facebook'),function (req,res) {
    //res.send('wellcome ' + req.user.facebook.name);
    res.redirect('/#/profile/' + req.user.id);
});

 module.exports = router;
