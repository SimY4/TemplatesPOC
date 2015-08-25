'use strict';

services.factory('templatesService', ['$resource', function ($resource) {
    return $resource('/templates/:action', {action: '@action'}, {
        upload: {
            method: 'POST',
            params: {action: 'upload'},
            withCredentials: true,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        }
    });
}]);