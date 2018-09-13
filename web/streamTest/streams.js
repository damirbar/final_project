module.exports = function() {
  /**
   * available streams 
   * the id key is considered unique (provided by socket.io)
   */
  var streamList = {};

  /**
   * Stream object
   *
   * Stored in JSON using socket.id as key
   */
  var Stream = function() {
  }

  return {
    addStream : function(id) {
      var stream = new Stream();
      streamList[id] = stream;
    },

    removeStream : function(id) {
      delete streamList[id];
    },

    // update function
    update : function(id, name) {
      var stream = streamList[id];
      stream.name = name;
      
      this.removeStream(id);     
      streamList[id] = stream;
    },

    getStreams : function() {
      return streamList;
    }
  }
};
