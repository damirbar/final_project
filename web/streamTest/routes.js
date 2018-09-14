module.exports = function(app, streams) {

    // GET home
    var index = function(req, res) {
        console.log("dirname: " + __dirname);
        res.render('/Users/user/final_project/web/streamTest/views/index', {
            title: 'STREAM',
            header: 'ERAN live streaming',
            footer: 'wtf??',
            id: req.params.id
        });
    };

    // GET streams as JSON
    var displayStreams = function(req, res) {
        var streamList = streams.getStreams();
        // JSON exploit to clone streamList.public
        var data = (JSON.parse(JSON.stringify(streamList)));

        res.json(200, data);
    };

    app.get('/streams', displayStreams);
    app.get('/stream', index);
    app.get('/:id', index);
};