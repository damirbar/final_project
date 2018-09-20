// View Model
var RTCViewModel = function(client, path) {
  var client = client,
      path = path,
      mediaConfig = {
        audio:true,
        video: {
          mandatory: {},
          optional: []
        }
      },
      availableStreams = {},
      isStreaming = ko.observable(false),
      link = ko.observable(),
      localVideoEl = document.getElementById('localVideo');

  function getReadyToStream(stream) {
    attachMediaStream(localVideoEl, stream);
    client.setLocalStream(stream);
    client.send('readyToStream');
    link(window.location.host + "/" + client.getId()); 
    isStreaming(true);
  }
  function loadStreamsFromServer() {
      availableStreams ={}
  }

  return {
    streams: availableStreams,
    isStreaming: isStreaming,
    link: link,
    localCamButtonText: ko.computed(
      function() {
        return isStreaming() ? "Stop" : "Start";
      }
    ),

    refresh: loadStreamsFromServer,
    toggleLocalVideo: function() {
      if(isStreaming()){
        client.send('stopStreaming');
        localVideoEl.src = '';
        client.setLocalStream(null);
        isStreaming(false);
      } else {
        getUserMedia(mediaConfig, getReadyToStream, function () {
          throw new Error('Failed to get access to local media.');
        });
      }
    },
  }
};