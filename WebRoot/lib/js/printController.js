/**
 * Created by Shao on 2016/12/20.
 */
var app = angular.module('erpPrint', []);

app.controller('printCtrl', function ($scope, $http, $interval, $rootScope, $timeout) {
    $scope.showDocker = 1;
    $scope.opt = {
        buju: '0',
        chicun: '1',
    };
    //默认打印机
    $scope.currPrint = {
        name: "打印机1",
        value: 'print1'
    };
    //获取打印机列表
    $scope.loadPrintList = function(){
        $http.post('', null, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (ret) {
            if(ret.status==1) {
                alert('打印机列表请求失败！');
            }else{
                $scope.printList=ret.data;
            }
        });
    };

    //获取打印预览样式
    $scope.loadFrame = function(){
        $http.post('', null, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (ret) {
            if(ret.status==1) {
                alert('打印预览失败！');
            }else{
                $scope.frameList=ret.data;
            }
        });
    };


    //
    $scope.printOpt = function (type) {
        console.log(type);
        $scope.showDocker = type;
        $("#printModal").modal('show');
    //    获取打印机和预览样式
    //     $scope.loadPrintList();
    //     $scope.loadFrame();
    };


    $scope.printList = [
        {
            name: "打印机1",
            value: 'print1'
        }, {
            name: "打印机2",
            value: "print2"
        },
    ];

    $scope.toggleSelection = function (item) {
        $scope.selection = item.value;
        $scope.currPrint = item;
        $scope.opt.printValue = item.value;
        console.log($scope.opt.printValue);
    };
//    设置页码
    $scope.setYema = function () {
        $scope.opt.yema = 0;
    };
    //
    $scope.frameList=[
        {
            id:"baidu",
            src:"test.html",
            name:"百度"
        }
    ]

});