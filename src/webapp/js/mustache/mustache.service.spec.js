'use strict';

describe('MustacheService', function() {
    var $httpBackend;
    var MustacheService;

    beforeEach(function() {
        jasmine.addCustomEqualityTester(angular.equals);
    });

    beforeEach(module('templates.mustache'));

    beforeEach(inject(function(_$httpBackend_, _MustacheService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/mustache').respond({
            'template'  : '',
            'parameters': []
        });

        MustacheService = _MustacheService_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should fetch template data from `/mustache`', function() {
        var template = MustacheService.get();
        $httpBackend.flush();
        expect(template).toEqual({
            'template'  : '',
            'parameters': []
        });
    });

});