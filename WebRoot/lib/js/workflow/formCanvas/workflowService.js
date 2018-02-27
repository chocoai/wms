var WorkflowServices = angular.module('myApp.WorkflowServices',[ ]);

WorkflowServices.factory('FormDefService', ['$http', function ($http) {


    var find = function (postData) {
        return $http.post("/api/findForms",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    //反回对应的对象
    return {
        find: function (postDate) {
            return find(postDate);
        },
        
    };
}]);