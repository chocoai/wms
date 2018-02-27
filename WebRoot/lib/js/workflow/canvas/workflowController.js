﻿
var workflowModule = angular.module('workflow', ['myApp.myDirective', 'myApp.WorkflowServices']);

/**
 * workflow.myLineControler
 * workflow.activityDefController
 * workflow.processControler
 *
 *
 */

workflowModule.controller('workflow.myLineControler', function ($scope, LineformService) {

    $scope.line = {};


    $scope.$watch("line.name", function (newValue, oldValue, $scope) {

        if (oldValue && newValue != oldValue) {
            $scope.modeChange();
            workflow.render();
        }
    });

    $scope.$watch("line.labelName", function (newValue, oldValue, $scope) {

        if (oldValue && newValue != oldValue) {
            $scope.modeChangeLabelName();
            workflow.render();
        }
    });

    $scope.saveLine = function () {
        var tid = workflow.cid2oid[workflow.selected.__getCID()];
        LineformService.postLine({
            tid: tid, name: $scope.line.name,
        }).success(
            function (responseData) {
                $('#processDef').modal('hide');
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
    }

    $scope.modeChange = function () {
        $scope.line = workflow.selected;
        var line = $scope.line.name;
        if (line == undefined) {
            $scope.line.name = workflow.selected.text;
        }
        if ($scope.line.name != undefined) {
            workflow.selected.text = $scope.line.name;
        }
        // $scope.$apply();

    };
    $scope.modeChangeLabelName = function () {
        $scope.line = workflow.selected;
        var line = $scope.line.labelName;
        if (line == undefined) {
            $scope.line.labelName = workflow.selected.labelName;
        }
        if ($scope.line.labelName != undefined) {
            workflow.selected.labelName = $scope.line.labelName;
        }
        $scope.$apply();

    };

    $.postOffice.subscribe("workflow.line.selected", $scope.modeChange,
        $scope);
});


workflowModule.controller('workflow.activityDefController', function ($scope, ActivityformService) {

    $scope.po = {};
    $scope.po.eventdefinition = [];//活动事件
    $scope.po.processuser = [];//活动用户定义
    $scope.po.activityform = [];//活动表单

    $scope.currentRow = {};
    ActivityformService.find({}).success(
        function (responseData) {
            $scope.currentRow.activityforms = responseData.activityforms;
            $scope.currentRow.processlist = responseData.processlist;
        }
    ).error(function (data, status, headers, config) {
        alert("连接错误，错误码：" + status);
    });


    $scope.$watch("po.activitydefinition.name", function (newValue, oldValue, $scope) {

        if (oldValue && newValue != oldValue) {
            $scope.modeChange();
            workflow.render();
        }
    });
    $scope.$watch("po.activitydefinition.labelName", function (newValue, oldValue, $scope) {

        if (oldValue && newValue != oldValue) {
            $scope.modeChangeLabelName();
            workflow.render();
        }
    });

    $scope.suxingclick = function () {
        // debugger;
        if (workflow.selected) {
            if (workflow.selected) {
                var adid = workflow.cid2oid[workflow.selected.__getCID()];
                ActivityformService.find2({
                    adid: adid,
                }).success(
                    function (responseData) {
                        $scope.po.activitydefinition = responseData.activitydefinition;
                        $scope.po.activitycontroller = responseData.activitycontroller;
                        $scope.po.eventdefinition = responseData.eventdefinition;
                        $scope.po.processuser = responseData.processuser;
                        $scope.po.activityform = responseData.activityform;
                    }
                ).error(function (data, status, headers, config) {
                    alert("连接错误，错误码：" + status);
                });
            }

        }

    }

    /**
     * 保存【活动属性设置】
     */
    $scope.saveActivityDef = function () {
        var shape = JSON.parse($scope.po.activitydefinition.shape);
        shape.text = $scope.po.activitydefinition.name;
        shape.labelName = $scope.po.activitydefinition.labelName;
        $scope.po.activitydefinition.shape = shape;

        ActivityformService.post({
            text: JSON.stringify($scope.po),
        }).success(function (responseData) {
                alert("保存成功！");
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $("#activityDef").modal('hide');
    };

    $scope.modeChange = function () {

        //$scope.po = workflow.selected.po;
        var activitydefinition = $scope.po.activitydefinition;
        if (activitydefinition == undefined) {
            $scope.po.activitydefinition = {};
            $scope.po.activitydefinition.name = workflow.selected.text;
        }

        if ($scope.po.activitydefinition != null) {

            workflow.selected.text = $scope.po.activitydefinition.name;
        }
        // $scope.$apply();
    };

    $scope.modeChangeLabelName = function () {

        var activitydefinition = $scope.po.activitydefinition;
        if (activitydefinition == undefined) {
            $scope.po.activitydefinition = {};
            $scope.po.activitydefinition.labelName = workflow.selected.labelName;
        }

        if ($scope.po.activitydefinition != null) {
            workflow.selected.labelName = $scope.po.activitydefinition.labelName;
        }
        $scope.$apply();
    };


    $scope.modeChange2 = function () {

        var type = workflow.selected.__getType(); //选中类型

        var typeFinal = type.substring(type.lastIndexOf(".") + 1);
        $scope.type = typeFinal;
    };

    $.postOffice.subscribe("workflow.activity.selected", $scope.modeChange2, $scope);


    if (!$scope.addEventdefinition) {
        $scope.addEventdefinition = {};
    }

    if (!$scope.addProcessuser) {
        $scope.addProcessuser = {};
    }

    if (!$scope.addActivityform) {
        $scope.addActivityform = {};
    }

    if (!$scope.editActivityform) {
        $scope.editActivityform = {};
    }
    if (!$scope.editProcessuser) {
        $scope.editProcessuser = {};
    }
    if (!$scope.editEventdefinition) {
        $scope.editEventdefinition = {};
    }


    //表单
    $scope.addActivityformInfo = function () {
        var formName = $("select[name='formId']").find("option:selected").text();
        $scope.addActivityform.formName = formName;
        if ($scope.po.activityform == undefined) {
            $scope.po.activityform = [];
        }
        $scope.po.activityform.push($scope.addActivityform);
        ActivityformService.post2({
            text: JSON.stringify($scope.addActivityform),
            flag: 'form',
            flag2: 'add',
            adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.addActivityform = {};
        //my-modal-addActivityform
        $('#my-modal-addActivityform').modal("hide");
    }

    $scope.afedit = function (form, index) {
        $scope.editActivityform = form;
        $('#afindexHidden').val(index);

        $('#my-modal-editActivityform').modal('show');
    }

    $scope.editActivityformInfo = function () {
        var index = $("#afindexHidden").val();
        $scope.editActivityform.formName = $("select[name='formId']").find("option:selected").text();
        $scope.po.activityform[index] = $scope.editActivityform;
        ActivityformService.post2({
            text: JSON.stringify($scope.editActivityform),
            flag: 'form',
            flag2: 'edit',
            adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.editActivityform = {};
        $('#my-modal-editActivityform').modal("hide");
    }

    /**
     * 保存、删除、编辑 【活动表单】的活动事件、活动用户、活动表单
     */
    $scope.afdelete = function (form, index) {
        debugger;
        ActivityformService.post2({
            id: form.id, flag: 'form', flag2: 'delete', adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.po.activityform.splice(index, 1);

    }
    //
    //用户
    $scope.addProcessuserInfo = function () {

        var name = $("#" + $scope.addProcessuser.uid).html();
        $scope.addProcessuser.name = name;
        if ($scope.po.processuser == undefined) {
            $scope.po.processuser = [];
        }
        ActivityformService.post2({
            text: JSON.stringify($scope.addProcessuser),
            flag: 'user',
            flag2: 'add',
            adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {
                $scope.addProcessuser.id = responseData.id;
                $scope.po.processuser.push($scope.addProcessuser);
                $scope.addProcessuser = {};
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $('#my-popup-addProcessuser').modal("hide");
    }

    $scope.delActivityForm = function (index) {
        var form = $scope.po.activityform[index];
        if (!form.id) {
            $scope.po.activityform.splice(index, 1);
        } else {

            ActivityformService.delActivityForm({
                id: form.id
            }).success(
                function (responseData) {

                    if (responseData.status == 1) {
                        $scope.po.activityform.splice(index, 1);
                    } else {
                        alert("操作失败，请重试！");
                    }

                }
            ).error(function (data, status, headers, config) {
                alert("操作发生异常");
            });
        }
    }


    $scope.psedit = function (process, index) {
        $scope.editProcessuser = process;
        $('#puindexHidden').val(index);
        $('#my-popup-editProcessuser').modal('show');
    }

    $scope.editProcessuserInfo = function () {
        var index = $("#puindexHidden").val();
        $scope.editProcessuser.name = $("#" + $scope.editProcessuser.uid).html();
        $scope.po.processuser[index] = $scope.editProcessuser;
        ActivityformService.post2({
            text: JSON.stringify($scope.editProcessuser),
            flag: 'user',
            flag2: 'edit',
            adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.editProcessuser = {};
        $('#my-popup-editProcessuser').modal("hide");
    }

    $scope.psdelete2 = function (process, index) {
        ActivityformService.post2({
            id: process.id, flag: 'user', flag2: 'delete', adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.po.processuser.splice(index, 1);
    }
    //
    //事件
    $scope.addEventInfo = function () {
        var id = workflow.pdId;
        var adid = workflow.cid2oid[workflow.selected.__getCID()];
        if ($scope.po.eventdefinition == undefined) {
            $scope.po.eventdefinition = [];
        }
        //往活动事件表中插值
        ActivityformService.post2({
            text: JSON.stringify($scope.addEventdefinition), flag: 'event', flag2: 'add', adid: adid, pid: id
        }).success(
            function (responseData) {
                $scope.addEventdefinition.id = responseData.id;
                $scope.po.eventdefinition.push($scope.addEventdefinition);
                $scope.addEventdefinition = {};
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $('#my-popup-addEvent').modal("hide");
    }

    $scope.ededit = function (event, index) {
        $scope.editEventdefinition = event;
        $('#edindexHidden').val(index);
        $('#my-popup-editEvent').modal('show');
    }
    $scope.editEventInfo = function () {
        var adid = workflow.cid2oid[workflow.selected.__getCID()];
        var index = $("#edindexHidden").val();
        $scope.po.eventdefinition[index] = $scope.editEventdefinition;
        //往活动事件表中更新
        ActivityformService.post2({
            text: JSON.stringify($scope.editEventdefinition),
            flag: 'event',
            flag2: 'edit',
            id: $scope.editEventdefinition.id,
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.editEventdefinition = {};
        $('#my-popup-editEvent').modal("hide");
    }

    $scope.eddelete = function (event, index) {
        //往活动事件表中删除
        ActivityformService.post2({
            id: event.id, flag: 'event', flag2: 'delete', adid: workflow.cid2oid[workflow.selected.__getCID()],
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.po.eventdefinition.splice(index, 1);

    }
    //

    $scope.loadActivityDef = function (adid) {
        alert(adid);
    }

});


workflowModule.controller('workflow.processControler', function ($scope, ProcessFormService) {

    var id = workflow.pdId;
    $scope.currentRow = {};

    $scope.addProcessform = {};
    $scope.editProcessform = {};
    if (!$scope.addEventdefinition) {
        $scope.addEventdefinition = {};
    }
    if (!$scope.editEventdefinition) {
        $scope.editEventdefinition = {};
    }
    ProcessFormService.findpd({
        id: id,
    }).success(
        function (responseData) {

            $scope.currentRow.processdefinition = responseData.processdefinition;
            $scope.currentRow.processform = responseData.processform;
            $scope.currentRow.activityforms = responseData.activityforms;
            $scope.currentRow.eventdefinition = responseData.eventdefinition;
        }
    ).error(function (data, status, headers, config) {
        alert("连接错误，错误码：" + status);
    });

    $scope.addProcessformInfo = function () {
        var pdId = workflow.pdId;
        var formName = $("select[name='formId']").find("option:selected").text();
        $scope.addProcessform.formName = formName;
        $scope.addProcessform.pdId = pdId;
        ProcessFormService.saveOrUpdateProcessform({
            processform: angular.toJson($scope.addProcessform)
        }).success(
            function (responseData) {
                if (responseData.status == 1) {
                    $scope.addProcessform = responseData.form
                    $scope.currentRow.processform.push($scope.addProcessform);
                    $scope.addProcessform = {};
                }
            }
        ).error(
            function (data, status, headers, config) {
                alert("操作失败，请重试！");
            }
        );
        $('#my-popup-addProcessform').modal("hide");
    }

    /**
     * 保存【流程属性设置】
     */
    $scope.updateProcess = function () {
        var tidName = $("#" + $scope.currentRow.processdefinition.tid).html();
        $scope.currentRow.processdefinition.tidName = tidName;
        ProcessFormService.postU({
            processdefinition: angular.toJson($scope.currentRow.processdefinition),
            processform: angular.toJson($scope.currentRow.processform),
        }).success(
            function (responseData) {
                if (responseData.status == 1) {

                } else {
                    alert("操作失败，请重试！");
                }
                $("#processDef").modal('hide');
            }
        ).error(function (data, status, headers, config) {

        });
    }

    $scope.afedit = function (form, index) {
        $scope.editProcessform = form;
        $('#afindexHidden').val(index);
        $('#my-popup-editProcessform').modal('show');
    }

    $scope.editProcessformInfo = function () {
        var index = $("#afindexHidden").val();
        $scope.editProcessform.formName = $("select[name='formId']").find("option:selected").text();
        $scope.currentRow.processform[index] = $scope.editProcessform;
        ProcessFormService.saveOrUpdateProcessform({
            processform: angular.toJson($scope.editProcessform)
        }).success(
            function (responseData) {
            }
        ).error(
            function (data, status, headers, config) {
                alert("操作失败，请重试！");
            }
        );
        $scope.editProcessform = {};
        $('#my-popup-editProcessform').modal("hide");
    }

    $scope.delProcessForm = function (index) {

        var form = $scope.currentRow.processform[index];
        //新增表单
        if (!form.id) {
            $scope.currentRow.processform.splice(index, 1);
            return;
        }

        ProcessFormService.delProcessForm({
            id: form.id
        }).success(
            function (responseData) {

                if (responseData.status == 1) {
                    $scope.currentRow.processform.splice(index, 1);
                } else {
                    alert("操作失败，请重试！");
                }

            }
        ).error(function (data, status, headers, config) {
            alert("操作失败，请重试！");
        });

    }

    $scope.addEventInfo = function () {
        var id = workflow.pdId;
        var adid = '';

        if ($scope.currentRow.eventdefinition == undefined) {
            $scope.currentRow.eventdefinition = [];
        }
        //往活动事件表中插值
        ProcessFormService.processEvent({
            text: JSON.stringify($scope.addEventdefinition), flag: 'event', flag2: 'add', adid: adid, pid: id
        }).success(
            function (responseData) {
                $scope.addEventdefinition.id = responseData.id;
                $scope.currentRow.eventdefinition.push($scope.addEventdefinition);
                $scope.addEventdefinition = {};
            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });

        $('#my-popup-addProcessdeEvent').modal("hide");
    }

    $scope.processdeEdit = function (event, index) {
        $scope.editEventdefinition = event;
        $('#edindexHidden').val(index);
        $('#my-popup-editProcessdeEvent').modal('show');
    }

    $scope.editEventInfo = function () {
        var adid = '';
        var index = $("#edindexHidden").val();
        $scope.currentRow.eventdefinition[index] = $scope.editEventdefinition;
        //往活动事件表中更新
        ProcessFormService.processEvent({
            text: JSON.stringify($scope.editEventdefinition), flag: 'event', flag2: 'edit', id: id,
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.editEventdefinition = {};
        $('#my-popup-editProcessdeEvent').modal("hide");
    }

    $scope.processdeDel = function (event, index) {
        //往活动事件表中删除
        ProcessFormService.processEvent({
            id: event.id, flag: 'event', flag2: 'delete',
        }).success(
            function (responseData) {

            }
        ).error(function (data, status, headers, config) {
            alert("连接错误，错误码：" + status);
        });
        $scope.currentRow.eventdefinition.splice(index, 1);
    }

});

