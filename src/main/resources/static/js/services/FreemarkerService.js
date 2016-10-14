'use strict';

services.factory('freemarkerService', ['$resource', function ($resource) {
    return $resource('/freemarker');
}]);