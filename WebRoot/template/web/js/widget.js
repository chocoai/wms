/**
 * Created by other on 2016/1/12.
 */
/**
 * 处理dom创建
 */
$.postOffice.subscribe("new_dom_created",function(data){

	var _this = $('div[data-tab]');
	var flag = _this.parent().attr('id');
	if(flag=='viewTpl')
	{
		loadProduct2();
	}else
	{
		loadProduct();
	}
    loadUserInfo();
    var dataScript = data.find("div[data-script]");
    var dataTab = data.find("div[data-tab]");
    var dataWidget = data.find("*[data-widget]");
    var dataValidate = data.find("*[data-validate]");
    var dataValidateStr = data.find("*[data-validateStr]");
    var dataMultipliable = data.find("*[data-multipliable]");

    if(dataScript.length>0){
        var url=dataScript.attr('data-script');
        var ns=dataScript.attr('data-script-ns');
        loadJS(url,ns);
    }
    if(dataTab.length>0){
        createTab();
    }
    if(dataWidget.length>0){
        createWidget();
    }
    if(dataValidate.length>0){
        checkValidate();
    }
    if(dataValidateStr.length>0){
        checkNotNull();
    }
    if(dataMultipliable.length>0 && dataMultipliable.attr('data-multipliable')=='yes'){
        //cloneForm();
    }
});
function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            oldonload();
            func();
        }
    }
}

//展开收起
function toggle(icon){
    var _body=$(icon).parents('.am-panel-hd').siblings('.am-panel-bd');
    if(_body.is(":hidden")){
        _body.show();
        $(icon).text('-');
    }else{
        _body.hide();
        $(icon).text('+');
    }
}
//新增多份表单
function cloneForm(){
    var _this=$('div[data-multipliable]');
    var _addBtn='<button class="button am-btn-sm am-btn-success" onclick="addForm(this)">新增</button>';
    _this.append(_addBtn);
}

function addForm(addBtn){
    var _body=  $(addBtn).siblings('.copyContent').html();
    var _delBtn='<button class="button am-btn-sm am-btn-warning" onclick="delItem(this)">删除</button>';
    $('#viewTpl div[data-multipliable]').append(_body);
    $(addBtn).siblings('.am-panel').find('.delWrap').html(_delBtn);
    checkValidate();
}

function delItem(del){
    $(del).parents('.am-del').remove();
}

