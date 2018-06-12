wizerApp.service('SearchService', function (socketIO) {

    socketIO.on('newNotification', function(notification){
        console.log(notification);
    });

});