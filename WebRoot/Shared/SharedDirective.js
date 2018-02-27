var SharedDirectiveModule = angular.module('myApp.SharedDirective', ['myApp.SharedService']);


//通用  注入js&css链接到head部分方法
SharedDirectiveModule.factory('Common', [
    '$http', '$q', function ($http, $q) {
        return {
            loadScript: function (url, callback) {
                var head = document.getElementsByTagName("head")[0];
                var script = document.createElement("script");
                script.setAttribute("type", "text/javascript");
                script.setAttribute("src", url);
                script.setAttribute("async", true);
                script.setAttribute("defer", true);
                head.appendChild(script);
                //ie
                if (document.all) {
                    script.onreadystatechange = function () {
                        var state = this.readyState;
                        if (state === 'loaded' || state === 'complete') {
                            callback && callback();
                        }
                    }
                }
                else {
                    //firefox, chrome
                    script.onload = function () {
                        callback && callback();
                    }
                }
            },
            loadCss: function (url) {
                var ele = document.createElement('link');
                ele.href = url;
                ele.rel = 'stylesheet';
                if (ele.onload == null) {
                    ele.onload = function () {
                    };
                }
                else {
                    ele.onreadystatechange = function () {
                    };
                }
                angular.element(document.querySelector('body')).prepend(ele);
            }
        }
    }
]);


/**
 * 分页指令
 * 其中：page-info指向父类变量
 *     //页面有关的信息
 $scope.pageInfo = {
                pageSize: 1,
                totalCount: 0,
                pageCount: 0,
                pageIndex: 1,
                pageItems: [1, 2, 3],
                pageStart: 1,
                pageEnd: 3
            };
 * load指向当页面变动时，需要加载页面的方法（如果页面变量是作为搜索条件一部分使用）
 * <div paging  page-info="pageInfo"  load="load()"/>
 *
 *
 */
SharedDirectiveModule.directive('paging', function () {
    return {
        restrict: 'A',
        scope: {
            pageInfo: "=",
            load: "&"
        },
        templateUrl: "/Views/_paging.html",
        controller: function ($scope, $element, $attrs, $transclude) {//指令的控制器
            var offsetPageInfo = function () {
                $scope.pageInfo.pageItems = [];
                for (var i = $scope.pageInfo.pageStart; i <= $scope.pageInfo.pageEnd; i++) {
                    $scope.pageInfo.pageItems.push(i);
                }
            };

            $scope.currentPage = function (page) {
                $scope.pageInfo.pageIndex = page;
                $scope.load();
            };

            $scope.prePage = function () {
                if ($scope.pageInfo.pageIndex > 1) {
                    $scope.pageInfo.pageIndex = Number($scope.pageInfo.pageIndex) - 1;
                    if ($scope.pageInfo.pageIndex < $scope.pageInfo.pageStart) {
                        //整体向左偏移
                        $scope.pageInfo.pageStart = Number($scope.pageInfo.pageStart) - 1;
                        $scope.pageInfo.pageEnd = Number($scope.pageInfo.pageEnd) - 1;
                        offsetPageInfo();
                    }
                    $scope.load();
                }
            };
            $scope.nextPage = function () {
                if ($scope.pageInfo.pageIndex < $scope.pageInfo.pageCount) {
                    $scope.pageInfo.pageIndex = Number($scope.pageInfo.pageIndex) + 1;
                    if ($scope.pageInfo.pageIndex > $scope.pageInfo.pageEnd) {
                        //整体向右偏移
                        $scope.pageInfo.pageStart = Number($scope.pageInfo.pageStart) + 1;
                        $scope.pageInfo.pageEnd = Number($scope.pageInfo.pageEnd) + 1;
                        offsetPageInfo();
                    }

                    $scope.load();
                }
            };
            $scope.toPage = function () {
                var toPage = $element.find("#toPageIndex").val();
                // var toPage =$("#toPageIndex").val();
                if (toPage <= $scope.pageInfo.pageCount && toPage > 0) {
                    $scope.pageInfo.pageIndex = toPage;

                    if (Number(toPage) + 10 <= $scope.pageInfo.pageCount) {
                        $scope.pageInfo.pageStart = toPage;
                        $scope.pageInfo.pageEnd = Number(toPage) + 9;
                    } else {
                        if ((Number(toPage) - (9 - ($scope.pageInfo.pageCount - Number(toPage)))) < 1) {
                            $scope.pageInfo.pageStart = 1;
                        } else {
                            $scope.pageInfo.pageStart = Number(toPage) - (9 - ($scope.pageInfo.pageCount - Number(toPage)));
                        }
                        $scope.pageInfo.pageEnd = $scope.pageInfo.pageCount;
                    }
                    offsetPageInfo();
                    $scope.load();
                }
                else if (!toPage) {
                    alert("请输入跳转页数");
                }
                else if (toPage <= 0) {
                    alert("页面为正整数");
                } else {
                    alert("您输入的页数超过了最大页数，请重试");
                }
            };

        }
    };
});


