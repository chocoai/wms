/**
 * Created by other on 2016/1/7.
 */
//data-tab>1的时候生成tab标签
function createTab(){
    var _this = $('div[data-tab]');
    var dataLen = _this.length;
    var dataArray = new Array();
    for (var i = 0; i < dataLen; i++) {
        var dataAttr = _this.eq(i).attr('data-tab');
        dataArray.push(dataAttr);
    };

    $('div[data-tab]:nth-child(2n+2)').css('display','none');

    //$('div[data-tab]:nth-child(n+2)').css('display','none');
    _this.eq(0).css('display','block');

    console.log(dataArray);
    if(dataLen>1){
        var html = '';
        html+='<ul class="tab-ul clearfix">';
        for (var i = 0; i < dataArray.length; i++) {
            html += '<li>' + dataArray[i] + '</li>';//循环生成html
        };
        html+='</ul>';
        $('#viewTpl').prepend(html);
        $('.tab-ul li:nth-child(1)').addClass('curr');
    }
    $('.tab-ul li').on('click', function() {
        var tabName=$(this).text();
        var ti=$(this).index();
        $(this).addClass('curr').siblings('li').removeClass('curr');
        //$("div[data-tab="+tabName+"]").show().siblings('div[data-tab]').hide();
        $("div[data-tab]").eq(ti).show().siblings('div[data-tab]').hide();
    });
};
//生成tab标签end；
//提示框
function notification(title,content){
    if($(".notification").length>0){
        $(".notification .notification-title").html(title);
        $(".notification .content").html(content);
        $(".notification").show();
        setTimeout(function(){$(".notification").hide();},3000)
    }
    else
    {
        $("body").append('<div class="notification notification-animated"><div class="notification-content"><h3 class="notification-title"></h3><div class="content"></div></div><span class="icon icon-close notification-icon"></span></div>');
        $(".notification .notification-title").html(title);
        $(".notification .content").html(content);
        setTimeout(function(){$(".notification").hide();},3000)
    }
}
//校验
function checkValidate(){
    //正则
    var checkIdCard=/(^\d{15}$)|(^\d{17}([0-9]|X)$)/;
    var checkMobile=/(^13\d{9}$)|(^14)[5,7]\d{8}$|(^15[0,1,2,3,5,6,7,8,9]\d{8}$)|(^17)[0,6,7,8]\d{8}$|(^18\d{9}$)/;
    var checkTel=/^0\d{2,3}-?\d{7,8}$/;
    var checkEmail=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var checkWebUrl=/^(\w+:\/\/)?\w+(\.\w+)+.*$/;
    var checkNum=/^(([1-9]\d{0,999})|([0-9]+\.[0-9]{1,2}))$/;
    var checkInteger=/^\+?[1-9][0-9]*$/;
    //
    var _this=$('* [data-validate]');
    $('* [data-validate]').each(function(index,item){
        var _thisName=[item.name];
        var _thisVal=$('* [name='+_thisName+'] ').attr('data-validate').toLowerCase();
        console.log(_thisVal);
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
                }
            }else if(_thisAttr=='idcard'){
                if(checkIdCard.test(_thisValue)==false){
                    notification('请输入正确的身份证号码');
                    $(this).focus();
                }
            }else if(_thisAttr=='idcard'){
                if(checkEmail.test(_thisValue)==false){
                    notification('请输入正确的Email');
                    $(this).focus();
                }
            }
            else if(_thisAttr=='idcard2'){
                if(checkIdCard.test(_thisValue)==false){
                    notification('请输入正确的身份证号码');
                    $(this).focus();
                }
            }else if(_thisAttr=='mobile'){
                if(checkMobile.test(_thisValue)==false){
                    notification('请输入正确的手机号码');
                    $(this).focus();
                }
            }else if(_thisAttr=='weburl'){
                if(checkWebUrl.test(_thisValue)==false){
                    notification('请输入正确的网址');
                    $(this).focus();
                }

            }else if(_thisAttr=='telephone'){
                if(checkTel.test(_thisValue)==false){
                    notification('请输入正确的固定电话');
                    $(this).focus();
                }

            }else if(_thisAttr=='number'){
                if(checkNum.test(_thisValue)==false){
                    notification('请输入正确的数字');
                    $(this).focus();
                }
            }else if(_thisAttr=='integer'){
                if(checkInteger.test(_thisValue)==false){
                    notification('请输入正确的整数');
                    $(this).focus();
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
                console.log(_minVal,_maxVal,_thisValue);
                if( eval(_minVal) <=  _thisValue  &&  _thisValue <= eval(_maxVal) ){
                }else {
                    notification('请输入'+_minVal+'至'+_maxVal+'的数字');
                }
            }else{
                if(_thisAttr.test(_thisValue)==false){
                    notification('请输入正确信息');
                    $(this).focus();
                }
            }

    });
}
//生成select
function createSelect(){
    var _this=$('div[data-widget]');
    var _thisLen=$('div[data-widget]').length;
    _this.each(function(i,item){
        var _thisValue=_this.attr('data-widget');
        //var dataArray = new Array();
        var _thisTitle=$(item).attr('data-title');
        var _thisVal=$(item).attr('data-value');
        var _selectList=_thisTitle.split(',');
        var _selectValueList=_thisVal.split(',');
        var _selectLen=_selectList.length;
        //debugger;
        console.log(_selectLen);
        var html='';
        html +='<select>';
            for(var i=0;i<_selectLen;i++){
                html += '<option value='+_selectValueList[i]+'>' + _selectList[i] + '</option>';
            }
        html +='</select>';
        console.log(item);
        $(item).append(html);
    });
}