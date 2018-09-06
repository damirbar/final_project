module.exports = function(io, streams) {

  io.sockets.on('connection', function(client) {
    console.log(client.id);
    client.id = 'stream_' + client.id;
    console.log(client.id);
    console.log('eran ' + client.id + ' Connected');
    client.emit('id', client.id);

    client.on('message', function (details) {
      let id = details.to.slice(7);
      let otherClient = io.sockets.sockets[id];

      if (!otherClient) {
        return;
      }
        delete details.to;
        details.from = client.id;
        otherClient.emit('message', details);
    });
      
    client.on('readyToStream', function() {
      console.log('eran ' + client.id + ' is ready to stream!!!');
      
      streams.addStream(client.id);
    });
    
    client.on('update', function() {
      streams.update(client.id);
    });

    function leave() {
        console.log('eran ' + client.id + ' Disconnected');
      streams.removeStream(client.id);
    }

    client.on('disconnect', leave);
    client.on('leave', leave);
  });
};