/**
 * 单选框指令
 */
SharedDirectiveModule.directive('radiogroup', function () {
    return {
        restrict: 'A',
        scope: {
            checkModel: "=",
            action: "@",
            key: "@",
            value: "@",
            fields: "@",
            heads: "@",
            searchs: "@",
        },
        templateUrl: "/Views/_radiogroup.html",
        link: function ($scope, $element, $attrs, common_service) {//链接函数
            //监控数据模型的变化
            $scope.$watch("checkModel", function (newValue, oldValue, scope) {
                $scope.initDom();
            });
            $scope.$watch("$currentData", function (newValue, oldValue, scope) {
                if (newValue) {
                    $scope.initDom();
                }
            });
            //初始时，链接对应的DOM结构
            $scope.initDom();

        },

        controller: function ($scope, $element, $attrs, $transclude, common_service) {//指令的控制器

            if (!$scope.$currentData) {
                common_service.list($scope.action).success(
                    function (responseData) {
                        $scope.$currentData = responseData;
                    }
                ).error(function (data, status, headers, config) {
                        alert("连接错误，错误码：" + status);
                    });
            }
            $scope.tid = "T" + GUID();
            $scope.pid = "F" + GUID();
            $scope.head_list = $scope.heads.split(",");
            $scope.fields_list = $scope.fields.split(",");
            if($scope.searchs!=undefined)
            {
            	$scope.search_list = $scope.searchs.split(";");
            }
                    
            $scope.check_arr = [];
            //初始化dom,如果dom不存在
            $scope.initDom = function () {
                if ($scope.$currentData) {
                    $scope.createDom();

                }
            }

            $scope.createDom = function () {
                $element.find("#" + $scope.tid).empty();
                //初始显示值
                if ($scope.checkModel) {
                	$scope.checkModel2={};
                	$scope.checkModel2.value= $scope.checkModel;
                	$scope.checkModel2.valueStr= $scope.checkModel.toString();
                    var t_checkes = $scope.checkModel2.valueStr.split(",");
                    $scope.check_arr = t_checkes;
                } else {
                    $scope.check_arr = [];
                    var t_checkes = [];
                }
                for (var i = 0; i < t_checkes.length; i++) {
                    var t_key = t_checkes[i];//获取key
                    var t_value = $scope.findValue(t_key);
                    if (t_value) {
                        if (i == t_checkes.length - 1) {
                            $element.find("#" + $scope.tid).append("<span class='checkboxgroud'>" + t_value[$scope.value] + "</span>");

                        } else {
                            $element.find("#" + $scope.tid).append("<span class='checkboxgroud'>" + t_value[$scope.value] + ",</span>");
                        }
                    }
                }


                $element.find("#" + $scope.pid).off("change");
                $element.find("#" + $scope.pid).on("change", "input[type='radio']", function () {
                    var $this = $(this);
                    var result = $this.prop("checked");
                    var value = $this.attr("value");
                    
                    $scope.checkModel=value;
                    $scope.$apply();

                });
            }

            //查询对应的value
            $scope.findValue = function (key) {
                if ($scope.$currentData) {
                    for (var i = 0; i < $scope.$currentData.length; i++) {
                        if ($scope.$currentData[i][$scope.key] == key) {
                            return $scope.$currentData[i];
                        }
                    }
                }
            };
            $scope.test = function () {
                $("#" + $scope.pid).modal();
            }

            $scope.close = function () {
                $("#" + $scope.pid).modal('hide');
            }
            
            $scope.sure = function () {
            	var sear_code= "";
            	var sear_value = "";
            	
            	for(var i = 0; i < $scope.search_list.length; i++)
            	{
            		
            		sear_code += $scope.search_list[i].split(',')[1]+",";
            		sear_value += $("#"+$scope.search_list[i].split(',')[1]).val()+",";

            	}
            	//$scope.check_arr = [];
            	//$element.find("#" + $scope.tid).empty();
            	$scope.check_arr.length=0;
            	$scope.searchCondition = {
            		sear_code: sear_code,
            		sear_value:sear_value,
            	};
            	var postData = $scope.searchCondition;
    
                 	common_service.list2(postData,$scope.action).success(
                        function (responseData) {
                            $scope.$currentData = responseData;
                            if ($scope.$currentData) {
                            
                                $scope.createDom();

                            }
                        }
                    ).error(function (data, status, headers, config) {
                            alert("连接错误，错误码：" + status);
                        });
            }
            
            $scope.mySplit = function(string, nb) {
                var array = string.split(',');
                return array[nb];
            }
        }
    }
});



