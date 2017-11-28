var mongoose = require('mongoose');

var fileSchema = new mongoose.Schema({

   id:{type: String,required:true},
   url: {type: String,required: true},
   type: String,
   name: String

});

module.export = mongoose.model('File',fileSchema);