'use strict';

const student = require('../schemas/student');
const bcrypt = require('bcryptjs');

exports.registerUser = (display_name, mail, password) =
>

new Promise((resolve, reject) = > {

    const salt = bcrypt.genSaltSync(10);
const hash = bcrypt.hashSync(password, salt);

const newStudent = new student({

    display_name: display_name,
    mail: mail,
    password: hash,
    register_date
:
new Date()
})
;

newStudent.save()

    .then(() = > resolve({status: 201, message: 'User Registered Sucessfully !'})
)

.
catch(err = > {

    if(err.code == 11000
)
{

    reject({status: 409, message: 'User Already Registered !'});

}
else
{

    reject({status: 500, message: 'Internal Server Error !'});
}
})
;
})
;


// exports.registerUser = function (display_name, mail, password) {
//
//     new Promise(function (resolve, reject) {
//
//         const salt = bcrypt.genSaltSync(10);
//         const hash = bcrypt.hashSync(password, salt);
//
//         const newStudent = new student({
//             display_name: display_name,
//             mail: mail,
//             password: hash,
//             register_date: new Date()
//         });
//
//         newStudent.save().then(function () {
//             resolve({status: 201, message: 'User Registered Successfully !'})
//         })
//             .catch(function (err) {
//                 if (err.code == 11000) {
//                     reject({status: 409, message: 'User Already Registered !'});
//                 }
//                 else {
//                     reject({status: 500, message: 'Internal Server Error !'});
//                 }
//             })
//         ;
//     })
// };


