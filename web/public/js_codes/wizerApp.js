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

        .when('/edit-profile', {
            templateUrl: '../pages/edit_profile.html',
            controller: 'editProfileController'
        })

    // .otherwise({ redirectTo: '/'});

    // $locationProvider.html5Mode({
    //     enabled: true,
    //     requireBase: false
    // });
});





wizerApp.factory('AuthInterceptor', function ($window, $q) {
    return {
        request: function(config) {
            config.headers = config.headers || {};
            if ($window.localStorage.getItem('token')) {
                config.headers['x-access-token'] = $window.localStorage.getItem('token');
            }
            return config || $q.when(config);
        },
        response: function(response) {
            if (response.status === 401) {
                //  Redirect user to login page / signup Page.
            }
            return response || $q.when(response);
        }
    };


});


wizerApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('AuthInterceptor');
});



