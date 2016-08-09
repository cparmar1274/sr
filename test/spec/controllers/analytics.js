'use strict';

describe('Controller: AnalyticsCtrl', function () {

  // load the controller's module
  beforeEach(module('predictiveApp'));

  var AnalyticsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    AnalyticsCtrl = $controller('AnalyticsCtrl', {
      $scope: scope
      // place here mocked dependencies
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(AnalyticsCtrl.awesomeThings.length).toBe(3);
  });
});
