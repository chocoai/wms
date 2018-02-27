angular.module('MyFilter', [])
        .controller('ExampleController', ['$scope', function ($scope) {

        }])
        .directive('ensureUnique', ['$http','$timeout','$window',function($http,$timeout,$window) {
            return {
                restrict:"A",
                require: 'ngModel',
                link: function(scope, ele, attrs, ngModelController) {
                    scope.$watch(attrs.ngModel, function(n) {
                        if (!n) return;
                        if(workflow.selected.ruleKey) return;
                        $timeout.cancel($window.timer);
                        $window.timer = $timeout(function(){
                            $http({
                                method: 'post',
                                url: '/billdesign/checkCode', //根据换成自己的url
                                params:{
                                    "code":n
                                }
                            }).success(function(data) {
                                ngModelController.$setValidity('unique', data.status); //这个取决于你返回的，其实就是返回一个是否正确的字段，具体的这块可以自己修改根据自己的项目
                            }).error(function(data) {
                                ngModelController.$setValidity('unique', false);
                            });
                        },500);
                    });
                }
            };
        }])
    .directive('ensureExist', ['$http','$timeout','$window', function ($http, $timeout, $window) {
            return {
                restrict:'A',
                require : '^?ngModel',
                scope:false,
                link : function (scope, element, attrs, mainCtrl) {
                	element.on('blur',function(){
                		$http({
                            method: 'post',
                            url: '/billdesign/checkListenerByclassName', //根据换成自己的url
                            params:{
                                "className":mainCtrl.$modelValue
                            }
                        }).success(function(data) {
                        	var index = element.attr("index")
                        	mainCtrl.$setValidity('exist'+index, data.status); //这个取决于你返回的，其实就是返回一个是否正确的字段，具体的这块可以自己修改根据自己的项目
                        }).error(function(data) {
                        	mainCtrl.$setValidity('exist'+index, false);
                        	mainCtrl.$setValidity('ok'+index, true);
                        });
                	});
                	
                	
//                    scope.$watch(attrs.ngModel, function (n) {
//                        if(!n) return ;
//                        $timeout.cancel($window.timer);
//                        $window.timer = $timeout(function(){
//                            $http({
//                                method: 'post',
//                                url: '/billdesign/checkListenerByclassName', //根据换成自己的url
//                                params:{
//                                    "className":n
//                                }
//                            }).success(function(data) {
//                            	ngModel.$setValidity('exist', data.status); //这个取决于你返回的，其实就是返回一个是否正确的字段，具体的这块可以自己修改根据自己的项目
//                            }).error(function(data) {
//                            	ngModel.$setValidity('exist', false);
//                            	ngModel.$setValidity('ok', true);
//                            });
//                        },1000);
//                    }, true)
                }
            };
    }]);