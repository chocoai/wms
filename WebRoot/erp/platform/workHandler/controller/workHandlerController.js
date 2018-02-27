/*******************************************************************************
 * 工作台---主模型定义
 ******************************************************************************/
/**
 * 定义入口模块（main）的类，模块名称xyyerp
 * 
 * @type {*|module|angular.Module}
 */
// 模块的$controllerProvider,用来动态注册controller
var controllerProvider = null;
// 创建一个模块,并记录$controllerProvider
var myModule = angular.module('_controllerProvider', [], function(
		$controllerProvider) {
	controllerProvider = $controllerProvider;
});

var main = angular.module('xyy.workflow', [ 'ngRoute', '_controllerProvider',
		'xyy.workflow.workflowServices', 'myApp.myDirective' ]);
main.run(function($rootScope, $interval) {
	if (!$rootScope._model) {
		$rootScope._model = new $Model();
	}
	if (!$rootScope._env) {
		$rootScope._env = new $Env();
	}
	// 如果存在如下对象，则扩展之
	try {
		mixer(ERPApp, $rootScope);
	} catch (e) {

	}

});
main.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/view/myTaskList/:id', {
		templateUrl : 'erp/platform/workHandler/views/task.html',
	})// 工作处理器
	.when('/view/doTask/:id/:handle', {
		templateUrl : 'erp/platform/workHandler/views/doWork.html',
	}).otherwise({
		redirectTo : '/'
	});
} ]);
// 工具栏控制器
main.controller("xyy.workflow.wftoolBoxCtr", function($scope, $routeParams, $http, $compile,WorkService) {
    $scope.taskInstanceId = $routeParams.id;
    $scope.handle = $routeParams.handle;
	$scope.viewStatus = 1;
	$scope.setViewStatus = function(viewStatus) {
		$scope.viewStatus = viewStatus;
	};
    $scope.shenhe={
    	shenhejieguo:'1'
    };

	//获取活动控制器的数据
    WorkService.init({"ti":$scope.taskInstanceId}).success(function(responseData){
        if (responseData.status == "1") {
            $scope.activityController = responseData.activityController;
        } else {
            alert(responseData.errorMsg);
        }
    }).error(function(data, status, headers, config) {
        alert("连接错误，错误码：" + status);
    });
	// 操作表单
	$scope.operatorForm = function () {
        $('.doWorkWrap').hide();
        $("#dataFormDiv").hide();
        $("#operatorFormDiv").show();
	};

	/*----------------- 流程审核变淡：workflowshenhe--------------------------------------------------------*/

	// 数据表单
	$scope.dataForms = function(){
		$scope.viewStatus='12';
		$('.doWorkWrap').hide();
		$("#operatorFormDiv").hide();
    	$("#dataFormDiv").show();
	};
    if($scope.handle=='false'){
        $scope.dataForms();
    }else{
        $scope.operatorForm();
	};

	// 保存
	$scope.save = function() {
		var subng = $("#operatorFormDiv div[BillType]");
        var scopeEnvs=[];
		var isCanSave=false;
		for (var i = 0; i < subng.length; i++) {
			var subScope = angular.element("#" + $(subng[i]).attr('id')).scope();
            scopeEnvs.push(subScope);
            for(var j=0;j<subScope.checkRuleLists.length;j++){
                var _key=subScope.checkRuleLists[j].key;
                $("[key="+_key+"]").focus();
            };
            if(subScope.errList.length>0){
                var isCanSave=false;
                alert("请填写正确信息")
                return;
            }else{
                var isCanSave=true;
			}
		};

		if(!isCanSave){
            alert("请检查单据信息是否填写有误！")
		}else{
            scopeEnvs[0].saveForWF(scopeEnvs);
		}

	};
	// 提交
	$scope.submit = function() {
        if(!$scope.shenhe.shenheyijian){
			alert("审核意见不能为空！");
            $scope.viewStatus=2;
			return;
		}
        var data1 = {};
        data1["taskInstance"] =$scope.taskInstanceId;
        data1["shenhe"] =angular.toJson($scope.shenhe);
        WorkService.submitExamine(data1).success(function(data) {
           if(data.status!=''&& data.status==1){
               var ret=data.transitionList;
               if(ret){
                   $scope.ruleLists=ret;
                   $('#choose-rule').modal('show');
               }else{
                   window.location.href = "process#/view/myTaskList/2";
			   };
		   }

        }).error(function(data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    };
   
	$scope.ruleSelection=function (x) {
        $scope.transitionSelection=x.id;
    };
	$scope.ruleFun=function () {
		var scopeParam=[];
		for(var i=0;i<$scope.scopeEnvs.length;i++){
			var scopeEnv={};
			var me=$scope.scopeEnvs[i];
			clone(me._env,scopeEnv);
			scopeEnv.model=angular.toJson(me._model);
			scopeParam.push(scopeEnv);
		};
		$http.post('/process/submitWF',$.param({'scopeParam':angular.toJson(scopeParam),'transitionId':$scope.transitionSelection, "taskInstance" : $scope.taskInstanceId}),
                {
                    headers : {
                        'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }).success(function(responseData) {
                	if(responseData.status!=''&&responseData.status==1){
                        alert("提交成功");
                        $('#choose-rule').modal('hide');
                        window.location.href = "process#/view/myTaskList/2";
					}else{
                        alert("连接错误，错误码：" + status);
					}
                }).error(function(err) {
            alert("连接错误，错误码：" + status);
        });
    };
	// 移交
	$scope.transfor = function() {
        $http.post('/department/findProcessDept',null,
                {
                    headers : {
                        'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }).success( function(responseData) {
                    $scope.treedata = eval(responseData.groupTreeData);
                    $scope.$broadcast('$_MODEL_CHANGE_$', $scope.treedata);
                }).error(function(err) {
            alert("连接错误，错误码：" + status);
        });
        $('.doWorkWrap').hide();
		$("#transfer").show();
	};
	$scope.$on('transferTree',function(e,data){
        $http.post('/department/findProcessUser',$.param({'id':data[0].id}),
            {
                headers : {
                    'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success( function(responseData) {
            $scope.dUsers = responseData.queryUserResult;
        }).error(function(err) {
            alert("连接错误，错误码：" + status);
        });
	});

    $scope.setSelection=function (u) {
        $scope.selection=u.id;
    };

    $scope.addTransfer=function(){
    	$http.post('/process/transfer',$.param({'transferUsers':$scope.selection,'taskInstance':$scope.taskInstanceId}),
            {
                headers : {
                    'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success( function(responseData) {
				if(responseData.status==1){
                    alert("移交成功");
                    window.location.href = "process#/view/myTaskList/2";
				}else{
					alert("移交失败:原因"+responseData.errorMsg);
				}
        }).error(function(err) {
            alert("连接错误，错误码：" + status);
        });
	}
	
    
    //移交--用户搜索
    $scope.searchName="";
    $scope.searchTransfer=function(){
    	 $http.post('/user/queryUserByName',$.param({'userName':$scope.searchName}),
    	            {
    	                headers : {
    	                    'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
    	                }
    	            }).success( function(responseData) {
    	            	$scope.dUsers = responseData.userList;
    	        }).error(function(err) {
    	            alert("连接错误，错误码：" + status);
    	        });
    }
	
	// 子流程
	$scope.childProcess = function() {
		alert("childProcess");
	};
	//否单弹窗
    $scope.refuse = function () {
        $scope.viewStatus = 5;
        $('#refuse-box').modal();
	};
    //否单提交
    $scope.refuseFun = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        data1["reason"] = $scope.refuseform.reason;
        WorkService.forceEnd(data1)
            .success(function(data) {
                if(data.status==1){
                    alert('否单成功！');
                    $('#refuse-box').modal('hide');
                    window.location.href = "process#/view/myTaskList/2";
                }
            })
            .error(function(data, status, headers, config) {
                alert(
                    "连接错误，错误码：" + status);

            });

    },
    //回退弹窗
    $scope.back = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        WorkService.canBackList(data1).success(function(data) {
			if (data.status=='1'){
				if(data.backList==null){
					alert('此节点未设置退回');
					return;
				}else {
					$scope.backList = data.backList;
                    $scope.viewStatus = 7;
                    $('#return-box').modal();
				}
			}
        }).error(function(data, status, headers, config) {
            alert("连接错误，错误码：" + status);

        });
    },
    //回退提交
    $scope.returnFun = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        data1["canBackName"] = $scope.backName;
        data1["orderReturn"] = $scope.reBackform.reason;
        WorkService.back(data1)
            .success(function(data) {
                if(data.status==1){
                    alert('回退成功！');
                    $('#return-box').modal('hide');
                    window.location.href = "process#/view/myTaskList/2";
                }
            })
            .error(function(data, status, headers, config) {
                alert(
                    "连接错误，错误码：" + status);

            });

    },
    //预约弹窗
    $scope.appointment = function () {
        $scope.viewStatus = 8;
    	$('#order-box').modal();
    },
    //预约提交
    $scope.orderFun = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        data1["reason"] = $scope.orderform.reason;
        data1["time"] = $scope.orderform.time;
        WorkService.OrderTask(data1)
            .success(function(data) {
                if(data.status==1){
                    alert('预约成功！');
                    $('#order-box').modal('hide');
                    window.location.href = "process#/view/myTaskList/2";
                }
            })
            .error(function(data, status, headers, config) {
                alert(
                    "连接错误，错误码：" + status);

            });

    },
    //子流程
    $scope.childProcess = function () {
        alert("childProcess");
    },
    //挂起弹窗
    $scope.suspendBtn = function () {
    	$('#suspend-box').modal();
    },
    //挂起提交
    $scope.suspendFun = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        data1["reason"] = $scope.suspendform.reason;
        WorkService.suspend(data1)
            .success(function(data) {
                if(data.status==1){
                    alert('挂起成功！');
                    $('#suspend-box').modal('hide');
                    window.location.href = "process#/view/myTaskList/2";
                }
            })
            .error(function(data, status, headers, config) {
               alert( "连接错误，错误码：" + status);
            });
    },
    //恢复弹窗
    $scope.recoveryBtn = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        WorkService.getTaskInstanceStatus(data1).success(function(data) {
            if(data.status=='1'&&data.TaskStatus==3){
                $('#recovery-box').modal();
			}else {
            	alert('当前任务不可恢复');
            	return;
			}
        }).error(function(data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });

    },
    //恢复提交
    $scope.recoveryFun = function () {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        WorkService.recovery(data1)
            .success(function(data) {
                if(data.status==1){
                    alert('恢复成功！');
                    $('#recovery-box').modal('hide');
                }
            })
            .error(function(data, status, headers, config) {
               alert("连接错误，错误码：" + status);
            });
    },
    
    /*-----------------   流程图：showCanvas--------------------------------------------------------*/
	$scope.showCanvas = function() {
		var data1 = {};
		data1["taskInstance"] = $scope.taskInstanceId;
		WorkService.shape(data1).success(function(data) {
			$scope.viewStatus = 9;
            $('.doWorkWrap').hide();
			$("#viewTpl").html(data);
			$("#viewTpl").show();
		}).error(function(data, status, headers, config) {
			alert("连接错误，错误码：" + status);

		});
	},

	/*-----------------   流程附件：workflowFile--------------------------------------------------------*/
	$scope.workflowFile = function() {
        var data1 = {};
        data1["taskInstance"] = $scope.taskInstanceId;
        WorkService.workflowFile(data1).success(function(data) {
            $scope.viewStatus = 10;
            $('.doWorkWrap').hide();
            $("#viewTpl").html(data);
            $("#viewTpl").show();

            if($scope.handle=='false'){
                $("#uploadForm").hide();
            };

        }).error(function(data, status, headers, config) {
           alert("连接错误，错误码：" + status);
        });
	}

	/*----------------- 流程日志：workflowLog--------------------------------------------------------*/
	$scope.workflowLog = function() {
		var data1 = {};
		data1["taskInstance"] =$scope.taskInstanceId;
		WorkService.processLog(data1).success(function(data) {
			$scope.viewStatus = 11;
            $('.doWorkWrap').hide();
			$("#viewTpl").html(data);
			$("#viewTpl").show();
		}).error(function(data, status, headers, config) {
			alert("连接错误，错误码：" + status);
		});
	};
    $scope.workflowExamine = function() {
        var data1 = {};
        data1["taskInstance"] =$scope.taskInstanceId;
        WorkService.processExamine(data1).success(function(data) {
			if(data.status!==0){
                $scope.viewStatus = 13;
                $('.doWorkWrap').hide();
                $("#viewTpl").html(data);
                $("#viewTpl").show();
			}else{
				alert(data.errorMsg);
			};
        }).error(function(data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    };


});


main.controller('xyy.workflow.taskCtrl',
				function($scope, $routeParams, TaskService,WorkService) {
					// 页面有关的信息
					$scope.pageInfo = {
						pageSize : 10,
						totalCount : 0,
						pageCount : 0,
						pageIndex : 1,
						pageItems : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
						pageStart : 1,
						pageEnd : 10
					};

					// 待办任务
					$scope.myTaskList = function() {
						// 加载数据部分
						var postData = {};
						postData.pageSize = $scope.pageInfo.pageSize;
						postData.pageIndex = $scope.pageInfo.pageIndex;
						postData.totalCount = $scope.pageInfo.totalCount;
						TaskService
								.myTaskList(postData)
								.success(
										function(responseData) {
											$scope.taskInstanceList = responseData.taskInstancePage.list;
											for (var i = 0; i < $scope.taskInstanceList.length; i++) {
												$scope.taskInstanceList[i].extFields = eval($scope.taskInstanceList[i].extFields);
											}
											$scope.pageInfo.totalCount = responseData.taskInstancePage.totalRow;
											$scope.pageInfo.pageCount = Math
													.floor(($scope.pageInfo.totalCount
															+ $scope.pageInfo.pageSize - 1)
															/ $scope.pageInfo.pageSize);
										})
								.error(
										function(data, status, headers, config) {
											alert(
													"连接错误，错误码：" + status);

										});
					}

					// 在途任务
					$scope.myDoingTaskList = function() {
						// 加载数据部分
						var postData = {};
						postData.pageSize = $scope.pageInfo.pageSize;
						postData.pageIndex = $scope.pageInfo.pageIndex;
						postData.totalCount = $scope.pageInfo.totalCount;
						TaskService
								.myDoingTaskList(postData)
								.success(
										function(responseData) {
											$scope.taskInstanceList = responseData.taskInstancePage.list;
											for (var i = 0; i < $scope.taskInstanceList.length; i++) {
												$scope.taskInstanceList[i].extFields = eval($scope.taskInstanceList[i].extFields);
											}
											$scope.pageInfo.totalCount = responseData.taskInstancePage.totalRow;
											$scope.pageInfo.pageCount = Math
													.floor(($scope.pageInfo.totalCount
															+ $scope.pageInfo.pageSize - 1)
															/ $scope.pageInfo.pageSize);
										})
								.error(
										function(data, status, headers, config) {
											alert(
													"连接错误，错误码：" + status);

										});
					}

					// 已办任务
					$scope.myDoneTaskList = function() {
						// 加载数据部分
						var postData = {};
						postData.pageSize = $scope.pageInfo.pageSize;
						postData.pageIndex = $scope.pageInfo.pageIndex;
						postData.totalCount = $scope.pageInfo.totalCount;
						TaskService
								.myDoneTaskList(postData)
								.success(
										function(responseData) {
											$scope.taskInstanceList = responseData.taskInstancePage.list;
											for (var i = 0; i < $scope.taskInstanceList.length; i++) {
												$scope.taskInstanceList[i].extFields = eval($scope.taskInstanceList[i].extFields);
											}
											$scope.pageInfo.totalCount = responseData.taskInstancePage.totalRow;
											$scope.pageInfo.pageCount = Math
													.floor(($scope.pageInfo.totalCount
															+ $scope.pageInfo.pageSize - 1)
															/ $scope.pageInfo.pageSize);
										})
								.error(
										function(data, status, headers, config) {
											alert(
													"连接错误，错误码：" + status);

										});
					}

					// 预约任务
					$scope.myAppointTaskList = function() {
						// 加载数据部分
						var postData = {};
						postData.pageSize = $scope.pageInfo.pageSize;
						postData.pageIndex = $scope.pageInfo.pageIndex;
						postData.totalCount = $scope.pageInfo.totalCount;
						TaskService
								.myAppointTaskList(postData)
								.success(
										function(responseData) {
											$scope.taskInstanceList = responseData.taskInstancePage.list;
											for (var i = 0; i < $scope.taskInstanceList.length; i++) {
												$scope.taskInstanceList[i].extFields = eval($scope.taskInstanceList[i].extFields);
											}
											$scope.pageInfo.totalCount = responseData.taskInstancePage.totalRow;
											$scope.pageInfo.pageCount = Math
													.floor(($scope.pageInfo.totalCount
															+ $scope.pageInfo.pageSize - 1)
															/ $scope.pageInfo.pageSize);
										})
								.error(
										function(data, status, headers, config) {
											alert(
													"连接错误，错误码：" + status);

										});
					}

					// 挂起任务
					$scope.myHangupTaskList = function() {
                        // 加载数据部分
                        var postData = {};
                        postData.pageSize = $scope.pageInfo.pageSize;
                        postData.pageIndex = $scope.pageInfo.pageIndex;
                        postData.totalCount = $scope.pageInfo.totalCount;
                        TaskService
                            .myHangupTaskList(postData)
                            .success(
                                function(responseData) {
                                    $scope.taskInstanceList = responseData.taskInstancePage.list;
                                    for (var i = 0; i < $scope.taskInstanceList.length; i++) {
                                        $scope.taskInstanceList[i].extFields = eval($scope.taskInstanceList[i].extFields);
                                    }
                                    $scope.pageInfo.totalCount = responseData.taskInstancePage.totalRow;
                                    $scope.pageInfo.pageCount = Math
                                        .floor(($scope.pageInfo.totalCount
                                            + $scope.pageInfo.pageSize - 1)
                                            / $scope.pageInfo.pageSize);
                                })
                            .error(
                                function(data, status, headers, config) {
                                    alert(
                                        "连接错误，错误码：" + status);

                                });
					}

					// 页面初始化加载数据
					$scope.load = function() {
						if ($routeParams.id == null) {
							$scope.myTaskList();
						} else if ($routeParams.id == 1) {
							$scope.myTaskList();
						} else if ($routeParams.id == 2) {
							$scope.myDoingTaskList();
						} else if ($routeParams.id == 3) {
							$scope.myDoneTaskList();
						} else if ($routeParams.id == 4) {
							$scope.myAppointTaskList();
						} else if ($routeParams.id == 5) {
							$scope.myHangupTaskList();
						}
						;
					}

					$scope.load();

					// 处理任务
					$scope.chuli = function(ti) {
						WorkService.init({"ti":ti}).success(function(responseData){
							if (responseData.status == "1") {
								$scope.activityController = responseData.activityController;
								window.localStorage.setItem('activityController',JSON.stringify($scope.activityController));
								window.location.href = "process#/view/doTask/" + ti+"/true";
							} else {
								alert(responseData.errorMsg);
							}
						}).error(function(data, status, headers, config) {
							alert("连接错误，错误码：" + status);
						});
					};
                    //

					// 查看任务
					$scope.chakan = function(id) {
						window.location.href = "process#/view/doTask/" + id+"/false";
					}

					// 解锁任务
					$scope.jiesuo = function(id) {
						TaskService.clear({
							taskInstance : id
						}).success(function(responseData) {
							if (responseData.status == "1") {
								alert("解锁成功！");
								$scope.load();
							} else {
								alert("解锁失败！");
								$scope.myDoingTaskList();
							}
						}).error(function(data, status, headers, config) {
							alert("连接错误，错误码：" + status);

						});
					}

					// 受理任务
					$scope.shouli = function(id) {
						TaskService.shouli({
							id : id
						}).success(function(responseData) {
							if (responseData.status == "1") {
								alert("受理成功！");
								$scope.myDoingTaskList();
							} else {
								alert("受理失败！");
								$scope.load();
							}
						}).error(function(data, status, headers, config) {
							alert("连接错误，错误码：" + status);

							$scope.load();
						});
					}

					// 恢复任务
					$scope.recovery = function(id) {
						TaskService.recovery({
							id : id
						}).success(function(responseData) {
							if (responseData.status == "1") {
								alert("恢复成功！");
								window.location.href = "/view/myTaskList/:2";
							} else {
								alert("恢复失败！");
								$scope.load();
							}
						}).error(function(data, status, headers, config) {
							alert("连接错误，错误码：" + status);

							$scope.load();
						});
					}
				});
