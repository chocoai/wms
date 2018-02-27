
var loginMentModule = angular.module('myApp.LoginMent', []);

loginMentModule.controller("myApp.LoginMent.login",function($scope,$http){

    $(".form-group input").on("focus",function(){
        $(this).siblings('.error-notic').fadeIn();
    });
    $(".form-group input").on("blur",function(){
        $(this).siblings('.error-notic').fadeOut();
    });

    // 键盘回车事件
    var tds = $("html");
    tds.keydown(function(event){
        var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
        switch(myEvent.keyCode) {
            case 13://回车事件
                $scope.validata();
            break;
        }
    });

    //初始化页面焦点
        var userName = $("#username");
        if(userName.val() == ""){
            userName.focus();
        }else{
            $("#password").focus();
        }
    $scope.user={};
    $scope.error={
        mobile:'',
        pwd:'请输入密码',
        authCode:'请输入验证码',
        orgId:''
    };


    $scope.checkPSW=function(){
        if(null!=$scope.user.password){
            if($scope.user.password.length<6){
                $scope.error.pwd='密码格式不正确';
                //$(this).siblings('.error-notic').text('密码格式不正确');
            }else{
                $scope.error.pwd='';
                // $(this).siblings('.error-notic').hide().text('请输入密码');
            }
        }

}

    $scope.validata=function(){
        $scope.error.authCode='';
        $http.post('/login/vali',$.param($scope.user),{
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            }
        }).success(function (response) {
            if(response.result==1){
                window.location.href="/index.html";
            }else if(response.result==2){
                if( $scope.user.isMoblie){
                    $scope.error.mobile='手机不存在或有重复的手机';
                }else{
                    $scope.error.mobile='用户不存在或有重复的用户';
                }
                $(".error-notic:eq(0)").show();
            }else if(response.result==3){
                $(".error-notic:eq(1)").show();
                $scope.error.pwd='请确认密码';
            }else{
                $(".error-notic:eq(2)").show();
                $scope.error.authCode='验证码有误';
            }
        });
    }

    $scope.Org=function () {
        var myreg = /(^13\d{9}$)|(^14)[5,7]\d{8}$|(^15[0,1,2,3,5,6,7,8,9]\d{8}$)|(^17)[0,6,7,8]\d{8}$|(^18\d{9}$)/;
        if(myreg.test($scope.user.username)){
            $scope.user.isMoblie=true;
        }else{
            $scope.user.isMoblie=false;
        }
        $http.post('/login/org',$.param($scope.user),{
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            }
        }).success(function (response) {
            if(response.result){
                $scope.organization=response.result;
                $scope.user.orgId=$scope.organization[0].id;
            }else{
                $scope.organization='';
                // $scope.user.orgId=''
            }
        });
    }
});
