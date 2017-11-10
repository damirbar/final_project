var less = require('less');
var fs = require('fs');
// var lessc = require('less-compiler');
var path = require('path');

var lessCompiler = function () {

    var buffer = "";

    var files = fs.readdirSync(path.join(__dirname, "public/stylesheets/less"));

    // console.log(files);
    files.forEach(function (file, index) {
        buffer += fs.readFileSync(path.join(__dirname, "public/stylesheets/less/") + file);
        console.log("Reading Less file to convert: " + path.join(__dirname, "public/stylesheets/less/") + file);
    });

    // for (var i in files) {
    //     buffer += fs.readFileSync(path.join(__dirname, "public/stylesheets/less/") + files[i]);
    //     console.log("Reading Less file to convert: " + path.join(__dirname, "public/stylesheets/less/") + files[i]);
    // }
    // console.log("The buffer: " + buffer);

    fs.writeFile(path.join(__dirname, "/public/stylesheets/main.less"), buffer, function (err) {
        if (err) {
            return console.log("Error writing css! " + err);
        }
        console.log("Successfully wrote main Less file!");
    });




    fs.readFile('./public/stylesheets/main.less', function (err, data) {
        if (err) {
            return console.log("Error reading main.less. " + err);
        }
        data = data.toString();
        less.render(buffer)
            .then(function(output) {
                    // console.log(output.css);
                    fs.writeFileSync('./public/stylesheets/css/main.css', output.css);
                },
                function(err){
                    console.log("And error occurred during rendering less files. " + err);
                });
        // var opts = {
        //     paths: ["./public/stylesheets/less"],
        //     outputDir: "./public/stylesheets/css",
        //     optimization: 1,
        //     filename: "main.less",
        //     compress: true,
        //     sourceMap: true
        // };
        // less.render(data, function (err, css) {
        //     fs.writeFile('./public/stylesheets/css/main.css', css, function (err) {
        //         if (err) {
        //             return console.log("Error writing main.css. " + err);
        //         }
        //         console.log("Done making main.css!");
        //         console.log("css = " + css)
        //
        //     })
        // })
    });


};

module.exports = lessCompiler;