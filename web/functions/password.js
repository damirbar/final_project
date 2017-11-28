'use strict';

const student = require('../schemas/student');
const bcrypt = require('bcryptjs');
const nodemailer = require('nodemailer');
const randomstring = require("randomstring");
const config = require('../config/config.json');

exports.changePassword = (mail, password, newPassword) =>

new Promise((resolve, reject) => {

    student.find({ mail: mail })

    .then(students => {

    let student = students[0];
const hashed_password = student.password;

if (bcrypt.compareSync(password, hashed_password)) {

    const salt = bcrypt.genSaltSync(10);
    const hash = bcrypt.hashSync(newPassword, salt);

    student.password = hash;

    return student.save();

} else {

    reject({ status: 401, message: 'Invalid Old Password !' });
}
})

.then(student => resolve({ status: 200, message: 'Password Updated Sucessfully !' }))

.catch(err => reject({ status: 500, message: 'Internal Server Error !' }));

});

exports.resetPasswordInit = mail =>

new Promise((resolve, reject) => {

    const random = randomstring.generate(8);

student.find({ mail: mail })

    .then(students => {

    if (students.length == 0) {

    reject({ status: 404, message: 'User Not Found !' });

} else {

    let student = students[0];

    const salt = bcrypt.genSaltSync(10);
    const hash = bcrypt.hashSync(random, salt);

    student.country = hash;
    student.last_update = new Date();

    return student.save();
}
})

.then(student => {

    const transporter = nodemailer.createTransport(`smtps://${config.email}:${config.password}@smtp.gmail.com`);

const mailOptions = {

    from: `"${config.name}" <${config.email}>`,
    to: mail,
    subject: 'Reset Password Request ',
    html: `Hello ${student.display_name},<br><br>
    			&nbsp;&nbsp;&nbsp;&nbsp; Your reset password token is <b>${random}</b>. 
    			If you are viewing this mail from a Android Device click this <a href = "http://wizer/${random}">link</a>. 
    			The token is valid for only 2 minutes.<br><br>
    			Thanks,<br>
    			wizer.`

};

return transporter.sendMail(mailOptions);

})

.then(info => {

    console.log(info);
resolve({ status: 200, message: 'Check mail for instructions' })
})

.catch(err => {

    console.log(err);
reject({ status: 500, message: 'Internal Server Error !' });

});
});

exports.resetPasswordFinish = (mail, token, password) =>

new Promise((resolve, reject) => {

    student.find({ mail: mail })

    .then(students => {

    let student = students[0];

const diff = new Date() - new Date(student.last_update);
const seconds = Math.floor(diff / 1000);
console.log(`Seconds : ${seconds}`);

if (seconds < 120) {

    return student;

} else {

    reject({ status: 401, message: 'Time Out ! Try again' });
}
})

.then(student => {

    if (bcrypt.compareSync(token, student.country)) {

    const salt = bcrypt.genSaltSync(10);
    const hash = bcrypt.hashSync(password, salt);
    student.password = hash;
    student.country = undefined;
    student.last_update = undefined;

    return student.save();

} else {

    reject({ status: 401, message: 'Invalid Token !' });
}
})

.then(student => resolve({ status: 200, message: 'Password Changed Sucessfully !' }))

.catch(err => reject({ status: 500, message: 'Internal Server Error !' }));

});