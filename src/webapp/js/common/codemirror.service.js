'use strict';

angular.module('templates.common').factory('CodeMirrorNg', [function () {
    return function (element, options) {
        return CodeMirror.fromTextArea(document.getElementById(element), options);
    };
}]);