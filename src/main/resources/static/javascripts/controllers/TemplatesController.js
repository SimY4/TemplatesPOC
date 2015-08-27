'use strict';

templates.controller('TemplatesController', ['$scope', '$interval', 'templatesService', function ($scope, $interval, templatesService) {

    var changed = false;

    $scope.uploadedTemplate = CodeMirror.fromTextArea(document.getElementById("uploadedTemplate"), {
        mode: 'velocity',
        lineWrapping: true,
        viewportMargin: Infinity
    });
    $scope.uploadedTemplate.on("changes", function () { changed = true; });

    $scope.convertedResult = CodeMirror.fromTextArea(document.getElementById("convertedResult"), {
        mode: 'xml',
        readOnly: true,
        lineWrapping: true,
        viewportMargin: Infinity
    });

    $scope.parameters = [];
    $scope.context = [];

    $scope.addParameter = function (parameter) {
        var paramIndex = $scope.parameters.indexOf(parameter);
        if (paramIndex > -1) {
            $scope.parameters.splice(paramIndex, 1)
        }
        $scope.context.push({
            paramName: parameter,
            paramValue: ''
        });
        $scope.convert($scope.context)
    };

    $scope.removeParameter = function (parameter) {
        var paramIndex = $scope.context.indexOf(parameter);
        if (paramIndex > -1) {
            $scope.context.splice(paramIndex, 1)
        }
        $scope.parameters.push(parameter.paramName);
        $scope.convert($scope.context)
    };

    $scope.uploadTemplate = function (files) {
        $scope.templateFileName = files[0].name;
        var fd = new FormData();
        fd.append("template", files[0]);
        templatesService.upload(fd).$promise.then(function(templateDetails) {
            updateTemplate($scope.uploadedTemplate, templateDetails)
        });
    };

    $scope.convert = function (parameters, template) {
        var request;
        if (angular.isDefined(template)) {
            request = { '__template__' : template };
        } else {
            request = {};
        }
        for (var i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            request[parameter.paramName] = parameter.paramValue;
        }
        templatesService.save(request).$promise.then(function(templateDetails) {
            updateTemplate($scope.convertedResult, templateDetails)
        });
    };

    $interval(function() {
        if (changed) {
            $scope.convert($scope.context, $scope.uploadedTemplate.getValue());
            changed = false;
        }
    }, 500);

    function updateTemplate(editorInstance, templateDetails) {
        $scope.context = $scope.context.filter(function(i) {
            return templateDetails.parameters.indexOf(i.paramName) > -1
        });
        $scope.parameters = $scope.parameters.filter(function(i) {
            return templateDetails.parameters.indexOf(i) > -1
        });
        for (var i = 0; i < templateDetails.parameters.length; i++) {
            const parameter = templateDetails.parameters[i];
            if ($scope.parameters.indexOf(parameter) > -1) continue;

            var found = false;
            for (var j = 0; j < $scope.context.length; j++) {
                found |= $scope.context[i].paramName == parameter;
            }
            if (!found) {
                $scope.parameters.push(parameter);
            }
        }
        editorInstance.setValue(templateDetails.template);
    }

}]);