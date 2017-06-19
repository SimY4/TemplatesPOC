describe('CommonController', function() {
    var $controller;
    var $interval;
    var $injector;

    var MockTemplateService;
    var CodeMirrorNg;
    var deferredRequest;
    
    beforeEach(module('templates.common'));

    beforeEach(function() {
        $injector = jasmine.createSpyObj('$injector', ['get']);
        MockTemplateService = jasmine.createSpyObj('MockTemplateService', ['get', 'save']);
        CodeMirrorNg = function(elem, options) {
            expect(elem).toBeDefined();
            expect(options).toBeDefined();
            return jasmine.createSpyObj('CodeMirror', ['on']);
        }
    });

    beforeEach(inject(function(_$controller_, _$interval_, $q){
        $controller = _$controller_;
        $interval = _$interval_;
        deferredRequest = $q.when({
            template  : '',
            parameters: []
        });
    }));

    it('should add parameter to template and rebuild the template', function() {
        $injector.get.and.returnValue(MockTemplateService);
        MockTemplateService.get.and.returnValue({
            $promise: deferredRequest
        });
        MockTemplateService.save.and.returnValue({
            $promise: deferredRequest
        });

        var $scope = {};
        var controller = $controller('CommonController', {
            $scope       : $scope,
            $interval    : $interval,
            $injector    : $injector,
            $route       : { current : { templateService : 'MockTemplateService' } },
            CodeMirrorNg: CodeMirrorNg
        });

        $scope.addParameter("test");
        expect(MockTemplateService.get).toHaveBeenCalled();
        expect(MockTemplateService.save).toHaveBeenCalled();
    });

    it('should remove parameter to template and rebuild the template', function() {
        $injector.get.and.returnValue(MockTemplateService);
        MockTemplateService.get.and.returnValue({
            $promise: deferredRequest
        });
        MockTemplateService.save.and.returnValue({
            $promise: deferredRequest
        });

        var $scope = {};
        var controller = $controller('CommonController', {
            $scope       : $scope,
            $interval    : $interval,
            $injector    : $injector,
            $route       : { current : { templateService : 'MockTemplateService' } },
            CodeMirrorNg: CodeMirrorNg
        });

        $scope.removeParameter("test");
        expect(MockTemplateService.get).toHaveBeenCalled();
        expect(MockTemplateService.save).toHaveBeenCalled();
    });

});