function createMenu(id){
    var _this = $('div[data-tab]');
    var dataLen = $('#'+id+'  div[data-tab]').length;
    var dataArray = new Array();
    for (var i = 0; i < dataLen; i++) {
        var dataAttr = _this.eq(i).attr('data-tab');
        dataArray.push(dataAttr);
    };
    $('#'+id+' div[data-tab]:nth-child(n+1)').css('display','none');
    _this.eq(0).css('display','block');
    //console.log(dataArray);
    if(dataLen>1){
        var html = '';
        html+='<ul class="tab-ul clearfix">';
        for (var i = 0; i < dataArray.length; i++) {
            html += '<li>' + dataArray[i] + '</li>';//循环生成html
        };
        html+='</ul>';
        $('#'+id ).prepend(html);
        $('#'+id+'  .tab-ul li:nth-child(1)').addClass('curr');
    }
}
//创建tab
function createTab(){
    var _this = $('div[data-tab]');
    if(_this.parent().attr('id')=='operateForm'){
        createMenu('operateForm');
    }else if(_this.parent().attr('id')=='viewTpl'){
        createMenu('viewTpl');
    }
    $('.tab-ul li').on('click', function() {
        var ti=$(this).index();
        $(this).addClass('curr').siblings('li').removeClass('curr');
        $(this).parent('.tab-ul').siblings('div[data-tab]').eq(ti).show().siblings('div[data-tab]').hide();
    });
};
//用户信息加载
function loadUserInfo(){
	var pid = $("input[name='personInfo_id']").val();
	
	if(pid==null || pid==""){
		return;
	}
	var formData={};
    formData["personId"]=pid;
	$.post("/process/loadUserInfo", formData, function (data) {
		
		    loadData("#personInfo","kinsfolk",data.KinsfolkList);
			loadData("#personInfo","OtherPerson",data.OtherPersonList);//loadData 方法在 tem 文件夹下 tableDataUtil.js 中定义
			loadData("#personInfo","WorkInfo",data.PersonWorkList);//
			loadData("#personFinance","PersonHouseInfo",data.PersonHousenList);//
			loadData("#personFinance","PersonCarInfo",data.PersonCarList);//
			loadData("#personFinance","PersonDebtInfo",data.PersonDebtList);//
			loadData("#personFinance","PersonInsuranceInfo",data.PersonInsuranceList);//
			loadData("#personFinance","PersonStockInfo",data.PersonStockList);//
			loadData("#personFinance","PersonTaxInfo",data.PersonTaxList);//
			loadData("#personFinance","PersonOtherAssetInfo",data.PersonOtherAssetList);//
			
   });
}
//加载所有可用的产品
function loadProduct2(){
	var formData={};
	$.post("/process/loadAllProductNoShowBack", formData, function (data) {
		$('.order_productName').attr('data-widget-title',data.order_product_name);
		$('.order_productName').attr('data-widget-value',data.order_product_id);
        createSelect();
   });
}
function loadProduct(){
	var formData={};
	$.post("/process/loadAllProduct", formData, function (data) {
		$('.order_productName').attr('data-widget-title',data.order_product_name);
		$('.order_productName').attr('data-widget-value',data.order_product_id);
        createSelect();
   });
}
//
function createWidget(){
    var _this=$('div[data-widget]');
    var dataLen = _this.length;
    var dataArray = new Array();
    for (var i = 0; i < dataLen; i++) {
        var dataAttr = _this.eq(i).attr('data-widget-type');
        dataArray.push(dataAttr);
    };
    if (dataArray.indexOf("select")>-1) {
        createSelect();
    }
    if (dataArray.indexOf("address")>-1 ) {
        createAddress();
    }
}
/*
  locadjs
 */
function loadJS(url,ns){
    //var _this=$('div[data-script]');
    //var _thisNs=$('div[data-script-ns]');
    jQuery.getScript(url).done(function() {
            if(ns){
               ns;
            }
        });
}

function createSelect(){
        var _this=$('div[data-widget-type="select"]');
        var _thisLen=$('div[data-widget-type="select"]').length;
        _this.each(function(i,item){
            var _thisValue=$(this).attr('data-widget');
            var _thisTitle=$(this).attr('data-widget-title');
            var _thisVal=$(this).attr('data-widget-value');
            var _thisName=$(this).attr('data-widget-name');
            var _thisHiddenName=$(this).attr('data-widget-hidden-name');
            var _thisHiddenValue=$(this).attr('data-widget-hidden-value');
            var _selected=$(this).attr('data-widget-selected-value');
            //console.log(_thisTitle,_thisVal,_selected);
            if(_thisTitle!=='' && $('select[name="order_productId"]').length<1){
                var _selectList=_thisTitle.split(',');
                var _selectValueList=_thisVal.split(',');
                var _selectLen=_selectList.length;
                var html='';
                html +='<select name='+_thisName+'>';
                for(var i=0;i<_selectLen;i++){
                    html += '<option value='+_selectValueList[i]+'>' + _selectList[i] + '</option>';
                }
                html +='</select><input type="hidden" name='+_thisHiddenName+' value="">';
                $(item).append(html);
                $('input[type="hidden"][name='+_thisHiddenName+']').val(_thisHiddenValue);
                $('select[name='+_thisName+']').val(_selected);
                $('select[name='+_thisName+']').change(function(){
                    var _thisHiddenVal=$(this).find("option:selected").text();
                    $('input[type="hidden"][name='+_thisHiddenName+']').val(_thisHiddenVal);
                })
            }
        });

}

