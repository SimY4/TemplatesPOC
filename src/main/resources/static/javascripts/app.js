'use strict';

var templates = angular.module('templates', ['ui.bootstrap', 'ngRoute', 'templates.services'])
    .config(['$routeProvider', '$locationProvider', '$httpProvider',
        function ($routeProvider, $locationProvider, $httpProvider) {

            /* Register view routing */
            $routeProvider.when('/velocity', {
                controller: 'TemplateController',
                templateUrl: '/views/template.html',
                templateService: 'velocityService'
            }).when('/freemarker', {
                controller: 'TemplateController',
                templateUrl: '/views/template.html',
                templateService: 'freemarkerService'
            }).otherwise({
                redirectTo: '/'
            });

            $locationProvider.hashPrefix('!');

            /* Register request error interceptor that shows alerts on UI or
             redirects to login page on unauthenticated requests */
            $httpProvider.interceptors.push(['$q', '$rootScope', '$log', function ($q, $rootScope, $log) {
                return {
                    'responseError': function (rejection) {
                        var status = rejection.status;
                        var config = rejection.config;
                        var method = config.method;
                        var url = config.url;

                        if (!status) {
                            $rootScope.addAlert('danger', 'Server maintenance is currently taking place. Please stand by');
                        } else if (status == 400) {
                            $log.warn('Template is invalid')
                        } else {
                            $rootScope.addAlert('danger', method + ' on ' + url + ' failed with status ' + status);
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

var services = angular.module('templates.services', ['ngResource']);