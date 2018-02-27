var fileTypeInit = function (_cmbProvince, _cmbCity, defaultProvince, defaultCity) {
    var cmbProvince = document.getElementById(_cmbProvince);
    var cmbCity = document.getElementById(_cmbCity);
    function cmbSelect(cmb, str) {
        for (var i = 0; i < cmb.options.length; i++) {
            if (cmb.options[i].value == str) {
                cmb.selectedIndex = i;
                return;
            }
        }
    }

    function cmbAddOption(cmb, str, obj) {
        var option = document.createElement("OPTION");
        cmb.options.add(option);
        option.innerHTML = str;
        option.value = str;
        option.obj = obj;
    }

    function changeProvince() {
        cmbCity.options.length = 0;
        cmbCity.onchange = null;
        if (cmbProvince.selectedIndex == -1)return;
        var item = cmbProvince.options[cmbProvince.selectedIndex].obj;
        for (var i = 0; i < item.cityList.length; i++) {
            cmbAddOption(cmbCity, item.cityList[i], item.cityList[i]);
        }
        cmbSelect(cmbCity, defaultCity);
    }

    for (var i = 0; i < provinceList.length; i++) {
        cmbAddOption(cmbProvince, provinceList[i].name, provinceList[i]);
    }
    cmbSelect(cmbProvince, defaultProvince);
    changeProvince();
    cmbProvince.onchange = changeProvince;
}

var provinceList = [
    {name: '个人资料', cityList: ['身份证', '户口本', '结婚证', '储蓄卡', '其他']},
    {name: '资产信息', cityList: ['房产证', '购房合同', '车辆行车证', '其他']},
    {name: '经营信息', cityList: ['营业执照', '税务登记证', '组织机构代码证', '开户许可证', '信用机构代码证', '公司章程', '验资报告', '场地租赁合同', '水电费发票', '上游合同及凭证', '流水', '其他']},
    {name: '信用信息', cityList: ['申请人征信', '配偶征信', '共同还款人征信', '其他']},
    {name: '家访照片', cityList: ['家庭环境照片', '单位环境照片', '其他']},
    {name: '合同签署照片', cityList: ['文本合同签字照片', '其他']},
    {name: '反担保资料照片', cityList: ['反担保资料照片', '其他']}
];
var imageType={    "jpg":1,    "jpeg":1,    "JPG":1,    "JPEG":1,    "png":1,    "PNG":1,    "gif":1,    "GIF":1,    "bmp":1,    "BMP":1,    "doc":1,    "docx":1,    "DOC":1,    "DOCX":1,    "xls":1,    "xlsx":1,    "XLS":1,    "XLSX":1,    "ppt":1,    "PPT":1,    "pptx":1,    "PPTX":1,    "pdf":1,    "PDF":1,    "txt":1,    "TXT":1,    "wps":1,    "WPS":1,    "et":1,    "ET":1,    "dps":1,    "DPS":1};

//
$(function () {
    $('.am-gallery-item img').each(function (i,item) {
        var _thisSrc=$(item).attr('src');
        var _imgType=_thisSrc.split('.');
        var _thisType=_imgType[_imgType.length-1];
        console.log(_thisType);
        var fileName = "";
        for(var i = 0; i<_imgType.length -1; i++){
        	fileName += _imgType[i] + ".";
        }
        fileName = fileName.substring(0, fileName.length -1);
        if(_thisType=='png' || _thisType=='jpg' || _thisType=='bmp' || _thisType=='jpeg' || _thisType=='PNG' || _thisType=='JPG' || _thisType=='BMP' || _thisType=='JPEG'){
            $(item).attr('src', fileName + "_thumb." + _thisType );
        }else{
            _thisType.toLowerCase();
            if(_thisType=='doc'|| _thisType=='docx'|| _thisType=='wps'){
                $(item).attr('src','/template/web/images/doc.jpg')
            }else if(_thisType=='xls'|| _thisType=='xlsx'|| _thisType=='et'){
                $(item).attr('src','/template/web/images/xls.jpg')
            }else if(_thisType=='ppt'|| _thisType=='pptx'|| _thisType=='dps'){
                $(item).attr('src','/template/web/images/ppt.jpg')
            }else if(_thisType=='pdf'){
                $(item).attr('src','/template/web/images/pdf.jpg')
            }else if(_thisType=='txt'){
                $(item).attr('src','/template/web/images/txt.jpg')
            }else{
                $(item).attr('src','/template/web/images/weizhi.jpg')
            }
        }
    });
    var rotateIndex=0;
    $('.am-rotate').on('click',function(){
        rotateIndex = (rotateIndex+90)%360;
        $('.am-pureview-slider li.am-active img').css("transform", "rotate("+rotateIndex+"deg)")
    });
});

