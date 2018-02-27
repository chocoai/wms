/*******************************************************************************
 * 系统管理---主模型定义
 ******************************************************************************/
var systemManagementModule = angular.module('myApp.systemManagement', ['ngRoute', 'treeControl', 'myApp.myDirective', 'myApp.SystemManagementServices']);

/*******************************************************************************
 * 系统管理---路由器
 ******************************************************************************/
systemManagementModule.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', { 
            templateUrl: 'erp/platform/systemManager/views/permission.html',
        })
        .when('/view/user', {
            templateUrl: 'erp/platform/systemManager/views/user.html',
        })
        .when('/view/role', {
            templateUrl: 'erp/platform/systemManager/views/role.html',
        })
        .when('/view/permission', {
            templateUrl: 'erp/platform/systemManager/views/permission.html',
        })
        .when('/view/department', {
            templateUrl: 'erp/platform/systemManager/views/department.html',
        })
        .when('/view/module', {
            templateUrl: 'erp/platform/systemManager/views/module.html',
        })
        .when('/view/station', {
            templateUrl: 'erp/platform/systemManager/views/station.html',
        })
        .when('/view/operation', {
        	templateUrl: 'erp/platform/systemManager/views/operation.html',
        })
        .when('/view/systems', {
            templateUrl: 'erp/platform/systemManager/views/systems.html',
        })
        .when('/view/menu', {
            templateUrl: 'erp/platform/systemManager/views/menu.html',
        })
        .when('/view/org', {
        	templateUrl: 'erp/platform/systemManager/views/org.html',
        })
        .when('/view/select', {
            templateUrl: 'erp/platform/systemManager/views/select.html',
        })
        .when('/view/processTypeDefinition', {
            templateUrl: 'erp/platform/workflow/views/processTypeDefinition.html'
        })
        .when('/view/process', {
            templateUrl: 'erp/platform/workflow/views/process.html'
        })
        .when('/view/runLog', {
        	templateUrl: 'erp/platform/workflow/views/runLog.html'
        })
        .when('/view/resumeProcess', {
        	templateUrl: 'erp/platform/workflow/views/resume.html'
        })
        .when('/view/processMonitor', {
        	templateUrl: 'erp/platform/workflow/views/processMonitor.html'
        })
        .when('/view/processEdit/:flag/:id', {
            templateUrl: 'erp/platform/workflow/views/processEdit.html'
        })
        .otherwise({
            redirectTo: '/'
        });
}]);
/*******************************************************************************
 * 系统管理---用户控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.userCtrl', function ($scope, UserService) {
    $scope.users = [];
    $scope.selected = {};
    $scope.sexArr = [{'name': '男', 'value': 1}, {'name': '女', 'value': 2}];
    $scope.stateArr = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];

    $scope.currentSex = {};
    $scope.currentState = {};
    $scope.currentCheckedObj = {};

    // 选中行id集合
    $scope.selectedArr = [];
    // 新增对象模型
    $scope.createUser = {};
    // 查询条件模型
    $scope.searchObj = {};
    
    $scope.selected= {
            id:''
        };
    
    

    // 页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    // 角色树
    $scope.treeOptions = {
		nodeChildren: "childrens",
        dirSelectable: true,
    }
    //组织树
    $scope.treeOptions1 = {
	    nodeChildren: "children",
	    dirSelectable: true,
        // multiSelection: true,
        injectClasses: {

        }
    };
    //岗位树
    $scope.treeOptions2 = {
		nodeChildren: "childrens",
        multiSelection: true,
    }

    // 设置用户角色树属性
    $scope.relationTreeOptions = {
        nodeChildren: "childrens",
        multiSelection: true,
    }
    
    // 设置用户机构树属性
    $scope.orgTreeOptions = {
        nodeChildren: "childrens",
        multiSelection: true,
    }

    // 角色树选中节点模型
// $scope.selectedNode = {};
    
    // 角色树点击事件
    $scope.buttonClick = function (node,selected) {
        if(selected){
            $scope.selected = node;
        }else{
            $scope.selected = {};
        }
    	$scope.searchObj.roleId = node.id;
    	$scope.queryUser();
	}
    
    //查询用户信息
    $scope.queryUser = function(){
        NProgress.start();
    	var postData = {};
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.searchCondition = angular.toJson($scope.searchObj);
        UserService.init(postData).success(function (responseData) {
            $scope.users = responseData.userPage.list;
            $scope.pageInfo.totalCount = responseData.userPage.totalRow;
            $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
            $scope.selectedArr.clear();
            NProgress.done();
        }).error(function (data, status, headers, config) {
            $("#tipModalLab").text("连接错误，错误码：" + status);
            $('#tipModal').modal('show');
            NProgress.done();
        });
    }
    
    
	// 设置用户角色树点击事件
    $scope.relationTreeClick = function (node) {
    	
    }

    // 点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.totalCount = 0;
        $scope.pageInfo.pageCount = 0;
        $scope.pageInfo.pageIndex = 1;
        $scope.queryUser();
    };

    // 查询条件重置
    $scope.reset = function(){
    	var roleId = $scope.searchObj.roleId;
        $scope.searchObj = {};
        $scope.searchObj.roleId = roleId;
        $scope.pageInfo.pageIndex = 1;
        $scope.queryUser();
    }

    // 页面初始化加载数据
    $scope.load = function () {
        // 加载数据部分
        var postData = {};
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.searchCondition = angular.toJson($scope.searchObj);
        UserService.init(postData).success(function (responseData) {
            $scope.users = responseData.userPage.list;
            $scope.pageInfo.totalCount = responseData.userPage.totalRow;
            $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

            $scope.dataForTheTree = eval(responseData.roleTree);
            $scope.expandedNodes = [$scope.dataForTheTree[0]];
        }).error(function (data, status, headers, config) {
            $("#tipModalLab").text("连接错误，错误码：" + status);
            $('#tipModal').modal('show');
        });
    }

    $scope.load();

    /**
	 * 新增窗口
	 */
    $scope.add = function () {
    	$scope.createUser = {};
    	if ($scope.sexArr) {
            $scope.currentSex.value = $scope.sexArr[0];
        };

        if ($scope.stateArr) {
            $scope.currentState.value = $scope.stateArr[0];
        };
        $('#createModal').modal('show');
    }

    /**
	 * 编辑窗口
	 */
    $scope.showEditDialog = function () {
        if ($scope.selectedArr.length > 0) {
        	UserService.queryById({id: $scope.currentCheckedObj.id}).success(function (responseData) {
            	$scope.selected = responseData.user;
            	$scope.currentSex.value = responseData.user.gender;
            	$scope.currentState.value = responseData.user.state;
            	$('#userModal').modal('show');
            }).error(function (data, status, headers, config) {

            });
        } else {
        	// alert("请选择一行数据");
            $("#tipModalLab").text('请选择一行数据');
            $("#tipModal").modal('show');
        }
    }

    /**
	 * 删除
	 */
    $scope.delete = function () {
        if ($scope.selectedArr.length > 0) {
           if(confirm("确认要删除吗！")==true){
               UserService.delete({id: $scope.currentCheckedObj.id}).success(function (responseData) {
                   if (responseData.status == "success") {
                       $("#tipModalLab").text('删除成功');
                       $('#tipModal').modal('show');
                       setTimeout(function(){
                           $scope.load();
                       },500);
                   }
               }).error(function (data, status, headers, config) {
                   $("#tipModalLab").text("连接错误，错误码：" + status);
                   $('#tipModal').modal('show');
               });
           }
        } else {
            // alert("请选择一行数据");
            $("#tipModalLab").text('请选择一行数据');
            $("#tipModal").modal('show');
        }
    }

    $scope.selectedNodes = [];

    $scope.selectednodesId = "";

    /**
	 * 用户设置【数据授权】
	 */
    $scope.dataTreeOptions = {
    		nodeChildren: "childrens",
            multiSelection: true,
    }

    $scope.assignData = function(){
    	$scope.selectedNodes = [];
        if ($scope.selectedArr.length > 0) {
            UserService.queryDataUserRelationTree({userId: $scope.currentCheckedObj.id}).success(function (responseData) {
            	debugger;
                $scope.dataTree = eval(responseData.dataTree);
                if($scope.dataTree != null || $scope.orgTree != 'undefined'){
                	$scope.dataExpandedNodes = [$scope.dataTree[0]];
                }
                $scope.dataSelectedNodes = eval(responseData.selectedNodes);
                $('#dataModal').modal('show')
            }).error(function (data, status, headers, config) {
                $("#tipModalLab").text("连接错误，错误码：" + status);
                $('#tipModal').modal('show');
            });
        } else {
            $("#tipModalLab").text('请选择一行数据');
            $("#tipModal").modal('show');
        }
    }
    /**
     * 用户设置机构
     */
    $scope.assignOrg = function(){
    	$scope.selectedNodes = [];
    	if ($scope.selectedArr.length > 0) {
    		UserService.queryOrgUserRelationTree({userId: $scope.currentCheckedObj.id}).success(function (responseData) {
    			$scope.orgTree = eval(responseData.orgTree);
    			if($scope.orgTree != null || $scope.orgTree != 'undefined'){
    				$scope.orgExpandedNodes = [$scope.orgTree[0]];
    			}
    			$scope.orgSelectedNodes = eval(responseData.selectedNodes);
    			$('#orgModal').modal('show')
    		}).error(function (data, status, headers, config) {
    			$("#tipModalLab").text("连接错误，错误码：" + status);
    			$('#tipModal').modal('show');
    		});
    	} else {
    		$("#tipModalLab").text('请选择一行数据');
    		$("#tipModal").modal('show');
    	}
    }
    
    /**
     * 用户设置角色
     */
    $scope.assignRole = function(){
    	$scope.selectedNodes = [];
    	if ($scope.selectedArr.length > 0) {
    		UserService.queryRoleUserRelationTree({userId: $scope.currentCheckedObj.id}).success(function (responseData) {
    		    if(!responseData.roleTree){
    		        alert("角色信息为空，请联系管理员！");
    		        return;
                }
    			$scope.relationRoleTree = eval(responseData.roleTree);
    			$scope.expandedNodes = [$scope.relationRoleTree[0]];
    			
    			$scope.selectedNodes = eval(responseData.selectedNodes);
    			$('#roleModal').modal('show')
    		}).error(function (data, status, headers, config) {
    			$("#tipModalLab").text("连接错误，错误码：" + status);
    			$('#tipModal').modal('show');
    		});
    	} else {
    		$("#tipModalLab").text('请选择一行数据');
    		$("#tipModal").modal('show');
    	}
    }
    
    /**
	 * 用户设置组织
	 */
    $scope.assignGroup = function(){
    	$scope.node1 = {};
        if ($scope.selectedArr.length > 0) {
            UserService.queryGroupUserRelationTree({userId: $scope.currentCheckedObj.id}).success(function (responseData) {
                $scope.relationGroupTree = eval(responseData.groupTree);
                $scope.expandedNodes1 = [$scope.relationGroupTree[0]];
                $scope.node1 = eval('(' + responseData.selectedNode1 + ')');
                $('#groupModal').modal('show')
            }).error(function (data, status, headers, config) {
                $("#tipModalLab").text("连接错误，错误码：" + status);
                $('#tipModal').modal('show');
            });
        } else {
            $("#tipModalLab").text('请选择一行数据');
            $("#tipModal").modal('show');
        }
    }
    
    /**
	 * 用户设置岗位
	 */
    $scope.assignStation = function(){
    	$scope.selectedNodes2 = [];
        if ($scope.selectedArr.length > 0) {
            UserService.queryStationUserRelationTree({userId: $scope.currentCheckedObj.id}).success(function (responseData) {
                $scope.relationRoleTree2 = eval(responseData.stationTree);
                $scope.expandedNodes2 = [$scope.relationRoleTree2[0]];

                $scope.selectedNodes2 = eval(responseData.selectedNodes2);
                $('#stationModal').modal('show')
            }).error(function (data, status, headers, config) {
                $("#tipModalLab").text("连接错误，错误码：" + status);
                $('#tipModal').modal('show');
            });
        } else {
            $("#tipModalLab").text('请选择一行数据');
            $("#tipModal").modal('show');
        }
    }

    /**
	 * 保存用户与数据权限的关系
	 */
    $scope.saveDataUserRelation = function(){
    	$scope.selectednodesId = "";
    	for (var i = 0; i < $scope.dataSelectedNodes.length; i++) {
    		$scope.selectednodesId = $scope.selectednodesId + "," + $scope.dataSelectedNodes[i].id;
		}
	    var result = UserService.saveDataUserRelation({dataIds: $scope.selectednodesId,userId:$scope.currentCheckedObj.id}).success(
	            function (responseData) {
	            	$('#dataModal').modal('hide');
	                setTimeout(function(){
	                    $("#tipModalLab").text('保存成功');
	                    $('#tipModal').modal('show');
	                	$scope.load();
	                },500);
	            }
	        ).error(function (data, status, headers, config) {
	        	$("#tipModalLab").text("连接错误，错误码：" + status);
	            $('#tipModal').modal('show');
	            });
    };
    
    /**
     * 保存机构用户关系
     */
    $scope.saveOrgUserRelation = function(){
    	$scope.selectednodesId = "";
    	for (var i = 0; i < $scope.orgSelectedNodes.length; i++) {
    		$scope.selectednodesId = $scope.selectednodesId + "," + $scope.orgSelectedNodes[i].id;
    	}
    	var result = UserService.saveOrgUserRelation({orgIds: $scope.selectednodesId,userId:$scope.currentCheckedObj.id}).success(
    			function (responseData) {
    				$('#orgModal').modal('hide');
    				setTimeout(function(){
    					$("#tipModalLab").text('保存成功');
    					$('#tipModal').modal('show');
    					$scope.load();
    				},500);
    			}
    	).error(function (data, status, headers, config) {
    		$("#tipModalLab").text("连接错误，错误码：" + status);
    		$('#tipModal').modal('show');
    	});
    };
    
    /**
     * 保存角色用户关系
     */
    $scope.saveRoleUserRelation = function(){
    	$scope.selectednodesId = "";
    	for (var i = 0; i < $scope.selectedNodes.length; i++) {
    		$scope.selectednodesId = $scope.selectednodesId + "," + $scope.selectedNodes[i].id;
    	}
    	var result = UserService.saveRoleUserRelation({roleIds: $scope.selectednodesId,userId:$scope.currentCheckedObj.id}).success(
    			function (responseData) {
    				$('#roleModal').modal('hide');
    				setTimeout(function(){
    					$("#tipModalLab").text('保存成功');
    					$('#tipModal').modal('show');
    					$scope.load();
    				},500);
    			}
    	).error(function (data, status, headers, config) {
    		$("#tipModalLab").text("连接错误，错误码：" + status);
    		$('#tipModal').modal('show');
    	});
    };
    
    /**
	 * 保存组织用户关系
	 */
    $scope.saveGroupUserRelation = function(){
	    var result = UserService.saveGroupUserRelation({groupId: $scope.node1.id,userId:$scope.currentCheckedObj.id}).success(
	            function (responseData) {
	            	$('#groupModal').modal('hide');
	                setTimeout(function(){
	                    $("#tipModalLab").text('保存成功');
	                    $('#tipModal').modal('show');
	                	$scope.load();
	                },500);
	            }
	        ).error(function (data, status, headers, config) {
	        	$("#tipModalLab").text("连接错误，错误码：" + status);
	            $('#tipModal').modal('show');
	            });
    };
    
    
    /**
	 * 保存岗位用户关系
	 */
    $scope.saveStationUserRelation = function(){
    	$scope.selectednodesId2 = "";
    	for (var i = 0; i < $scope.selectedNodes2.length; i++) {
    		$scope.selectednodesId2 = $scope.selectednodesId2 + "," + $scope.selectedNodes2[i].id;
		}
	    var result = UserService.saveStationUserRelation({stationIds: $scope.selectednodesId2,userId:$scope.currentCheckedObj.id}).success(
	            function (responseData) {
	            	$('#stationModal').modal('hide');
	                setTimeout(function(){
	                    $("#tipModalLab").text('保存成功');
	                    $('#tipModal').modal('show');
	                	$scope.load();
	                },500);
	            }
	        ).error(function (data, status, headers, config) {
	        	$("#tipModalLab").text("连接错误，错误码：" + status);
	            $('#tipModal').modal('show');
	            });
    };
    
    /**
	 * 用户信息 保存
	 */
    $scope.save = function () {
		UserService.save({user: angular.toJson($scope.createUser)}).success(function
            (responseData) {
            if (responseData.status == "success") {
                $('#createModal').modal('hide');
                setTimeout(function(){
                    $("#tipModalLab").text('保存成功');
                    $('#tipModal').modal('show');
                	$scope.load();
                },500);
            }
        }).error(function (data, status, headers, config) {
            $("#tipModalLab").text("连接错误，错误码：" + status);
            $('#tipModal').modal('show');
        });
    }

    /**
	 * 用户信息 更新
	 */
    $scope.update = function () {
		UserService.update({user: angular.toJson($scope.selected)}).success(function
	            (responseData) {
	            if (responseData.status == "success") {
	                $('#userModal').modal('hide');
	                setTimeout(function(){
                        $("#tipModalLab").text('更新成功');
                        $('#tipModal').modal('show');
	                	$scope.load();
	                },500);
	            }
	        }).error(function (data, status, headers, config) {
                $("#tipModalLab").text("连接错误，错误码：" + status);
                $('#tipModal').modal('show');
	        });
    }

    /**
	 * 行点击事件
	 * 
	 * @param row
	 */
    $scope.clickRow = function (row, $event) {
        // var checkbox = $event.target;
        // var action = (checkbox.checked);
        // if(action){
        // checkbox.checked = false;
        // }else{
        // checkbox.checked = true;
        // }
        // $scope.selectedArr.push(row.id);
        // $scope.currentCheckedObj.id = row.id;
    }

    if ($scope.sexArr) {
        $scope.currentSex.value = $scope.sexArr[0];
    };

    if ($scope.stateArr) {
        $scope.currentState.value = $scope.stateArr[0];
    };

    $scope.changeSex = function (curSex, type) {
    	if(type == 'save'){
    		$scope.createUser.gender = curSex;
    	}else if(type == 'update'){
    		$scope.selected.gender = curSex;
    	}
    };

    $scope.changeState = function (currentState, type) {
    	if(type == 'save'){
    		$scope.createUser.state = currentState;
    	}if(type == 'update'){
    		$scope.selected.state = currentState;
    	}
    };

    // 全选事件
    $scope.checkAll = function ($event) {
        var checkbox = $event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        for (var i = 0; i < $scope.users.length; i++) {
            var entity = $scope.users[i];
            updateSelected(action, entity.id);
        }
    };

    var updateSelected = function (action, id) {
        if (action == 'add' & $scope.selectedArr.indexOf(id) == -1) {
            $scope.selectedArr.push(id);
        }
        if (action == 'remove' && $scope.selectedArr.indexOf(id) != -1) {
            $scope.selectedArr.splice($scope.selectedArr.indexOf(id), 1);
        }
    };

    // 每行选中事件
    $scope.updateSelection = function ($event, entity) {
        var checkbox = $event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        updateSelected(action, entity.id);
        $scope.currentCheckedObj.id = entity.id;
    };

    $scope.getSelectedClass = function (entity) {
        return $scope.isSelected(entity.id) ? 'selected' : '';
    };


    $scope.isSelected = function (entity) {
        return $scope.selectedArr.indexOf(entity.id) >= 0;
    };


    // something extra I couldn't resist adding :)
    $scope.isSelectedAll = function () {
        return $scope.selectedArr.length === $scope.users.length && $scope.users.length>0;
    };

});

