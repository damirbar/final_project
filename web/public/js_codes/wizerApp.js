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

        .when('/search-by-name/:fname/:lname', {
            templateUrl: '../pages/searchbyname.html',
            controller: 'getProfilesController'
        })

        .when('/search-by-name/:fname', {
            templateUrl: '../pages/searchbyname.html',
            controller: 'getProfilesController'
        })

    // .otherwise({ redirectTo: '/'});

    // $locationProvider.html5Mode({
    //     enabled: true,
    //     requireBase: false
    // });
});