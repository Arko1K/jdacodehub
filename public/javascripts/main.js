hljs.initHighlightingOnLoad();

var app = angular.module('jda', ['ngMaterial', 'infinite-scroll']).config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
      .primaryPalette('light-blue')
      .accentPalette('blue');
});

app.controller('controller-root', ['$scope', '$http', function($scope, $http) {
  // Constants
  $scope.baseUrl = "http://localhost:9000/submission";


  // Initialization
  $scope.loading = true;
  $scope.componentsLoaded = 0;
  $scope.page = -1;
  $scope.statuses = ['All'];

  $http({
    method: 'GET',
    url: $scope.baseUrl + "/status"
  }).success(function (response) {
    $scope.statuses.push.apply($scope.statuses, Object.keys(response));
    $scope.statusIndices = [0];
    $scope.initialLoad();
  });

  $http({
    method: 'GET',
    url: $scope.baseUrl + "/stats"
  }).success(function (response) {
    $scope.statuses[0].All = response['total-submissions'];
    $scope.stats = response;
  });


  $scope.search = function (page) {
    $scope.loading = true;

    var searchParams = {};
    if (page != null)
      $scope.page = page;
    else
      $scope.page += 1;
    if ($scope.page != 0)
      searchParams['page'] = $scope.page;
    if ($scope.statusParams != null && $scope.statusParams.length > 0)
      searchParams['status'] = $scope.statusParams;
    if ($scope.query != null && $scope.query.length > 1)
      searchParams['query'] = $scope.query;

    $http({
      method: 'GET',
      url: $scope.baseUrl,
      params: searchParams
    }).success(function (response) {
      if ($scope.page == 0)
        $scope.searchResponse = response;
      else
        $scope.searchResponse.result.push.apply($scope.searchResponse.result, response.result);

      console.log($scope.searchResponse);

      $scope.loading = false;
    });
  };

  $scope.infiniteSearch = function () {
    if ($scope.searchResponse != null && $scope.searchResponse.result.length == $scope.searchResponse.count)
      return;
    $scope.search();
  };

  $scope.toggleStatus = function (index) {
    var i = $scope.statusIndices.indexOf(index);
    if (i == -1)
      $scope.statusIndices.push(index);
    else
      $scope.statusIndices.splice(i, 1);

    var all = false;
    if (index == 0) {
      if (i == -1)
        all = true;
    }
    else if ($scope.statusIndices.indexOf(0) != -1)
      all = true;
    if (all)
      $scope.statusParams = null;
    else {
      var typeArr = [];
      for (i = 0; i < $scope.statusIndices.length; i++)
        typeArr.push($scope.statuses[$scope.statusIndices[i]]);
      $scope.statusParams = typeArr.join(',');
    }

    $scope.page = -1;
    $scope.search();
  };

  $scope.querySearch = function () {
    if ($scope.query.length > 1) {
      $scope.page = -1;
      $scope.search();
    }
  };


  $scope.initialLoad = function () {
    $scope.componentsLoaded += 1;
    if ($scope.componentsLoaded == 1)
      $scope.search();
  };
}]);