/*******************************************************************************
 * 系统管理---角色控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.roleCtrl', function ($scope,RoleService) {

	$scope.sexArr = [{'name': '是', 'value': 1}, {'name': '否', 'value': 0}];
	$scope.stateArr = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
	
    $scope.selectedNodes = [];
    $scope.selected={};
    $scope.selectednodesId = "";
    $scope.treeOptions = {
    	    nodeChildren: "childrens",
    	    dirSelectable: true,
    	    injectClasses: {

    	    }
    }

    $scope.treeOptions1 = {
    	    nodeChildren: "childrens",
    	    multiSelection: true,
    }


    $scope.rows = {};// 保存查询的数据行

	$scope.load = function () {
        // 加载数据部分
        var result = RoleService.init().success(
            function (responseData) {
            	$scope.allList = responseData.allList;
                $scope.dataForTheTree = eval(responseData.roleTree);
                $scope.expandedNodes = [$scope.dataForTheTree[0]
                	];
                RoleService.queryRole({roleId: $scope.dataForTheTree[0].id}).success(
                    function (responseData) {
                        $scope.dataForTheTree1 = eval(responseData.PermTree);

                        $scope.expandedNodes1 = [$scope.dataForTheTree1[0]
                        ];

                    }
                ).error(function (data, status, headers, config) {
                    alert("连接错误，错误码：" + status);
                });
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

    }

	$scope.load();

	$scope.buttonClick = function (node,selected) {
		$scope.rows = {};
		$scope.selectedNodes = [];
		if(selected){
            $scope.selected=node;
        }else{
            $scope.selected={};
            return;
        }
	    var result = RoleService.queryRole({roleId: node.id}).success(
	            function (responseData) {
	                // $scope.rList = responseData.rList;
                    $scope.rows = responseData.role;
	                $scope.dataForTheTree1 = eval(responseData.PermTree);

	                $scope.expandedNodes1 = [$scope.dataForTheTree1[0]
	                	];
	                $scope.selectedNodes = eval(responseData.selectedNodes);
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}

	$scope.save = function () {
	    var result = RoleService.save({role: angular.toJson($scope.rows)}).success(
	            function (responseData) {
	            	alert("保存成功！");
                    $("#myModal").modal('hide');
	            	$scope.load();
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}
    $scope.edit=function(){
        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
            if($scope.selected.id=='' || $scope.selected.id==null){
                $("#tipText").text("请选择角色节点！");
                $("#tipModal").modal();
                return;
            }
        }
        var result = RoleService.edit({roleId: $scope.selected.id}).success(
            function (responseData) {
                $scope.rows = responseData.role;
                $scope.rList = responseData.rList;
                $("#myModal").modal();
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    }
	$scope.del = function () {
        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
            if($scope.selected.id=='' || $scope.selected.id==null){
                $("#tipText").text("请选择角色节点！");
                $("#tipModal").modal();
                return;
            }
        }

        var msg = "您真的确定要删除吗？";
        if(confirm(msg)==true){
             RoleService.del({roleId: $scope.selected.id}).success(
                function (responseData) {
                    if(responseData.success!='0'){
                        alert("删除成功！");
                        $scope.rows = {};
                        $scope.load();
                    }else{
                        alert("该角色存在子角色不可删除！");
                    }
                }
            ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
        };

	}

	$scope.add = function () {
		$scope.rows = {};
		$scope.node1 = {};
		$("#myModal").modal();
	}

	$scope.addSave = function () {
		if ($scope.rows.roleName==null) {
			alert("请输入完整信息！");
			return;
		}
	    var result = RoleService.add({role: angular.toJson($scope.rows)}).success(
	            function (responseData) {
	            	$scope.rows = {};
	            	alert("新增成功！");
                    $("#myModal").modal('hide');
	            	$scope.load();
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}

	$scope.RPRsave = function () {
		$scope.selectednodesId = "";
    	for (var i = 0; i < $scope.selectedNodes.length; i++) {
    		$scope.selectednodesId = $scope.selectednodesId + "," + $scope.selectedNodes[i].id;
		}
	    var result = RoleService.RPRsave({permIds: $scope.selectednodesId,roleId:$scope.rows.id}).success(
	            function (responseData) {
	            	alert("保存成功！");
	            	$scope.load();
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}


});

/*******************************************************************************
 * 组织管理控制器
 ******************************************************************************/
