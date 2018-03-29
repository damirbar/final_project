
wizerApp.controller('homeController', ['$scope', '$http', function($scope, $http) {
    $scope.dummyreq = function() {
        $http.get("/").then(function(data){ console.log("OK");}, function(){ console.log("NOT OK"); })
    }
}]);