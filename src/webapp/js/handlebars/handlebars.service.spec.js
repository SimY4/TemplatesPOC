'use strict';

describe('HandlebarsService', function() {
    var $httpBackend;
    var HandlebarsService;

    beforeEach(function() {
        jasmine.addCustomEqualityTester(angular.equals);
    });

    beforeEach(module('templates.handlebars'));

    beforeEach(inject(function(_$httpBackend_, _HandlebarsService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/handlebars').respond({
            'template'  : '',
            'parameters': []
        });

        HandlebarsService = _HandlebarsService_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should fetch template data from `/handlebars`', function() {
        var template = HandlebarsService.get();
        $httpBackend.flush();
        expect(template).toEqual({
            'template'  : '',
            'parameters': []
        });
    });

});