systemManagementModule.controller('deptController', function ($scope, DeptService,UserService) {

    /* 树对象数据 */
    $scope.treedata = {};

    /* 点击节点选择数据 */
    $scope.selected={
        id:''
    };

    $scope.opts = {
        nodeChildren: "childrens",
        dirSelectable: true,
    };

    /* 组织-用户分页信息 */
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    $scope.pageInfo2 = {
        pageSize: 5,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    $scope.save = function(){
        var cc=[];
        for(var i=0;i<$scope.dUsers.length;i++){
            if($scope.dUsers[i].isChecked && $scope.dUsers[i].isChecked == true){
                cc.push($scope.dUsers[i].id);
            }
        }
        console.log(cc);

    }


    /* 初始化组织树 */
    $scope.init = function () {
        var postData = {};
        postData.pageSize2 = $scope.pageInfo2.pageSize;
        postData.pageIndex2 = $scope.pageInfo2.pageIndex;
        postData.groupUserData = $scope.selected.id;
        DeptService.init(postData).success(function (responseData) {
            $scope.treedata = eval(responseData.groupTreeData);

            $scope.mrows=responseData.queryResult.list;
            $scope.pageInfo2.totalCount = responseData.queryResult.totalRow;
            $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);
            $scope.$broadcast('$_MODEL_CHANGE_$', $scope.treedata);
        }).error(function (data, status, headers) {
            $("#tipText").text('初始化失败，请检查数据库连接！');
            $("#tipModal").modal();
        });
    };
    /* 初始化加载 */
    $scope.init();
    $scope.$on('departTree',function(e,data){
        $scope.selected = data[0];
        $scope.load();
    });


    /* 点击组织节点分页显示组织下用户信息 */
    $scope.load = function () {
        var postData = {};
        postData.pageSize2 = $scope.pageInfo2.pageSize;
        postData.pageIndex2 = $scope.pageInfo2.pageIndex;
        if($scope.selected.id == null || $scope.selected.id == ''){
            postData.pageSize = $scope.pageInfo.pageSize;
            postData.pageIndex = $scope.pageInfo.pageIndex;
            UserService.init(postData).success(function (responseData) {

                $scope.groupUsers = responseData.userPage.list;
                $scope.pageInfo.totalCount = responseData.userPage.totalRow;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

            }).error(function (data, status) {
                $("#tipText").text('连接失败！状态码：'+status+',请检查数据库连接');
                $("#tipModal").modal();
                alert("");
                $scope.init();
            });
        }else{
            postData.pageSize = $scope.pageInfo.pageSize;
            postData.pageIndex = $scope.pageInfo.pageIndex;
            postData.groupUserData = $scope.selected.id;
            DeptService.queryGroupUser(postData).success(function (responseData) {
                $scope.groupUsers = responseData.queryUserResult.list;
                $scope.pageInfo.totalCount = responseData.num.length;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

                $scope.mrows=responseData.queryResult.list;
                $scope.pageInfo2.totalCount = responseData.queryResult.totalRow;
                $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);

            }).error(function (data, status) {
                $("#tipText").text('连接失败！状态码：'+status+',请检查数据库连接');
                $("#tipModal").modal();
                $scope.init();
            });
        }
    };
    $scope.load();

    // /* 添加组织 */
    $scope.addGroup = {};
    $scope.addGroups = function () {
        if($scope.selected.id=='' || $scope.selected.id==null){
            $("#tipText").text('请点击组织节点进行添加');
            $("#tipModal").modal();
            return;
        };
        $scope.addGroup = {
            parentId : $scope.selected.id
        };
        $("#addGroup").modal();
    };
    $scope.add = function () {
        if(isNaN($scope.addGroup.sortNo)){
            $("#tipText").text('序号为数字！请重新输入');
            $("#tipModal").modal();
            return;
        }
        DeptService.save({group: angular.toJson($scope.addGroup)}).success(function (responseData) {
            if (responseData.status == "success") {
                $scope.init();
                $scope.load();
                $('#groupClose').click();
            }
            if (responseData.status == 'fail') {
                $("#tipText").text('添加失败，请检查您的输入是否有误！');
                $("#tipModal").modal();
            }
        }).error(function (data, status) {
            alert("连接失败！状态码："+status+",请检查数据库连接！");
        });
    };

    // /* 更新组织 */
    // $scope.groupData = {};
    //
    $scope.editSave = function (id) {
        DeptService.queryGroup({groupData: id}).success(function (responseData) {
                $scope.addGroup=responseData.deptEdit;
                $("#addGroup").modal();

        }).error(function (data, status) {
            // alert("连接失败！状态码："+status+",请检查数据库连接");
        });

    };

    /* 删除组织 */
    $scope.delData = {};
    $scope.deleteGroup = function () {
        if($scope.selected.id=='' || $scope.selected.id==null){
            $("#tipText").text('请选择组织进行删除！');
            $("#tipModal").modal();
            return;
        };
        if($scope.selected.parentId == 'null'){
            $("#tipText").text('该组织不能删除，请选择其他组织！');
            $("#tipModal").modal();
            return;
        };
    	var msg = "您真的确定要删除吗？";
        if(confirm(msg)==true){
            DeptService.deleteGroup({delData: $scope.selected.id}).success(function () {
                $scope.selected = {};
                $scope.groupList = {};
                $scope.init();
                $scope.load();
            }).error(function (data, status, headers, config) {
            	alert("删除失败！请重试");
            });
        };
        
    };

});


/*******************************************************************************
 * 模块控制器
 ******************************************************************************/
