'use strict';

describe('PebbleService', function() {
  var $httpBackend;
  var PebbleService;

  beforeEach(function() {
    jasmine.addCustomEqualityTester(angular.equals);
  });

  beforeEach(module('templates.pebble'));

  beforeEach(inject(function(_$httpBackend_, _PebbleService_) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/pebble').respond({
      'template': '',
      'parameters': []
    });

    PebbleService = _PebbleService_;
  }));

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should fetch template data from `/pebble`', function() {
    var template = PebbleService.get();
    $httpBackend.flush();
    expect(template).toEqual({
      'template': '',
      'parameters': []
    });
  });

});
