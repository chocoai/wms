var workflowModule = angular.module('workflow', [ 'myApp.SharedDirective','myApp.WorkflowServices']);


/**
 * workflow.myLineControler
 * workflow.activityDefController
 * workflow.processControler
 */

workflowModule.controller('workflow.myLineControler', function ($scope) {
    
	$scope.line = {};


    $scope.$watch("line.name", function (newValue, oldValue, $scope) {
        if (oldValue && newValue!=oldValue) {
        	$scope.modeChange();
        	workflow.render();
        }
    });
    
	
    $scope.modeChange = function() {
    	$scope.line = workflow.selected;
    	var line=$scope.line.name;
         if(line==undefined)
         {
        	 $scope.line.name = workflow.selected.text;
         } 
        if($scope.line.name!=undefined){
        	workflow.selected.text = $scope.line.name;
        }
        $scope.$apply();
       
    };
    $.postOffice.subscribe("workflow.line.selected", $scope.modeChange,
        $scope);
});



workflowModule.controller('workflow.formDefController', function ($scope,FormDefService) {

	$scope.po = {};
    $scope.po.formAttr=[]; //表单属性定义
    $scope.po.formEntry=[]; //分录定义
    $scope.po.formTemplate=[]; //模板定义
 
    
    $scope.$watch("po.formDef.fid", function (newValue, oldValue, $scope) {
        if (oldValue && newValue!=oldValue) {
        	$scope.modeChange();
        	workflow.render();
        }
    });
    
    $scope.modeChange = function() {
    	
        $scope.po = workflow.selected.po;
        
        var formDef=$scope.po.formDef;
        if(formDef==undefined)
        {
        	$scope.po.formDef={};
        	$scope.po.formDef.fid = workflow.selected.text;
        }
        
        var type = workflow.selected.__getType(); //选中类型
        var typeFinal = type.substring(type.lastIndexOf(".")+1);
        $scope.type=typeFinal;
        
       if($scope.po.formDef!=null)
       {
        	workflow.selected.text = $scope.po.formDef.fid;
       }
        $scope.$apply();
    };
    
    $.postOffice.subscribe("workflow.activity.selected",$scope.modeChange, $scope);
    

     if(!$scope.addFormAttr){
         $scope.addFormAttr={};
     }
     
     if(!$scope.addEntry){
         $scope.addEntry={};
     }
       
     if(!$scope.addTemplate){
         $scope.addTemplate={};
     }
     
     $scope.addFormAttrsInfo=function(){
    	 if($scope.po.formAttr==undefined)
    	 {
    		 $scope.po.formAttr=[];
    	 }
         $scope.po.formAttr.push( $scope.addFormAttr);
         $scope.addFormAttr={};
         $('#my-popup-addFormAttr').modal("close");
     }
     
     $scope.addTemplatesInfo=function(){
    	
    	 if($scope.po.formTemplate==undefined)
    	 {
    		 $scope.po.formTemplate=[];
    	 }
         $scope.po.formTemplate.push( $scope.addTemplate);
         $scope.addTemplate={};
         $('#my-popup-addTemplates').modal("close");
     }
    
     $scope.addEntrysInfo=function(){
    	 var formName = $("#"+$scope.addEntry.formId).html();
    	 $scope.addEntry.formName=formName;
    	 if($scope.po.formEntry==undefined)
    	 {
    		 $scope.po.formEntry=[];
    	 }
         $scope.po.formEntry.push( $scope.addEntry);
         $scope.addEntry={};
         $('#my-popup-addJournalizing').modal("close");
     } 
});



workflowModule.controller('workflow.processControler', function ($scope ) {

});

