'use strict';

templates.controller('TemplatesController', ['$scope', 'templatesService', function ($scope, templatesService) {

    $scope.templateFileName = '';
    $scope.uploadedTemplate = CodeMirror.fromTextArea(document.getElementById("uploadedTemplate"), {
        mode: 'velocity',
        lineNumbers: true,
        lineWrapping: true,
        readOnly: true,
        viewportMargin: Infinity
    });

    $scope.convertedResult = CodeMirror.fromTextArea(document.getElementById("convertedResult"), {
        mode: 'xml',
        readOnly: true,
        lineWrapping: true,
        viewportMargin: Infinity
    });

    $scope.templateDetails = {};
    $scope.parameters = [];
    $scope.context = [];

    $scope.addParameter = function (parameter) {
        $scope.context.push({
            paramName: parameter,
            paramValue: ''
        })
    };

    $scope.removeParameter = function (parameter) {
        var paramIndex = $scope.context.indexOf(parameter);
        if (paramIndex > -1) {
            context.slice(paramIndex, 1)
        }
    };

    $scope.uploadTemplate = function (files) {
        $scope.templateFileName = files[0].name;
        var fd = new FormData();
        fd.append("template", files[0]);
        templatesService.upload(fd).$promise.then(updateTemplate);
    };

    $scope.convert = function (parameters) {
        var context = {};
        for (var i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            context[parameter.paramName] = parameter.paramValue;
        }
        templatesService.convert(context).$promise.then(function (response) {
            $scope.convertedResult.setValue(response.content);
        });
    };

    function updateTemplate() {
        templatesService.get().$promise.then(function (templateDetails) {
            $scope.uploadedTemplate.setValue(templateDetails.template);
            $scope.parameters = templateDetails.parameters;
        });
    }

    updateTemplate();

}]);