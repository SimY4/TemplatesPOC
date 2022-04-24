'use strict';

angular.module('templates.handlebars').factory('HandlebarsService', ['$resource', function ($resource) {
    return $resource('/handlebars');
}]);