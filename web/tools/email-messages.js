
const ACCOUNT = 'http://localhost:3000/auth/get-user-from-google?token=';

const SIGNITURE = '<div class="wizeUp-signature" style="width:60vw;margin:50px auto;line-height: 1.4"><div><a class="no-decoration pointer" style="cursor:pointer;text-decoration: none;" href="localhost:3000">wizeUp.com</a></div>' +
    '<div>Tel Aviv, Israel</div></div>';
const STYLE = '<style type="text/css">' +
    '.wizeUp-wrapper{width:60vw;margin:auto;padding:2vw;border-bottom: 1px solid rgba(199, 167, 104, 0.4);}' +
    '.wizeUp-title{text-align:center;font-size:30px;font-weight:bold;color:#0E5D7C;margin-bottom:15px}' +
    '.wizeUp-text{word-break: normal;line-height: 1.4;font-size: 18px;color: #484848;}' +
    '.no-decoration{text-decoration: none;}' +
    '.wizeUp-action-button{text-decoration: none;padding: 15px;color: white;text-align: center;margin: auto;background-color:#0E5D7C;width: 230px;;margin-top: 20px;font-size: 20px; border-radius: 5px;}' +
    '.pointer{cursor:pointer}' +
    '.wizeUp-signature{60vw;margin:50px auto;line-height: 1.4}' +
    '@media (max-width: 564px){.wizeUp-wrapper{width:90%}.title{font-size:24px;}.wizeUp-text{font-size: 16px;}}';
'</style>'


var options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };

var messages = {

    registration: function(user){
        return '<div class="wizeUp-wrapper" style="width:60vw;margin:auto;padding:2vw;border-bottom: 1px solid rgba(199, 167, 104, 0.4);"> <div class="wizeUp-title" style="text-align:center;font-size:30px;font-weight:bold;color:#0E5D7C;margin-bottom:15px">Welcome To wizeUp </div></br>' +
            '<div class="wizeUp-text" style="word-break: normal;line-height: 1.4;font-size: 18px;color: #484848;">Hey ' + user.first_name + ', thanks for joining wizeUp. ' +
            'You are ready to start learning smarter, be sure to complete your profile and add all the necessary information ' + '</div>' +
            '<a class="no-decoration" style="text-decoration: none;" href="' + ACCOUNT + user.accessToken + '"><div class="wizeUp-action-button" style="text-decoration: none;padding: 15px;color: white;text-align: center;margin: auto;background-color:#0E5D7C;width: 230px;margin-top: 20px;font-size: 20px; border-radius: 5px;">Go To wizeUp</div></a>' +
            '</div>' + SIGNITURE + STYLE
    },

    reset_password: function (user, token) {
        return '<div class="wizeUp-wrapper" style="width:60vw;margin:auto;padding:2vw;border-bottom: 1px solid rgba(199, 167, 104, 0.4);"> <div class="wizeUp-title" style="text-align:center;font-size:30px;font-weight:bold;color:#0E5D7C;margin-bottom:15px"> Reset wizeUp Password </div></br>' +
            '<div class="wizeUp-text" style="word-break: normal;line-height: 1.4;font-size: 18px;color: #484848;">Hey ' + user.first_name + ', We all forget...  your temp password is: <br>' + token +'</div>' +
        '<a class="no-decoration" style="text-decoration: none;"><div class="swap-action-button" style="text-decoration: none;padding: 15px;color: white;text-align: center;margin: auto;background-color:#0E5D7C;width: 230px;margin-top: 20px;font-size: 20px; border-radius: 5px;">Reset password</div></a>' +
        '</div>' + SIGNITURE + STYLE
    }
};


module.exports = messages;