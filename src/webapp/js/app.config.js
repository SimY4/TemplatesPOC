'use strict';

angular.module('templates')
    .config(['$routeProvider', '$locationProvider', '$httpProvider',
        function ($routeProvider, $locationProvider, $httpProvider) {

            /* Register view routing */
            $routeProvider.when('/velocity', {
                controller: 'CommonController',
                templateUrl: '/views/template.html',
                templateService: 'VelocityService'
            }).when('/freemarker', {
                controller: 'CommonController',
                templateUrl: '/views/template.html',
                templateService: 'FreemarkerService'
            }).when('/handlebars', {
                controller: 'CommonController',
                templateUrl: '/views/template.html',
                templateService: 'HandlebarsService'
            }).otherwise({
                redirectTo: '/'
            });

            $locationProvider.hashPrefix('!');

            /* Register request error interceptor that shows alerts on UI */
            $httpProvider.interceptors.push(['$q', '$rootScope', '$log', function ($q, $rootScope, $log) {
                return {
                    'responseError': function (rejection) {
                        var status = rejection.status || -1;
                        var config = rejection.config;
                        var method = config.method;
                        var url = config.url;

                        switch (status) {
                            case -1:
                                $rootScope.addAlert('danger', 'Server maintenance is currently taking place. ' +
                                    'Please stand by');
                                break;
                            case 400:
                                $log.warn('Template is invalid');
                                break;
                            default:
                                $rootScope.addAlert('danger', method + ' on ' + url + ' failed with status ' + status);
                                break;
                        }
                        return $q.reject(rejection);
                    }
                };
            }]);

        }]).run(['$rootScope', '$timeout', function ($rootScope, $timeout) {
    $rootScope.alerts = [];

    $rootScope.addAlert = function (type, message) {
        $rootScope.alerts.push({type: type, msg: message});
        $timeout(function () {
            $rootScope.alerts.shift();
        }, 3000);
    };
}]);