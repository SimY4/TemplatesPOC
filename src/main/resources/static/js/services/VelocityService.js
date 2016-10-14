'use strict';

services.factory('velocityService', ['$resource', function ($resource) {
    return $resource('/velocity');
}]);