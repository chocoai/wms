/**
 * Created by Shao on 2016/12/20.
 */
var app = angular.module('erpApp', ['ui.bootstrap']);

app.controller('indexCtrl', function ($scope, $http,$interval,$rootScope,$timeout) {

    //检测窗口是否被放大
    $scope.devicePixelRatio=window.devicePixelRatio;
    $scope.closeDpiTips=function () {
        $scope.devicePixelRatio=1;
    };

    $rootScope.testclass="abc";
	//个人信息下拉菜单
    $scope.userSubNav=false;
    $scope.showCommonApp=false;
    //缓存皮肤class
    var cacheSkin=localStorage.getItem('skin');
    if(cacheSkin && cacheSkin!==''){
        $scope.skinClass=cacheSkin;
    }
    $scope.skins=[
        {
            name:"海浪",
            class:"skin-waves",
            img:"/lib/images/skins/waves.jpg"
        },
        {
            name:"岩滩",
            class:"skin-rocky-beach",
            img:"/lib/images/skins/rocky-beach.jpg"
        },
        {
            name:"山峰",
            class:"skin-mountains",
            img:"/lib/images/skins/mountains.jpg"
        },
        {
            name:"星空",
            class:"skin-starry",
            img:"/lib/images/skins/starry.jpg"
        },
        {
            name:"晚霞",
            class:"skin-sunrise",
            img:"/lib/images/skins/sunrise.jpg"
        },
        {
            name:"素描",
            class:"skin-sumiao",
            img:"/lib/images/skins/sumiao.jpg"
        },
        {
            name:"水墨",
            class:"skin-shuimo",
            img:"/lib/images/skins/shuimo.jpg"
        },
        {
            name:"山水",
            class:"skin-xingkong",
            img:"/lib/images/skins/xingkong.jpg"
        },
    ];
    $scope.chooseSkin=function (item) {
        $scope.skinClass=item.class;
        localStorage.setItem('skin',item.class);
        $("iframe").contents().find("div[user-agent]").removeClass();
        $("iframe").contents().find("div[user-agent]").addClass(item.class);
    };
    //获取常用app
    $scope.getAppMenuList=function () {
        $http.post('/queryMyMenuList', {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (responseData) {
            if(responseData && responseData.menuList!==''){
                $scope.appMenuList=responseData.menuList;
            };
        });
    };
    $scope.getAllAppMenuList=function () {
            $http.post('/queryMenuList', {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'X-Requested-With' : 'XMLHttpRequest'
                }
            }).success(function (responseData) {
                if(responseData && responseData.menuList!==''){
                    $scope.allMenuList=responseData.menuList;
                };
            });
        };

    $scope.getAppMenuList();
    $scope.getAllAppMenuList();

    //管理常用app
    $scope.moreApp='open';
    $scope.showMoreApp=function () {
        $('.manageApp').show();
        $('.manageApp').removeClass('animated bounceOutDown');
        $('.manageApp').addClass('animated bounceInDown');
    };
    $scope.hideMoreApp=function () {
        $('.manageApp').addClass('animated bounceOutDown');
    };
    //添加app
    $scope.addAppMenu=function (item) {
        item.src=item.url;
        item.menuId=item.id;
        if($scope.appMenuList.length<10){
            $scope.appMenuList.push(item);
        }else{
            alert('您的常用应用已经足够多了');
        }
    };
    //删除常用app
    $scope.removeApp=function (index) {
        $scope.appMenuList.splice(index,1);
    };
    //保存常用应用
    $scope.saveCommonApp=function () {
        $http.post('/saveMyMenuList',$.param({myMenuList: JSON.stringify($scope.appMenuList)}) ,{
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (responseData) {
            alert("保存成功！");
            $('.manageApp').addClass('animated bounceOutDown');
        });
    };
    //系统升级
    $scope.updateSys=function () {
        $scope.isNew=false;
        $scope.loadingSys="检查中，请稍等……"
        $('#updateSys').show();
        $('#updateSys').addClass('animated zoomIn');
        $timeout(function () {
        //
            $scope.isNew=true;
            $scope.loadingSys="恭喜，您的系统已是最新版本！";
        },3500)
    };
    $scope.closeUpdateSys=function () {
        $('#updateSys').hide();
    };

    //
	$scope.tabs = [];
    $scope.user = {};
    $scope.getCookie=function(cookieName){
        var name = cookieName + "=";
        var ca = document.cookie.split(';');
        for(var i=0; i<ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1);
            if (c.indexOf(name) != -1){
                return c.substring(name.length, c.length);
            }
        }
        return "";
    };
    //请求子系统
    $scope.getMenus = function (sysId) {
        $http.post('/index/queryMenuTree', $.param({systemId: sysId}), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (response) {
        	if(response.status == "fail") {
        		alert("会话过期，请重新登录");
        		window.top.location.href = "/login/index";
        		return ;
        	}else{
        		$scope.user = response.user;
        		$scope.tabs = [];
        		$scope.items = eval(response.menuTree);
        		$scope.orgName=decodeURIComponent($scope.getCookie('orgName'));
        		
        		if($scope.items && $scope.items.length>0){
        			for(var i=0;i<$scope.items.length;i++){
            			if(i==0){
            				$scope.items[i].isOpen=true;
            				if($scope.items[i].menu.length>0){
            					$scope.creatTabs($scope.items[i].menu[0]);
            				}
            			}else{
            				$scope.items[i].isOpen=true;
            			}
            		}
        		}
        		$('.subSysWrap').addClass('animated bounceOutRight');
        	}
        });
    };

    //记录当前点击的时候菜单
    $scope.setCurrItem=function(currName){
    	if($scope.currItemName==currName){
    		$scope.currItemName='';
    	}else{
    		$scope.currItemName=currName	
    	};
    	
    };
    
    $scope.getMenus(null);
    $scope.loadSystem = function(){
	    	$http.post('/index/querySystems', null, {
	    		headers: {
	    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
	    			'X-Requested-With' : 'XMLHttpRequest'
	    		}
	    	}).success(function (response) {
	    		if(response.status == "fail") {
	    			alert("会话过期，请重新登录");
	    			window.top.location.href = "/login/index";
	    			return ;
	    		}else{
	    			$scope.subSys = response.Systems;
	    		}
	    	});
	    }

    //请求菜单
    $scope.showAttr = function (_this) {
        if (_this.name) {
            if($scope.tabs.length>0 && $scope.tabs.length<17){
                for( var i in $scope.tabs){
                    if($scope.tabs[i].name==_this.name) {
                        $scope.isActive = _this.name;
                        var isIn=1;
                    }
                };
                if(isIn!==1){
                    $scope.creatTabs(_this);
                };
            }else if ($scope.tabs.length > 16) {
                alert('您已打开16个tab页了，再开可能会导致浏览器崩溃~');
            }else {
                $scope.creatTabs(_this);
            }
        }
    };
//创建删除tab
    $scope.showActive = function (_this) {
        $scope.isActive = _this.name;
    };
    $scope.creatTabs = function (_this) {
        $scope.tabs.push(_this);
        $scope.isActive = _this.name;

        if ($(window).height() < $('body').height()) {
            $('.grail_aside').css('min-height', $('body').height());
            $('.tabs').height($('body').height() - 108);
        } else {
            $('.grail_aside').css('min-height', $(window).height());
            $('.tabs').height($(window).height() - 108);

        }
    };
    $scope.removeTab = function (i, tab) {
        $scope.tabs.splice(i, 1);
        if($scope.tabs.length>0){
            $scope.isActive = $scope.tabs[$scope.tabs.length-1].name;
        }
    };
    
    $scope.logout = function(){
    	var a=confirm("确定注销吗？");
        if(a){
        	window.location.href = '/login/logout';
        }
    }

//关闭
    $scope.close=function (i) {
        $scope.removeTab(i);
        $scope.$apply();
    };
//刷新
    $scope.refresh=function (tab) {
        $('iframe#'+tab.key).attr('src',tab.src);
    };
//关闭全部
    $scope.closeall=function () {
        var a=confirm("确定关闭全部标签？");
        if(a){
            $scope.tabs=[];
            $scope.$apply();
        }
    };
//  关闭其他
    $scope.closeother=function (tab) {
        if($scope.tabs.length>1){
            var a=confirm("确定关闭其他标签？");
            if(a){
                $scope.tabs=[];
                $scope.tabs.push(tab);
                $scope.isActive = tab.name;
                $scope.$apply();
            }
        }
    };
//新标签页打开
    $scope.opennewtab=function (tab) {
        // window.open($('iframe#'+tab.key).attr('src'), "_blank")
        window.open(tab.src, "_blank");
    }
    $scope.editpwd={};
    $scope.$watch("editpwd.newpwd1",function(){
            if($scope.editpwd.newpwd1!=$scope.editpwd.newpwd){
                $scope.editpwd.newpwd1err=true;
            }else{
                $scope.editpwd.newpwd1err=false;
            }
    });
    $scope.editpwd.newpwderr=false;
    $scope.$watch("editpwd.newpwd",function(){
        var pwdtest=/^[a-zA-Z0-9]{6,30}$/;
            if(pwdtest.test($scope.editpwd.newpwd)==false){
                $scope.editpwd.newpwderr=true;
                $scope.editpwd.newpasswordmsg="密码格式错误";
            }else if( $scope.editpwd.newpwd && $scope.editpwd.newpwd==$scope.editpwd.oldpwd && $scope.editpwd.newpwd!==''){
                $scope.editpwd.newpwderr=true;
                $scope.editpwd.newpasswordmsg="新密码不能和旧密码相同";
            }else{
                $scope.editpwd.newpwderr=false;
            }
    });

    $scope.updatePwd=function(){
        $http.post('/login/edit',$.param({'oldpwd':$scope.editpwd.oldpwd,'newpwd':$scope.editpwd.newpwd}), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With' : 'XMLHttpRequest'
            }
        }).success(function (response) {
            if(response.result==1){
                window.location.href = '/login/logout';
            }else if(response.result==0){
                $scope.editpwd.oldpwd1err=true;
            }else{
                alert("修改异常");
            }
        });
    }
//    全屏
    $scope.isFullscreen=1;
    $scope.fullscreen=function (type) {

        if(type){
            $scope.isFullscreen=0;
            $(".grail").css('padding-left','0');
        }else if(!type){
            $scope.isFullscreen=1;
            $(".grail").css('padding-left','200px');
        }
        $scope.$apply();
    };
    var count;
    $('html').keydown(function (event) {
        var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
        switch (myEvent.keyCode) {
            case 115:// F4
                count=!count;
                $scope.fullscreen(count);
                break;
        }
    });
});

app.directive('rightcontext', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            close:"&",
            closeall:"&",
            closeother:"&",
            refresh:"&",
            opennewtab:"&"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            element.contextMenu('mymenu',{
                bindings: {
                    'refresh': function (item) {
                        scope.refresh();
                    },
                    'close': function (item) {
                        scope.close();
                    },
                    'closeall': function (item) {
                        scope.closeall();
                    },
                    'closeother': function (item) {
                        scope.closeother();
                    },
                    'opennewtab': function (item) {
                        scope.opennewtab();
                    }
                }
            });
        }
    }
})