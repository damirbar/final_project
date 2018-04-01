'use strict';

const auth = require('basic-auth');
const jwt = require('jsonwebtoken');

const register = require('../functions/register');
const login = require('../functions/login');
const profile = require('../functions/profile');
const password = require('../functions/password');
const config = require('../config/config.json');

var Student = require("../schemas/student");


module.exports = router => {

    router.get('/', (req, res) => res.end('Welcome to Wizer !'));

    router.post('/authenticate', (req, res) => {
        console.log("GOT REQUESTTTTTTTTT " + JSON.stringify(req.body));
        const credentials = auth(req);

        if (!credentials) {

            res.status(400).json({message: 'Invalid Request !'});

        } else {

            login.loginUser(credentials.name, credentials.pass)

                .then(result => {

                    const token = jwt.sign(result, config.secret, {expiresIn: '1d'});
                    Student.findOne({mail: credentials.name}, function (err, student) {
                        if (err) return next(err);
                        student.accessToken = token;
                        student.save()
                            .then(function (item) {
                                console.log("Saved a token to the student " + credentials.name);
                            })
                            .catch(function (err) {
                                console.log("\nCouldn't save student with token to the DB\nError: " + err.errmsg + "\n");
                            });

                    });


                    res.status(result.status).json({message: result.message, token: token});

                })

                .catch(err => res.status(err.status).json({message: err.message}));
        }
    });

    router.post('/users', (req, res) => {

        const name = req.body.display_name;
        const mail = req.body.mail;
        const password = req.body.password;

        if (!name || !mail || !password || !name.trim() || !mail.trim() || !password.trim()) {

            res.status(400).json({message: 'Invalid Request !'});

        } else {

            register.registerUser(name, mail, password)

                .then(result => {

                    res.setHeader('Location', '/users/' + mail);
                    res.status(result.status).json({message: result.message})
                })

                .catch(err => res.status(err.status).json({message: err.message}));
        }
    });

    router.get('/users/:id', (req, res) => {

        const token = req.headers['x-access-token'];
        console.log("Token from header is: " + token);

        Student.find({accessToken: token}, function (err, student) {
            if (err) next(err);

            console.log("The student is: " + JSON.stringify(student));
        });

        if (checkToken(req)) {

            profile.getProfile(req.params.id)

                .then(result => res.json(result))

                .catch(err => res.status(err.status).json({message: err.message}));

        } else {

            res.status(401).json({message: 'Invalid Token !'});
        }
    });

    router.put('/users/:id', (req, res) => {

        if (checkToken(req)) {

            const oldPassword = req.body.password;
            const newPassword = req.body.newPassword;

            if (!oldPassword || !newPassword || !oldPassword.trim() || !newPassword.trim()) {

                res.status(400).json({message: 'Invalid Request !'});

            } else {

                const token = req.headers['x-access-token'];
                Student.findOne({accessToken: token}, function (err, stud) {
                    if (err) next(err);

                    password.changePassword(stud.mail, oldPassword, newPassword)

                        .then(result => res.status(result.status).json({message: result.message}))

                        .catch(err => res.status(err.status).json({message: err.message}));
                });


            }
        } else {

            res.status(401).json({message: 'Invalid Token !'});
        }
    });

    router.post('/users/:id/password', (req, res) => {

        const mail = req.params.id;
        const token = req.body.token;
        const newPassword = req.body.password;

        if (!token || !newPassword || !token.trim() || !newPassword.trim()) {

            password.resetPasswordInit(mail)

                .then(result => res.status(result.status).json({message: result.message}))

                .catch(err => res.status(err.status).json({message: err.message}));

        } else {

            password.resetPasswordFinish(mail, token, newPassword)

                .then(result => res.status(result.status).json({message: result.message}))

                .catch(err => res.status(err.status).json({message: err.message}));
        }
    });

    function checkToken(req) {

        const token = req.headers['x-access-token'];

        if (token) {

            try {

                var decoded = jwt.verify(token, config.secret);

                return decoded.message === req.params.id;

            } catch (err) {

                return false;
            }

        } else {

            return false;
        }
    }
}