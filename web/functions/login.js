'use strict';

const student = require('../schemas/student');
const bcrypt = require('bcryptjs');

exports.loginUser = (mail, password) =>

new Promise((resolve,reject) => {

    student.find({mail: mail})

    .then(students => {

    if (students.length == 0) {

    reject({ status: 404, message: 'User Not Found !' });

} else {

    // return students[0];
        return students[0].mail;

}
})

.then(student => {

    const hashed_password = student.password;

if (bcrypt.compareSync(password, hashed_password)) {

    resolve({ status: 200, message: mail });

} else {

    reject({ status: 401, message: 'Invalid Credentials !' });
}
})

.catch(err => reject({ status: 500, message: 'Internal Server Error !' }));

});

	
