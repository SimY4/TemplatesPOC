'use strict';

angular.module('templates.common').controller('CommonController',
    ['$scope', '$interval', '$injector', '$route', 'CodeMirrorNg',
        function ($scope, $interval, $injector, $route, CodeMirrorNg) {

            var changed = false;
            var templateService = $injector.get($route.current.templateService);

            $scope.template = CodeMirrorNg('template', {
                mode: 'velocity',
                lineWrapping: true,
                viewportMargin: Infinity
            });
            $scope.template.on("changes", function () {
                changed = true;
            });

            $scope.result = CodeMirrorNg('result', {
                mode: 'xml',
                readOnly: true,
                lineWrapping: true,
                viewportMargin: Infinity
            });

            $scope.parameters = [];
            $scope.context = [];
            $scope.conversionTime = -1;

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

            $scope.convert = function (parameters, template) {
                var request = {};
                if (angular.isDefined(template)) {
                    request['__template__'] = template;
                }
                for (var i = 0; i < parameters.length; i++) {
                    var parameter = parameters[i];
                    request[parameter.paramName] = parameter.paramValue;
                }
                templateService.save(request).$promise.then(function (templateDetails) {
                    updateTemplate($scope.result, templateDetails)
                });
            };

            $interval(function () {
                if (changed) {
                    $scope.convert($scope.context, $scope.template.getValue());
                    changed = false;
                }
            }, 500);

            templateService.get().$promise.then(function (templateDetails) {
                updateTemplate($scope.template, templateDetails)
            }, function () {
                updateTemplate($scope.template, { template: "", parameters: [] })
            });

            function updateTemplate(editorInstance, templateDetails) {
                $scope.conversionTime = templateDetails.conversionTime;
                $scope.context = $scope.context.filter(function (i) {
                    return templateDetails.parameters.indexOf(i.paramName) > -1
                });
                $scope.parameters = $scope.parameters.filter(function (i) {
                    return templateDetails.parameters.indexOf(i) > -1
                });
                for (var i = 0; i < templateDetails.parameters.length; i++) {
                    const parameter = templateDetails.parameters[i];
                    if ($scope.parameters.indexOf(parameter) > -1) continue;

                    var found = false;
                    for (var j = 0; j < $scope.context.length; j++) {
                        found |= $scope.context[i].paramName === parameter;
                    }
                    if (!found) {
                        $scope.parameters.push(parameter);
                    }
                }
                editorInstance.setValue(templateDetails.template);
            }

        }
    ]);