wizerApp.controller('homeController', ['$scope', '$http', 'SessionService', function ($scope, $http, SessionService) {
    $scope.dummyreq = function () {
        $http.get("/").then(function (data) {
            console.log("OK");
        }, function () {
            console.log("NOT OK");
        })
    };

    $scope.session = {};

    $scope.dummyreq = function () {
        SessionService.getSessionByID("1234")
            .then(function (data) {
                console.log("Looking for session " + "1234");
                // $scope.profile = data;
                console.log(data);
            });
    }


}]);