//创建地区
function createAddress(){
    var addrVal=$('#ssq').val();
    if(addrVal!==',,'){
        var sheng=addrVal.split(',')[0];
        var shi=addrVal.split(',')[1];
        var qu=addrVal.split(',')[2];
        addressInit('province', 'city', 'area', sheng,shi, qu);
    }else{
        var uip=returnCitySN["cip"];
        var url='http://api.map.baidu.com/location/ip?ak=C93b5178d7a8ebdb830b9b557abce78b&callback&ip='+uip
        $.ajax({
            type: "GET",
            dataType: "jsonp",
            url: url,
            beforeSend: function(){},
            success: function (json) {
                if(json.status==0){
                    var _pro=json.content.address_detail.province;
                    var _city=json.content.address_detail.city;
                    var _area=json.content.address_detail.district;
                    if(_city=='仙桃市' || _city=='天门市' || _city=='潜江市' || _city=='神农架'  ){
                        addressInit('province', 'city', 'area',_pro,'省直辖行政单位',_area);
                    }else{
                        addressInit('province', 'city', 'area',_pro,_city,_area);
                    }
                }else{
                    addressInit('province', 'city', 'area','湖北省','武汉市');
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                addressInit('province', 'city', 'area','湖北省','武汉市');
            }
        });
    }
}
//
//提示框
function notification(title,content){
    if($(".notification").length>0){
        $(".notification .notification-title").html(title);
        $(".notification .content").html(content);
        $(".notification").stop().show();
        setTimeout(function(){$(".notification").stop().hide();},3000)
    }
    else
    {
        $("body").append('<div class="notification am-alert am-alert-warning" data-am-alert><button type="button" class="am-close">&times;</button><p class="notification-title"></p><p class="content"></p></div>');
        $(".notification .notification-title").html(title);
        $(".notification .content").html(content);
        setTimeout(function(){$(".notification").stop().hide();},3000)
    }
}
//验证必填
function checkNotNull(){
    var _this=$('*[data-validateStr]');
    if(_this.parent().attr('id')=='operateForm') {
        _this.each(function (index, item) {
            var _thisDataForm = $(this).attr('data-form');
            var thisData = $(this).attr("data-validatestr");
            var dataArray = thisData.split(",");
            var span = "<span style='color:red'>*</span>";
            for (var i = 0; i < dataArray.length; i++) {
                var _name = dataArray[i].split('|')[0];
                var _tipsContent = dataArray[i].split('|')[1];
                $('*[name=' + _thisDataForm + '_' + _name + ']').parent().append(span);
                $('*[name=' + _thisDataForm + '_' + _name + ']').attr('placeholder', _tipsContent);
                $('*[name=' + _thisDataForm + '_' + _name + ']').attr('required', 'true');
            }
        })
    }
}

//校验
function checkValidate(){
    //正则
    var checkIdCard=/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;
    var checkMobile=/(^13\d{9}$)|(^14)[5,7]\d{8}$|(^15[0,1,2,3,5,6,7,8,9]\d{8}$)|(^17)[0,6,7,8]\d{8}$|(^18\d{9}$)/;
    var checkTel=/^0\d{2,3}-?\d{7,8}$/;
    var checkEmail=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var checkWebUrl=/^(\w+:\/\/)?\w+(\.\w+)+.*$/;
    var checkNum=/^(([1-9]\d{0,999})|([0-9]+\.[0-9]{1,2}))$/;
    var checkInteger=/^\+?[1-9][0-9]*$/;
    //
    var _this=$('* [data-validate]');
    var errorMsg=_this.attr('data-errorMsg');
    $('* [data-validate]').each(function(index,item){
        var _thisName=[item.name];
        var _thisVal=$('* [name='+_thisName+'] ').attr('data-validate').toLowerCase();
        //console.log(_thisVal);
    });
    _this.focus(function(){
        //    获得焦点时执行方法
    });
    _this.blur(function(){
        var _thisAttr=$(this).attr('data-validate').toLowerCase();
        var _thisValue=$(this).val();
        //console.log(_thisAttr);
        if(_thisAttr=='notnull'){
            if(_thisValue==''){
                notification('不能为空');
                //$(this).focus();
            }
        }else if(_thisAttr=='idcard'){
            if(checkIdCard.test(_thisValue)==false){
                notification('请输入正确的身份证号码');
                //$(this).focus();
            }
        }else if(_thisAttr=='idcard'){
            if(checkEmail.test(_thisValue)==false){
                notification('请输入正确的Email');
                //$(this).focus();
            }
        }
        else if(_thisAttr=='idcard2'){
            if(checkIdCard.test(_thisValue)==false){
                notification('请输入正确的身份证号码');
                //$(this).focus();
            }
        }else if(_thisAttr=='mobile'){
            if(checkMobile.test(_thisValue)==false){
                notification('请输入正确的手机号码');
                //$(this).focus();
            }
        }else if(_thisAttr=='weburl'){
            if(checkWebUrl.test(_thisValue)==false){
                notification('请输入正确的网址');
                //$(this).focus();
            }

        }else if(_thisAttr=='telephone'){
            if(checkTel.test(_thisValue)==false){
                notification('请输入正确的固定电话');
                //$(this).focus();
            }

        }else if(_thisAttr=='number'){
            if(checkNum.test(_thisValue)==false){
                notification('请输入正确的数字');
                //$(this).focus();
            }
        }else if(_thisAttr=='integer'){
            if(checkInteger.test(_thisValue)==false){
                notification('请输入正确的整数');
                //$(this).focus();
            }
        }else if(_thisAttr.substr(0,3)=='len'){
            var pa=/.*\((.*)\)/;
            var _formatVal=_thisAttr.match(pa)[1];
            var _minVal=_formatVal.split(',')[0];
            var _maxVal=_formatVal.split(',')[1];
            if( eval(_minVal) <=  _thisValue  &&  _thisValue <= eval(_maxVal) ){

            }else {
                notification('请输入'+_minVal+'至'+_maxVal+'的数字');
            }
        }else if(_thisAttr.substr(0,4)=='rang'){
            //debugger;
            var pa=/.*\((.*)\)/;
            var _formatVal=_thisAttr.match(pa)[1];
            var _minVal=_formatVal.split(',')[0];
            var _maxVal=_formatVal.split(',')[1];
            //console.log(_minVal,_maxVal,_thisValue);
            if( eval(_minVal) <=  _thisValue  &&  _thisValue <= eval(_maxVal) ){
            }else {
                notification('请输入'+_minVal+'至'+_maxVal+'的数字');
            }
        }else{
            if(eval('/'+_thisAttr+'/').test(_thisValue)==false){
                notification('请输入正确信息');
                //$(this).focus();
            }
        }
    });
}
//初始化UI样式
//
$(function(){
    $('.moreList span').on('click',function(){
        $('.moreList').hide();
    });
});
//
function changeShow(showID){
    var _thisName=$(showID).attr('name');
    var _val=$(showID).val();
    $(showID).change(function(){
        $('*[id^='+_thisName+']').hide();
        $('#'+_thisName+_val).show();
    })
}
//
function showErrorMsg(data,title) {
    if(data){
        var dataArray=data.split(",")
        var tipsHtml='';
        for(var i=0;i<dataArray.length;i++){
            var _name=dataArray[i].split('|')[0];
            var _tipsContent=dataArray[i].split('|')[1];
            tipsHtml+='<li>'+_name+'：<span>'+_tipsContent+'</span></li>'
        }
        $('#saveNotice .am-modal-bd ul').html(tipsHtml);
        $('#saveNotice').modal({});
    }
    if(title){
        $('#saveNotice .am-modal-hd span').html(title);
    }else{
        $('#saveNotice .am-modal-hd span').html('');
    }
};