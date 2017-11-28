'use strict';

const student = require('../schemas/student');

exports.getProfile = mail =>

new Promise((resolve,reject) => {

    student.find({ mail: mail }, { display_name: 1, mail: 1, register_date: 1, _id: 0 })

    .then(students => resolve(students[0]))

.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

});