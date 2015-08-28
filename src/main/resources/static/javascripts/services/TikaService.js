'use strict';

services.factory('tikaService', ['$resource', function ($resource) {
    return $resource('/tika/:action', {action: '@action'}, {
        upload: {
            method: 'POST',
            params: {action: 'upload'},
            withCredentials: true,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        }
    })
}]);
