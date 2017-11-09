var wizerApp = angular.module('wizerApp', ['ngRoute']);

wizerApp.config(function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: '../pages/home.html',
            controller: 'homeController'
        })
        .when('/profile/:id', {
            templateUrl: '../pages/profile.html',
            controller: 'profileController'
        });
});