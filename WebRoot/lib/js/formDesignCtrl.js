var app = angular.module('formDesign', ['MyFilter']);
app.controller('designCtrl', function ($scope, $http,$interval) {
    /**
     *  定义转换规则模型
     */
    $scope.billEdgeController = {};
    $scope.billTransformEdge = {};
    $scope.billGroupJoinEdge = {};
    $scope.billBackWriteEdge = {};
    //规则过滤
    $scope.ruleFilterEdge = {};
    $scope.ruleFilterEdge.ruleHeadFilterEdge = {
        filter: {
            formula: ''
        }
    };
    $scope.ruleFilterEdge.ruleBodyFilterEdge = {
        /*filter : [{
         sourceKey : '',
         formula : ''
         }]*/
    };
    //数据过滤
    $scope.dataFilterEdge = {};
    $scope.dataFilterEdge.sourceBillHeadFilterEdge = {
        filter: {
            formula: ''
        }
    };
    $scope.dataFilterEdge.sourceBillBodyFilterEdge = {
        filter: []
    };

    //Hookp配置
    $scope.billEdgeHook = {};


    /**
     * BillEdge对象模型
     */
    $scope.billEdge = {
        key: '',
        caption: '',
        sourceBillKey: '',
        targetBillKey: '',
        code: '',
        name: '',
        version: '1.0.0',
        description: {
            desc: '',
        },
        billEdgeController: $scope.billEdgeController,
        billTransformEdge: $scope.billTransformEdge,
        billGroupJoinEdge: $scope.billGroupJoinEdge,
        billBackWriteEdge: $scope.billBackWriteEdge,
        dataFilterEdge: $scope.dataFilterEdge,
        ruleFilterEdge: $scope.ruleFilterEdge,
        billEdgeHook:$scope.billEdgeHook
    };

    /**
     * BillEdgeController对象模型
     */
    $scope.multiConvertCtrlArr = [{'name': '允许多次生成并提示', 'value': 'AllowAndWarning'}, {
        'name': '允许多次生成但不提示',
        'value': 'AllowAndNotWarning'
    }, {'name': '不允许多次生成', 'value': 'Forbid'}];
    $scope.billEdgeCtrlStateArr = [{'name': '启用', 'value': 1}, {'name': '禁用', 'value': 0}];

    $scope.curState = {};
    $scope.curState.state = 1;  //默认值

    $scope.changeState = function (flag) {
        $scope.curState.state = flag;
    };

    $scope.billEdgeController.autoSaveController = {
        ctrType: "NoSave"
    };
    $scope.billEdgeController.backWriteController = {
        ctrType: "Save"
    };
    $scope.billEdgeController.displayController = {
        ctrType: "NoGo"
    };
    $scope.billEdgeController.multiConvertCtrl = {
        ctrType: "AllowAndWarning"
    };

    /**
     * Hook配置项
     */
    $scope.selectedRow= {};
    $scope.isDelete = false;
    $scope.HeadTableHook = [];
    $scope.selectedRowsMap=new Map();
    $scope.selectedObj = [];
    $scope.listenerType = [
        {name:'选项',value:''},
        {name:'单据转换监听器',value:'BillConvertListener'},
        {name:'单据头转换监听器',value:'BillHeadConvertListener'},
        {name:'单据体转换监听器',value:'BillBodyConvertListener'},
        {name:'单据体明细转换监听器',value:'BillBodyItemConvertListener'},
        {name:'单据一级分组合并监听器',value:'BillHeadGroupJoinListener'},
        {name:'单据二级分组合并监听器',value:'BillBodyGroupJoinListener'},
        {name:'单据明细分组合并监听器',value:'BillDtlGroupJoinListener'},
    ];
    $scope.listenerTypeHandler = function (cos, rowIndex) {
        // if($scope.selectedRowsMap.containsKey(rowIndex)){
        //     $scope.listenerType.push($scope.selectedRowsMap.get(rowIndex));
        //     $scope.selectedRowsMap.remove(rowIndex);
        // }
        // $scope.selectedRowsMap.put(rowIndex, cos);
        // $.each($scope.listenerType ,function (index, entry) {
        //     $.each($scope.selectedRowsMap.values(), function (index2, entry2) {
        //         if(entry.value == entry2.listenerType){
        //             $scope.listenerType.splice(index, 1);
        //         }
        //     });
        // });
    };
    $scope.hookload = function () {
        if($scope.billEdgeHook && $scope.billEdgeHook.listeners){
            $scope.HeadTableHook = $scope.billEdgeHook.listeners;
        }
    };
    $scope.createListenerHandler = function () {
        $scope.isDelete = false;
        $scope.HeadTableHook.push({
            listenerType:'',
            classPath : ''
        });
    };
    $scope.removeListenerHandler = function () {
        if($scope.HeadTableHook && $scope.HeadTableHook.length > 0){
             for(var i = 0; i < $scope.HeadTableHook.length; i++){
                 if($scope.HeadTableHook[i].classPath ==  $scope.selectedRow.classPath){
                     $scope.HeadTableHook.splice(i,1);
                 }
             }
            if($scope.HeadTableHook.length == 0){
                $scope.isDelete = false;
            }
        }
    };
    $scope.selectedRowHandler = function (row) {
        $scope.isDelete = true;
        $scope.selectedRow = row;
    };

    /**
     * 保存单据转换规则
     */
    $scope.setRule = function () {
    	$scope.billEdge.billGroupJoinEdge = $scope.billGroupJoinEdge;
        $scope.billEdge.billBackWriteEdge = $scope.billBackWriteEdge;
        $.each($scope.billEdge.billTransformEdge.billHeadTransformEdge, function (index, entry) {
    		delete entry['fieldName'];
        });
        $.each($scope.billEdge.billTransformEdge.billBodyTransformEdge, function (index, entry) {
    		$.each(entry.billDtlEdges, function (index2, entry2) {
    			$.each(entry2.targetBillFields, function (index3, entry3) {
    	    		delete entry3['fieldName'];
                });
            });
        });
        $.each($scope.dataFilterEdge.sourceBillBodyFilterEdge.filter, function (index, entry) {
            delete entry['caption'];
        });
        var postData = {billEdge : angular.toJson($scope.billEdge)};
        $http.post("/billdesign/createBillEdge",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        ).success(function (data, status, headers, config) {
            if (data.state == "success") {
                workflow.selected.ruleKey = data.ruleKey;
                workflow.render();
                //rule key 设置成功保存画布
                $scope.saveTranInfo();
                $('#setRuleModal').modal('hide');
                alert("Success");
            }else{
                alert("fail" + data.errorMsg);
                $('#setRuleModal').modal('hide');
            }
        });
    };
//保存设计信息

    $scope.saveTranInfo=function () {
        // NProgress.start();//遮罩层弹出

        var result = workflow.buildProcessSaveParameters();
        //保存表单转换规则设计器内容
        var postData = {result : angular.toJson(result)};
        $http.post("/billdesign/saveResultForBillEdge",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        ).success(function (data, status, headers, config) {
            if (data.state == "success") {
                // alert("保存成功");
                // $('#setRuleModal').modal('hide');
            }else{
                alert("保存失败！");
            }

            NProgress.done();//遮罩层弹出
        });
    };

//        选择单据
    $scope.setNode = function (type) {
        $scope.toolType = 1;
        $scope.currNode = {};
        $scope.bills = [];
        localStorage.setItem('nodeType',type);
        $http.post("/billdesign/bills").success(
            function (data, status, headers, config) {
            	if(type == 'BILL'){
            		$.each(data.bills, function (index, entry) {
                        $scope.bills.push(entry);
                    });
            	}else if(type == 'MULTIBIBLL'){
            		$.each(data.multiBills, function (index, entry) {
                        $scope.bills.push(entry);
                    });
            	}else{
            		$.each(data.dics, function (index, entry) {
                        $scope.bills.push(entry);
                    });
            	}
                
                $('#setNodeModal').modal('show');
                $scope.currNode.testSelect=$scope.bills[0];
            }
        ).error(function (data, status, headers, config) {
//            console.log(status);
        });
    };

//        设置画布大小，画布缩放之后坐标对应不上
       $scope.scale={
           range:'1.0'
       };
       $scope.setSize=function () {
           var c=document.getElementById("myworkflow");
           var ctx=c.getContext("2d");
           ctx.scale($scope.scale.range,$scope.scale.range);
           workflow.render();
       };
        $scope.toolhide=1;
        $scope.settoolhide=function (num) {
            $scope.toolhide=num;
       };


    $scope.getNodeName = function () {
        // $scope.currNode.testSelect  为选中的select对象，再取name或者key的值
        var _nodeName = $scope.currNode.testSelect.name;
        var _caption = $scope.currNode.testSelect.key;

        $scope.shapeStyle.text =_nodeName;
        $scope.shapeStyle.caption = _caption;

        var _nodeArr = workflow.container.values();
        for (var i = 0; i < _nodeArr.length; i++) {
            if (_caption == _nodeArr[i].caption) {
                alert(_nodeArr[i].text + '已存在，请重新选择');
                return;
            }
        }
        if (_nodeName) {
            localStorage.setItem('nodeName', _nodeName);
            localStorage.setItem('_caption', _caption);
        } else {
            localStorage.removeItem('nodeName');
            localStorage.removeItem('_caption');
            alert('请选择单据');
            return;
        }
        workflow.setOperatorMode(2);
        $('#setNodeModal').modal('hide');
    };

    //双击事件
    $scope.dblClick = function () {
    	$scope.billEdge = {
    	        key: '',
    	        caption: '',
    	        sourceBillKey: '',
    	        targetBillKey: '',
    	        code: '',
    	        name: '',
    	        version: '1.0.0',
    	        description: {
    	            desc: '',
    	        },
    	        billEdgeController: $scope.billEdgeController,
    	        billTransformEdge: $scope.billTransformEdge,
    	        billGroupJoinEdge: $scope.billGroupJoinEdge,
    	        billBackWriteEdge: $scope.billBackWriteEdge,
    	        dataFilterEdge: $scope.dataFilterEdge,
    	        ruleFilterEdge: $scope.ruleFilterEdge,
                billEdgeHook:$scope.billEdgeHook
    	    };
    	$('#code').removeAttr('readonly');
        if (workflow.selected) {
            $scope.selectedType = workflow.selected.__getType();
            if ($scope.selectedType == 'workflow.node.task') {

            } else if ($scope.selectedType == 'workflow.line') {
                $scope.billEdge.sourceBillKey = workflow.selected.source.caption;
                $scope.billEdge.targetBillKey = workflow.selected.target.caption;
                $http.post('/billdesign/getBill', $.param({targetKey: $scope.billEdge.targetBillKey,ruleKey:workflow.selected.ruleKey,sourceDtlKey:workflow.selected.source.caption}), {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }).success(function (response) {
                	//清楚明细分组历史记录
                	$scope.selectedGroup = null;
                	$scope.selectedGroupType = null;
                	//
                	$scope.billTransformEdge.billHeadTransformEdge =[];
                	$scope.billTransformEdge.billBodyTransformEdge =[];
                	$scope.dtlList = [];
                	$scope.tabKey=response.bodyList[0].key;
                	if (response.billEdge == null) {
                		$scope.billEdge.name = workflow.selected.source.text+'-'+workflow.selected.target.text
                		$.each(response.HeadTable.fields, function (index, entry) {
                    		$scope.billTransformEdge.billHeadTransformEdge.push({'key':entry.fieldKey,'fieldName':entry.fieldName,'category':'property'});
                        });
                    	$.each(response.bodyList, function (index, entry) {
                    		$scope.billTransformEdge.billBodyTransformEdge.push({'targetDltKey':entry.key,'billDtlEdges':[{'sourceDltKey':'','targetBillFields':[]}]})
                    		$.each(entry.fields, function (index2, entry) {
                    			$scope.billTransformEdge.billBodyTransformEdge[index].billDtlEdges[0].targetBillFields.push({'key':entry.fieldKey,'fieldName':entry.fieldName,'category':'property'});
                            });
                        });
                    	$scope.billGroupJoinEdge = response.billGroupJoin;
                        $scope.billBackWriteEdge=response.backWriteEdge;
                	}else{
                		$('#code').attr('readonly','readonly');
                		$scope.billEdge = response.billEdge;
                		$scope.billEdgeController = response.billEdge.billEdgeController;
                		$scope.billTransformEdge =response.billEdge.billTransformEdge;
                        $scope.billGroupJoinEdge =response.billEdge.billGroupJoinEdge;
                        $scope.billBackWriteEdge =response.billEdge.billBackWriteEdge;
                        $scope.dataFilterEdge =response.billEdge.dataFilterEdge;
                        $scope.ruleFilterEdge =response.billEdge.ruleFilterEdge;
                        $scope.billEdgeHook = response.billEdge.billEdgeHook;
                	}
                    $scope.HeadTable = response.HeadTable;
                    $scope.bodyList = response.bodyList;
                    if($scope.dtlList == '' ||　$scope.dtlList　== null){
                        $scope.dtlList = response.dtlList;
                    }

                });
                $('#setRuleModal').modal('show');
            }
        } else {
            //                背景被选中
//                            $('#setRuleModal').modal('show');
        }
    };

    /**
     * 规则过滤、数据过滤模型
     */
    $scope.ruleFilter = function () {
        $('#ruleFilterModal').modal('show');
    };
    $scope.dataFilter = function () {
        if($scope.dtlList != '' ||　$scope.dtlList　!= null){
            $('#dataFilterModal').modal('show');
        } else {
            $http.post('/billdesign/getBillDtl', $.param({sourcekey:workflow.selected.source.caption}),{
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success(function (response) {
                $scope.dtlList = response.dtlList;
            });
            $('#dataFilterModal').modal('show');
        }
    };
    //取消
    $scope.quRule = function () {
        $scope.ruleFilterEdge = {};
    };

    //规则过滤确认按钮confirmRule ()
    $scope.confirmRule = function () {
        $('#ruleFilterModal').modal('hide');
    };
    $scope.selectedDtl = {};
    $scope.selectDtl = function(){
        if($scope.selectedDtl != null){
            $scope.dataFilterEdge.sourceBillBodyFilterEdge.filter.
            push({sourceKey:$scope.selectedDtl.key, formula:'',caption:$scope.selectedDtl.caption});
        }else{
            $scope.dataFilterEdge.sourceBillBodyFilterEdge.filter.
            push({sourceKey:'', formula:'',caption:''});
        }
    };
    //数据过滤确认按钮confirmData ()
    $scope.confirmData = function () {
        $('#dataFilterModal').modal('hide');
    };
    //取消
    $scope.quData = function () {
        $scope.dataFilterEdge = {};
    };

    //添加一行映射
    $scope.addRow = function () {
        var tableKey = $('.bodyTable.active').attr('id');
        var index = $('.bodyTable.active').attr('index');
        for (var i = 0; i < $scope.bodyList.length; i++) {
            if ($scope.bodyList[i].key == tableKey) {
                var headList = [];
                for (var j = 0; j < $scope.bodyList[i].fields.length; j++) {
                    var head ={'key':'','category':''}
                    head.key = $scope.bodyList[i].fields[j].fieldKey;
                    head.category = 'property';
                    headList.push(head);
                }
                $scope.billTransformEdge.billBodyTransformEdge[i].billDtlEdges.push({'sourceDltKey':'','targetBillFields':headList});
            }
        }
    };

    //记录key
    $scope.getTabKey=function (tabKey) {
        $scope.tabKey=tabKey;
    };

    //选择行
    $scope.selRow=function (row,index) {
        $scope.co=row;
        $scope._index=index;
    };

    //删除映射行
    $scope.delRow=function () {
        if($scope.co && $scope.co!=''){
            var _dataSet=$scope.billTransformEdge.billBodyTransformEdge;
            for(var i=0;i<_dataSet.length;i++){
                if($scope.tabKey==_dataSet[i].targetDltKey ){
                    _dataSet[i].billDtlEdges.splice($scope._index, 1);
                    $scope.co='';
                }
            }
        // .splice(i, 1);
        }else{
            alert('请选择要删除的行');
        }
    };
    $scope.addBwRow=function (type,index) {
        if(type=='head'){
            $scope.billBackWriteEdge.billHeadBackWriteEdge.backWriteRules.push({});
        }else{
            $scope.billBackWriteEdge.billBodyBackWriteEdge.backWriteRules.push({});
        }
    }
    $scope.delBwRow=function (type,index) {
        if($scope.bw  && $scope.bw!=''){
            if(type=='head'){
                $scope.billBackWriteEdge.billHeadBackWriteEdge.backWriteRules.splice($scope.$_index, 1);
            }else{
                $scope.billBackWriteEdge.billBodyBackWriteEdge.backWriteRules.splice($scope.$_index, 1);
            }
        }
        $scope.bw='';
    }
    $scope.selBwRow=function (row,index) {
        $scope.bw=row;
        $scope.$_index=index;
    };
    $scope.selectedGroup = {};
    //添加明细分组弹窗
    $scope.addGroup = function () {
    	var flag = false;
    	$.each(this.billTransformEdge.billHeadTransformEdge,function(index,entry){
            if(entry.category=='group'||entry.category=='formulaGroup'){
                alert("已经存在明细分组，不可再次添加");
                flag = true;
            }
        });
        if(!flag){
        	$('#TransformHead').modal('show');
        };

    }
    //保存明细分组
    $scope.setGroup = function () {
    	if (!$scope.selectedGroup) {
			alert("请选择明细分组");
			return;
		}
    	if (!$scope.selectedGroupType) {
			alert("请选择明细分组类型");
			return;
		}
        var head1 ={'key':'','category':'','targetDltKey':'','fieldName':''}
        head1.key = $scope.selectedGroup.key;
        head1.category = $scope.selectedGroupType;
        head1.targetDltKey =  $scope.selectedGroup.key;
        head1.fieldName =  $scope.selectedGroup.caption;
        $scope.billTransformEdge.billHeadTransformEdge.push(head1);
        $('#TransformHead').modal('hide');
    }

    $scope.delLine = function (){
    	if(workflow.selected.ruleKey!=''&& workflow.selected.ruleKey!=null){
    		$http.post('/billdesign/delEdge', $.param({ruleKey: workflow.selected.ruleKey}), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success(function (response) {
                if (response.state=='1') {
                	workflow.removeSelectedObject();
				}else {
					alert("删除失败");
				}
            });
    	}else {
    		workflow.removeSelectedObject();
		}



    }




    //        删除事件
    $("html").keydown(function (event) {
        var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
        switch (myEvent.keyCode) {
            case 46://delete事件
                workflow.removeSelectedObject();
                break;
        }
    });

    $scope.toolType = 1;

    $.postOffice.subscribe(
        "workflow.node.task.selected", function () {
            $scope.toolType = 1;
            $scope.shapeStyle = {
                fillStyle: workflow.selected.fillStyle, // 背景色
                strokeStyle: workflow.selected.strokeStyle, // 边框颜色
                fontColor: workflow.selected.textColor,//字体颜色
                text:workflow.selected.text,
                caption:workflow.selected.caption
            };
            $scope.$apply();
        });
    $.postOffice.subscribe("workflow.line.selected",
        function () {
            $scope.toolType = 2;
            $scope.lineStyle = {
                //            strokeColor:$scope.strokeColor,  // 箭头颜色strokeColor
                //            strokeStyle:$scope.strokeColor,  // 线颜色
                width:workflow.selected.lineWidth,  //线宽度
                type: workflow.selected.lineType, //线类型  1：折线；2：贝斯曲线
                text:workflow.selected.text,
                textColor:workflow.selected.textColor,
                fillStyle:workflow.selected.fillStyle,
                strokeColor:workflow.selected.strokeStyle,
                fontColor:workflow.selected.textColor,
            };
            $scope.$apply();
        });
    $scope.shapeStyle = {
        caption:'',// xml key
        fillStyle: "#355878", // 背景色
        strokeStyle: "#355878", // 边框颜色
        //            lineWidth: 1,  //边框宽度
        shadowOffsetX: -2, //投影x偏移
        shadowOffsetY: -2,
        shadowColor: "gray", //投影颜色
        shadowBlur: 8, //投影模糊度
        fontColor: "#ffffff",//字体颜色
    };
    $scope.lineStyle = {
        //            strokeColor:$scope.strokeColor,  // 箭头颜色strokeColor
        //            strokeStyle:$scope.strokeColor,  // 线颜色
        //            lineWidth:$scope.width,  //线宽度
        type: 1, //线类型  1：折线；2：贝斯曲线
        //            text:$scope.text,
    };

    $scope.setSelectType = function () {
        //            var me = workflow.operatorHandler[workflow.getOperatorMode()];
    };

    $scope.render = function () {
        if (workflow.selected) {
            $scope.selectedType = workflow.selected.__getType();
            if ($scope.selectedType == 'workflow.node.task') {
                var options = {};
                var me = workflow.selected;
                // var curPoint = {
                //     x: localStorage.getItem('x'),
                //     y: localStorage.getItem('y')
                // }

                // me.selected = workflow.getTarget(curPoint);
                if($scope.shapeStyle.caption){
                    me.caption = $scope.shapeStyle.caption;
                }
                me.fillStyle = $scope.shapeStyle.fillStyle; // 背景色
                me.strokeStyle = $scope.shapeStyle.strokeStyle; // 边框颜色
                me.lineWidth = $scope.shapeStyle.lineWidth; //边框宽度
                me.shadowOffsetX = -2; //投影x偏移
                me.shadowOffsetY = -2;
                me.shadowColor = "gray"; //投影颜色
                me.shadowBlur = 8; //投影模糊度
                //            me.selected.width = $scope.shapeStyle.width;
                //            me.selected.height = $scope.shapeStyle.height;
                me.textColor = $scope.shapeStyle.fontColor;
                if ($scope.shapeStyle.text) {
                    me.text = $scope.shapeStyle.text;
                }
                workflow.render();
                //
            } else if ($scope.selectedType == 'workflow.line') {
                $scope.renderLine();
            }
        }

    }
    $scope.renderLine = function () {
        var me = workflow.selected;
        if( me && me!==''){
            me.fillStyle = $scope.lineStyle.strokeColor; // 背景色
            me.strokeStyle = $scope.lineStyle.strokeColor; // 边框颜色
            me.lineWidth = $scope.lineStyle.width; //边框宽度
            me.lineType = $scope.lineStyle.type;
            me.textColor = $scope.lineStyle.fontColor;
            if ($scope.lineStyle.text) {
                me.text = $scope.lineStyle.text;
            }
            workflow.render();
        }

    }


//    复制规则
    $scope.copyRule=function () {
        localStorage.setItem('_ruleInfomartion',JSON.stringify($scope.billEdge));
        alert('复制成功');
    };
//    粘贴规则
    $scope.pasteRule=function () {
        var _ruleInfo=localStorage.getItem('_ruleInfomartion');
        if(_ruleInfo && _ruleInfo!==''){
            _ruleInfo=eval("("+_ruleInfo+")");
            if(workflow.selected.source.caption!==_ruleInfo.sourceBillKey && workflow.selected.target.caption!==_ruleInfo.targetBillKey){
                alert('目标单据与源单据信息不一致！')
            }else{
                $scope.billEdge=_ruleInfo;
            }
        }else{
            alert('转换规则为空，无法粘贴！')
        }


    }
});