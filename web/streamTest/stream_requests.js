module.exports = function (app, streams) {

    // GET home
    var index = function (req, res) {
        if (req.originalUrl === '/favicon.ico' ||
            req.originalUrl === '/stream') {
            res.render('/Users/user/final_project/web/streamTest/views/index', {
                // title: 'STREAM',
                // header: 'ERAN live streaming',
                // footer: 'wtf??',
                id: req.params.id
            });
        }
        else {
            res.render('/Users/user/final_project/web/streamTest/views/index2', {
                // title: 'STREAM',
                // header: 'ERAN live streaming',
                // footer: 'wtf??',
                id: req.params.id
            });
        }
    };

    // GET streams as JSON
    var displayStreams = function (req, res) {
        var streamList = streams.getStreams();
        var data = (JSON.parse(JSON.stringify(streamList)));
        res.status(200).json(data);
    };

    app.get('/streams', displayStreams);
    app.get('/stream', index);
    app.get('/:id', index);
};