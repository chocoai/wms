/**
 * Created with JetBrains PhpStorm.
 * User: xuheng
 * Date: 12-8-8
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
var templates = [
    {
        "pre":"pre3.png",
        'title':lang.loanCar,
        'preHtml':'<pre class="ueditorContent">' +
            '<h1 style="text-align: center;">贷款详情</h1>' +
            '<blockquote>贷款信息</blockquote>' +
            '<table>' +
            '<tr><td>贷款编号</td><td> </td><td>贷款期限</td><td> </td><td>贷款类型</td><td> </td></tr>' +
            '<tr><td>还款方式</td><td> </td><td>募标起始日期</td><td colspan="3"></td></tr>' +
            '</table>' +
            '<blockquote>基本信息</blockquote>' +
            '<table>' +
            '<tr><td>姓名</td><td> </td><td>性别</td><td> </td><td>年龄</td><td> </td></tr>' +
            '<tr><td>婚姻状况</td><td> </td><td>户籍城市</td><td colspan="3"> </td></tr>' +
            '</table>' +
            '<blockquote>补充信息</blockquote>' +
            '<p>如果是车易贷客户，需要填写模糊居住地、资产情况、公司类型、职位、收入范围。其他信息还可补充，列举项为必填内容。<br>例如：借款人现居住于湖北省武汉市江汉区，名下有一套有贷款的商品房。就职于一所大型金融企业，担任部门经理，月收入10000以上。如果是企易贷客户，需要填写公司类型、模糊公司地址、成立时间、年销售额。其他信息还可补充，列举项为必填内容。例如：国内某知名食品生产企业坐落于武汉市江岸区，成立于2000年，年销售额在2000万左右。</p>' +
            '<blockquote>贷款用途</blockquote>' +
            '<p>如果是车易贷客户，需要填写车辆品牌，型号，颜色，新车还是二手，如果是二手需要填写二手车的已使用年限。<br>例如：用于购买了一辆全新的白色一汽大众polo。<br>或者是：用于购买了一辆二手的白色宝马X1，已使用三年。<br>如果是企易贷客户，填写具体用户即可。<br>例如：目前经营内容的市场需求扩大，用于扩大生产。</p>' +
             '<blockquote>抵押物描述</blockquote>' +
            '<p>如果是车易贷客户，抵押物为已购车辆，还需要填写是否已安装GPS定位。<br>比如：用已购新车作为抵押，且新车已安全GPS定位，车融所能实时监控车辆的行驶范围。<br>如果是企易贷客户，抵押物描述参考担保公司提供的抵押物描述，保证语句通顺逻辑正确即可。</p>' +
             '<blockquote>还款来源</blockquote>' +
            '<p>如果是车易贷客户，填写个人收入或其他收入<br>例如：每月工资还款，如果出现逾期，担保公司会先行垫付，后续会扣留借款者通过贷款购买的车辆来要求借款者及时还款。<br>如果是企易贷客户，填写企业的盈利情况或投资获益情况<br>例如：每月固定盈利50万可用于还款，如果出现逾期，担保公司会先行垫付，后续通过处理借款者的抵押物来偿还贷款。</p>' +
            '</pre>',

        "html":'<pre class="ueditorContent">' +
            '<h1 style="text-align: center;">贷款详情</h1>' +
            '<blockquote>贷款信息</blockquote>' +
            '<table>' +
            '<tr><td>贷款编号</td><td> </td><td>贷款期限</td><td> </td><td>贷款类型</td><td> </td></tr>' +
            '<tr><td>还款方式</td><td> </td><td>募标起始日期</td><td></td></tr>' +
            '</table>' +
            '<blockquote>基本信息</blockquote>' +
            '<table>' +
            '<tr><td>姓名</td><td> </td><td>性别</td><td> </td><td>年龄</td><td> </td></tr>' +
            '<tr><td>婚姻状况</td><td> </td><td>户籍城市</td><td> </td></tr>' +
            '</table>' +
            '<blockquote>补充信息</blockquote>' +
            '<p>如果是车易贷客户，需要填写模糊居住地、资产情况、公司类型、职位、收入范围。其他信息还可补充，列举项为必填内容。<br>例如：借款人现居住于湖北省武汉市江汉区，名下有一套有贷款的商品房。就职于一所大型金融企业，担任部门经理，月收入10000以上。如果是企易贷客户，需要填写公司类型、模糊公司地址、成立时间、年销售额。其他信息还可补充，列举项为必填内容。例如：国内某知名食品生产企业坐落于武汉市江岸区，成立于2000年，年销售额在2000万左右。</p>' +
            '<blockquote>贷款用途</blockquote>' +
            '<p>如果是车易贷客户，需要填写车辆品牌，型号，颜色，新车还是二手，如果是二手需要填写二手车的已使用年限。<br>例如：用于购买了一辆全新的白色一汽大众polo。<br>或者是：用于购买了一辆二手的白色宝马X1，已使用三年。<br>如果是企易贷客户，填写具体用户即可。<br>例如：目前经营内容的市场需求扩大，用于扩大生产。</p>' +
            '<blockquote>抵押物描述</blockquote>' +
            '<p>如果是车易贷客户，抵押物为已购车辆，还需要填写是否已安装GPS定位。<br>比如：用已购新车作为抵押，且新车已安全GPS定位，车融所能实时监控车辆的行驶范围。<br>如果是企易贷客户，抵押物描述参考担保公司提供的抵押物描述，保证语句通顺逻辑正确即可。</p>' +
            '<blockquote>还款来源</blockquote>' +
            '<p>如果是车易贷客户，填写个人收入或其他收入<br>例如：每月工资还款，如果出现逾期，担保公司会先行垫付，后续会扣留借款者通过贷款购买的车辆来要求借款者及时还款。<br>如果是企易贷客户，填写企业的盈利情况或投资获益情况<br>例如：每月固定盈利50万可用于还款，如果出现逾期，担保公司会先行垫付，后续通过处理借款者的抵押物来偿还贷款。</p>' +
            '</pre>'
    }
   , {
        "pre":"pre1.png",
        'title':lang.loanCompany,
        'preHtml':'<pre class="ueditorContent">' +
            '<h1 style="text-align: center;">贷款详情</h1>' +
            '<blockquote>贷款信息</blockquote>' +
            '<ul class="loanOrderList">' +
            '<li>贷款编号：</li>' +
            '<li>贷款期限：</li>' +
            '<li>贷款类型：</li>' +
            '<li>还款方式：</li>' +
            '<li>募标起始日期：</li>' +
            '</ul>' +
            '<blockquote>基本信息</blockquote>' +
            '<ul class="loanOrderList">' +
            '<li>姓名：</li>' +
            '<li>性别：</li>' +
            '<li>年龄：</li>' +
            '<li>婚姻状况：</li>' +
            '<li>户籍城市：</li>' +
            '</ul>' +
            '<table class="table1">' +
            '<tr><td>补充信息</td>' +
            '<td>如果是车易贷客户，需要填写模糊居住地、资产情况、公司类型、职位、收入范围。其他信息还可补充，列举项为必填内容。<br>例如：借款人现居住于湖北省武汉市江汉区，名下有一套有贷款的商品房。就职于一所大型金融企业，担任部门经理，月收入10000以上。如果是企易贷客户，需要填写公司类型、模糊公司地址、成立时间、年销售额。其他信息还可补充，列举项为必填内容。例如：国内某知名食品生产企业坐落于武汉市江岸区，成立于2000年，年销售额在2000万左右。</td></tr>' +
            '<tr><td>贷款用途</td>' +
            '<td>如果是车易贷客户，需要填写车辆品牌，型号，颜色，新车还是二手，如果是二手需要填写二手车的已使用年限。<br>例如：用于购买了一辆全新的白色一汽大众polo。<br>或者是：用于购买了一辆二手的白色宝马X1，已使用三年。<br>如果是企易贷客户，填写具体用户即可。<br>例如：目前经营内容的市场需求扩大，用于扩大生产。</td></tr>' +
            '<tr><td>抵押物描述</td>' +
            '<td>如果是车易贷客户，抵押物为已购车辆，还需要填写是否已安装GPS定位。<br>比如：用已购新车作为抵押，且新车已安全GPS定位，车融所能实时监控车辆的行驶范围。<br>如果是企易贷客户，抵押物描述参考担保公司提供的抵押物描述，保证语句通顺逻辑正确即可。</td></tr>' +
            '<tr><td>还款来源</td>' +
            '<td>如果是车易贷客户，填写个人收入或其他收入<br>例如：每月工资还款，如果出现逾期，担保公司会先行垫付，后续会扣留借款者通过贷款购买的车辆来要求借款者及时还款。<br>如果是企易贷客户，填写企业的盈利情况或投资获益情况<br>例如：每月固定盈利50万可用于还款，如果出现逾期，担保公司会先行垫付，后续通过处理借款者的抵押物来偿还贷款。</td></tr>' +
            '</table>' +
            '</pre>',

        "html":'<pre class="ueditorContent">' +
        '<h1 style="text-align: center;">贷款详情</h1>' +
        '<blockquote>贷款信息</blockquote>' +
        '<ul class="loanOrderList">' +
        '<li>贷款编号：</li>' +
        '<li>贷款期限：</li>' +
        '<li>贷款类型：</li>' +
        '<li>还款方式：</li>' +
        '<li>募标起始日期：</li>' +
        '</ul>' +
        '<blockquote>基本信息</blockquote>' +
        '<ul class="loanOrderList">' +
        '<li>姓名：</li>' +
        '<li>性别：</li>' +
        '<li>年龄：</li>' +
        '<li>婚姻状况：</li>' +
        '<li>户籍城市：</li>' +
        '</ul>' +
            '<table class="table1">' +
            '<tr><td>补充信息</td>' +
            '<td>如果是车易贷客户，需要填写模糊居住地、资产情况、公司类型、职位、收入范围。其他信息还可补充，列举项为必填内容。<br>例如：借款人现居住于湖北省武汉市江汉区，名下有一套有贷款的商品房。就职于一所大型金融企业，担任部门经理，月收入10000以上。如果是企易贷客户，需要填写公司类型、模糊公司地址、成立时间、年销售额。其他信息还可补充，列举项为必填内容。例如：国内某知名食品生产企业坐落于武汉市江岸区，成立于2000年，年销售额在2000万左右。</td></tr>' +
            '<tr><td>贷款用途</td>' +
            '<td>如果是车易贷客户，需要填写车辆品牌，型号，颜色，新车还是二手，如果是二手需要填写二手车的已使用年限。<br>例如：用于购买了一辆全新的白色一汽大众polo。<br>或者是：用于购买了一辆二手的白色宝马X1，已使用三年。<br>如果是企易贷客户，填写具体用户即可。<br>例如：目前经营内容的市场需求扩大，用于扩大生产。</td></tr>' +
            '<tr><td>抵押物描述</td>' +
            '<td>如果是车易贷客户，抵押物为已购车辆，还需要填写是否已安装GPS定位。<br>比如：用已购新车作为抵押，且新车已安全GPS定位，车融所能实时监控车辆的行驶范围。<br>如果是企易贷客户，抵押物描述参考担保公司提供的抵押物描述，保证语句通顺逻辑正确即可。</td></tr>' +
            '<tr><td>还款来源</td>' +
            '<td>如果是车易贷客户，填写个人收入或其他收入<br>例如：每月工资还款，如果出现逾期，担保公司会先行垫付，后续会扣留借款者通过贷款购买的车辆来要求借款者及时还款。<br>如果是企易贷客户，填写企业的盈利情况或投资获益情况<br>例如：每月固定盈利50万可用于还款，如果出现逾期，担保公司会先行垫付，后续通过处理借款者的抵押物来偿还贷款。</td></tr>' +
            '</table>' +
        '</pre>'
    }
//  ,  {
//        "pre":"pre2.png",
//        'title':lang.resume,
//        'preHtml':'<h1 label="Title left" name="tl" style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;margin:0px 0px 10px;"><span style="color:#e36c09;" class=" ">WEB前端开发简历</span></h1><table width="100%" border="1" bordercolor="#95B3D7" style="border-collapse:collapse;"><tbody><tr><td width="100" style="text-align:center;"><p><span style="background-color:transparent;">插</span><br /></p><p>入</p><p>照</p><p>片</p></td><td><p><span style="background-color:transparent;"> 联系电话：</span><span class="ue_t" style="background-color:transparent;">[键入您的电话]</span><br /></p><p><span style="background-color:transparent;"> 电子邮件：</span><span class="ue_t" style="background-color:transparent;">[键入您的电子邮件地址]</span><br /></p><p><span style="background-color:transparent;"> 家庭住址：</span><span class="ue_t" style="background-color:transparent;">[键入您的地址]</span><br /></p></td></tr></tbody></table><h3><span style="color:#E36C09;font-size:20px;">目标职位</span></h3><p style="text-indent:2em;" class=" ">WEB前端研发工程师</p><h3><span style="color:#e36c09;font-size:20px;">学历</span></h3><p><span style="display:none;line-height:0px;" id="_baidu_bookmark_start_26">﻿</span></p><ol style="list-style-type:decimal;"><li><p><span class="ue_t">[起止时间]</span> <span class="ue_t">[学校名称] </span> <span class="ue_t">[所学专业]</span> <span class="ue_t">[所获学位]</span></p></li></ol><h3><span style="color:#e36c09;font-size:20px;" class="ue_t">工作经验</span></h3><p><br /></p>',
//        "html":'<h1 label="Title left" name="tl" style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;margin:0px 0px 10px;"><span style="color:#e36c09;" class="ue_t">[此处键入简历标题]</span></h1><p><span style="color:#e36c09;"><br /></span></p><table width="100%" border="1" bordercolor="#95B3D7" style="border-collapse:collapse;"><tbody><tr><td width="200" style="text-align:center;" class="ue_t">【此处插入照片】</td><td><p><br /></p><p> 联系电话：<span class="ue_t">[键入您的电话]</span></p><p><br /></p><p> 电子邮件：<span class="ue_t">[键入您的电子邮件地址]</span></p><p><br /></p><p> 家庭住址：<span class="ue_t">[键入您的地址]</span></p><p><br /></p></td></tr></tbody></table><h3><span style="color:#e36c09;font-size:20px;">目标职位</span></h3><p style="text-indent:2em;" class="ue_t">[此处键入您的期望职位]</p><h3><span style="color:#e36c09;font-size:20px;">学历</span></h3><p><span style="display:none;line-height:0px;" id="_baidu_bookmark_start_26">﻿</span></p><ol style="list-style-type:decimal;"><li><p><span class="ue_t">[键入起止时间]</span> <span class="ue_t">[键入学校名称] </span> <span class="ue_t">[键入所学专业]</span> <span class="ue_t">[键入所获学位]</span></p></li><li><p><span class="ue_t">[键入起止时间]</span> <span class="ue_t">[键入学校名称]</span> <span class="ue_t">[键入所学专业]</span> <span class="ue_t">[键入所获学位]</span></p></li></ol><h3><span style="color:#e36c09;font-size:20px;" class="ue_t">工作经验</span></h3><ol style="list-style-type:decimal;"><li><p><span class="ue_t">[键入起止时间]</span> <span class="ue_t">[键入公司名称]</span> <span class="ue_t">[键入职位名称]</span> </p></li><ol style="list-style-type:lower-alpha;"><li><p><span class="ue_t">[键入负责项目]</span> <span class="ue_t">[键入项目简介]</span></p></li><li><p><span class="ue_t">[键入负责项目]</span> <span class="ue_t">[键入项目简介]</span></p></li></ol><li><p><span class="ue_t">[键入起止时间]</span> <span class="ue_t">[键入公司名称]</span> <span class="ue_t">[键入职位名称]</span> </p></li><ol style="list-style-type:lower-alpha;"><li><p><span class="ue_t">[键入负责项目]</span> <span class="ue_t">[键入项目简介]</span></p></li></ol></ol><p><span style="color:#e36c09;font-size:20px;">掌握技能</span></p><p style="text-indent:2em;"> &nbsp;<span class="ue_t">[这里可以键入您所掌握的技能]</span><br /></p>'
//
//    },
//    {
//        "pre":"pre3.png",
//        'title':lang.richText,
//        'preHtml':'<h1 label="Title center" name="tc" style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;text-align:center;margin:0px 0px 20px;" class="ue_t">[此处键入文章标题]</h1><p><img src="http://img.baidu.com/hi/youa/y_0034.gif" width="150" height="100" border="0" hspace="0" vspace="0" style="width:150px;height:100px;float:left;" />图文混排方法</p><p>图片居左，文字围绕图片排版</p><p>方法：在文字前面插入图片，设置居左对齐，然后即可在右边输入多行文</p><p><br /></p><p><img src="http://img.baidu.com/hi/youa/y_0040.gif" width="100" height="100" border="0" hspace="0" vspace="0" style="width:100px;height:100px;float:right;" /></p><p>还有没有什么其他的环绕方式呢？这里是居右环绕</p><p><br /></p><p>欢迎大家多多尝试，为UEditor提供更多高质量模板！</p>',
//        "html":'<p><br /></p><h1 label="Title center" name="tc" style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;text-align:center;margin:0px 0px 20px;" class="ue_t">[此处键入文章标题]</h1><p><img src="http://img.baidu.com/hi/youa/y_0034.gif" width="300" height="200" border="0" hspace="0" vspace="0" style="width:300px;height:200px;float:left;" />图文混排方法</p><p>1. 图片居左，文字围绕图片排版</p><p>方法：在文字前面插入图片，设置居左对齐，然后即可在右边输入多行文本</p><p><br /></p><p>2. 图片居右，文字围绕图片排版</p><p>方法：在文字前面插入图片，设置居右对齐，然后即可在左边输入多行文本</p><p><br /></p><p>3. 图片居中环绕排版</p><p>方法：亲，这个真心没有办法。。。</p><p><br /></p><p><br /></p><p><img src="http://img.baidu.com/hi/youa/y_0040.gif" width="300" height="300" border="0" hspace="0" vspace="0" style="width:300px;height:300px;float:right;" /></p><p>还有没有什么其他的环绕方式呢？这里是居右环绕</p><p><br /></p><p>欢迎大家多多尝试，为UEditor提供更多高质量模板！</p><p><br /></p><p>占位</p><p><br /></p><p>占位</p><p><br /></p><p>占位</p><p><br /></p><p>占位</p><p><br /></p><p>占位</p><p><br /></p><p><br /></p>'
//    },
//    {
//        "pre":"pre4.png",
//        'title':lang.sciPapers,
//        'preHtml':'<h2 style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;margin:0px 0px 10px;text-align:center;" class="ue_t">[键入文章标题]</h2><p><strong><span style="font-size:12px;">摘要</span></strong><span style="font-size:12px;" class="ue_t">：这里可以输入很长很长很长很长很长很长很长很长很差的摘要</span></p><p style="line-height:1.5em;"><strong>标题 1</strong></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">这里可以输入很多内容，可以图文混排，可以有列表等。</span></p><p style="line-height:1.5em;"><strong>标题 2</strong></p><ol style="list-style-type:lower-alpha;"><li><p class="ue_t">列表 1</p></li><li><p class="ue_t">列表 2</p></li><ol style="list-style-type:lower-roman;"><li><p class="ue_t">多级列表 1</p></li><li><p class="ue_t">多级列表 2</p></li></ol><li><p class="ue_t">列表 3<br /></p></li></ol><p style="line-height:1.5em;"><strong>标题 3</strong></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">来个文字图文混排的</span></p><p style="text-indent:2em;"><br /></p>',
//        'html':'<h2 style="border-bottom-color:#cccccc;border-bottom-width:2px;border-bottom-style:solid;padding:0px 4px 0px 0px;margin:0px 0px 10px;text-align:center;" class="ue_t">[键入文章标题]</h2><p><strong><span style="font-size:12px;">摘要</span></strong><span style="font-size:12px;" class="ue_t">：这里可以输入很长很长很长很长很长很长很长很长很差的摘要</span></p><p style="line-height:1.5em;"><strong>标题 1</strong></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">这里可以输入很多内容，可以图文混排，可以有列表等。</span></p><p style="line-height:1.5em;"><strong>标题 2</strong></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">来个列表瞅瞅：</span></p><ol style="list-style-type:lower-alpha;"><li><p class="ue_t">列表 1</p></li><li><p class="ue_t">列表 2</p></li><ol style="list-style-type:lower-roman;"><li><p class="ue_t">多级列表 1</p></li><li><p class="ue_t">多级列表 2</p></li></ol><li><p class="ue_t">列表 3<br /></p></li></ol><p style="line-height:1.5em;"><strong>标题 3</strong></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">来个文字图文混排的</span></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">这里可以多行</span></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">右边是图片</span></p><p style="text-indent:2em;"><span style="font-size:14px;" class="ue_t">绝对没有问题的，不信你也可以试试看</span></p><p><br /></p>'
//    }
];