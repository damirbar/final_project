var wizerApp = angular.module('wizerApp', ['ngRoute']);

wizerApp.config(function($routeProvider, $locationProvider) {
    $locationProvider.hashPrefix('');
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