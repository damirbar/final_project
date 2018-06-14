wizerApp.controller('profileController',
    function ($scope, $routeParams, $http, $rootScope, $timeout, ProfileService, CourseService) {

        console.log("Hello from profileController");
        var defaultProfilePicture = "https://images.youtrendit.com/1487525287/desktop/avatar-600/youtrendit_Australopitekus.jpg";

        $scope.loading = true;
        $scope.pictureLoading = true;

        $scope.userArr = [];
        $scope.profile = {};
        $scope.readMore = false;
        $scope.courses = [];
        $scope.editUser = {};
        $scope.editUser.gender = 'unspecified';


        // NEEDS TO CHANGE
        $scope.editUser.birthday = new Date("00-00-0000");
        $scope.profile = new Date("00-00-0000");


        var initPage = function() {
            CourseService.getCoursesById($routeParams.id)
                .then(function (data) {
                    console.log("got courses");
                    $scope.courses = data;
                    console.log(JSON.stringify(data));
                });
        };


        $scope.getProfile = function() {
            ProfileService.getProfileByID($routeParams.id)
                .then(function (data) {
                    console.log("Looking for " + $routeParams.id);
                    $scope.profile = data;
                    $scope.editUser = data;
                    console.log(data);
                    initPage();
                    $scope.loading = false;
                    $scope.pictureLoading = false;
                });
        };
        $scope.getProfile();

        $scope.getPhoto = function() {
            console.log("PROFILE IMAGE = " + $scope.profile.profile_img);
            return $scope.profile.profile_img == '' ? defaultProfilePicture : $scope.profile.profile_img;
        };

        $scope.readMoreButton = function() {
            $scope.readMore = !$scope.readMore;
        };


        if ($rootScope.loggedUser._id === $routeParams.id) {
            ProfileService.getProfileEvents(0, 10)
                .then(function (data) {
                console.log("Events: " + JSON.stringify(data));
                $scope.events = data;
            });
        }


        $scope.editProfile = function() {

            console.log("SENDING TO EDIT PROFILE: " + JSON.stringify($scope.editUser));
            ProfileService.editProfile($scope.editUser)
                .then(function (data) {
                    console.log("GOT FROM EDIT PROFILE: " + JSON.stringify(data))
                })
                .catch(function (err) {
                    console.log("GOT ERROR FROM EDIT PROFILE: " + err);
                });
        };

        $scope.uploadPhoto = function(){

            var file = $scope.myFile;
            var uploadUrl = "/students/post-profile-image";
            var fd = new FormData();
            fd.append('recfile', file);

            $http.post(uploadUrl,fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .then(function(data){
                        console.log("upload success!!!");
                    },
                    function(){
                        console.log("error!!");
                    });
        };



        $scope.calculateAge = function calculateAge(birthday) { // birthday is a date
            birthday = new Date(birthday);
            var ageDifMs = Date.now() - birthday.getTime();
            var ageDate = new Date(ageDifMs); // miliseconds from epoch
            return Math.abs(ageDate.getUTCFullYear() - 1970);
        };



        $scope.myImage='';
        $scope.myCroppedImage='';

        var handleFileSelect=function(evt) {
            var file=evt.currentTarget.files[0];
            var reader = new FileReader();
            reader.onload = function (evt) {
                $scope.$apply(function($scope){
                    $scope.myImage=evt.target.result;
                    $scope.profile.profile_img = $scope.myCroppedImage;
                    $scope.uploadPhoto();
                });
            };
            reader.readAsDataURL(file);
            $scope.pictureLoading = true;
            $timeout(function() {
                $scope.getProfile();

            }, 6000);
        };
        angular.element(document.querySelector('#file-profile-image')).on('change',handleFileSelect);



        $scope.genderChoose = function(gender) {
            console.log("Called role choose");
            $('.dropdown-header').text(gender.charAt(0).toUpperCase() + gender.slice(1));
            $scope.editUser.gender = gender;
        };




    });