function CheckMobilityCtrl($scope, $http) {
    $scope.result = null;
    $scope.isLoading = false;

    $scope.checkUrl = function() {
        $scope.result = null;
        $scope.error = null;

        if (!$scope.isLoading) {
            _gaq.push(['_trackEvent', 'Mobile', 'Check', this.site.url]);

            $scope.isLoading = true;
            $http({method: 'POST', url: '/check-mobility', data: this.site}).
                success(function(data, status, headers, config) {
                    $scope.isLoading = false;
                    $scope.result = data;
                    if ($scope.result.redirect) $scope.redirectClass = 'ok'; else $scope.redirectClass = 'no';
                    if ($scope.result.viewport) $scope.viewportClass = 'ok'; else $scope.viewportClass = 'no';
                    if ($scope.result.mediaQueries) $scope.mediaQueriesClass = 'ok'; else $scope.mediaQueriesClass = 'no';
                    if (($scope.result.scriptCount + $scope.result.cssCount) < 15) $scope.countClass = 'ok'; else $scope.countClass = 'no';

                }).
                error(function(data, status, headers, config) {
                    $scope.isLoading = false;
                    $scope.result = {broken: true};
                    $scope.error = 'For some reason we were not able to check your site';
                }
            );
        }
    };
}