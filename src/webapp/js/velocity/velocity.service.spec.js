'use strict';

describe('VelocityService', function() {
    var $httpBackend;
    var VelocityService;

    beforeEach(function() {
        jasmine.addCustomEqualityTester(angular.equals);
    });

    beforeEach(module('templates.velocity'));

    beforeEach(inject(function(_$httpBackend_, _VelocityService_) {
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/velocity').respond({
            'template'  : '',
            'parameters': []
        });

        VelocityService = _VelocityService_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('should fetch template data from `/velocity`', function() {
        var template = VelocityService.get();
        $httpBackend.flush();
        expect(template).toEqual({
            'template'  : '',
            'parameters': []
        });
    });

});