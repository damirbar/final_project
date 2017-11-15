var wizerApp = angular.module('wizerApp', ['ngRoute']);

wizerApp.config(function ($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');
    $routeProvider

        .when('/', {
            templateUrl: '../pages/home.html',
            controller: 'homeController'
        })

        .when('/profile/:id', {
            templateUrl: '../pages/profile.html',
            controller: 'profileController'
        })

        .when('/students/search-by-name/:fname/:lname', {
            templateUrl: '../pages/searchbyname.html',
            controller: 'getProfilesController'
        })

        .when('/students/search-by-name/:fname', {
            templateUrl: '../pages/searchbyname.html',
            controller: 'getProfilesController'
        })

        .when('/courses',{
            templateUrl:'../pages/courses.html',
            controller:'courseController'
        })

        .when('/courses/search-by-name/:course_name', {
            templateUrl: '../pages/courses.html',
            controller: 'courseController'
        })

    // .otherwise({ redirectTo: '/'});

    // $locationProvider.html5Mode({
    //     enabled: true,
    //     requireBase: false
    // });
});