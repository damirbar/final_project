let nodemailer = require('nodemailer');
let config = require('./../config/config');

let transporter;

let emailService = {

    init: function(){
        transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: config.email.email,
                pass: config.email.password
            }
        });
    },

    sendMail: function(email, subject, body){
        let mailOptions = {
            from: config.name,
            to: email,
            subject: subject ,
            html : body
        };
        transporter.sendMail(mailOptions, function (err, info) {
            if(err)
                console.log(err);
            else
                console.log(info);
        });
    }



};

module.exports = emailService;