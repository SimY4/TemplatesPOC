'use strict';

describe('FreemarkerService', function() {
    var $httpBackend;
    var FreemarkerService;

    beforeEach(function() {
        jasmine.addCustomEqualityTester(angular.equals);
    });

    beforeEach(module('templates.freemarker'));

    beforeEach(inject(function(_$httpBackend_, _FreemarkerService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/freemarker').respond({
            'template'  : '',
            'parameters': []
        });

        FreemarkerService = _FreemarkerService_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should fetch template data from `/freemarker`', function() {
        var template = FreemarkerService.get();
        $httpBackend.flush();
        expect(template).toEqual({
            'template'  : '',
            'parameters': []
        });
    });

});