'use strict';

angular.module('templates.freemarker').factory('FreemarkerService', ['$resource', function ($resource) {
    return $resource('/freemarker');
}]);