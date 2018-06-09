wizerApp.controller('editProfileController', ['$scope',
function($scope) {

    var currActive = ".personal-details";

    $scope.personalDetails = function() {
        if (currActive == ".personal-details") return;

        $(currActive).removeClass("active");
        $(".personal-details").addClass("active");
        currActive = ".personal-details";
    };

    $scope.aboutMe = function() {
        if (currActive == ".about-me") return;

        $(currActive).removeClass("active");
        $(".about-me").addClass("active");
        currActive = ".about-me";
    };

}]);