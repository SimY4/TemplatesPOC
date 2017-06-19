'use strict';

angular.module('templates.common').factory('CodeMirrorNg', [function () {
    var CodeMirrorNg = function (element, options) {
        return CodeMirror.fromTextArea(document.getElementById(element), options);
    };
    return CodeMirrorNg;
}]);