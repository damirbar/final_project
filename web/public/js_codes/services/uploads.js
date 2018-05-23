wizerApp.service('UploadService', function ($http) {



    this.uploadVideoToUrl = function(file, sid){
        var fd = new FormData();
        fd.append('file', file);

        $http.post('/sessions/post-video', fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })

            .success(function(){
            })

            .error(function(){
            });
    }
});