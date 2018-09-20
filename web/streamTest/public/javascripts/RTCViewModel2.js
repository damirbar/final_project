// Model2!!!!
var RTCStream = function(id) {
  this.id = id;
  this.isPlaying = ko.observable(false);
};

// View Model
var RTCViewModel2 = function(client, path) {
  var client = client,
      path = path,
      availableStreams = ko.observable([]),
      isStreaming = ko.observable(false);

  function loadStreamsFromServer() {
    // Load JSON data from server
    $.getJSON(path, function(data) {

      var mappedStreams = [];
      for(var remoteId in data) {
          if(remoteId !== client.getId()) {
            mappedStreams.push(new RTCStream(remoteId));
          }
      }
      availableStreams(mappedStreams);
    });
  }

  return {
    streams: availableStreams,
    isStreaming: isStreaming,
    refresh: loadStreamsFromServer,
    toggleRemoteVideo: function(stream) {
      client.peerInit(stream.id);
      stream.isPlaying(!stream.isPlaying());
    },
  }
};