'use strict';

angular.module('templates.pebble').factory('PebbleService', ['$resource', function($resource) {
  return $resource('/pebble');
}]);
