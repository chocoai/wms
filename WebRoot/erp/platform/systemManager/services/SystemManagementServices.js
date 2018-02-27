/*******************************************************************************
 * 系统管理---核心服务集合定义
 ******************************************************************************/
var SystemManagementServices = angular.module('myApp.SystemManagementServices',[ ]);

/*******************************************************************************
 * 系统管理---用户服务
 ******************************************************************************/
SystemManagementServices.factory('UserService', ['$http', function ($http) {
    // 初始化方法
    var init = function (postData) {
        return $http.post("/user/init",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    // 查詢用戶
    var query = function(postData){
    	return $http.post("/user/query",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 更新操作
    var update = function(updateData){
        return $http.post("/user/update",
            $.param(updateData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }

    // 删除操作
    var del = function(deleteData){
        return $http.post("/user/delete",
            $.param(deleteData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 新增操作
    var save = function(postData){
        return $http.post("/user/save",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }

    // 获取指定用户
    var queryById = function(postData){
    	return $http.post("/user/queryById",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }

    // 获取用户机构数据权限树
    var queryDataUserRelationTree = function(postData){
        return $http.post("/user/queryDataUserRelationTree",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }
    // 获取用户机构树
    var queryOrgUserRelationTree = function(postData){
    	return $http.post("/user/queryOrgUserRelationTree",
    			$.param(postData) ,
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    }
    // 获取用户角色树
    var queryRoleUserRelationTree = function(postData){
    	return $http.post("/user/queryRelationRoleTree",
    			$.param(postData) ,
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    }
    
  // 获取用户组织树
    var queryGroupUserRelationTree = function(postData){
        return $http.post("/user/queryRelationGroupTree",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }
    
  // 获取用户岗位树
    var queryStationUserRelationTree = function(postData){
        return $http.post("/user/queryRelationStationTree",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }
    
    var saveDataUserRelation = function(postData){
    	return $http.post("/user/saveDataUserRelation",
                $.param(postData) ,
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }
            )
    }
    
    var saveOrgUserRelation = function(postData){
    	return $http.post("/user/saveOrgUserRelation",
    			$.param(postData) ,
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    }
    var saveRoleUserRelation = function(postData){
    	return $http.post("/user/saveRoleUserRelation",
    			$.param(postData) ,
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    }
    
    var saveGroupUserRelation = function(postData){
    	return $http.post("/user/saveGroupUserRelation",
                $.param(postData) ,
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }
            )
    }
    
    var saveStationUserRelation = function(postData){
    	return $http.post("/user/saveStationUserRelation",
                $.param(postData) ,
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }
            )
    }

    // 反回对应的对象
    return {
    	init: function (postData) {
            return init(postData);
        },
        query : function(postData){
        	return query(postData);
        },
        update : function(updateData){
        	return update(updateData);
        },
        delete: function(deleteData){
            return del(deleteData);
        },
        save : function(postData){
            return save(postData);
        },
        queryById : function(postData){
        	return queryById(postData);
        },
        queryDataUserRelationTree : function (postData) {
            return queryDataUserRelationTree(postData);
        },
        queryOrgUserRelationTree : function (postData) {
        	return queryOrgUserRelationTree(postData);
        },
        queryRoleUserRelationTree : function (postData) {
        	return queryRoleUserRelationTree(postData);
        },
        queryGroupUserRelationTree : function (postData) {
            return queryGroupUserRelationTree(postData);
        },
        queryStationUserRelationTree : function (postData) {
            return queryStationUserRelationTree(postData);
        },
        saveDataUserRelation : function (postData){
        	return saveDataUserRelation(postData)
        },
        saveOrgUserRelation : function (postData){
        	return saveOrgUserRelation(postData)
        },
        saveRoleUserRelation : function (postData){
        	return saveRoleUserRelation(postData)
        },
        saveGroupUserRelation : function (postData){
        	return saveGroupUserRelation(postData)
        },
        saveStationUserRelation : function (postData){
        	return saveStationUserRelation(postData)
        }
    };
}]);

/*******************************************************************************
 * 系统管理---角色服务
 ******************************************************************************/
SystemManagementServices.factory('RoleService', ['$http', function ($http) {
    // 初始化方法
    var init = function () {
        return $http.post("/role/init",
            null ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    
    var queryRole = function (postData) {
    	return $http.post("/role/queryRole",
    		$.param(postData) ,
    		{
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    			}
    		}
    	)
    	
    };
    
    var save = function (postData) {
    	return $http.post("/role/save",
    		$.param(postData) ,
    		{
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    			}
    		}
    	)
    	
    };
    var edit = function (postData) {
        return $http.post("/role/edit",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )

    };
    
    var del = function (postData) {
    	return $http.post("/role/del",
    		$.param(postData) ,
    		{
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    			}
    		}
    	)
    	
    };
    
    var add = function (postData) {
    	return $http.post("/role/add",
    		$.param(postData) ,
    		{
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    			}
    		}
    	)
    	
    };
    
    var RPRsave = function (postData) {
    	return $http.post("/role/RPRsave",
    		$.param(postData) ,
    		{
    			headers: {
    				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    			}
    		}
    	)
    	
    };

    // 反回对应的对象
    return {
    	init: function () {
            return init();
        },
        queryRole : function(postData){
        	return queryRole(postData);
        },
        save : function(postData){
        	return save(postData);
        },
        del : function(postData){
        	return del(postData);
        },
        add : function(postData){
        	return add(postData);
        },
        edit : function(postData){
        return edit(postData);
    },
        RPRsave : function(postData){
        	return RPRsave(postData);
        }
    };
}]);


/*******************************************************************************
 * 系统管理---权限服务
 ******************************************************************************/
SystemManagementServices.factory('PermissionService', ['$http', function ($http) {
	var queryPermissionTree=function(postData){
        return   $http.post("/permission/queryPermissionTree",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var queryPermissionList=function(postData){
        return   $http.post("/permission/queryPermissionList",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    /**
	 * 【编辑时】查询权限信息！
	 */
    var find = function (postData) {
    	
        return $http.post("/permission/queryPermissionInfo",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var findPermission = function (postData) {
        return $http.post("/permission/findPermission",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var post = function (postDate) {
        return $http.post("/permission/savePermission",postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},   
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };
    

    var update=function(updateDate){
        return   $http.post("/permission/updatePermission",
            $.param(updateDate),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var delPermission=function(delDate){
    	return   $http.post("/permission/delPermission",
    			$.param(delDate),
    			{
		    		headers: {
		    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
		    		}
		    	}
    	)
    };

    
    var queryModuleTree=function(mpostData){
        return   $http.post("/module/queryModuleTree",
            $.param(mpostData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var savePermResRelation = function (savePermResRelationData) {
    	return $http.post("/permission/savePermResRelation",
    			$.param(savePermResRelationData),
    			{
		    		headers: {
		    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
		    		}
    			}
    	)
    };
    
    var queryOperationList=function(postData){
        return   $http.post("/permission/queryOperationList",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    
    /**
	 * 查询功能树
	 */
    var queryOpetationTree=function(postData){
    	return   $http.post("/permission/queryOperationTree",
    			$.param(postData),
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    };
    
    
    /**
	 * 查询系统菜单树
	 */
    var querySystemMenuTree=function(postData){
    	return   $http.post("/permission/querySystemMenuTree",
    			$.param(postData),
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    };
    
    
    /**
	 * 选中父节点，子节点全选中
	 */
    var selectNodesByParentId=function(postData){
    	return   $http.post("/permission/selectNodesByParentId",
    			$.param(postData),
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    };
    
    /**
	 * 取消选中父节点，子节点全不选中
	 */
    var cancelSelectedNodes=function(postData){
    	return   $http.post("/permission/cancelSelectedNodes",
    			$.param(postData),
    			{
    		headers: {
    			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    		}
    			}
    	)
    };
    
    
    
    // 反回对应的对象
    return {
    	queryModuleTree: function (mpostData) {
            return queryModuleTree(mpostData);
        },
        savePermResRelation: function (savePermResRelationData) {
            return savePermResRelation(savePermResRelationData);
        },
        queryOperationList: function (postData) {
            return queryOperationList(postData);
        },
        querySystemMenuTree: function (postData) {
        	return querySystemMenuTree(postData);
        },
        cancelSelectedNodes: function (postData) {
        	return cancelSelectedNodes(postData);
        },
    	
        
        
        queryPermissionTree: function (postData) {
            return queryPermissionTree(postData);
        },
        queryPermissionList: function (postData) {
            return queryPermissionList(postData);
        },
        find: function (findData) {
            return find(findData);
        },
        findPermission: function (postData) {
            return findPermission(postData);
        },
        post: function (postDate) {
            return post(postDate);
        },
        update: function (updateData) {
            return update(updateData);
        },
        delPermission: function (delDate) {
        	return delPermission(delDate);
        }
    };
}]);



/*******************************************************************************
 * 系统管理---机构服务
 ******************************************************************************/
SystemManagementServices.factory('OrgService', ['$http', function ($http) {
	var queryOrgTree=function(postData){
		return   $http.post("/org/queryOrgTree",
				$.param(postData),
				{
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
			}
				}
		)
	};
	
	var queryOrgList=function(data){
		return   $http.post("/org/queryOrgList",
				$.param(data),
				{
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
			}
				}
		)
	};
	
	var findOrgById = function (postData) {
		return $http.post("/org/findOrgById",
				$.param(postData),
				{
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
			}
				}
		)
	};
	
	var addOrUpdateOrg = function (postDate) {
		return $http.post("/org/addOrUpdateOrg",postDate, {
			headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},   
			transformRequest: function (data) {
				return $.param(data);
			}
		})
	};
	
	var findOrgByOrgCode = function (postDate) {
		return $http.post("/org/findOrgByOrgCode",postDate, {
			headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},   
			transformRequest: function (data) {
				return $.param(data);
			}
		})
	};
	
	

	
	// 反回对应的对象
	return {

		queryOrgTree: function (postData) {
			return queryOrgTree(postData);
		},
		findOrgByOrgCode: function (postData) {
			return findOrgByOrgCode(postData);
		},
		findOrgById: function (data) {
			return findOrgById(data);
		},
		queryOrgList: function (data) {
			return queryOrgList(data);
		},
		addOrUpdateOrg: function (updateData) {
			return addOrUpdateOrg(updateData);
		}
	};
}]);


/*******************************************************************************
 * 系统管理---组织服务
 ******************************************************************************/
SystemManagementServices.factory('DeptService', ['$http', function ($http) {
    // 初始化组织树
    var init = function (groupData) {
        return $http.post("/department/init",
            $.param(groupData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    // 更新组织
    var update = function(groupData){
        return $http.post("/department/update",
            $.param(groupData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 删除组织
    var deleteGroup = function(delData){
        return $http.post("/department/delete",
            $.param(delData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 新增组织
    var save = function(postData){
        return $http.post("/department/save",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 根据id查询组织信息
    var queryGroup = function(groupData){
        return $http.post("/department/queryGroupById",
            $.param(groupData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 根据ID查询组织下的用户信息
    var queryGroupUser = function (groupUserData) {
        return $http.post("/department/queryGroupUser",
            $.param(groupUserData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 反回对应的对象
    return {
        init: function (postData) {
            return init(postData);
        },
        update : function(updateData){
            return update(updateData);
        },
        deleteGroup : function(deleteData){
            return deleteGroup(deleteData);
        },
        save : function(saveData){
            return save(saveData);
        },
        queryGroup : function (queryGroupData) {
            return queryGroup(queryGroupData);
        },
        queryGroupUser : function (queryGroupUserData) {
            return queryGroupUser(queryGroupUserData);
        }
    };
}]);


/*******************************************************************************
 * 系统管理---模块服务
 ******************************************************************************/
SystemManagementServices.factory('ModuleService', ['$http', function ($http) {
	var init = function (postData) {
        return $http.post("/module/init",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var queryModule = function (postData) {
        return $http.post("/module/queryModule",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var editModule = function (postData) {
        return $http.post("/module/editModule",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var queryParentList = function (postData) {
        return $http.post("/module/queryParentList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var saveModule = function (postData) {
        return $http.post("/module/saveModule",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var delModule = function (postData) {
        return $http.post("/module/delModule",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    return {
    	init: function (postData) {
            return init(postData);
        },
        queryModule: function (postData) {
            return queryModule(postData);
        },
        editModule: function (postData) {
            return editModule(postData);
        },
        queryParentList: function (postData) {
            return queryParentList(postData);
        },
        saveModule: function (postData) {
            return saveModule(postData);
        },
        delModule: function (postData) {
            return delModule(postData);
        }
        
    };
    
}]);


/*******************************************************************************
 * 系统管理---子系统服务
 ******************************************************************************/
SystemManagementServices.factory('SystemsService', ['$http', function ($http) {
    // 初始化方法
    var init = function () {
        return $http.post("/systems/init",
            null ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    
    var saveSystem = function (postData) {
        return $http.post("/systems/saveSystem",
        	$.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var delSystem = function (postData) {
        return $http.post("/systems/delSystem",
        	$.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    
    // 反回对应的对象
    return {
    	init: function (postData) {
            return init(postData);
        },
        saveSystem: function (postData) {
            return saveSystem(postData);
        },
        delSystem: function (postData) {
            return delSystem(postData);
        },
    };
    
}]);


/*******************************************************************************
 * 系统管理---功能服务
 ******************************************************************************/
SystemManagementServices.factory('OperationService', ['$http', function ($http) {
    
	// 初始化方法
    var init = function (postData) {
        return $http.post("/operation/init",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var saveOpt = function (postData) {
        return $http.post("/operation/saveOpt",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    
    var queryModuleTree = function (postData) {
        return $http.post("/operation/queryModuleTree",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    
    var updateModule = function (postData) {
        return $http.post("/operation/updateModule",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };


    var findOperationById = function (postData) {
        return $http.post("/operation/findOperationById",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };


	
	 return {
	    	init: function (postData) {
	            return init(postData);
	        },
	        saveOpt: function (postData) {
	            return saveOpt(postData);
	        },
	        queryModuleTree: function (postData) {
	            return queryModuleTree(postData);
	        },
	        updateModule: function (postData) {
	            return updateModule(postData);
	        },
            findOperationById: function (postData) {
                 return findOperationById(postData);
             }
	 }
}]);


/*******************************************************************************
 * 系统管理---下拉框字典服务
 ******************************************************************************/
SystemManagementServices.factory('selectService', ['$http', function ($http) {
    
	// 初始化方法
    var init = function (postData) {
        return $http.post("/select/init",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var save = function (postData) {
        return $http.post("/select/save",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var findById = function (postData) {
        return $http.post("/select/findById",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    return {
	    	init: function (postData) {
	            return init(postData);
	        },
	        save: function (postData) {
	            return save(postData);
	        },
            findById: function (postData) {
                 return findById(postData);
             }
	 }
}]);

/*******************************************************************************
 * 系统管理---岗位服务
 ******************************************************************************/
SystemManagementServices.factory('StationService', ['$http', function ($http) {
    // 初始化岗位树
    var init = function (stationData) {
        return $http.post("/station/init",
            $.param(stationData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 更新岗位
    var update = function(stationData){
        return $http.post("/station/update",
            $.param(stationData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 新增岗位
    var save = function(postData){
        return $http.post("/station/add",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 根据ID删除岗位
    var deleteStation = function(delData){
        return $http.post("/station/delete",
            $.param(delData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 根据id查询岗位信息
    var queryStation = function(stationData){
        return $http.post("/station/queryStationById",
            $.param(stationData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 根据ID查询岗位下的用户信息
    var queryStationUser = function (stationUserData) {
        return $http.post("/station/queryStationUser",
            $.param(stationUserData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    // 反回对应的对象
    return {
        init: function (postData) {
            return init(postData);
        },
        update : function(updateData){
            return update(updateData);
        },
        deleteStation : function(deleteData){
            return deleteStation(deleteData);
        },
        save : function(saveData){
            return save(saveData);
        },
        queryStation : function (queryStationData) {
            return queryStation(queryStationData);
        },
        queryStationUser : function (queryStationUserData) {
            return queryStationUser(queryStationUserData);
        }
    };
}]);

/************************
 * 系统管理---菜单服务
 ***********************/
SystemManagementServices.factory('MenuService', ['$http', function ($http) {
	var init = function (postData) {
        return $http.post("/menu/init",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var queryMenu = function (postData) {
        return $http.post("/menu/queryMenu",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var editMenu = function (postData) {
        return $http.post("/menu/editMenu",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var queryParentList = function (postData) {
        return $http.post("/menu/queryParentList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var saveMenu = function (postData) {
        return $http.post("/menu/saveMenu",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    
    var queryModuleAndOpt = function (postData) {
        return $http.post("/menu/queryModuleAndOpt",
        	$.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    return {
    	init: function (postData) {
            return init(postData);
        },
        queryMenu: function (postData) {
            return queryMenu(postData);
        },
        editMenu: function (postData) {
            return editMenu(postData);
        },
        queryParentList: function (postData) {
            return queryParentList(postData);
        },
        saveMenu: function (postData) {
            return saveMenu(postData);
        },
        queryModuleAndOpt: function (postData) {
            return queryModuleAndOpt(postData);
        }
        
    };
    
}]);





/*******************************************
 * 工作流引擎控制器代码 --- 开始
 *******************************************/

//流程设计管理
SystemManagementServices.factory('ProcessManageService', ['$http', function ($http) {

    var list=function(postData){
        return   $http.post("/api/processDefine",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var find = function (postData) {
        return $http.post("/api/findProcess",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var update=function(updateData){
        return   $http.post("/api/updateProcess",
            $.param(updateData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var copy=function(updateData){
        return   $http.post("/api/copyProcess",
            $.param(updateData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var del=function(data){
        return   $http.post("/api/delProcess",
            $.param(data),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }


    var unlock=function(data){//解锁
        return   $http.post("/api/unlockProcess",
            $.param(data),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }

    //反回对应的对象
    return {
        list: function (postData) {
            return list(postData);
        },
        update: function (updateData) {
            return update(updateData);
        },
        find: function (updateData) {
            return find(updateData);
        },
        del: function (updateData) {
            return del(updateData);
        },
        unlock:function(unlockData){
            return unlock(unlockData);
        },
        copy:function(updateData){
            return copy(updateData);
        }


    };
}]);

//流程设计定义管理
SystemManagementServices.factory('ProcessEditManageService', ['$http', function ($http) {

    //提交数据
    var post = function (postDate) {

        return $http.post("/api/saveProcessDefine", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    }

    //提交数据
    var postU = function (postDate) {

        return $http.post("/api/updateProcessDefine", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    }

    var update=function(updateDate){
        return   $http.post("/api/updateProcess",
            $.param(updateDate),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    }

    var find = function (postData) {
        return $http.post("/api/findProcess",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var find2 = function (postData) {
        return $http.post("/api/findForms",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var delProcessForm=function(updateDate){
    	
   	 return   $http.post("/api/delProcessForm",
   	            $.param(updateDate),
   	            {
   	                headers: {
   	                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
   	                }
   	            }
   	        )
   	
   };

    //反回对应的对象
    return {
        list: function (postData) {
            return list(postData);
        },
        post: function (postDate) {
            return post(postDate);
        },
        postU: function (postDate) {
            return postU(postDate);
        },
        find: function (updateData) {
            return find(updateData);
        },
        find2: function (updateData) {
            return find2(updateData);
        },
        delProcessForm: function (updateData) {
            return delProcessForm(updateData);
        }
    };
}]);

//流程类别管理
SystemManagementServices.factory('ProcessTypeDefinitionService', ['$http', function ($http) {
 
	var list=function(postData){
        return   $http.post("/api/processTypeDefinitionInfo",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var ptList=function(postData){
        return   $http.post("/api/findProcessTypeDefinition2",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var find = function (postData) {
        return $http.post("/api/findProcessTypeDefinition",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var findChildNodeForProcessType = function (postData) {
        return $http.post("/api/findChildNodeForProcessType",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var post = function (postDate) {
    	
        return $http.post("/api/postProcessTypeDefinition",postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},   
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };
    

    var postU = function (postDate) {
        return $http.post("/api/postProcessTypeDefinitionUpdate", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };

    
    //反回对应的对象
    return {
        list: function (postData) {
            return list(postData);
        },
        ptList: function (postData) {
            return ptList(postData);
        },
        find: function (findData) {
            return find(findData);
        },
        findChildNodeForProcessType: function (findData) {
            return findChildNodeForProcessType(findData);
        },
        post: function (postDate) {
            return post(postDate);
        },
        postU: function (postDate) {
            return postU(postDate);
        },
        
    };
	
}]);

//运行日志 -- 异常流程管理
SystemManagementServices.factory('RunLogManageService', ['$http', function ($http) {
    /*查看*/
    var list=function(postData){
        return   $http.post("/api/exceptionInfo",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    /*解决*/
    var update = function (postData) {
        return $http.post("/api/updateException",
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
        list: function (postData) {
            return list(postData);
        },
        update: function (postDate) {
                return update(postDate);
        }
    };
}]);

//运行日志 -- 异常流程恢复
SystemManagementServices.factory('ResumeProcessService', ['$http', function ($http) {
    /*一键恢复*/
    var post = function (postData) {
        return $http.post("/api/resumeProcess",
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
        post: function (postData) {
            return post(postData);
        }
    };
}]);

//运行日志 -- 流程监控
SystemManagementServices.factory('processMonitorService', ['$http', function ($http) {

    var list=function(postData){
        return   $http.post("/api/getProcessMonitorList",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var find=function(postData){
        return   $http.post("/api/findTaskinstance",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var deletePi=function(postData){
        return   $http.post("/api/deletePi",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var processDtl=function(postData){
        return   $http.post("/api/processMonitorDtl",
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
        list: function (postData) {
            return list(postData);
        },
        find: function (postData) {
            return find(postData);
        },
        deletePi: function (postData) {
            return deletePi(postData);
        },
        processDtl: function (postData) {
            return processDtl(postData);
        }
    };
    
}]);

/*******************************************
 * 工作流引擎控制器代码 --- 结束
 *******************************************/