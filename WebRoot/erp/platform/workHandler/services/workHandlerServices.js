/*******************************************************************************
 * 工作台核心服务集合定义
 ******************************************************************************/
var workflowServices = angular.module('xyy.workflow.workflowServices',[ ]);
/*******************************************************************************
 * 工作处理器服务
 ******************************************************************************/
workflowServices.factory('WorkService', ['$http', function ($http) {
    var init = function (postData) {
        return $http.post("/process/doTask",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var save = function (postData) {
        return $http.post("/process/save",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var submit = function (postData) {
        return $http.post("/process/submit",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getTransitionsByAD = function (postData) {
        return $http.post("/process/getTransitionsByAD",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getRandomMembers = function (postData) {
        return $http.post("/process/getRandomMembers",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getConditionMembers = function (postData) {
        return $http.post("/process/getConditionMembers",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getRandomConditionMembers = function (postData) {
        return $http.post("/process/getRandomConditionMembers",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var hasMultiTransition = function (postData) {
        return $http.post("/process/hasMultiTransition",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var assignment = function (postData) {
        return $http.post("/process/assignment",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getConditionMembers3 = function (postData) {
        return $http.post("/process/getConditionMembers3",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getRandomConditionMembers2 = function (postData) {
        return $http.post("/process/getRandomConditionMembers2",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var back = function (postData) {
        return $http.post("/process/back",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var canBackList = function (postData) {
        return $http.post("/process/canBackList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var OrderTask = function (postData) {
        return $http.post("/process/OrderTask",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var shape = function (postData) {
        return $http.post("/process/shape",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var workflowFile = function (postData) {
        return $http.post("/process/workflowFile",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var processLog = function (postData) {
        return $http.post("/process/processLog",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var dataForm = function (postData) {
        return $http.post("/process/dataForm",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var childProcess = function (postData) {
        return $http.post("/process/childProcess",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var childThreadShape = function (postData) {
        return $http.post("/process/childThreadShape",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var suspend = function (postData) {
        return $http.post("/process/suspend",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var recovery = function (postData) {
        return $http.post("/process/recovery",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    var getBackInfo = function (postData) {
        return $http.post("/process/getBackInfo",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var getTaskInstanceStatus = function (postData) {
        return $http.post("/process/getTaskInstanceStatus",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    
    var forceEnd = function (postData) {
        return $http.post("/process/forceEnd",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var processExamine = function (postData) {
        return $http.post("/process/processExamine",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    var submitExamine = function (postData) {
        return $http.post("/process/submitExamine",
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
        forceEnd: function (postData) {
            return forceEnd(postData);
        },
        getTaskInstanceStatus: function (postData) {
            return getTaskInstanceStatus(postData);
        },
        save: function (postData) {
            return save(postData);
        },
        submit: function (postData) {
            return submit(postData);
        },
        getTransitionsByAD: function (postData) {
            return getTransitionsByAD(postData);
        },
        getRandomMembers: function (postData) {
            return getRandomMembers(postData);
        },
        getConditionMembers: function (postData) {
            return getConditionMembers(postData);
        },
        getRandomConditionMembers: function (postData) {
            return getRandomConditionMembers(postData);
        },
        hasMultiTransition: function (postData) {
            return hasMultiTransition(postData);
        },
        getConditionMembers3: function (postData) {
            return getConditionMembers3(postData);
        },
        getRandomConditionMembers2: function (postData) {
            return getRandomConditionMembers2(postData);
        },
        back: function (postData) {
            return back(postData);
        },
        canBackList: function (postData) {
            return canBackList(postData);
        },
        OrderTask: function (postData) {
            return OrderTask(postData);
        },
        shape: function (postData) {
            return shape(postData);
        },
        workflowFile: function (postData) {
            return workflowFile(postData);
        },
        processLog: function (postData) {
            return processLog(postData);
        },
        dataForm: function (postData) {
            return dataForm(postData);
        },
        childProcess: function (postData) {
            return childProcess(postData);
        },
        childThreadShape: function (postData) {
            return childThreadShape(postData);
        },
        suspend: function (postData) {
            return suspend(postData);
        },
        recovery: function (postData) {
            return recovery(postData);
        },
        doTaskDefualtForm: function (postData) {
            return doTaskDefualtForm(postData);
        },
        getBackInfo: function (postData) {
            return getBackInfo(postData);
        },
        processExamine: function (postData) {
            return processExamine(postData);
        },
        submitExamine: function (postData) {
            return submitExamine(postData);
        }
    };
}]);

/*******************************************************************************
 * 工作台服务
 ******************************************************************************/
workflowServices.factory('TaskService', ['$http', function ($http) {
    //待办任务
	var myTaskList = function (postData) {
        return $http.post("/process/myTaskList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //受理任务
    var shouli = function (postData) {
        return $http.post("/process/acceptTask",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //恢复任务
    var recovery = function (postData) {
        return $http.post("/process/recovery",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //在途任务
    var myDoingTaskList = function (postData) {
        return $http.post("/process/myDoingTaskList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //已办任务
    var myDoneTaskList = function (postData) {
        return $http.post("/process/myDoneTaskList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //预约任务
    var myAppointTaskList = function (postData) {
        return $http.post("/process/myAppointTaskList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //挂起任务
    var myHangupTaskList = function (postData) {
        return $http.post("/process/myHangupTaskList",
            $.param(postData) ,
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };
    //解锁任务
    var clear = function (postData) {
        return $http.post("/process/clear",
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
    	myTaskList: function (postData) {
            return myTaskList(postData);
        },
        shouli: function (postData) {
            return shouli(postData);
        },
        recovery: function (postData) {
            return recovery(postData);
        },
        myDoingTaskList: function (postData) {
            return myDoingTaskList(postData);
        },
        myDoneTaskList: function (postData) {
            return myDoneTaskList(postData);
        },
        myAppointTaskList: function (postData) {
            return myAppointTaskList(postData);
        },
        myHangupTaskList: function (postData) {
            return myHangupTaskList(postData);
        },
        clear: function (postData) {
            return clear(postData);
        }
    };
}]);


