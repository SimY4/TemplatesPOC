'use strict';

templates.controller('TikaController', ['$scope', '$interval', 'tikaService', function ($scope, $interval, tikaService) {

    $scope.contents = CodeMirror.fromTextArea(document.getElementById("contents"), {
        readOnly: true,
        lineWrapping: true,
        viewportMargin: Infinity
    });

    $scope.uploadFile = function (files) {
        $scope.fileName = files[0].name;
        var fd = new FormData();
        fd.append("file", files[0]);
        tikaService.upload(fd).$promise.then(function(result) {
            $scope.contents.setValue(result.contents);
        });
    };

}]);