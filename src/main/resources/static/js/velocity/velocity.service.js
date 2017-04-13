'use strict';

angular.module('templates.velocity').factory('VelocityService', ['$resource', function ($resource) {
    return $resource('/velocity');
}]);