systemManagementModule.controller("myApp.systemManagement.module",function($scope,$http,ModuleService){
	
	$scope.stateList = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
	$scope.selected={
        childrens:[]
    };


    $scope.pageInfo = {
        pageSize: 5,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        pageStart: 1,
        pageEnd: 10
    };
	$scope.treeOptions = {
	    nodeChildren: "childrens",
	    dirSelectable: true,
	    injectClasses: {
	        
	    },
		isSelectable: function(node) {
	        return node.type != 'root';
	    }
	
    }
	
	$scope.load = function () {
	    var postData={};
        postData.pageIndex=$scope.pageInfo.pageIndex;
        postData.pageSize=$scope.pageInfo.pageSize;
        postData.moduleId=$scope.selected.id;
        postData.child=$scope.selected.childrens.length;
		var result = ModuleService.init(postData).success(
	            function (responseData) {
	            	$scope.dataForTheTree = eval(responseData.moduleTree);
	            	$scope.systemList = responseData.systemList;

                    $scope.parentList = responseData.parentList;
                    $scope.mrows=responseData.modulePage.list;
                    $scope.pageInfo.totalCount=responseData.modulePage.totalRow;
                    $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

                }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}
	
	$scope.load();

	$scope.buttonClick = function (node) {
	    $scope.selected=node;
        var postData={};
        postData.pageIndex=$scope.pageInfo.pageIndex;
        postData.pageSize=$scope.pageInfo.pageSize;
        postData.moduleId=node.id;
        postData.child=node.childrens.length;
		var result = ModuleService.queryModule(postData).success(
	            function (responseData) {
                    $scope.mrows=responseData.modulePage.list;
                    $scope.pageInfo.totalCount=responseData.modulePage.totalRow;
                    $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
	            	// $scope.module = responseData.module;
	            	// $scope.parentList = responseData.parentList;
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
	$scope.editModule=function(id){
        var result = ModuleService.editModule({moduleId:id}).success(
            function (responseData) {
                $scope.newModule = responseData.module;
                $scope.moduleList = responseData.parentList;
                $('#myModal').modal('show');
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    }
	
	$scope.$watch('newModule.systemId', function(newVal) {
        if (newVal){
        	var result = ModuleService.queryParentList({systemId:newVal,moduleId:$scope.newModule.id}).success(
    	            function (responseData) {
    	            	$scope.moduleList = responseData.parentList;
                        $scope.$apply();
    	            }
    	        ).error(function (data, status, headers, config) {
    	                alert("连接错误，错误码：" + status);
    	            });
        	
        } 
    });
	
	$scope.addModule = function () {
        $scope.newModule={};
		$('#myModal').modal('show');
	}
	
	$scope.delModule = function (id) {

        var msg = "您真的确定要删除吗？";
        if(confirm(msg)==true) {
            ModuleService.delModule({moduleId:id}).success(
                function (responseData) {
                    if(responseData.success=='0'){
                        alert("该对象不能删除");
                    }else{
                        alert("删除成功！");
                        $scope.load();
                    }
                }
            ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
        }

	}
	
	$scope.saveModule = function () {
		var result = ModuleService.saveModule({module:angular.toJson($scope.newModule)}).success(
	            function (responseData) {
	            	if(responseData.success=='1'){
                        alert("保存成功");
                        $('#myModal').modal('hide');
                        $scope.load();
                    }else{
	            	    alert("保存失败，请重新编辑");
                        $('#myModal').modal('hide');
                    }
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}

})



/*******************************************************************************
 * 系统管理---机构控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.orgController', function ($scope, $http,OrgService) {
	
	
	// 操作状态：1，新增；2，编辑
    $scope.operationStatus ={'status':1};
    $scope.stateArr = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
    $scope.operationList = [];
    $scope.node={};
    $scope.treeOptions = {
    	    nodeChildren: "childrens",
    	    dirSelectable: true,
    	    injectClasses: {
    	        
    	    }
    }
    
    $scope.rows = {};// 保存查询的数据行
    // 控制器加载动作
    $scope.load = function () {
        var result = OrgService.queryOrgTree({}).success(
            function (responseData) {
                $scope.dataForTheTree = eval(responseData.orgList);
                $scope.expandedNodes = [
                                        $scope.dataForTheTree[0]
                                        // $scope.dataForTheTree[0]
                ];
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        
    }
    
    $scope.queryOrgList = function(){
    	OrgService.queryOrgList({}).success(
            function (responseData) {
            	$scope.orgList = responseData.orgList;
            	if($scope.orgList.length>0){
            		$scope.org={
            			parentId:$scope.orgList[0].id
            		};
            	}
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
        });
    }
    
    // 加载动作
    $scope.load();
    $scope.queryOrgList();
    /**
	 * 点击【机构树】节点
	 */
    $scope.buttonClick = function(node, selected) {
    	if(selected){
    		$scope.node = node;
    		var postData = {};
            postData.orgId = node.id;
    		OrgService.findOrgById(postData).success(
    	            function (responseData) {
    	            	$scope.org = responseData.org;
    	            	if($scope.org.parentId==null){
    	            		$scope.isHide=true;
    	            	}else{
    	            		$scope.isHide=false;
    	            	}
    	            	$scope.orgList = responseData.orgList;
    	            	$("select[name='parentId']").attr('disabled','disabled')
    	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	        });
    	}else{
    		$scope.org = {};
    	}

    }
    
    /*
     * 点击【新增】按钮
     */
    $scope.addOrg = function(){
	    if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
	    	if($scope.node.id=='' || $scope.node.id==null){
	        	alert("请选择机构节点！");
	        	return;
	        }
	    }
		$scope.org={};
		$scope.org.parentId = $scope.node.id;
		
		
    }
    
    
    
    
    
    /*
	 * 新增或编辑【机构】的保存操作
	 */
    $scope.addOrUpdateOrg=function(){
    	OrgService.addOrUpdateOrg({org:angular.toJson($scope.org)}).success(
            function (responseData) {
                if(responseData.status==1)
                {
                    alert("保存成功！");
                    $scope.load();
                    $scope.org={};
                }else
                {
                    alert("操作失败，请重试！");
                }
            }
        ).error(function (data, status, headers, config) {

        });

    }
    
    $scope.checkOrgCode = function(){
    	if (/[\u4E00-\u9FA5]/i.test($scope.org.orgCode)) {
    	    alert('机构编码不能有中文');
    	    $scope.org.orgCode='';
    	    return false;
    	}
    	OrgService.findOrgByOrgCode({orgCode:angular.toJson($scope.org.orgCode)}).success(
                function (responseData) {
                    if(responseData.status==1)
                    {
                        alert("此机构编码已存在，请重新输入！");
                        $scope.org.orgCode='';
                    }
                }
            ).error(function (data, status, headers, config) {

            });
    }
    

    
  /**
	 * 进入【机构】新增页面
	 */
//    $scope.gcadd = function () {
//    	$scope.operationStatus.status=1;
//        $scope.currentRow = {};
//        $scope.currentRow.role = {};
//        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
//        	if($scope.node.id=='' || $scope.node.id==null){
//	        	alert("请选择权限节点！");
//	        	return;
//        	}
//        }
//        OrgService.queryOrgList({}).success(
//            function (responseData) {
//                $scope.roleList = responseData.roleList;
//                $scope.currentRow.role.parentId = $scope.node.id;
//                $("select[name='parentId']").attr('disabled','disabled');
//            }
//        ).error(function (data, status, headers, config) {
//                alert("连接错误，错误码：" + status);
//            });
//        $('#toAddPer').modal({closeViaDimmer:false});
//    }

    /*
	 * 进入【权限】编辑页面
	 */
//    $scope.editOrg = function () {
//    	$scope.operationStatus.status=2;
//        $scope.currentRow = {};
//        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
//        	if($scope.node.id=='' || $scope.node.id==null){
//	        	alert("请选择权限节点！");
//	        	return;
//        	}
//        }
//        OrgService.find({
//            id: $scope.node.id,
//        }).success(
//            function (responseData) {
//                $scope.currentRow.role = responseData.role;
//                $("select[name='parentId']").attr('disabled','disabled');
//                $scope.roleList = responseData.roleList;
//            }
//        ).error(function (data, status, headers, config) {
//                alert("连接错误，错误码：" + status);
//            });
//
//        $('#toAddPer').modal({closeViaDimmer:false});
//        $("#toAddPer  input,select,textarea").removeAttr('disabled');
//    }

    
    /*
	 * 弹出删除权限窗口
	 */
//    $scope.toDelOrg=function(){
//    	 $('#toDel').modal({closeViaDimmer:false});
//    }
    
    
    /*
     * 删除权限
//     */
//    $scope.delOrg=function(){
//    	OrgService.delOrg({id:$scope.node.id}).success(
//                    function (responseData) {
//                        if(responseData.status==1)
//                        {
//                            alert("删除成功！");
//                            $scope.mload();
//                        }else
//                        {
//                            alert("操作失败，请重试！");
//                        }
//                    }
//                ).error(function (data, status, headers, config) {
//
//                });
//    }
    
    

    

    /*
	 * 新增【权限】保存操作
	 */ 
//    $scope.saveOrg=function(){
//    	PermissionService.post({permission:angular.toJson($scope.currentRow.role)}).success(
//            function (responseData) {
//                if(responseData.status==1)
//                {
//                    // 成功
//                    alert("新增成功！");
//                    $scope.mload();
//                }else
//                {
//                    alert("操作失败，请重试！");
//                }
//            }
//        ).error(function (data, status, headers, config) {
//
//        });
//
//    }
    

		// 功能树
//		$scope.mnode2={};
//	   // 存储选中数据
//	    $scope.mpostData2 = {};
//	    $scope.selectedNodes2 = [];
//	    $scope.opts = {
//	    	    nodeChildren: "childrens",
//	    	    multiSelection: true,
//	    	    injectClasses: {
//	    	        
//	    	    }
//	    ,
//	    	    isSelectable: function(node) {
//		             return node.id.indexOf("erp_") != 0;
//		         }
//	    }
	    
	    // 控制器加载动作
//	    $scope.load222 = function () {
//	    	var resIds = [];
//			$.each($scope.selectedNodes2,function(k,v){
//				resIds.push(v.id);
//			});
//	    	
//	    	var mpostData2 = {};
//	    	mpostData2.resIds = angular.toJson(resIds);
//	    	
//	        var result = PermissionService.querySystemMenuTree(mpostData2).success(
//	            function (responseData) {
//	                $scope.mdataForTheTree2 = eval(responseData.operationList);
//	                $scope.mexpandedNodes2 = [
//                            $scope.mdataForTheTree2[0],
//                            $scope.mdataForTheTree2[0]
//	                ];
//	            }
//	        ).error(function (data, status, headers, config) {
//	                alert("连接错误，错误码：" + status);
//	            });
//	    }
//
//	    $scope.findNode = function (array,node) {
//	    	for (var i = 0; i < array.length; i++) {
//				if (array[i].id==node.id) {
//					return array[i];
//				}
//			}
//
//        }

	    // 单击【资源】树节点
//	    $scope.mbuttonClick2 = function(node,selected) {
//	        if(selected){
//	            if(node.childrens.length>0){
//                    $.each(node.childrens,function(k,v){
//                        $scope.selectedNodes2.push(v);
//                        $.each(v.childrens,function(k,v){
//                            $scope.selectedNodes2.push(v);
//                        });
//                    });
//                }
//            }else {
//                if(node.childrens.length>0){
//                    $.each(node.childrens,function(k,v){
//                        $scope.selectedNodes2.remove($scope.findNode($scope.selectedNodes2,v));
//                        $.each(v.childrens,function(k,v){
//                            $scope.selectedNodes2.remove($scope.findNode($scope.selectedNodes2,v));
//                        });
//                    });
//                }
//            }
//
//
//	    }
	    
		// 加载动作
//		$scope.load222();
		
		// 保存【权限功能关系】
//		$scope.savePermResRelation2 = function(){
//        	if($scope.node.id=='' || $scope.node.id==null){
//	        	alert("请选择权限节点！");
//	        	return;
//        	}
//			var resIds = [];
//			$.each($scope.selectedNodes2,function(k,v){
//				resIds.push(v.id);
//			});
//			PermissionService.savePermResRelation({permId:$scope.node.id,resIds:angular.toJson(resIds)}).success(
//	            function (responseData) {
//	            	if(responseData.status==1)
//	                {
//	                    alert("保存成功！");
//	                }else
//	                {
//	                    alert("操作失败，请重试！");
//	                }
//	            }
//	        ).error(function (data, status, headers, config) {
//	                alert("连接错误，错误码：" + status);
//	        });
//			
//		};
    
})


/*******************************************************************************
 * 系统管理---权限控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.permissionController', function ($scope, $http,PermissionService) {
	// 操作状态：1，新增；2，编辑
    $scope.operationStatus ={'status':1};
    $scope.stateArr = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
    $scope.operationList = [];
    $scope.node={};
    $scope.treeOptions = {
    	    nodeChildren: "childrens",
    	    dirSelectable: true,
    	    injectClasses: {
    	        
    	    }
    }
    
    
    // 分页信息
    $scope.pageInfo = {
        pageSize: 5,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };
    
    $scope.rows = {};// 保存查询的数据行
    // 控制器加载动作
    $scope.mload = function () {
        var result = PermissionService.queryPermissionTree({}).success(
            function (responseData) {
            	// $scope.operationList = responseData.operationList;
                // $scope.pageInfo.totalCount = 10;
                // $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
                // $scope.rows = responseData.operationList;
                // $scope.pageInfo.pageIndex=1;
                $scope.dataForTheTree = eval(responseData.rootList);
                $scope.expandedNodes = [
                                        $scope.dataForTheTree[0]
                                        // $scope.dataForTheTree[0]
                ];
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        
    }

    /**
	 * 点击【权限树】节点
	 */
    $scope.buttonClick = function(node, selected) {
    	if(selected){
    		$scope.node = node;
    		var postData = {};
            postData.permId = node.id;
    		PermissionService.findPermission(postData).success(
    	            function (responseData) {
    	            	$scope.mdataForTheTree2 = {};
    	            	$scope.mexpandedNodes2 = [];
    	            	$scope.mdataForTheTree2 = eval(responseData.rootList);
    	                $scope.mexpandedNodes2 = [
    	                        $scope.mdataForTheTree2[0],
    	                        $scope.mdataForTheTree2[0]
    	                ];
    	            	$scope.selectedNodes2 = eval(responseData.selectedNodes);
    	            }
    	        ).error(function (data, status, headers, config) {
    	                alert("连接错误，错误码：" + status);
    	        });
    	}else{
    		$scope.node = {};
    		$scope.selectedNodes2 = [];
    	}

    }
    
    // 加载动作
    $scope.mload();

    
  /**
	 * 进入【权限】新增页面
	 */
    $scope.gcadd = function () {
    	$scope.operationStatus.status=1;
        $scope.currentRow = {};
        $scope.currentRow.role = {};
        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
        	if($scope.node.id=='' || $scope.node.id==null){
	        	alert("请选择权限节点！");
	        	return;
        	}
        }
        PermissionService.queryPermissionList({}).success(
            function (responseData) {
                $scope.roleList = responseData.roleList;
                $scope.currentRow.role.parentId = $scope.node.id;
                $("select[name='parentId']").attr('disabled','disabled');
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
        $('#toAddPer').modal({closeViaDimmer:false});
    }

    /*
	 * 进入【权限】编辑页面
	 */
    $scope.editPermission = function () {
    	$scope.operationStatus.status=2;
        $scope.currentRow = {};
        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
        	if($scope.node.id=='' || $scope.node.id==null){
	        	alert("请选择权限节点！");
	        	return;
        	}
        }
        PermissionService.find({
            id: $scope.node.id,
        }).success(
            function (responseData) {
                $scope.currentRow.role = responseData.role;
                $("select[name='parentId']").attr('disabled','disabled');
                $scope.roleList = responseData.roleList;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        $('#toAddPer').modal({closeViaDimmer:false});
        $("#toAddPer  input,select,textarea").removeAttr('disabled');
    }

    
    /*
	 * 弹出删除权限窗口
	 */
    $scope.toDelPermission=function(){
    	 $('#toDel').modal({closeViaDimmer:false});
    }
    
    
    /*
     * 删除权限
     */
    $scope.delPermission=function(){
        	PermissionService.delPermission({id:$scope.node.id}).success(
                    function (responseData) {
                        if(responseData.status==1)
                        {
                            alert("删除成功！");
                            $scope.mload();
                        }else
                        {
                            alert("操作失败，请重试！");
                        }
                    }
                ).error(function (data, status, headers, config) {

                });
    }
    
    

    /*
	 * 编辑【权限】的保存操作
	 */
    $scope.updatePermission=function(){
    	PermissionService.update({permission:angular.toJson($scope.currentRow.role)}).success(
            function (responseData) {
                if(responseData.status==1)
                {
                    alert("保存成功！");
                    $scope.mload();
                }else
                {
                    alert("操作失败，请重试！");
                }
            }
        ).error(function (data, status, headers, config) {

        });

    }

    /*
	 * 新增【权限】保存操作
	 */ 
    $scope.savePermission=function(){
    	PermissionService.post({permission:angular.toJson($scope.currentRow.role)}).success(
            function (responseData) {
                if(responseData.status==1)
                {
                    // 成功
                    alert("新增成功！");
                    $scope.mload();
                }else
                {
                    alert("操作失败，请重试！");
                }
            }
        ).error(function (data, status, headers, config) {

        });

    }
    

		// 功能树
		$scope.mnode2={};
	   // 存储选中数据
	    $scope.mpostData2 = {};
	    $scope.selectedNodes2 = [];
	    $scope.opts = {
	    	    nodeChildren: "childrens",
	    	    multiSelection: true,
	    	    injectClasses: {
	    	        
	    	    }
	    ,
	    	    isSelectable: function(node) {
		             return node.id.indexOf("erp_") != 0;
		         }
	    }
	    
	    // 控制器加载动作
	    $scope.load222 = function () {
	    	var resIds = [];
			$.each($scope.selectedNodes2,function(k,v){
				resIds.push(v.id);
			});
	    	
	    	var mpostData2 = {};
	    	mpostData2.resIds = angular.toJson(resIds);
	    	
//	        var result = PermissionService.queryOpetationTree(mpostData2).success(
	        var result = PermissionService.querySystemMenuTree(mpostData2).success(
	            function (responseData) {
	                $scope.mdataForTheTree2 = eval(responseData.operationList);
	                $scope.mexpandedNodes2 = [
                            $scope.mdataForTheTree2[0],
                            $scope.mdataForTheTree2[0]
	                ];
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	    }

	    $scope.findNode = function (array,node) {
	    	for (var i = 0; i < array.length; i++) {
				if (array[i].id==node.id) {
					return array[i];
				}
			}

        }

	    // 单击【资源】树节点
	    $scope.mbuttonClick2 = function(node,selected) {
	        if(selected){
	            if(node.childrens.length>0){
                    $.each(node.childrens,function(k,v){
                        $scope.selectedNodes2.push(v);
                        $.each(v.childrens,function(k,v){
                            $scope.selectedNodes2.push(v);
                        });
                    });
                }
            }else {
                if(node.childrens.length>0){
                    $.each(node.childrens,function(k,v){
                        $scope.selectedNodes2.remove($scope.findNode($scope.selectedNodes2,v));
                        $.each(v.childrens,function(k,v){
                            $scope.selectedNodes2.remove($scope.findNode($scope.selectedNodes2,v));
                        });
                    });
                }
            }


	    }
	    
		// 加载动作
		$scope.load222();
		
		// 保存【权限功能关系】
		$scope.savePermResRelation2 = function(){
        	if($scope.node.id=='' || $scope.node.id==null){
	        	alert("请选择权限节点！");
	        	return;
        	}
//			if($scope.mdataForTheTree2!=null && $scope.mdataForTheTree2!=""){
//	        	if($scope.selectedNodes.length == 0){
//	        		alert("请选择功能节点！");
//	        		return;
//	        	}
//	        }
			var resIds = [];
			$.each($scope.selectedNodes2,function(k,v){
				resIds.push(v.id);
			});
			PermissionService.savePermResRelation({permId:$scope.node.id,resIds:angular.toJson(resIds)}).success(
	            function (responseData) {
	            	if(responseData.status==1)
	                {
	                    alert("保存成功！");
	                }else
	                {
	                    alert("操作失败，请重试！");
	                }
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	        });
			
		};
    
})

/*******************************************************************************
 * 系统管理---岗位控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.stationCtrl', function($scope, StationService, UserService){

    /* 岗位列表数据 */
    $scope.stationList = {};
    /* 点击节点选择数据 */
    $scope.selected= {
        id:''
    };
    /* 树对象数据 */
    $scope.treedata = {};

    $scope.opts = {
        nodeChildren: "childrens",
        dirSelectable: true,
    };
    $scope.pageInfo2 = {
        pageSize: 5,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };
    /* 岗位-用户分页信息 */
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    /* 初始化岗位树 */
    $scope.init = function () {
        var postData =  {};
        postData.pageSize2 = $scope.pageInfo2.pageSize;
        postData.pageIndex2 = $scope.pageInfo2.pageIndex;
        postData.id=$scope.selected.id;
        StationService.init(postData).success(function (responseData) {
            $scope.treedata = eval(responseData.stationTreeData);
            $scope.expandedNodes = [$scope.treedata[0]];

            $scope.mrows = responseData.queryResult.list;
            $scope.pageInfo2.totalCount = responseData.queryResult.totalRow;
            $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);
        }).error(function (data, status) {
            $("#tipText").text("初始化失败！状态码："+status+"，请不要频繁刷新页面，并检查数据库连接");
            $("#tipModal").modal();
        });
    };
    $scope.init();

    /* 检测数据是否修改 */
    var editStationName = '';
    var editSort = '';
    var editStatus = '';
    var editRemark = '';

    /* 树节点点击事件 */
    $scope.buttonClick = function (node,selected) {
        var i = 0;
        if(selected){
            $scope.selected = node;
            i = i + 1; //判断是否二次点击
        }else{
            $scope.selected = {};
        }
        // StationService.queryStation({stationData: node.id}).success(function (responseData) {
        //     if(i%2 == 1){
        //         $scope.stationList = responseData.queryResult;
        //
        //         editStationName = $scope.stationList.stationName;
        //         editSort = $scope.stationList.sort;
        //         editStatus = $scope.stationList.status;
        //         editRemark = $scope.stationList.remark;
        //     }else{
        //         $scope.stationList = {}
        //     }
        // }).error(function (data, status, headers, config) {
        //     $("#tipText").text("连接失败，错误码：" + status +",请检查数据库连接");
        //     $("#tipModal").modal();
        // });

        $scope.stationUsers = [];
        $scope.load();
    };

    /* 岗位-用户信息初始化 */
    $scope.load = function(){
        var postData =  {};
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.stationUserData = $scope.selected.id;
       if($scope.selected.id == null || $scope.selected.id ==''){
            UserService.init(postData).success(function (responseData) {
                $scope.stationUsers = responseData.userPage.list;
                $scope.pageInfo.totalCount = responseData.userPage.totalRow;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

            }).error(function (data, status, headers, config) {
                $("#tipText").text("初始化失败！状态码："+status+"，请不要频繁刷新页面");
                $("#tipModal").modal();
            });
       }else{
           postData.pageSize2 = $scope.pageInfo2.pageSize;
           postData.pageIndex2 = $scope.pageInfo2.pageIndex;
            StationService.queryStationUser(postData).success(function (responseData) {

                $scope.stationUsers = responseData.queryUserResult.list;
                $scope.pageInfo.totalCount = responseData.queryUserResult.totalRow;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);

                $scope.mrows = responseData.queryResult.list;
                $scope.pageInfo2.totalCount = responseData.queryResult.totalRow;
                $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);
            }).error(function (data, status, headers, config) {
                $("#tipText").text('该岗位下可能没有用户，请刷新页面后重试！');
                $("#tipModal").modal();
            })
       }
    };
    $scope.load();

    /* 添加岗位 */
    $scope.addStation = function () {
        $scope.station={};
        if($scope.selected.id=='' || $scope.selected.id==null){
            $("#tipText").text('请选择岗位进行添加！');
            $("#tipModal").modal();
            return;
        };
        $scope.station = {
            parentId : $scope.selected.id
        };
        $("#addStation").modal();
    };
    $scope.add = function () {
        if(isNaN($scope.station.sort)){
            $("#tipText").text('序号为数字，请重新输入！');
            $("#tipModal").modal();
            return;
        }
        StationService.save({stationData: angular.toJson($scope.station)}).success(function (responseData) {

            if(responseData.status=='success'){
                alert("保存成功");
                $scope.init();
                $('#stationClose').click();
            }

        }).error(function (data, status, headers, config) {
            $("#tipText").text('添加失败，状态码：'+status+'请检查您的输入是否有误, 或刷新页面后重试！');
            $("#tipModal").modal();
            $scope.init();
        });
    };

    /* 更新岗位 */
    $scope.editSave = function (id) {
        StationService.queryStation({stationData: id}).success(function (responseData) {
           $scope.station= responseData.station;
            $("#addStation").modal();
        }).error(function (data, status, headers, config) {
        });

    };

    /* 删除岗位 */
    $scope.delStation = function (id) {
        // if($scope.selected.id=='' || $scope.selected.id==null){
        //     $("#tipText").text('请点击岗位节点进行删除');
        //     $("#tipModal").modal();
        //     return;
        // };
        // if($scope.selected.parentId == 'null' || $scope.selected.parentId == ''){
        //     $("#tipText").text('此岗位不能删除， 请选择其他岗位！');
        //     $("#tipModal").modal();
        //     return;
        // };
        var msg = "您真的确定要删除吗？";
        if(confirm(msg)==true){
            StationService.deleteStation({delData: id}).success(function (responseData) {
                if(responseData.status=='fail'){
                    alert("该岗位下存在用户，不能删除！");
                }else{
                    $scope.selected = {};
                    $scope.stationList = {};
                    $scope.init();
                    $scope.load();
                }
            }).error(function (data, status, headers, config) {
                alert("删除失败！状态码："+status+",请刷新页面后重试");
            });
        };

    };

});

/*******************************************************************************
 * 系统管理---子系统控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.systems', function ($scope, $http,SystemsService) {
    
	//当前编辑的系统对象
	$scope.currentSys = {};
	
	//初始化
	$scope.load = function () {
		var result = SystemsService.init().success(
	            function (responseData) {
	            	$scope.sysList = responseData.sysList;
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
	
	$scope.load();
	
	//编辑系统信息
	$scope.editBtn = function (sys) {
		$scope.currentSys = {};
		$scope.currentSys = sys;
		$('#mySystems').modal('show');
	}
	
	//新增系统
	$scope.addSys = function () {
		$scope.currentSys = {};
		$('#mySystems').modal('show');
	}

	//删除系统
	$scope.delSys = function (sysId) {
		var result = SystemsService.delSystem({systemId:sysId}).success(
				function (responseData) {
					alert("删除成功！");
					$scope.load();
				}
		).error(function (){
			alert("连接错误，错误码："+status);
		})
	}
	
	//保存系统信息
	$scope.saveSystems = function () {
		var result = SystemsService.saveSystem({system:angular.toJson($scope.currentSys)}).success(
				function (responseData) {
					alert("保存成功！");
					$('#mySystems').modal('hide');
					$scope.load();
				}
		).error(function (){
			alert("连接错误，错误码："+status);
		})
	}
})

/*******************************************************************************
 * 系统管理---功能控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.operation', function ($scope, $http,OperationService) {
	$scope.authority = [{'name': '公共', 'value': 0}, {'name': '私有', 'value': 1}];
	$scope.stateList = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
	// 页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };
	
	//当前操作功能对象
	$scope.opt = {};
	
	$scope.selectedNodes = [];

	//查询对象
    $scope.searchObj={};
	
	//模块树
	$scope.treeOptions1 = {
		nodeChildren: "childrens",
	    dirSelectable: true,
	    injectClasses: {
	        
	    }	
	}

	//初始化
	$scope.load = function () {
		var postData = {};
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.searchObj=angular.toJson($scope.searchObj);
		var result = OperationService.init(postData).success(
	            function (responseData) {
	            	$scope.operationList = responseData.page.list;
	                $scope.pageInfo.totalCount = responseData.page.totalRow;
	                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
    // 点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    };

    // 查询条件重置
    $scope.reset = function(){
        $scope.searchObj = {};
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    }

	$scope.load();
	
	//编辑功能信息
	$scope.editOpt = function (id) {
        OperationService.findOperationById({id:id}).success(
            function (responseData) {
                if(responseData.operation!=null){
                    $scope.opt=responseData.operation;
                    $("#myModule2").modal();
                }
            }
        ).error(function(data, status, headers, config){
            alert("连接错误，错误码：" + status);
        })
		
	}
	
	//新增功能
	$scope.addOpt = function () {
		$scope.opt = {};
		$("#myModule2").modal();
	}
	
	//保存功能
	$scope.saveOpt = function () {
		var result = OperationService.saveOpt({opt:angular.toJson($scope.opt)}).success(
	            function (responseData) {
	            	alert("保存成功！");
	            	$scope.load();
                    $("#myModule2").modal('hide');
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
		
		
	}
	
	//绑定模块
	$scope.editModule = function (optId) {
		$scope.optId = "";
		$scope.optId = optId;
		$scope.node1 = {};
		var result = OperationService.queryModuleTree({optId:optId}).success(
	            function (responseData) {
	            	$scope.dataForTheTree1 = eval('('+responseData.moduleList+')');
	            	$scope.node1 = eval('('+responseData.selectedNode+')');
	            	$('#myModule').modal('show');
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}

	
	//保存模块绑定关系
	$scope.saveModule = function () {

		$scope.opt.moduleName = $scope.node1.name;
		$scope.opt.moduleId = $scope.node1.id;
		$('#myModule').modal('hide');
	}
	

})


/*******************************************************************************
 * 系统管理---下拉框控制器
 ******************************************************************************/
systemManagementModule.controller('myApp.systemManagement.select', function ($scope, $http,selectService) {
	// 页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };
	
	//当前操作功能对象
	$scope.select = {};
	

	//查询对象
    $scope.searchType=null;
	

	//初始化
	$scope.load = function () {
		var postData = {};
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.searchType=$scope.searchType;
		var result = selectService.init(postData).success(
	            function (responseData) {
	            	$scope.selectList = responseData.page.list;
	                $scope.pageInfo.totalCount = responseData.page.totalRow;
	                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
    // 点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    };

    // 查询条件重置
    $scope.reset = function(){
        $scope.searchType = null;
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    }

	$scope.load();
	
	//编辑功能信息
	$scope.edit = function (id) {
        selectService.findById({id:id}).success(
            function (responseData) {
                if(responseData.select!=null){
                    $scope.select=responseData.select;
                    $("#myModule2").modal();
                }
            }
        ).error(function(data, status, headers, config){
            alert("连接错误，错误码：" + status);
        })
		
	}
	
	//新增功能
	$scope.add = function () {
		$scope.select = {};
		$("#myModule2").modal();
	}
	
	//保存功能
	$scope.save = function () {
		var result = selectService.save({select:angular.toJson($scope.select)}).success(
	            function (responseData) {
	            	alert("保存成功！");
	            	$scope.load();
                    $("#myModule2").modal('hide');
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
		
		
	}

})

/*******************************************************************************
 * 菜单控制器
 ******************************************************************************/
systemManagementModule.controller("myApp.systemManagement.menu",function($scope,$http,MenuService){
	
	$scope.stateList = [{'name': '正常', 'value': 1}, {'name': '禁用', 'value': 0}];
	$scope.mobileStateList = [{'name': '显示', 'value': 1}, {'name': '隐藏', 'value': 0}];
    $scope.selected={
        childrens:[]
    };
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

	//菜单树
	$scope.treeOptions = {
	    nodeChildren: "childrens",
	    dirSelectable: true,
	    injectClasses: {
	        
	    },
		isSelectable: function(node) {
	        return node.type != 'root';
	    }
    }
	
	//模块功能树
	$scope.option3 = {
	    nodeChildren: "childrens",
	    dirSelectable: true,
	    injectClasses: {
	        
	    },
	    isSelectable: function(node) {
            return node.type != 0;
        }
    }
	
	$scope.load = function () {
        var postData = {};
        postData.pageIndex=$scope.pageInfo.pageIndex;
        postData.pageSize=$scope.pageInfo.pageSize;
        postData.menuId=$scope.selected.id;
        postData.child=$scope.selected.childrens.length;
		MenuService.init(postData).success(
	            function (responseData) {
	            	$scope.dataForTheTree = eval(responseData.menuTree);
	            	$scope.systemList = responseData.systemList;

                    $scope.mrows=responseData.menuPage.list;
                    $scope.pageInfo.totalCount=responseData.menuPage.totalRow;
                    $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });

	}
	
	$scope.load();

	$scope.buttonClick = function (node) {
        $scope.selected=node;
        var postData = {};
        postData.pageIndex=$scope.pageInfo.pageIndex;
        postData.pageSize=$scope.pageInfo.pageSize;
        postData.menuId=node.id;
        postData.child=node.childrens.length;
		var result = MenuService.queryMenu(postData).success(
	            function (responseData) {
                    $scope.mrows=responseData.menuPage.list;
                    $scope.pageInfo.totalCount=responseData.menuPage.totalRow;
                    $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
	            	// $scope.menu = responseData.menu;
	            	// $scope.parentList = responseData.parentList;
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}

    $scope.editMenu=function(id){
        var result = MenuService.editMenu({menuId:id}).success(
            function (responseData) {
                $scope.newMenu = responseData.menu;
                $scope.parentList = responseData.parentList;
                $('#myModal').modal('show');
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    }

	$scope.$watch('newMenu.systemId', function(newVal) {
        if (newVal){
        	var result = MenuService.queryParentList({systemId:newVal,menuId:$scope.newMenu.id}).success(
    	            function (responseData) {
    	            	$scope.parentList = responseData.parentList;
    	            	$scope.systemId = newVal;
    	            }
    	        ).error(function (data, status, headers, config) {
    	                alert("连接错误，错误码：" + status);
    	            });
        	
        } 
    });
	
	$scope.addMenu = function () {
	    $scope.newMenu={};
		$('#myModal').modal('show');
	}
	
	$scope.saveMenu = function () {
		var result = MenuService.saveMenu({menu:angular.toJson($scope.newMenu)}).success(
	            function (responseData) {
	            	alert("保存成功");
	            	$('#myModal').modal('hide');
	            	$scope.load();
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
	
	
	//查询模块功能树
	$scope.queryModuleAndOpt = function () {
		var result = MenuService.queryModuleAndOpt({systemId:$scope.systemId,menuId:$scope.newMenu.id}).success(
	            function (responseData) {
	            	$scope.DataForTheTree3 = eval(responseData.ModuleAndOptTree);
	            	$scope.node3 = eval('('+responseData.selectedNode+')');
	            	$('#moduleOptModal').modal('show');
	            }
	        ).error(function (data, status, headers, config) {
	                alert("连接错误，错误码：" + status);
	            });
	}
	
	$scope.saveOpt = function () {
		$scope.newMenu.operationId = $scope.node3.id;
		$scope.newMenu.operationName = $scope.node3.name;
		$('#moduleOptModal').modal('hide');
	}

})




/*******************************************
 * 工作流引擎控制器代码---开始
 *******************************************/

//流程设计控制器
systemManagementModule.controller('myApp.OperationManagement.process', function ($scope, $http,$location,ProcessManageService) {

    //当前视图状态
    $scope.viewStatus=2;//视图状态：1：草稿状态  2：发布状态

    //页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        pageStart: 1,
        pageEnd: 10
    };

    //搜索栏相关信息
    $scope.searchCondition = {
        status: $scope.viewStatus,
        name:null
    };

    //点击导航栏草稿
    $scope.draftStatus=function(){
        $scope.viewStatus=1;
        $scope.searchCondition.status=1;
        $scope.searchCondition.name=null;

        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };

    //点击导航栏已发布
    $scope.releaseStatus=function(){
        $scope.viewStatus=2;
        $scope.searchCondition.status=2;
        $scope.searchCondition.name=null;

        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };

    //点击搜索触发的动作
    $scope.searchProcess = function () {
        $scope.pageInfo.totalCount = 0;
        $scope.pageInfo.pageCount = 0;
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    };

    $scope.rows = {};//保存查询的数据行
    //控制器加载动作
    $scope.load = function () {
        //加载数据部分
        var postData = $scope.searchCondition;
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;

        var result = ProcessManageService.list(postData).success(
            function (responseData) {
                $scope.rows = responseData.rows;
                $scope.pageInfo.totalCount = responseData.total;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

    }

    //加载动作
    $scope.load();

    // $scope.view = function (row) {
    // 	$location.url("/view/processEdit/2/"+row.id);
    // }

    //编辑
    // $scope.edit = function (row) {
    // 	$location.url("/view/processEdit/1/"+row.id);
    // }

    //解锁
    $scope.unlock = function (row) {
        ProcessManageService.unlock({
            id: row.id,
        }).success(
            function (responseData) {
                alert("解锁成功");
                $scope.load();
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
    }


    //新增
    $scope.addProcess = function () {
    	$location.url("/view/processEdit/0/0");
    }
   
    //设计
    $scope.design = function (row) {
        //$scope.pdid=row.id;
        //$('#my-popup-designProcess').modal({closeViaDimmer:false});
    	//$location.url("/view/designProcess/"+row.id);
    	localStorage.setItem("pdid", row.id);
    	 $scope.currentRow = {};
         $scope.currentRow.processdefinition = row;
         ProcessManageService.find({
             id: $scope.currentRow.processdefinition.id,
         }).success(
             function (responseData) {
                 if(responseData.processdefinition.shape!='')
            	{
                	localStorage.setItem("shape", responseData.processdefinition.shape);
            	}else
            	{
            		localStorage.setItem("shape", '{"mian":[],"line":[]}');
            	}
                localStorage.setItem("pdName", responseData.processdefinition.name);
                localStorage.setItem("pdVersion", responseData.processdefinition.version);
                if(row.status==1)
                {
                	 window.location.href="/myCanvas.html";
                }else
                {
                	 window.location.href="/myCanvas2.html";
                }
             }
         ).error(function (data, status, headers, config) {
                 alert("连接错误，错误码：" + status);
             });
    }
    $scope.status='';
    $scope.currentCopyRow={};
    $scope.vertify=function(row,status){
        $scope.currentCopyRow.copyProcessName='';
        $scope.currentCopyRow.obj = row;
        $scope.status=status;
        $("#toDel").modal();
    }

    $scope.processPlan=function(){
        if($scope.status==2) {//发布
            //提交用户信息向服务器
            var id=$scope.currentCopyRow.obj.id;

            ProcessManageService.update({id:id,status:$scope.status}).success(
                function (responseData) {
                    $scope.currentCopyRow={};
                    if(responseData.status==1){
                        alert("发布成功");

                        $scope.load();
                    }else{
                        alert("提交数据失败，请与管理员联系");
                    }
                }
            ).error(function (data, status, headers, config) {
                $scope.currentCopyRow={};
                alert("连接错误，错误码：" + status);
            });
        }else if($scope.status==3){//停用
                                   //提交用户信息向服务器
            var id=$scope.currentCopyRow.obj.id;

            ProcessManageService.update({id:id,status:$scope.status}).success(
                function (responseData) {
                    $scope.currentCopyRow={};
                    if(responseData.status==1){
                        $scope.load();
                    }else{
                        alert("提交数据失败，请与管理员联系");
                    }
                }
            ).error(function (data, status, headers, config) {
                $scope.currentCopyRow={};
                alert("连接错误，错误码：" + status);
            });
        }else if($scope.status==0){
            ProcessManageService.del({
                id: $scope.currentCopyRow.obj.id,
            }).success(
                function (responseData) {
                    if(responseData.status==1){
                        $scope.currentCopyRow={};
                        $scope.load();
                    }else{
                        alert("删除失败");
                        $scope.currentCopyRow={};
                    }



                }
            ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
        }else{
            //提交用户信息向服务器
            var id=$scope.currentCopyRow.obj.id;
            if($scope.currentCopyRow.copyProcessName==""){
                alert("请输入流程名");
                return;
            }
            ProcessManageService.copy({id:id,newName:$scope.currentCopyRow.copyProcessName}).success(
                function (responseData) {
                    $scope.currentCopyRow={};
                    if(responseData.status==1){
                        alert("复制成功");
                        $scope.currentCopyRow.copyProcessName="";
                        $scope.load();
                    }else{
                        alert("提交数据失败，请与管理员联系");
                    }
                }
            ).error(function (data, status, headers, config) {
                $scope.currentCopyRow={};
                alert("连接错误，错误码：" + status);
            });
        }
    }
    

});

//流程设计定义控制器
systemManagementModule.controller('myApp.OperationManagement.processEdit', function ($scope, $http,$routeParams,ProcessEditManageService) {
	var flag = $routeParams.flag;
	var id = $routeParams.id;

	if(flag==0)
	{
		$scope.currentRow = {};
        $scope.currentRow.processform=[];
        $scope.flag = 1;
        ProcessEditManageService.find2({

        }).success(
            function (responseData) {
                $scope.currentRow.activityforms = responseData.activityforms;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
	}else if(flag==1)
	{
		$scope.currentRow = {};
		$scope.currentRow.processform=[];
		$scope.flag = 2;
		ProcessEditManageService.find({
            id: id,
        }).success(
            function (responseData) {
                $scope.currentRow.processdefinition = responseData.processdefinition;
                //$scope.currentRow.processcontroller = responseData.processcontroller;
                //$scope.currentRow.eventdefinition = responseData.eventdefinition;
                $scope.currentRow.processform = responseData.processform;
                $scope.currentRow.activityforms = responseData.activityforms;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

	}else
	{
		$scope.currentRow = {};
		$scope.currentRow.processform=[];
		$scope.flag = 0;
		ProcessEditManageService.find({
            id: id,
        }).success(
            function (responseData) {
                $scope.currentRow.processdefinition = responseData.processdefinition;
                //$scope.currentRow.processcontroller = responseData.processcontroller;
                //$scope.currentRow.eventdefinition = responseData.eventdefinition;
                $scope.currentRow.processform = responseData.processform;
                $scope.currentRow.activityforms = responseData.activityforms;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
	}

    if(!$scope.addProcessform){
        $scope.addProcessform={};
    }
    if(!$scope.editProcessform){
        $scope.editProcessform={};
    }

    $scope.addProcessformInfo=function(){
        var formName=$("select[name='formId']").find("option:selected").text();
        $scope.addProcessform.formName=formName;

        $scope.currentRow.processform.push( $scope.addProcessform);
        $scope.addProcessform={};
        $('#my-popup-addProcessform').modal("hide");
    }


    $scope.afedit=function(form,index){

        $scope.editProcessform= form;
        $('#afindexHidden').val(index);
        $('#my-popup-editProcessform').modal();
    }


    $scope.editProcessformInfo=function(){

   	 var index =$("#afindexHidden").val();
   	 $scope.currentRow.processform[index]= $scope.editProcessform;
   	 $scope.editProcessform={};
        $('#my-popup-editProcessform').modal("hide");
    }

    $scope.delProcessForm=function(index){

   	 var form=$scope.currentRow.processform[index] ;
   	 //新增表单
   	 if(!form.id){
   		 $scope.currentRow.processform.splice(index,1);
   		 return;
   	 }

   	ProcessEditManageService.delProcessForm({
	           id:form.id
	        }).success(
	            function (responseData) {
	              
	                if(responseData.status==1)
	                {
	                	 $scope.currentRow.processform.splice(index,1);
	                }else
	                {
	                    alert("操作失败，请重试！");
	                }
	                
	            }
	        ).error(function (data, status, headers, config) {

	            });
   	 
    }
    
    $scope.updateProcess=function(){
       var tidName = $("#"+$scope.currentRow.processdefinition.tid).html();
   	   $scope.currentRow.processdefinition.tidName=tidName;
    	ProcessEditManageService.postU({
            processdefinition:angular.toJson($scope.currentRow.processdefinition),
            //processcontroller:angular.toJson($scope.currentRow.processcontroller),
            //eventdefinition:angular.toJson($scope.currentRow.eventdefinition),
            processform:angular.toJson($scope.currentRow.processform),
        }).success(
            function (responseData) {
                if(responseData.status==1)
                {
                    alert("保存成功！");
                }else
                {
                    alert("操作失败，请重试！");
                }
            }
        ).error(function (data, status, headers, config) {

            });
    }
    
    $scope.saveProcess=function(){ 
    
    	 var tidName = $("#"+$scope.currentRow.processdefinition.tid).html();
    	 $scope.currentRow.processdefinition.tidName=tidName;
    	
    	ProcessEditManageService.post({
            processdefinition:angular.toJson($scope.currentRow.processdefinition),
            //processcontroller:angular.toJson($scope.currentRow.processcontroller),
            //eventdefinition:angular.toJson($scope.currentRow.eventdefinition),
            processform:angular.toJson($scope.currentRow.processform),
        }).success(
            function (responseData) {
                if(responseData.status==1)
                {
                    alert("新增成功！");
                    $scope.currentRow = {};
                }else
                {
                    alert("操作失败，请重试！");
                }
            }
        ).error(function (data, status, headers, config) {

            });
    }
});

//流程类别控制器                                                                         
systemManagementModule.controller('myApp.OperationManagement.processTypeDefinition', function ($scope, $http,ProcessTypeDefinitionService) {
	//当前视图状态
    $scope.viewStatus=1;//视图状态：部门信息
    $scope.mviewStatus=1;//视图状态：人员信息
    $scope.node={};
    //页面有关的信息
    $scope.pageInfo = {
        pageSize: 5,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    $scope.pageInfo2 = {
            pageSize: 10,
            totalCount: 0,
            pageCount: 0,
            pageIndex: 1,
            pageItems: [1, 2, 3,4,5,6,7,8,9,10],
            pageStart: 1,
            pageEnd: 10
        };
    $scope.treeOptions = {
    	    nodeChildren: "chidrens",
    	    dirSelectable: true,
    	    injectClasses: {
    	        
    	    }
    	}
    
    //搜索栏相关信息
    $scope.searchCondition = {
        status: $scope.viewStatus,
        mstatus: $scope.mviewStatus,
    };

  //点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.totalCount = 0;
        $scope.pageInfo.pageCount = 0;
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    };
    
    //点击导航栏启用
    $scope.okClick=function(){
        $scope.viewStatus=1;
        $scope.searchCondition.status=1;

        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };

    //点击导航栏草稿
    $scope.waitClick=function(){
        $scope.viewStatus=0;
        $scope.searchCondition.status=0;

        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };

    //点击导航栏停用
    $scope.stopClick=function(){
        $scope.viewStatus=2;
        $scope.searchCondition.status=2;

        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };
    
    $scope.rows = {};//保存查询的数据行
    //控制器加载动作
    $scope.load = function () {
        //加载数据部分
        var postData = $scope.searchCondition;
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.parentId = $scope.node.id;
        
        postData.mpageSize = $scope.pageInfo2.pageSize;
        postData.mpageIndex = $scope.pageInfo2.pageIndex;
        postData.mtotalCount = $scope.pageInfo2.totalCount;
        var result = ProcessTypeDefinitionService.list(postData).success(
            function (responseData) {
                $scope.rows = responseData.rows;
                $scope.pageInfo.totalCount = responseData.total;
                $scope.dataForTheTree = eval(responseData.rootList);
                
                $scope.expandedNodes = [
                    $scope.dataForTheTree[0],
                    $scope.dataForTheTree[0].chidrens[0],
                ];
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
                
                $scope.mrows = responseData.mrows;
                $scope.pageInfo2.totalCount = responseData.mtotal;
                $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        
    }
    
    $scope.options={};
    
    $scope.buttonClick = function(node) {
        $scope.node = node;
      //加载数据部分
        var postData = $scope.searchCondition;
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = 1;//$scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;
        postData.parentId = node.id;
        
        postData.mpageSize = $scope.pageInfo2.pageSize;
        postData.mpageIndex = 1;//$scope.pageInfo2.pageIndex;
        postData.mtotalCount = $scope.pageInfo2.totalCount;
        ProcessTypeDefinitionService.findChildNodeForProcessType(postData).success(
            function (responseData) {
                $scope.rows = responseData.rows;
                $scope.pageInfo.totalCount = responseData.total;
                $scope.dataForTheTree = eval(responseData.rootList);
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
                $scope.mrows = responseData.mrows;
                $scope.pageInfo2.totalCount = responseData.mtotal;
                $scope.pageInfo2.pageCount = Math.floor(($scope.pageInfo2.totalCount + $scope.pageInfo2.pageSize - 1) / $scope.pageInfo2.pageSize);
                $scope.pageInfo.pageIndex=1;
                $scope.pageInfo2.pageIndex=1;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
    }
    
    //加载动作
    $scope.load();

    //新增
    $scope.gcadd = function () {

        $scope.currentRow = {};
        $scope.currentRow.processTypeDefinition = {};

        if($scope.dataForTheTree!=null && $scope.dataForTheTree!=""){
        	if($scope.node.id=='' || $scope.node.id==null){
	        	alert("请选择部门节点！");
	        	return;
        	}
        }
        ProcessTypeDefinitionService.ptList({}).success(
            function (responseData) {
                $scope.currentRow.processTypeDefinition.parentId = $scope.node.id;
            	$scope.currentRow.ptds = responseData.ptds;
            	$("select[name='parentId']").attr('disabled','disabled');


            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        $('#myModal').modal();
    }

    //编辑
    $scope.gcedit = function (row) {
        $scope.currentRow = {};
        $scope.currentRow.processTypeDefinition = row;
        ProcessTypeDefinitionService.find({
            id: $scope.currentRow.processTypeDefinition.id,
        }).success(
            function (responseData) {
            	$scope.currentRow.processTypeDefinition = responseData.processTypeDefinition;
                $scope.currentRow.ptds = responseData.ptds;
                $("select[name='parentId']").attr('disabled','disabled');
                $scope.deptList = responseData.deptList;
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

        $('#myModal').modal('show');
    }

    $scope.saveptd=function(){
        if($scope.currentRow.processTypeDefinition.id==null){
            ProcessTypeDefinitionService.post({test:angular.toJson($scope.currentRow.processTypeDefinition)}).success(
                function (responseData) {
                    if(responseData.status==1)
                    {
                        //成功
                        alert("保存成功！");
                        $('#myModal').modal("hide");
                        $scope.load();
                    }else
                    {
                        alert("操作失败，请重试！");
                    }
                }
            ).error(function (data, status, headers, config) {

            });
        }else{
            ProcessTypeDefinitionService.postU({test:angular.toJson($scope.currentRow.processTypeDefinition)}).success(
                function (responseData) {
                    if(responseData.status==1)
                    {
                        alert("保存成功！");
                        $('#myModal').modal("hide");
                        $scope.load();
                    }else
                    {
                        alert("操作失败，请重试！");
                    }
                }
            ).error(function (data, status, headers, config) {

            });
        }

    }
});

//运行日志控制器 -- 异常流程管理
systemManagementModule.controller('myApp.OperationManagement.runLog', function ($scope, $http,RunLogManageService) {

    //当前视图状态
    $scope.viewStatus=0;

    //页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    //搜索栏相关信息
    $scope.searchCondition = {
        status: $scope.viewStatus,
    };

    //点击导航栏
    $scope.searchType=function(_type){
        $scope.viewStatus=_type;
        $scope.searchCondition.status=_type;
        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };

    //点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.totalCount = 0;
        $scope.pageInfo.pageCount = 0;
        $scope.pageInfo.pageIndex = 1;
        $scope.load();
    };

    $scope.rows = {};//保存查询的数据行
    //控制器加载动作
    $scope.load = function () {
        //加载数据部分
        var postData = $scope.searchCondition;
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;

        RunLogManageService.list(postData).success(
            function (responseData) {
                $scope.rows = responseData.rows;
                $scope.pageInfo.totalCount = responseData.total;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
        });

    };
    //加载动作
    $scope.load();

    //查看
    $scope.rlview = function (row) {

        $scope.currentRow = {};
        $scope.currentRow.exception = row;
        $scope.currentRow.flag=1;

        $('#my-popup-rlview').modal();
    }

    //解决
    $scope.currentVerifyRow={};
    $scope.acceptor={};
    $scope.rledit=function(row) {
        $scope.currentVerifyRow.obj = row;//当前验证行

        $('#my-prompt-rldeal').modal();
    };
    $scope.update = function(){
        //提交用户信息向服务器
        var id=$scope.currentVerifyRow.obj.id;
        if($scope.acceptor.status==undefined || $scope.acceptor.remark==undefined)
        {
            alert("请输入状态和处理说明");
        }else
        {
            RunLogManageService.update({id:id,status:$scope.acceptor.status,remark:$scope.acceptor.remark}).success(
                function (responseData) {
                    $scope.currentVerifyRow={};
                    if(responseData.status==1){
                        $('#close').click();
                    }else{
                        alert("提交数据失败，请与管理员联系");
                    }
                }
            ).error(function (data, status, headers, config) {
                $scope.currentVerifyRow={};
                alert("连接错误，错误码：" + status);
            });
        }
    };

});

//运行日志控制器 -- 异常流程恢复
systemManagementModule.controller('myApp.OperationManagement.resumeProcess', function ($scope, $http,ResumeProcessService) {
    $scope.id = '';
    $scope.resume = function () {
        if($scope.id =='' || $scope.id == null){
            alert("请输入流程实例ID");
            return;
        }
        ResumeProcessService.post({id:$scope.id}).success(
            function (responseData) {
                $scope.currentVerifyRow={};
                if(responseData.status==0){
                    alert("恢复成功");
                }else{
                    alert("恢复失败! 请检查ID输入是否正确，如无误，请联系管理员");
                }
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

    };
    
});

//运行日志控制器 -- 流程监控
systemManagementModule.controller('myApp.OperationManagement.processMonitor', function ($scope, $http,processMonitorService) {
	
    //当前视图状态
    $scope.viewStatus=1;

    //页面有关的信息
    $scope.pageInfo = {
        pageSize: 10,
        totalCount: 0,
        pageCount: 0,
        pageIndex: 1,
        pageItems: [1, 2, 3,4,5,6,7,8,9,10],
        pageStart: 1,
        pageEnd: 10
    };

    //搜索栏相关信息
    $scope.searchCondition = {
        status: $scope.viewStatus,
        name:null,
        id:null,
        startTime:null,
        endTime:null,
        pdName:null
    };

    //点击导航栏新发起
    $scope.newClick=function(){
        $scope.viewStatus=0;
        $scope.searchCondition.status=0;
        $scope.pageInfo.totalCount=0;
        $scope.pageInfo.pageCount=0;
        $scope.pageInfo.pageIndex=1;
        $scope.load();
    };


    
    //点击导航栏
    $scope.searchType=function(_type){
    	 $scope.viewStatus=_type;
         $scope.searchCondition.status=_type;
         $scope.pageInfo.totalCount=0;
         $scope.pageInfo.pageCount=0;
         $scope.pageInfo.pageIndex=1;
         $scope.load();
    };

    
    //点击搜索触发的动作
    $scope.search = function () {
        $scope.pageInfo.totalCount = 0;
        $scope.pageInfo.pageCount = 0;
        $scope.pageInfo.pageIndex = 1;
        if( $scope.searchCondition.id == null &&
            $scope.searchCondition.name == null &&
            $scope.searchCondition.pdName == null &&
            $scope.searchCondition.startTime == null &&
            $scope.searchCondition.endTime == null)
        {
            alert("请输入搜索条件");
            return;
        }
        $scope.load();
    };

    $scope.rows = {};//保存查询的数据行
    
    //控制器加载动作
    $scope.load = function () {
        //加载数据部分
        var postData = $scope.searchCondition;
        postData.pageSize = $scope.pageInfo.pageSize;
        postData.pageIndex = $scope.pageInfo.pageIndex;
        postData.totalCount = $scope.pageInfo.totalCount;

        processMonitorService.list(postData).success(
            function (responseData) {
                $scope.rows = responseData.rows;
                $scope.pageInfo.totalCount = responseData.total;
                $scope.pageInfo.pageCount = Math.floor(($scope.pageInfo.totalCount + $scope.pageInfo.pageSize - 1) / $scope.pageInfo.pageSize);
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });

    };
    //加载动作
    $scope.load();
    
    //查看
    $scope.oview = function (row) {
      
    	processMonitorService.find({
            piid: row.id,
        }).success(
            function (responseData) {
            	if(responseData.status==0){
            		alert("数据异常，请联系管理员");
            	}else{
            		window.location.href="process#/view/doTask/" +responseData.ti+"/false";
            	}
            }
        ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
   };

    //执行详情
    $scope.activitys=[];
    $scope.processlist=[];
    $scope.show = function (row) {
    	 $scope.activitys=[];
        processMonitorService.processDtl({
            piid: row.id,
        }).success(
            function (responseData) {
                if(responseData.status==0)
                {
                    alert("数据异常，请联系管理员");
                }else
                {
                    $('#my-show-popup').modal();
                	// $scope.activitys=responseData.activitys;
                	$scope.processlist=responseData.processlist;
                	console.log($scope.processlist);
                }
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    };
    
    $scope.currentTasklist=[];
    $scope.seeTask=function (activity) {
    	$('#my-show-popup1').modal();
    	$scope.currentTasklist=activity.tasklist;
    };

    /* 删除 */
    $scope.currentRow={};
    $scope.deletePi = function (row) {
    	$scope.currentRow.piid = row.id;//当前验证行
        // $('#my-prompt').modal();
        var a=confirm("确定删除该流程相关数据？");
        if(a){
            processMonitorService.deletePi({
                piid: $scope.currentRow.piid,
            }).success(
                function (responseData) {
                    if(responseData.status==0)
                    {
                        alert("删除成功！");
                        $scope.load();
                    }else{
                        alert("删除失败！");
                        $scope.load();
                    }
                }
            ).error(function (data, status, headers, config) {
                alert("连接错误，错误码：" + status);
            });
        }
    };

    $scope.processDtl= function (row) {
    	$scope.currentRow.piid=row.id;//当前行id
    };
    
    $scope.selectToken=function(row){
    	$scope.activitys=row.activitys;
    };
	
});


/*******************************************
 * 工作流引擎控制器代码---结束
 *******************************************/