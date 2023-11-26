'use strict';

angular.module('templates.mustache').factory('MustacheService', ['$resource', function ($resource) {
    return $resource('/mustache');
}]);