//树指令

// 树 billuitree
SharedDirectiveModule.directive('xyyztree', function ($http, $q, Common) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        // template: "<div class=\"ztree\" treemodel=\"treemodel\" treeoptions=\"treeoptions\" ></div>",
        scope: {
            bound: "@",//
            triggers: "@",
            checkRules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            tableKey: "@",
            treeoptions: "=",
            treemodel: "=",
            multiselect: "@",
            onSelection:"&",
            broadcastName:"@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用

            scope._$curTreeNode = null;
            //混合器，指令初始代码
            // mixer(abstractDirective, scope);
            ////树节点click的时候抛出事件以及当前节点
            scope.zTreeOnClick = function (event, treeId, treeNode) {
                var _treeNode = scope.tree.getSelectedNodes();
                scope.$emit(scope.broadcastName,_treeNode);
                //向上冒泡，广播名称由指令行上定义名称；
                scope.$apply(function () {
                    //将当前选中的节点放到ngModel中，控制器中直接取当前的ngModel
                    if (!scope._$curTreeNode || scope._$curTreeNode.tId != _treeNode) {
                        scope.ngModel.$setViewValue(_treeNode);
                        scope._$curTreeNode = _treeNode;
                    }

                });
            };
            //删除节点确认，具有子节点的节点禁止删除。
            scope.zTreeBeforeRemove = function (treeId, treeNode) {
                if (treeNode.children && treeNode.children.length > 0) {
                    alert('子节点不为空，禁止删除');
                    return false;
                } else {
                    return confirm("确定删除" + treeNode.name + " ？")
                }
            };
            //重命名节点 回调函数
            scope.zTreeOnRename = function (event, treeId, treeNode, isCancel) {

            };

            //拖拽结束事件
            scope.onDrop = function (event, treeId, treeNodes, targetNode, moveType) {

            };

            //拖拽放下前事件
            scope.beforeDrop = function (treeId, treeNodes, targetNode, moveType) {
                if (moveType == 'inner' && targetNode.nodeType == '2') {
                    alert(targetNode.name + '为明细节点，不能移入')
                    return false;
                }
            };

            //tree
            //    树默认配置，可在控制器中覆盖默认配置

            scope.treemodel = [];
            //设置是否允许多选
            var _multiselect = false;
            if (attrs.multiselect == 'true') {
                _multiselect = true;
            } else {
                _multiselect = false;
            };
            scope.treeoptions = {
                view: {
                    selectedMulti: _multiselect
                },
                edit: {
                    drag: {
                        autoExpandTrigger: true,
                    },
                    enable: true,
                    showRemoveBtn: false,
                    showRenameBtn: false
                },
                check: {
                    enable: false,
                    autoCheckTrigger: true,
                    chkStyle: "checkbox",
                    radioType: "all",    // "level" 时，在每一级节点范围内当做一个分组。radioType = "all" 时，在整棵树范围内当做一个分组。
                    chkboxType: {"Y": "ps", "N": "ps"}
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: scope.zTreeOnClick,
                    beforeRemove: scope.zTreeBeforeRemove,
                    onRename: scope.zTreeOnRename,
                    onDrag: scope.onDrag,
                    onDrop: scope.onDrop,
                    beforeDrop: scope.beforeDrop,
                    onExpand: scope.onExpand

                }
            };


            //递归,初始化的时候回显selected
            function _initSelected(node) {
                // status   :  0 禁用  1可用
                if (node && node.length) {
                    // var _tid=node[j].tId;
                    // angular.element(_tid).addClass();
                    for (var j = 0; j < node.length; j++) {
                        scope.setTreeClass(node[j]);
                        var _childs = node[j].children;
                        if (_childs.length > 0) {
                            for (var i = 0; i < _childs.length; i++) {
                                _initSelected(_childs[i]);
                            }
                        }
                    }
                } else {
                    scope.setTreeClass(node);
                    var _childs = node.children;
                    if (_childs.length > 0) {
                        for (var i = 0; i < _childs.length; i++) {
                            _initSelected(_childs[i]);
                        }
                    }
                }
                //
            };

            scope.setTreeClass = function (node) {
                // 0:草稿  10：启用   20：禁用   30：删除
                var _tid = node.tId + '_a';
                if (node.status == 0) {
                    angular.element('#' + _tid).addClass('disabled');
                } else if (node.status == 20) {
                    angular.element('#' + _tid).addClass('draft');
                }
            };

            //树初始化
            scope._initTree = function () {
                $.fn.zTree.init(element, scope.treeoptions, scope.treemodel);
                var treeID = attrs.id;
                var _nodes = scope.treemodel;
                scope.tree = $.fn.zTree.getZTreeObj(treeID);
                var __node = scope.tree.getNodes();
                scope.getSelectedNodes = function () {
                    return scope.tree.getSelectedNodes();
                };
                scope.getCheckedNodes = function () {
                    return scope.tree.getCheckedNodes();
                };
                scope.selectNode = function (node, addFlag, isSilent) {
                    return scope.tree.selectNode(node, addFlag, isSilent);
                };
                scope.getNodes = function () {
                    return scope.tree.getNodes();
                };
                // _initSelected(__node);
            };

            //默认不初始化树，在控制器中进行调用load初始化。
            scope.loadTree = function () {
                if (typeof $.fn.zTree != 'undefined') {
                    scope._initTree();
                } else {
                    try {
                        Common.loadCss('/lib/css/zTreeStyle/zTreeStyle.css', null);
                        Common.loadScript('/lib/js/jquery.ztree.all.js', function () {
                            scope._initTree();
                        });
                    } catch (e) {
                        alert('树资源没有正确加载！')
                    }
                }
            };

            scope.bindRowsModel = function () {
                scope.loadTree();
            };

            scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                scope.bindRowsModel();
            });
        }
    }
});


function GUID() {
    var S4=function(){
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    }
    return (S4()+S4()+S4()+S4()+S4()+S4()+S4()+S4());
};
