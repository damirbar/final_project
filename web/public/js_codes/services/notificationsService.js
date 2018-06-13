wizerApp.service('NotificationService', function (socketIO) {

    socketIO.on('newNotification', function(notification){
        console.log(notification);
    });

});