// const HashMap = require('hashmap');
//
// var socketIO = {};
//
//
// socketIO.clients = new HashMap();
// socketIO.sockets = new HashMap();
// socketIO.pendingSocket = new HashMap();
//
//
// socketIO.test = function(){
//     console.log('HIIIIIIIIIIIIII');
// }
//
// socketIO.onDisconnect = function(){
//
//     socketIO.test();
//
//     var socketID = socket.id;
//
//     if(pendingSockets.has(socketID)){
//         pendingSockets.remove(socketID);
//         console.log("unregistered socket "+ socket.id + " disconnected");
//
//     }else{
//         var userID = sockets.get(socketID);
//         socketIO.sockets.remove(socketID);
//         socketIO.clients.remove(userID);
//         console.log(userID + " disconnected");
//     }
// }
//
//
// socketIO.unregisterClientFromClients = function (user_id){
//
// }
//
//
//
//
//
// module.exports = socketIO;