var typeFlag = 1;
function toVaild(){
    $.AMUI.progress.start(); //遮罩层弹出
    if($("#firstType").val() == null || $("#firstType").val() == ""){
        $.AMUI.progress.done(); //遮罩层关闭
        alert("请选择一级分类！");
        return false;
    }
    if($("#secondType").val() == null || $("#secondType").val() == ""){
        $.AMUI.progress.done(); //遮罩层关闭
        alert("请选择二级分类！");
        return false;
    }
    if($("#attach").val() == null || $("#attach").val() == ""){
        $.AMUI.progress.done(); //遮罩层关闭
        alert("请选择上传文件！");
        return false;
    }
    
    if(eval(typeFlag) == 0){
        $.AMUI.progress.done(); //遮罩层关闭
        alert("文件上传格式不正确！");
        return false;
    }else if(eval(typeFlag) == 2){
        $.AMUI.progress.done(); //遮罩层关闭
        alert("单次上传文件最大支持10M！");
        return false;
    }
    // 	return true;
}

$("#uploadForm").ajaxForm(function(data){
	if(data.status == 1){
		$.ajax({
            type : "POST",
            url : "/process/saveAttach",
            data : "attachList="+data.attachList,
            async : false,
            success : function(data1) {
                if (data1.status == 1) {
                	$("#uploadFile").find("select,textarea,input[type='file'],input[type='text'],input[type='number'],input[type='tel'],input[type='email'],input[type='date']").each(function(index,item){
                        item.value="";
                    });
                    $.AMUI.progress.done(); //遮罩层关闭
                    workflowFile();
                    alert("上传成功");
                }else{
                	$.AMUI.progress.done();
                	alert("上传失败");
                }
            }
        });
	}else{
		$.AMUI.progress.done(); //遮罩层关闭
		alert("上传失败");
	}
});

function upload() {
    var names = $("#attach").val().split('.');
    var nameLen= names.length-1;
    var fileName=document.getElementById("attach");
    var fileSize = fileName.files;
    var fileSizeKb=0;
    for(var i=0;i<fileSize.length;i++){
        fileSizeKb += fileSize[i].size;
    }
    var fileSizeMb = fileSizeKb/1024/1024;//转换为kb
    console.log(fileSizeMb);
    if (imageType[names[nameLen]]!= '1') {
        $("#error").html("请上传以下格式文件：<br>jpg、png、doc、docx、xls、xlsx、ppt、pptx、pdf、txt、wps、et、dps");
        $("#error").show();
        typeFlag = 0;
        return;
    }else if(fileSizeMb>10){
        $("#error").html("单次上传文件最大支持10M");
        $("#error").show();
        typeFlag = 2;
        return;
    }else{
        $("#error").hide();
        typeFlag = 1;
    }
}

function deleteAttach(id){
    $.AMUI.progress.start(); //遮罩层关闭
    var a=confirm("确定删除附件？");
    if(a==true)
    {
        $.ajax({
            type : "POST",
            url : "/process/deleteAttach",
            data : "id="+id,
            async : false,
            success : function(data) {
                $.AMUI.progress.done(); //遮罩层关闭
                if (data.msg == "SUCCESS") {
                    alert("删除成功");
                }else{
                    alert(data.msg);
                }
            }
        });
    }else{
        $.AMUI.progress.done(); //遮罩层关闭
    }
    workflowFile();
}