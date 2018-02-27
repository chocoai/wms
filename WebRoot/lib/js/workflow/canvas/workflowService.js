var WorkflowServices = angular.module('myApp.WorkflowServices',[ ]);

WorkflowServices.factory('ActivityformService', ['$http', function ($http) {


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
    
    var find2 = function (postData) {
        return $http.post("/api/loadActivityDef",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var delActivityForm = function (postData) {
        return $http.post("/api/delActivityForm",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

   
  //提交数据
    var post = function (postDate) {

    	return $http.post("/api/postActivityDef", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };
    
    var post2 = function (postDate) {

    	return $http.post("/api/postActivityDefOthers", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };

   
    
    //反回对应的对象
    return {
        find: function (postDate) {
            return find(postDate);
        }, 
        find2: function (postDate) {
            return find2(postDate);
        }, 
        post: function (postDate) {
            return post(postDate);
        },
        post2: function (postDate) {
            return post2(postDate);
        },
        delActivityForm: function (postDate) {
            return delActivityForm(postDate);
        }
    };
}]);


WorkflowServices.factory('LineformService', ['$http', function ($http) {
	 var postLine = function (postDate) {

	    	return $http.post("/api/postLineName", postDate, {
	            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
	            transformRequest: function (data) {
	                return $.param(data);
	            }
	        })
	    };
	    
	  //反回对应的对象
	    return {
	      
	    	postLine: function (postDate) {
	            return postLine(postDate);
	        },
	        
	    };
}]);

WorkflowServices.factory('ProcessFormService', ['$http', function ($http) {
	
	  //通过pdid 获取
    var findpd = function (postData) {
        return $http.post("/api/findProcess",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    //提交数据
    var postU = function (postDate) {

        return $http.post("/api/updateProcessDefine", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    };

    //操作流程表单数据
    var saveOrUpdateProcessform=function(postDate){
    	return   $http.post("/api/saveOrUpdateProcessform",
                $.param(postDate),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }
            )
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
    };
    
    var processEvent=function(postDate){
    	return $http.post("/api/postActivityDefOthers", postDate, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: function (data) {
                return $.param(data);
            }
        })
    }
    
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
    
    return {
        
        findpd: function (postDate) {
            return findpd(postDate);
        },
        postU: function (postDate) {
            return postU(postDate);
        },
        update: function (postDate) {
            return update(postDate);
        },
        delProcessForm: function (postDate) {
            return delProcessForm(postDate);
        },
       processEvent: function(postDate){
        	return processEvent(postDate);
        },
        saveOrUpdateProcessform:function(postDate){
        	return saveOrUpdateProcessform(postDate);
        }
        
    };
    
}]);
