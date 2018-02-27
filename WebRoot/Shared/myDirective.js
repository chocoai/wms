var myDirectiveModule = angular.module('myApp.myDirective', ['myApp.SharedService']);


var _$util = {

    /**
     * 获取当前域的缓存,
     * @param scope
     */
    getCDCache: function (scope) {
        if (scope._$CACHE)
            return scope._$CACHE;
        var p = scope.$parent;
        while (!p["_$$controller$$"]) {
            p = p.$parent;
        }
        return p._$CACHE;
    },


    /**
     * 获取我所在范围的父
     * 如果范围内有：
     *    (1):_$$view$$，这是一个视图scope对象
     *    (2)：_$$controller$$:这是控制器对象
     *    (3):scope.parent为空，这是rootSceopt
     *   返回对象格式为--result:{type:"view|controller|rootScope",scope:scope}
     *
     *
     * @param scope
     */
    getParent: function (scope) {
        var p = scope.$parent;
        if (p == null) {
            return {type: "rootScope", scope: scope};
        }
        while (!(p["_$$view$$"] || p["_$$controller$$"] || p["_$$popwin$$"])) {
            if (p.$parent == null) {
                return {type: "rootScope", scope: p};
            } else {
                p = p.$parent;
            }
        }

        if (p["_$$view$$"]) {
            return {type: "view", scope: p};
        } else if (p["_$$popwin$$"]) {
            return {type: "popwin", scope: p};
        } else {
            return {type: "controller", scope: p};
        }

    },
    getController: function (scope) {
        var p = scope.$parent;
        if (p == null) {
            return {type: "rootScope", scope: scope};
        }
        while (!p["_$$controller$$"]) {
            if (p.$parent == null) {
                return {type: "rootScope", scope: p};
            } else {
                p = p.$parent;
            }
        }

        if (p["_$$controller$$"]) {
            return {type: "controller", scope: p};
        }

    }


};

var exprLib = {
    sum: function (a) {
        return a;
    },
    if: function (condition, v1, v2) {
        if (condition) {
            return v1;
        } else {
            return v2;
        }
    }
};


/**
 *   定义模型对象，模型对象中用于存放模型实体
 *
 *    entity:{
							key:modeKey,
						    head:"true|false",是否为头部对象
							cos:[],//对象列表
                            co:{},//操作的对象
                            sos:[],//选择可以对象
                            archetype:{},//对象创建原型
							original:""//原件对象，
	   }
 */


/**
 * 定义抽象指令，其中的方法会被混合到指令中
 * @type {{}}
 */
var abstractDirective = {

    //接口调用
    callService: function (url) {
        debugger;
        var me = this;
        var data = me.$parent._model;
        var postData = {};
        postData.models = JSON.stringify(data);
        var result = this.$http.post(url, $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success(function (data) {
//                   alert(data);
        }).error(function (err) {
            return;
        });


        return result;
    },

    getRows: function (billKey) {
        var me = this;
        if (this.$parent._model[billKey].sos.length > 0) {
            me._temp = this.$parent._model[billKey].sos;
            return this.$parent._model[billKey].sos;
        } else {
            alert("请选择一行记录")
            return false;
        }
    },


    findRowObject:function (me) {
        var _parent = me.$parent;
        while (!_parent.row) {
            _parent = _parent.$parent;
            if (!_parent) {
                return;
            }
        }
        return _parent.row;
    },

    checkOrder: function (sos) {
        var me = this;
        var postData = {};
        postData.models = JSON.stringify(me._temp);
        postData.clazz = "com.xyy.erp.platform.common.func.CheckOrder";
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },

    orderFunc: function (type, key, name) {
        var me = this;
        var postData = {};
        postData.clazz = "com.xyy.erp.platform.common.func.OrderFunc";
        postData.models = JSON.stringify([{type: type, key: key, name: name}]);
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },
    OnlineFunc: function (type,tableName, onlineid) {
        var me = this;
        var postData = {};
        postData.clazz = "com.xyy.erp.platform.common.func.OnlineFunc";
        postData.models = JSON.stringify([{type: type,tableName: tableName,  onlineid: onlineid}]);
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },
    XSCXFunc: function (cos) {
        var me = this;
        var postData = {};
        postData.clazz = "com.xyy.erp.platform.common.func.XSCXFunc";
        postData.models = JSON.stringify([{type:cos.danjuleixing,danjubianhao: cos.danjubianhao}]);
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },

    cancelOrder: function (danjubianhao) {
    var me = this;
    var postData = {};
    postData.clazz = "com.xyy.erp.platform.common.func.cancelOrder";
    postData.models = JSON.stringify([{danjubianhao:danjubianhao}]);
    var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
    return result;
},
    YHPZFunc: function (type) {
        var me = this;
        var postData = {};
        postData.clazz = "com.xyy.erp.platform.common.func.YHPZFunc";
        postData.models = JSON.stringify([{type:type}]);
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },
    zhujimaFunc: function (name) {
        var me = this;
        var postData = {};
        postData.clazz = "com.xyy.erp.platform.common.func.ZhujimaFunc";
        postData.models = JSON.stringify([{name: name}]);
        var result = abstractDirective.ajax("POST", "/bill/data/func?" + $.param(postData), null);
        return result;
    },

    update: function (tableName, sos, type) {
        var me = this;
        var postData = {};
        postData.tableName = tableName;
        postData.type = type;
        postData.biLLKey = me.$parent._env.billkey;
        postData.models = JSON.stringify(sos);
        postData.clazz = "com.xyy.erp.platform.common.func.UpdateFunc";
        var result = abstractDirective.ajax("POST", "/bill/data/SqlFunc?" + $.param(postData), null);
        return result;
    },

    updateSet: function (tableName, sos, updateKeySet, type, billType) {
        var me = this;
        var postData = {};
        postData.tableName = tableName;
        postData.type = type;
        postData.billType = billType;
        postData.biLLKey = me.$parent._env.billkey;
        for (var o in sos) {
            for (var index in updateKeySet) {
                sos[o][updateKeySet[index].key] = updateKeySet[index].value;
            }
        }
        postData.models = JSON.stringify(sos);
        postData.clazz = "com.xyy.erp.platform.common.func.UpdateFunc";
        var result = abstractDirective.ajax("POST", "/bill/data/SqlFunc?" + $.param(postData), null);
        return result;
    },

    edgeBill: function (sos, ruleKey, tagetURL) {
        var me = this;
        var postData = {};
        postData.ruleKey = ruleKey;
        postData.biLLKey = me.$parent._env.billkey;
        var sourceBillIDs = [];
        $.each(sos, function (index, entry) {
            if (entry.status < 40) {
                result = true;
                return false;
            }
            sourceBillIDs.push(entry.BillID);
        });
        postData.sourceBillIDs = angular.toJson(sourceBillIDs);
        var result = abstractDirective.ajax("POST", "/bill/data/edgeBill?" + $.param(postData), null);
        result.then(function (ret) {
            alert("转换成功.");
            window.parent.location.href = tagetURL;

        });
    },


    MIDService: function (code) {
        var result = abstractDirective.ajax("POST", "/spe/route/sqlService?code=" + code, null);
        return result;
    },


    sum: function (entityKey, col) {
        var $parentScope = _$util.getParent(this).scope;
        var total = 0;
        if ($parentScope._model && $parentScope._model[entityKey]) {
            var rows = $parentScope._model[entityKey].cos;
            for (var i = 0; i < rows.length; i++) {
                total += parseInt(rows[i][col] * 10000);
            }
        }
        var _total = total / 10000;
        return _total;
    },

    //获取模型的值
    getValue: function () {
        if (this.ngModel) {
            return this.ngModel.$modelValue;//返回模型中的值
        } else {
            return undefined;
        }
    },

    setEnvModel: function (model) {
        var _parent = _$util.getParent(this).scope;
        var _model = eval(model);
        clone(model, _parent._env);
    },
    setEnvToCache: function (model) {
        var model = JSON.stringify(model);
        window.localStorage.setItem('cacheEnv', model);
    },
    getEnvFromCache: function () {
        var _model = JSON.parse(window.localStorage.getItem('cacheEnv'));
        var _parent = _$util.getParent(this).scope;
        clone(_model, _parent._env);
        window.localStorage.clear('cacheEnv');
    },
    //设置模型的值
    setValue: function (value) {
        if (this.ngModel) {
            this.ngModel.$setViewValue(value);
            this.ngModel.$render();
        }
    },
    setZeroValue: function (value) {
        if(!value){
            value=~~undefined;
        }

        if (this.ngModel) {
            this.ngModel.$setViewValue(value);
            this.ngModel.$render();
        }
    },
    
    setHide: function () {
        angular.element("[key=" + this.key + "]").hide();
    },
    setShow: function () {
        angular.element("[key=" + this.key + "]").show();
    },
    setDisable: function () {
        angular.element("[key=" + this.key + "]").attr('disabled', 'disabled');
        angular.element("[key=" + this.key + "]").addClass('disabled');
    },
    setEnable: function () {
        angular.element("[key=" + this.key + "]").attr('disabled', false);
        angular.element("[key=" + this.key + "]").removeAttr("readonly");
        angular.element("[key=" + this.key + "]").removeClass('disabled');
    },
    setAllDisable: function () {
        $('body').find('input,button,textarea,select').attr('disabled', 'disabled');
    },
    setAllEnable: function () {
        $('body').find('input,button,textarea,select').attr('disabled', false);
    },
    getRealName: function () {
        return _$util.getParent(this).scope._env.user.realName;
    },
    getRealUserId: function () {
        return _$util.getParent(this).scope._env.user.userId;
    },
    getOrgId: function () {
        return _$util.getParent(this).scope._env.user.orgId;
    },
    insertRows: function (tableKey, sos) {
        console.log(_$util.getParent(this).scope);
        _$util.getParent(this).scope._model[tableKey].cos.push(sos);
    },
    getBodyRows: function (billKey, tbKey, modelKey, row, multiKey) {
        var me = this;
        debugger;
        multiKey = BASE64.encoder(multiKey);
        // jQuery.post(url,data,success(data, textStatus, jqXHR),dataType)
        $.post("/bill/data/multi-select", {
            BillID: row.BillID,
            tbKey: tbKey,
            billKey: billKey,
            multiKey: multiKey
        }, function (result) {
            var _cos = result.data[modelKey].cos;
            for (var i = 0; i < _cos.length; i++) {
                _$util.getParent(me).scope._model[tbKey].cos.push(_cos[i]);
            }
        });

    },

    ajax: function (method, url, data) {
        var request = new XMLHttpRequest();
        return new Promise(function (resolve, reject) {
            request.onreadystatechange = function () {
                if (request.readyState === 4) {
                    if (request.status === 200) {
                        resolve(request.responseText);
                    } else {
                        reject(request.status);
                    }
                }
            };
            request.open(method, url);
            request.send(data);
        });
    },


    //接受其他指令的事件调用
    __initInstrctonMethodCallback: function () {
        var me = this;
        //事件：$_INSTRUCTION_METHOD_CALLBACK
        this.$on('$_INSTRUCTION_METHOD_CALLBACK', function (e, param) {
            /**
             *
             * 监听事件，可以监听的条件
             * (1)key相同，鉴别接受这是否匹配
             * (2)renderId:代表一次视图渲染变量
             * (2)单据载体是否显示还有其他中继接受者，如果有，则消息需要向下级接受者中继后处理，
             *     一直到末端接受者
             *
             */
            if (param.data.key == me.key && me.renderid == param.data.renderid && param.data.body.indexOf("@") == -1) {//
                try {
                    var result = null;
                    if (param.data.params && param.data.params[0] == "") {
                        result = me[param.data.method].apply(me);
                    } else {
                        result = me[param.data.method].apply(me, param.data.params);//参数处理
                    }

                    if (result) {
                        param.data.deferred.resolve(result);
                    } else {
                        param.data.deferred.resolve("");
                    }
                }
                catch (err) {
                    param.data.deferred.reject(param.data.method + "  not found.");
                }
                //删除消息
                _$util.getCDCache(me).delQueue(param);
            }
        });
    },

    /**
     * 解析函数
     * var func="@mykey.service(1, 2,3);var test='hello';@mykey.@mykey1.@mykey.send(1,test);"
     * var widget_call = func.match(/(@[a-zA-z0-9_.]+)*\([a-zA-z0-9_.,\s\"\']*\)\s*;/g);
     * @param func
     * @returns {*}
     */
    __resolve: function (func) {
        //解析调用格式,如：var func="@mykey.service(1, 2,3);var test='hello';@mykey.@mykey1.@mykey.send(1,test);"
        func = "var $this=this;" + func;
        var widget_call = func.match(/(@[\w.$&]+)+\(.*?\)\s*;/g);
        if (widget_call) {
            for (var i = 0; i < widget_call.length; i++) {
                try {
                    func = this.__processFunc(func, widget_call[i]);
                } catch (err) {
                    alert(func + " 解析失败，请检查业务脚本");
                }
            }
        }
        return func;
    },

    /**
     * 解析函数
     * @param func
     *          需要处理的函数
     * @param resolover
     *          序号解析的部分
     *          格式为：@widget_key_uuid.function();
     *                  @view_key.@widget_key.send();
     */
    __processFunc: function (func, callFunc) {
        //this.$emit('$_INSTRUCTION_METHOD_CALL', {key:w_shengqingdengji,method:loadDataSource,params:[],body:"消息体"});
        var key = callFunc.substring(1, callFunc.indexOf("."));//消息key，即消息的接受者
        //分析方法名称--就是消息本体
        var method = callFunc.substring(this.__getMethodIndex(callFunc), callFunc.indexOf("("));
        //消息参数
        var params = callFunc.substring(callFunc.indexOf("(") + 1, callFunc.indexOf(")")).split(",");
        var body = this.__getMessageBody(callFunc, key);//消息体:消息-@接受者
        //解析参数，参数可以为变量，常量值，不能为函数调用
        var refCallProxyFunc = "$this.__refCallProxy('$_INSTRUCTION_METHOD_CALL'," + JSON.stringify({
                key: key,
                method: method,
                body: body,
                params: "$$$",
                renderid: this.renderid
            }) + ");"
        var param_array_str = "[";
        for (var i = 0; i < params.length; i++) {
            param_array_str = param_array_str + params[i] + ",";
        }
        var param_array_str = param_array_str + "'']";
        refCallProxyFunc = refCallProxyFunc.replace("\"$$$\"", param_array_str);
        func = func.replace(callFunc, refCallProxyFunc);
        return func;
    },


    //消息体:消息-@接受者
    __getMessageBody: function (callFunc, receiver) {
        return callFunc.substring(receiver.length + 2);//.和@占用一个位置，故这里加上2
    },

    __getMethodIndex: function (callFunc) {
        var lastAt = callFunc.lastIndexOf("@");
        var methodIndex = -1;
        for (var i = lastAt; i < callFunc.length; i++) {
            if (callFunc[i] == ".") {
                methodIndex = i + 1;
                break;
            }
        }
        return methodIndex;
    },

    /**
     * 回调函数代理
     * @param event
     * @param data
     * @returns {deferred.promise|{then, catch, finally}}
     * @private
     */
    __refCallProxy: function (event, data) {
        var deferred = this.$q.defer();//创建一个deferred对象
        var promise = deferred.promise;
        data.deferred = deferred;
        this.$emit(event, data);
        return promise;//defer.promise用于返回一个promise对象
    },

    /**
     * 初始化触发器代码
     * @private
     */
    __initTriggers: function () {
        if (!(this.triggers == null || this.triggers == "")) {
            var fun_str = BASE64.decoder(this.triggers);
            //分析脚本中@调用
            try {
                var fun_def = JSON.parse(fun_str);
                for (var i = 0; i < fun_def.length; i++) {
                    if (!this.triggerFuns) {
                        this.triggerFuns = [];//触发器数组
                    }
                    var trigger = this.__resolve(fun_def[i]);
                    this.triggerFuns.push(new Function('scope', trigger));
                }
            } catch (error) {
                alert(this.key + " 触发器语法错误，请核对.");
            }

        }
    },

    /**
     * 初始器代码
     * @private
     */
    __initInit: function () {
        //发送修改广播
        //this.$broadcast('$_MODEL_CHANGE_$', this._model);
        var me = this;
        me.$on('$_MODEL_CHANGE_$', function () {
            if (!me._$status || me._$status != "loaded") {
                //控件初始化-init,控件初始化动作；
                if (!(me.init == null || me.init == "")) {
                    var fun_str = BASE64.decoder(me.init);
                    //分析脚本中@调用
                    var func = me.__resolve(fun_str);
                    try {
                        var fun = new Function('scope', func);//构造函数体
                        //在当前scope范围类执行
                        fun.apply(me);
                    } catch (err) {
                        alert(fun_str + " 动态构建函数失败");
                    }
                    me._$status = "loaded";
                }
            }
        });


    },
    __initPopwin: function () {
        //发送修改广播
        //this.$broadcast('$_MODEL_CHANGE_$', this._model);
        var me = this;

        if (!me._$status || me._$status != "loaded") {
            //控件初始化-init,控件初始化动作；
            if (!(me.init == null || me.init == "")) {
                var fun_str = BASE64.decoder(me.init);
                //分析脚本中@调用
                var func = me.__resolve(fun_str);
                try {
                    var fun = new Function('scope', func);//构造函数体
                    //在当前scope范围类执行
                    fun.apply(me);
                } catch (err) {
                    alert(fun_str + " 动态构建函数失败");
                }
                me._$status = "loaded";
            }
        }


    },


    /**
     * CheckRules代码
     * @private
     */
    __initCheckRules: function () {
        //初始化checkrule
        //1、type：1.1、内置验证规则  builtin
        //          1.2、正则表达式   regular
        //2、rule：内置规则或者正则表达式
        //3、msg：输出的错误信息
        //{"type":"builtin","rule":"email","msg":"请输入正确信息"}

        var me = this;
        var _parent = _$util.getParent(this).scope;

        //内置常用验证规则
        var notNull = /[\w\W]+/,
            len616 = /^[\w\W]{6,16}$/,
            num = /^\d+$/,
            num616 = /^\d{6,16}$/,
            cn = /^[\u4E00-\u9FA5\uf900-\ufa2d\w\.\s]+$/,
            cn618 = /^[\u4E00-\u9FA5\uf900-\ufa2d\w\.\s]{6,18}$/,
            postCode = /^[0-9]{6}$/,
            mobile = /^13[0-9]{9}$|14[0-9]{9}|15[0-9]{9}$|18[0-9]{9}$/,
            tel = /^0\d{2,3}-?\d{7,8}(-\d{1,6})?$/,
            telAndMobile = /(^13[0-9]{9}$|14[0-9]{9}|15[0-9]{9}$|18[0-9]{9}$)|(^0\d{2,3}-?\d{7,8}(-\d{1,6})?$)/,
            email = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            url = /^(\w+:\/\/)?\w+(\.\w+)+.*$/,
            idCard = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
            bankCard = /^(\d{16}|\d{19})$/;

        _parent.errList = [];
        // _parent.checkRuleLists=[];
        var element = angular.element("[key=" + me.key + "]");
        if(! _parent.checkRuleLists){
            _parent.checkRuleLists=[];
        };
        // 模拟验证数据
        // me.rules={"type":"builtin","rule":'idCard',"msg":"请输入真实的身份证号码"};
        if (!(me.checkrules == null || me.checkrules == "")) {
            var rules = BASE64.decoder(me.checkrules);
            var _rules = eval(rules);
            _parent.checkRuleLists.push({
                key: me.key,
                rule: _rules
            });

            var _rule = eval(_rules[0].rule);

            var _err = {
                key: me.key,
                err: _rules[0].msg,
                rule: _rule
            };

            //验证；
            if (me.ngModel) {
                me.ngModel.$viewChangeListeners.push(
                    function () {
                        checkRules(_err);
                    }
                );
            };

            element.on('focus', function () {
                checkRules(_err);
            });

            element.on('blur', function () {
                // element.removeClass("isError");
                // element.popover('hide');
            });
        } else if (me.required && (!me.checkrules || me.checkrules == "")) {
            var rules = [{"type": "builtin", "rule": notNull, "msg": "请输入带*的必填项！"}];
            _parent.checkRuleLists.push({
                key: me.key,
                rule: rules
            });
            var _err = {
                key: me.key,
                err: rules[0].msg,
                rule: rules[0].rule
            };
            if (me.ngModel) {
                me.ngModel.$viewChangeListeners.push(
                    function () {
                        checkRules(_err);
                    }
                );
            };

            element.on('focus', function () {
                checkRules(_err);
            });
        }
        ;

        //    验证方法
        function checkRules(_err) {
            var _elm = $("[key=" + me.key + "]");
            if ((me.ngModel && _err.rule.test(me.ngModel.$viewValue) == false) || me.ngModel.$viewValue == undefined) {
                _elm.addClass("isError");
                _elm.attr("data-content", _err.err);
                _elm.attr("data-placement", "auto bottom");
                _elm.attr("data-container", "body");
                _elm.attr("data-original-title", "");
                _elm.attr("data-trigger", "focus");
                _elm.popover('show');
                if (!checkIn(_err)) {
                    _parent.errList.push(_err);
                }
                ;
            } else {
                _elm.removeClass("isError");
                _elm.popover('destroy');
                delErr(_err);
            }
        };

        function checkIn(_err) {
            var i = _parent.errList.length;
            while (i--) {
                if (_parent.errList[i].key == _err.key) {
                    return true;
                }
            }
            return false;
        };

        function delErr(_err) {
            var i = _parent.errList.length;
            while (i--) {
                if (_parent.errList[i].key == _err.key) {
                    _parent.errList.splice(i, 1);
                }
            }
        }

    },

    /**
     * 格式器代码
     * @private
     */
    __initFormatters: function (input, formula) {
        var me = this;
        var _addressFormat;
        var _selectFormat;
        //格式化器代码
        if (formula) {
            for (var j = 0; j < input.length; j++) {
                input[j].shadowObj = {};
                for (var i = 0; i < formula.length; i++) {
                    if (formula[i].formatter) {
                        var __formatter = window.BASE64.decoder(formula[i].formatter);
                        __formatter = eval(__formatter);
                        var _arr = eval("(" + __formatter[0].formatExpr + ")");
                        var _type = _arr[0].type;
                        var _getType = _arr[0].local;
                        if (_getType) {
                            //本地格式化，数据集写在xml中
                            for (var f = 0; f < _arr[0].url.length; f++) {
                                if (input[j][formula[i].key] == _arr[0].url[f].value) {
                                    input[j].shadowObj[formula[i].key] = _arr[0].url[f].name;
                                }
                            }
                        } else {
                            //该分支格式化省市区
                            if (_type && _type == 'addressFormat') {
                                if (_addressFormat && _addressFormat.length > 0) {
                                    $.each(_addressFormat, function (k, item) {
                                        if (item.areaCode == input[j][formula[i].key]) {
                                            input[j].shadowObj[formula[i].key] = item.areaName;
                                        }
                                    })
                                } else {
                                    $.ajax({
                                        url: "/lib/file/addressFormat.json",//json文件位置
                                        type: "GET",//请求方式为get
                                        dataType: "json", //返回数据格式为json
                                        async: false,
                                        success: function (data) {//请求成功完成后要执行的方法
                                            //each循环 使用$.each方法遍历返回的数据date
                                            if(data && data.length==0){
                                                alert("格式化数据集加载错误！");
                                                return;
                                            }else{
                                                _addressFormat = data;
                                            };
                                            $.each(data, function (k, item) {
                                                if (item.areaCode == input[j][formula[i].key]) {
                                                    input[j].shadowObj[formula[i].key] = item.areaName;
                                                }
                                            })
                                        }
                                    });
                                }

                            } else {
                                //该分支格式化其他通用selectSrc
                                if (_arr && _arr[0].url) {
                                    var _src = _arr[0].url;
                                    var _dataType = _arr[0].dataType;
                                    if (_dataType) {
                                        //区分字典和bills
                                        var _shadowV = $.ajax({
                                            url: _src + "?type=" + _type + "&key=" + input[j][formula[i].key] + "&dataType=" + _dataType,
                                            async: false
                                        });
                                    } else {
                                        var _shadowV = $.ajax({
                                            url: _src + "?type=" + _type + "&key=" + input[j][formula[i].key],
                                            async: false
                                        });
                                    }

                                    if (_shadowV.responseText && _shadowV.responseText !== '') {
                                        var _shadowValue = eval("(" + _shadowV.responseText + ")");
                                        if (_shadowValue && _shadowValue.data.length > 0) {
                                            input[j].shadowObj[formula[i].key] = _shadowValue.data[0].v;
                                        } else {
                                            input[j].shadowObj[formula[i].key] = input[j][formula[i].key];
                                        }
                                    }
                                    //传src进来的分支end
                                } else {
                                    //普通通用格式化action
                                    if (_selectFormat && _selectFormat.length > 0) {
                                        var _data = _selectFormat[_type];
                                        if(!_data || _data.length==0){
                                            alert("格式化数据集加载错误！");
                                            return;
                                        }
                                        $.each(_data, function (k, item) {
                                            if (input[j][formula[i].key] == item.key) {
                                                input[j].shadowObj[formula[i].key] = item.value;
                                            }
                                        })
                                    } else {
                                        $.ajax({
                                            url: "/lib/file/selectFormat.json",//json文件位置
                                            type: "GET",//请求方式为get
                                            dataType: "json", //返回数据格式为json
                                            async: false,
                                            success: function (data) {//请求成功完成后要执行的方法
                                                //each循环 使用$.each方法遍历返回的数据date
                                                _selectFormat = data;
                                                var _data = data[_type];
                                                if(!_data || _data.length==0){
                                                    alert("格式化数据集加载错误！");
                                                  return;
                                                };
                                                $.each(_data, function (k, item) {
                                                    if (input[j][formula[i].key] == item.key) {
                                                        input[j].shadowObj[formula[i].key] = item.value;
                                                    }
                                                })
                                            }
                                        });
                                    }
                                }
                            }
                            //

                        }

                    } else {
                        input[j].shadowObj[formula[i].key] = input[j][formula[i].key];
                    }
                }
            }
            return input;
        } else {
            return input;
        }
    },
    /**
     * 初始化属性代码
     * @private
     */
    __initProperties: function () {

    },

    __initTriggerWatcher: function () {
        var me = this;
        var _watch = function () {//trigger代码
            if (me.triggerFuns) {
                for (var i = 0; i < me.triggerFuns.length; i++) {
                    me.triggerFuns[i].apply(me);
                }
            }
        };

        if (me.ngModel) {
            me.ngModel.$viewChangeListeners.push(
                _watch
            );
        }

    },
    __initOnClickHandlerWatcher:function(){
            var me = this;
            if (!(me.onClickHandler == null || me.onClickHandler == "")) {
                var fun_str = BASE64.decoder(me.onClickHandler);
                //分析脚本中@调用
                try {
                    var fun_def = JSON.parse(fun_str);
                    for (var i = 0; i < fun_def.length; i++) {
                        if (!me.onclickFuns) {
                            me.onclickFuns = [];//触发器数组
                        }
                        var trigger = me.__resolve(fun_def[i]);
                        me.onclickFuns.clear();
                        me.onclickFuns.push(new Function('scope', trigger));
                    }
                } catch (error) {
                    alert(me.key + " 单击事件语法错误，请核对.");
                }
            };
    },

    $execute: function () {
        this.__initInstrctonMethodCallback();
        this.__initProperties();
        this.__initInit();
        this.__initTriggers();
        this.__initCheckRules();
        this.__initFormatters();
        this.__initTriggerWatcher();
        this.__initOnClickHandlerWatcher();
    },

    $executePopup: function () {
        this.__initInstrctonMethodCallback();
        this.__initProperties();
        // this.__initInit();
        this.__initTriggers();
        this.__initCheckRules();
        this.__initFormatters();
        // this.__initTriggerWatcher();
        this.__initOnClickHandlerWatcher();
    }
};

//模型服务
myDirectiveModule.factory("modeService", ['$http', function ($http) {
    //模型初始化
    var modeInit = function (postData) {
        return $http.post("/bill/data",
            $.param(postData),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }
        )
    };

    return {
        modeInit: function (postData) {
            return modeInit(postData);
        }

    }
}]);

//select的数据源服务
myDirectiveModule.factory("selectDSService", ['$http', function ($http) {
    //加载数据源datasource
    /**
     * datasource:{datasource:{type:remote|internal,value:"" or [{key:value,key:value}]}}
     * @param datasource
     * @returns {HttpPromise}
     *
     */
    var load = function (datasource) {
        if (datasource.type == "remote") {
            return $http.post("/bill/data",
                $.param(postData),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }
            ).sucess(function (data) {
                datasource.value = data;
            }).error(function (data) {
                alert('selectDSService……' + data);
            });
        } else if (datasource.type == "internal") {//内部数据源
            if (!(datasource.value instanceof Array)) {
                alert("内部数据源错误");
            }
        } else {
            alert("不支持的datasource格式");
        }

    };


    return {
        load: function (datasource) {
            return load(datasource);
        }

    }
}]);


//通用  注入js&css链接到head部分方法
myDirectiveModule.factory('Common', [
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
 *    billuiview指令
 */
myDirectiveModule.directive('billuiview', function ($http, $compile) {
    return {
        restrict: "EA",
        scope: {
            src: "@",
            renderid: "@",
            param: "@", //参数
            cosarchetype: "@",
            field:"@"
        },
        //控制
        controller: function ($scope, $element, $attrs, $transclude) {

        },
        link: function (scope, element, attrs, ngModel) {
            scope.$emit("__$_view_created__$__", {scope: scope});
            //视图标志
            scope._$$view$$ = "_$$view$$";
            //寻找我的父
            var parent = _$util.getParent(scope);
            if (!parent.scope._model) {
                alert("view的父模型构建不完整.");
            }
            scope._model = new $Model(parent.scope._model);//构建视图的数据模型
            scope._env = new $Env(parent.scope._env); // 构建视图的环境

            scope.viewInit = function () {
                scope.loadDataSet();
            };
            scope.bindModel = function (data) {
                if (!data || !data._models) {
                    return;
                }
                for (var i = 0; i < data._models.length; i++) {
                    var dataObject = data[data._models[i]];
                    scope._model[data._models[i]] = dataObject;
                    dataObject.co = dataObject.cos[0];
                    dataObject.sos = [];
                    dataObject.dels = [];
                    dataObject.archetype.shadowCol = [];
                    if (dataObject.head) {
                        scope[data._models[i]] = dataObject.co;
                        scope["$" + data._models[i]] = dataObject;
                    } else {
                        if (scope.cosarchetype && scope.cosarchetype !== '') {
                            var _cosarchetype = JSON.parse(window.BASE64.decoder(scope.cosarchetype));
                            for (var j = 0; j < _cosarchetype.length; j++) {
                                if(scope.field){
                                    if (_cosarchetype[j][scope.field]) {
                                        for (var k = 0; k < dataObject.cos.length; k++) {
                                            if (_cosarchetype[j][scope.field] == dataObject.cos[k][scope.field]) {
                                                //  用目标单的cos中的sourceid和源单中的BilldtlID 或者 billID对比，如果有一个相同就禁用
                                                //    判断视图中是否有已引用的行，如果有则禁用
                                                dataObject.cos[k].isDisable = true;
                                            }
                                        }
                                    }
                                }else{
                                    if (_cosarchetype[j].SourceID) {
                                        for (var k = 0; k < dataObject.cos.length; k++) {
                                            if (_cosarchetype[j].SourceID == dataObject.cos[k].BillDtlID || _cosarchetype[j].SourceID == dataObject.cos[k].BillID || _cosarchetype[j].SourceID == dataObject.cos[k].OID) {
                                                //  用目标单的cos中的sourceid和源单中的BilldtlID 或者 billID对比，如果有一个相同就禁用
                                                //    判断视图中是否有已引用的行，如果有则禁用
                                                dataObject.cos[k].isDisable = true;
                                            }
                                        }
                                    }
                                }


                            }
                        }
                        ;
                        if (dataObject.cos && dataObject.cos.length > 0) {
                            for (var p = 0; p < dataObject.cos.length; p++) {
                                dataObject.cos[p].shadowCol = [];
                            }
                        }
                        scope["__$" + data._models[i] + "$__"] == dataObject.cos;
                        scope["$" + data._models[i]] = dataObject;
                        window.$body[data._models[i]] = dataObject;
                    }
                }
            };
            /**
             * @param data
             */
            scope.loadDataSet = function () {
                var req = {};
                clone(scope._env, req);
                $http.post("/bill/data/viewport", $.param(req),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }).success(function (data) {
                    if (data.status == 0) {//action执行异常
                        alert('billuiview………' + data.error);
                    } else if (data.status == 1) {//正常返回数据
                        if (data.data) {
                            // scope.bindModel(data.data);
                            //发送修改广播，把私有模型的变化传递下去
                            // scope.$broadcast('$_MODEL_CHANGE_$', scope._model);
                            scope.$broadcast('$_MODEL_CHANGE_$', data.data);
                        }
                    } else if (data.status == 2) {//正常返回，但没有数据
                        console.log("no data.")
                    } else {
                        alert("billuiview--" + scope.key + "未知异常");
                    }
                }).error(function (err) {
                    console.log(err);
                });
            };
            /**
             * 接受上级传递的模型修改广播，重新绑定
             */
            scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                scope.bindModel(model);
            });

            //事件：$_INSTRUCTION_METHOD_CALLBACK
            scope.$on('$_INSTRUCTION_METHOD_CALLBACK', function (e, param) {
                //发送给我的消息
                if (param.data.key == attrs.key && scope.renderid == param.data.renderid && param.data.body.indexOf("@") == -1) {
                    try {
                        var result = null;
                        if (param.data.params && param.data.params[0] == "") {
                            result = scope[param.data.method].apply(scope);
                        } else {
                            result = me[param.data.method].apply(scope, param.data.params);//参数处理
                        }
                        if (result) {
                            param.data.deferred.resolve(result);
                        } else {
                            param.data.deferred.resolve("");
                        }
                    } catch (err) {
                        param.data.deferred.reject(param.data.method + "  not found.");
                    }
                    //删除消息
                    _$util.getCDCache(me).delQueue(param);
                } else if (param.data.key == attrs.key && scope.renderid == param.data.renderid && param.data.body.indexOf("@") == 0) {
                    /*我需要中继处理的消息,处理操序列如下：
                     (1)renderid换为subrenderid
                     (2)key替换为subkey
                     (3)body中去掉@**.内容，并且**==key
                     */
                    if (scope.subrenderid) {
                        var subkey = _getSubKeyFromMessageBody(param.data.body);
                        param.data.key = subkey;
                        param.data.body = param.data.body.substring(subkey.length + 2);
                        param.data.renderid = scope.subrenderid;
                    }
                }
            });

            var _getSubKeyFromMessageBody = function (mbody) {
                return mbody.substring(1, mbody.indexOf("."));
            }
            scope.url = "/bill/view/viewport?billKey=" + scope.src;
            //请求
            scope.requestUrl = function (url) {
                $http.post(scope.url).success(function (responseData) {
                    scope.subrenderid = responseData.renderid;//子渲染id
                    var env = responseData.env;
                    clone(env, scope._env);
                    var el = $compile(responseData.html)(scope);//编译，这里需要获取视图的，当时视图需要获取自己的渲染id
                    element.append(el);
                    //$("#aa").append(el);
                    scope.viewInit();
                }).error(function (data, status, headers, config) {
                    alert("请求视图：" + data);
                });
            };
            //请求数据
            scope.requestUrl(scope.url);

        }
    }
});


// billuirichtext  富文本编辑器
// 调用方法，  <div id="editor" billuirichtext ng-model=""></div>
myDirectiveModule.directive('billuirichtext', function (Common, $rootScope, $http, $q) {
    return {
        restrict: 'EA',
        require: '?ngModel',
        scope: {
            options: "=", // = 对象，@字符串，&方法
            bound: "@",//
            triggers: "@",
            checkRules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
        },
        link: function (scope, ele, attrs, ctrl) {
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            // $rootScope.$emit('loading', '初始化编辑器...');//广播loading事件，可以删除
            var _self = this,
                _initContent,
                editor,
                editorReady = false,
                base = 'lib/ueditor', //ueditor目录
                _id = attrs.ueditor;
            var editorHandler = {
                init: function () {
                    window.UEDITOR_HOME_URL = base + '/';
                    var _self = this;
                    if (typeof UE != 'undefined') {
                        editor = UE.getEditor(_id, {
                            toolbars: [
                                [
                                    'fullscreen', 'source', '|', 'undo', 'redo', '|',
                                    'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
                                    'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
                                    'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
                                    'directionalityltr', 'directionalityrtl', 'indent', '|',
                                    'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
                                    'link', 'unlink', 'anchor', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
                                    'simpleupload', 'insertimage', 'emotion', 'scrawl', 'insertvideo', 'music', 'attachment', 'map', 'gmap', 'insertframe', 'insertcode', 'pagebreak', 'template', 'background', '|',
                                    'horizontal', 'date', 'time', 'spechars', 'wordimage', '|',
                                    'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
                                    'print', 'preview', 'searchreplace', 'drafts'
                                ]
                            ]
                        });

                        editor.ready(function () {
                            editor.setHeight(800);
                            // $rootScope.$emit('loading', '');//编辑器初始化完成
                            editor.addListener('contentChange', function () {//双向绑定
                                if (!scope.$$phase) {
                                    scope.$apply(function () {
                                        ctrl.$setViewValue(editor.getContent());
                                    });
                                }
                            });
                        });
                    }
                    else {
                        Common.loadScript(base + '/ueditor.config.js', null);
                        Common.loadScript(base + '/ueditor.all.min.js', function () {
                            _self.init();
                        });
                    }
                },
                setContent: function (content) {
                    var _render = function () {
                        if (editor && content) {
                            editor.setContent(content);
                        }
                    }
                    window.setTimeout(_render, 500);
                }
            };
            ctrl.$render = function () {
                _initContent = ctrl.$isEmpty(ctrl.$viewValue) ? '' : ctrl.$viewValue;
                editorHandler.setContent(_initContent);//双向绑定
            };
            editorHandler.init();

            //事件
            $rootScope.$on('$routeChangeStart', function () {
                editor && editor.destroy();
            });
            $rootScope.resetContent = function () {
                ctrl.$setViewValue("");
            };
            scope.$execute();
        }
    }
});

// 图形
// <div chart class="echart" ec-config="lineConfig" ec-option="lineOption" ></div>
myDirectiveModule.directive("billuichart", function ($http, $q, Common) {
    return {
        scope: {
            bound: "@",//
            triggers: "@",
            checkRules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            datasetkey: "@",
            datatablekey: "@",
            charttype: "@",
            axis: "@",
            series: "@",
            legend: "@",
            grouptype: "@",
            groudby: "@",

            float: "@",
            toolbox: "@",
            tooltip: "@",
            title: "@",
            datasettype: "@"
        },
        restrict: "EA",
        link: function (scope, element, attrs, ngModel) {
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parentScope = _$util.getParent(scope).scope;
            var req = {};
            clone(_parentScope._env, req);
            if (!scope.datatablekey || scope.datatablekey == "") {
                alert("dataTableKey 不存在！");
                return;
            }
            ;
            scope.allModel = _parentScope._model[scope.datatablekey];
            req.datasettype = scope.datasettype;
            req.datatablekey = scope.datatablekey;
            req.charttype = scope.charttype;
            req.datasetkey = scope.datasetkey;
            req.axis = scope.axis;
            req.series = scope.series;
            req.legend = scope.legend;
            req.grouptype = scope.grouptype;
            req.groudby = scope.groudby;
            req.model = scope.allModel;

            //判断是否从页面内取模型，如果从页面内取模型，则执行watch

            scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                if (scope.datasettype == "model") {
                    scope.allModel = _parentScope._model[scope.datatablekey];
                    var _mode = {};
                    _mode[scope.datatablekey] = scope.allModel;
                    req.model = angular.toJson(_mode);
                    scope.$watch(scope.allModel, function (newValue, oldValue) {
                        scope.loadDataSource();
                    });
                }
                ;
            });

            scope.loadDataSource = function (param) {
                if (typeof echarts != 'undefined') {
                    scope.initCharts(param);
                } else {
                    try {
                        Common.loadScript('/lib/js/echarts/echarts.min.js', function () {
                            scope.initCharts(param);
                        });
                    } catch (e) {
                        alert('charts资源没有正确加载！')
                    }
                }
            };

            scope.initCharts = function (param) {
                NProgress.start();
                var params = {};
                clone(req, params);
                if (param) {
                    clone(param, params);
                }
                $http.post("/bill/data/charts", $.param(params), {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }).success(function (data) {
                    if (!data.result.legend) {
                        alert('图表信息异常');
                        NProgress.done();
                        element.hide();
                        return;
                    } else {
                        element.show();
                    }
                    if (scope.charttype == "GenerialChart") {

                        scope.option = {
                            backgroundColor: "#fff",
                            title: {
                                text: scope.title,
                                x: 'center'
                            },
                            tooltip: {
                                show: scope.tooltip,
                            },

                            legend: {
                                data: data.result.legend,
                                top: "30px",
                            },
                            grid: {
                                top: '20%',
                                left: '10%',
                            },
                            xAxis: {
                                type: 'category',
                                data: data.result.axis,
                            },
                            yAxis: {
                                type: 'value'
                            },
                            series: data.result.series
                        };

                    } else if (scope.charttype == "ScatterChart") {
                        scope.option = {
                            backgroundColor: "#fff",
                            title: {
                                text: scope.title,
                                x: 'center'
                            },
                            tooltip: {
                                show: scope.tooltip,
                                formatter: "{a} <br/>{b}: {c} ({d}%)"
                            },

                            legend: {
                                data: data.result.legend,
                                orient: 'vertical',
                                left: 'left',
                            },
                            grid: {
                                top: '20%',
                                left: '10%',
                            },
                            series: data.result.series
                        };
                    } else {
                        scope.option = {
                            backgroundColor: "#fff",
                            title: {
                                text: scope.title,
                                x: 'center'
                            },
                            tooltip: {
                                show: scope.tooltip,
                            },

                            legend: {
                                data: data.result.legend,
                                orient: 'vertical',
                                left: 'left',
                            },
                            grid: {
                                top: '20%',
                                left: '10%',
                            },
                            xAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            series: data.result.series
                        };
                    }
                    var myChart = echarts.init(element[0]);
                    myChart.setOption(scope.option);
                    NProgress.done();
                }).error(function (err) {
                    NProgress.done();
                    alert('图表信息异常' + err);
                });
            };
            //    拖拽方法
            scope.drag = function () {
                // var MeTrue = scope.candrag;
                $(element).mousedown(function (e) {
                    var $this = $(element);
                    var MePx = e.pageX;//获取鼠标X坐标
                    var MePy = e.pageY;//获取鼠标Y坐标
                    var MeX = $this.offset().left;//元素到左边距离
                    var MeY = $this.offset().top;//元素到头部距离
                    var MeLeft = MePx - MeX;//鼠标与元素左边相差距离
                    var MeTop = MePy - MeY;//鼠标与元素上边相差距离
                    var WinW = $(window).width() - $this.width();//用于元素不能右侧溢出窗口
                    var WinH = $(window).height() - $this.height();//用于元素不能底部溢出窗口
                    var MeTrue = true;
                    $(document).mousemove(function (e) {
                        if (MeTrue) {
                            var MePx = e.pageX;//获取鼠标X坐标
                            var MePy = e.pageY;//获取鼠标Y坐标
                            var Left = MePx - MeLeft;//元素左侧距离等于鼠标坐标减元素与鼠标左边相差距离
                            var Top = MePy - MeTop;//元素左侧距离等于鼠标坐标减元素与鼠标上边相差距离
                            //开始判断左右是否溢出窗口
                            if (Left <= 0) {
                                Left = 0;
                            } else if (Left > WinW) {
                                Left = WinW;
                            }
                            ;
                            //开始判断上下是否溢出窗口
                            if (Top <= 0) {
                                Top = 0;
                            } else if (Top > WinH) {
                                Top = WinH;
                            }
                            ;
                            $this.css({'left': Left, 'top': Top});
                        }
                        ;
                    });
                    $(document).mouseup(function (e) {
                        if (MeTrue) {
                            MeTrue = false;
                            var MePx = e.pageX;//获取鼠标X坐标
                            var MePy = e.pageY;//获取鼠标Y坐标
                            var Left = MePx - MeLeft;//元素左侧距离等于鼠标坐标减元素与鼠标左边相差距离
                            var Top = MePy - MeTop;//元素左侧距离等于鼠标坐标减元素与鼠标上边相差距离
                            $this.css({'left': Left, 'top': Top});
                        }
                        ;
                    });
                });
            };
            if (scope.float == "suspension") {
                $(element).css({'cursor': 'all-scroll', 'position': 'absolute', 'z-index': '98'});
                scope.drag();
            }
        },

    }
});


// 树 billuitree
myDirectiveModule.directive('billuitree', function ($http, $q, Common) {
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
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用

            scope._$curTreeNode = null;
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            ////树节点click的时候抛出事件以及当前节点
            scope.zTreeOnClick = function (event, treeId, treeNode) {
                var parent = _$util.getParent(scope);
                if (!parent.scope._model) {
                    alert("Tree父模型构建不完整.");
                }
                var entityKey = "$t_" + scope.tableKey;
                var _treeNode = scope.tree.getSelectedNodes();
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
                var parent = _$util.getParent(scope);
                var entityKey = "$t_" + scope.tableKey;
                parent.scope._model[entityKey] = scope.tree.getNodes();
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
            }
            ;
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
                _initSelected(__node);
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
                //寻找父
                var parent = _$util.getParent(scope);
                if (!parent.scope._model) {
                    alert("Tree父模型构建不完整.");
                }
                var entityKey = "$t_" + scope.tableKey;
                if (parent.scope._model[entityKey]) {
                    if (scope.treemodel && scope.treemodel != parent.scope._model[entityKey]) {
                        scope.treemodel = parent.scope._model[entityKey];
                        //模型赋值之后进行树初始化
                        scope.loadTree();
                    }
                }
            };

            scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                scope.bindRowsModel();
            });
            scope.$execute();
        }
    }
});


/*****************************************************************************
 * *****************************************************************************
 *    billui系列指令
 * *****************************************************************************
 ******************************************************************************/
/*
 billuiselect指令
 */
myDirectiveModule.directive('billuiselect', function ($http, $q, Common, $timeout, $interval) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        template: "<select ng-options=\"x.value as x.name for x in datasource.value\"></select>",
        // templateUrl: "/Views/_select.html",
        replace: true,
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            sourceSrc: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.datasource = {
                value: []
            };
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            if (scope.$parent._env && scope.$parent._env.alldisabled) {
                element.attr("disabled", "true");
            };

            scope.$execute();

            //解析property
            scope.clearDataSource = function () {
                scope.datasource.value.clear();
            };

            var _parent = _$util.getParent(scope).scope;
            var _billkey = _parent._env.billkey;
            scope.loadDataSource = function (src) {
                if (src && src !== '') {
                    $http.get(src).success(function (result, status, headers, config) {
                        var _ds = [];
                        var data = result.data;
                        if (data && data !== "") {
                            for (var i = 0; i < data.length; i++) {
                            	if(src.indexOf("|")>0){
                            		 _ds.push({
                                     	"name": data[i].v,
                                         "value": data[i].k
                                     	 
                                     });
                            	}else{
                            		_ds.push({
                            			"name": data[i][attrs.k],
                            			"value": data[i][attrs.v]
                            		});
                            	}
                            }
                            scope.datasource.value.clear();
                            scope.datasource.value.addAll(_ds);
                        }
                    }).error(function (data, status, headers, config) {
                        console.log(status);
                    });
                } else {
                    var _cacheSelect = window.sessionStorage.getItem(_billkey + '-' + scope.key);
                    if (_cacheSelect && _cacheSelect !== '') {
                        var _ds = JSON.parse(_cacheSelect);
                        scope.datasource.value.clear();
                        scope.datasource.value.addAll(_ds);
                    } else if (scope.sourceSrc && scope.sourceSrc !== "") {
                        // var _src = scope.sourceSrc;
                        var _arr=scope.sourceSrc.split("=");
                        var _type=_arr[1];
                        var _src = "/lib/file/selectFormat.json";
                        $http.get(_src).success(function (result, status, headers, config) {
                            var _ds = [];
                            var data = result[_type];
                            if (data && data !== '') {
                                for (var i = 0; i < data.length; i++) {
                                    _ds.push({
                                        "name": data[i].value,
                                        "value": data[i].key
                                    });
                                }
                                scope.datasource.value.clear();
                                scope.datasource.value.addAll(_ds);
                                window.sessionStorage.setItem(_billkey + '-' + scope.key, JSON.stringify(_ds));
                            }
                        }).error(function (data, status, headers, config) {
                            console.log(status);
                        });

                    } else if (scope.properties && scope.properties !== "") {
                        //property:{name:"datasource",type:remote|internal,value:"" or {} or []}
                        var ds_array = eval(BASE64.decoder(scope.properties));
                        for (var i = 0; i < ds_array.length; i++) {
                            if (ds_array[i].name == "datasource") {
                                scope.datasource.value.clear();
                                scope.datasource.value.addAll(ds_array[i].value);
                                break;
                            }
                        }


                        if (!scope.datasource) {
                            alert("没有正确设置数据源datasource属性，select控件无法工作");
                        }

                    } else {
                        alert("没有设置数据源datasource属性，select控件无法工作");
                    }
                    ;
                    //

                    if (scope.datasource.value && scope.datasource.value.length > 15) {
                        //option大于5的时候 显示筛选
                        if (typeof $.fn.select2 != 'undefined') {
                            $(element).select2();
                            scope
                        } else {
                            try {
                                Common.loadCss('/lib/js/select2/css/select2.css', null);
                                Common.loadScript('/lib/js/select2/js/select2.full.min.js', function () {
                                    $(element).select2();
                                });
                            } catch (e) {
                                alert('Select资源没有正确加载！')
                            }
                        }
                    }
                }
            };
            //    移除数据集方法
            scope.removeData = function (data) {
                var _dsl = scope.datasource.value;
                scope._d=data;
                if (!_dsl.length || _dsl.length==0) {
                    $timeout(function () {
                        scope.removeData(scope._d);
                    },1);
                } else {
                    scope.realDel(data);
                }
            };

            scope.realDel=function (data) {
                var delVal = data.split(',');
                for (var i = 0; i < scope.datasource.value.length; i++) {
                    for (var j = 0; j < delVal.length; j++) {
                        if (scope.datasource.value[i].value == parseFloat(delVal[j])) {
                            scope.datasource.value.delByValue(scope.datasource.value[i]);
                        };
                    }
                }
            }
            //
            ngModel.$viewChangeListeners.push(
                function () {
                    ngModel.$setViewValue(parseInt(ngModel.$viewValue));
                    ngModel.$render();
                }
            );
        }
    }
});


/*
 billuistring
 */

myDirectiveModule.directive('billuistring', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            datatablekey: "@",
            key: "@",
            renderid: "@",
            required: "@",
            onClickHandler: "@",
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }

            if (attrs.zhujima == "") {
                scope.$executePopup();
                scope.__initPopwin();
                if (scope.properties && scope.properties !== "") {
                    var ds = eval(BASE64.decoder(scope.properties));
                    for (var i = 0; i < ds.length; i++) {
                        if (ds[i].name == "dataTable") {
                            var _billKey = ds[i].value;
                            break;
                        } else {
                            var _billKey = "shangpinjibenxinxi";
                        }
                    }
                } else {
                    var _billKey = "shangpinjibenxinxi";
                }

                var el = $compile('<i class="searchIcon glyphicon glyphicon-search" ng-click="searchzjm()"></i>')(scope);
                element.parent().append(el);
                var _parent = _$util.getParent(scope).scope;
                // var _billKey = _parent._env._$parent.billkey;
                //助记码回车事件
                element.keydown(function (event) {
                    var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
                    switch (myEvent.keyCode) {
                        case 13://delete事件
                            scope.searchzjm();
                            break;
                    }
                });
                scope.searchzjm = function () {
                    if (element.val() == '') {
                        alert("助记码不能为空！！！");
                        return;
                    };
                    NProgress.start();
                    var req = {
                        billkey: _billKey,
                        params: element.val()
                    };
                    $http.post("/bill/data/dic-like", $.param(req), {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }).success(function (data) {
                        if (data.data && data.data.head !== '') {
                            scope._zhujimaheads = data.data.head;
                            scope._zhujimaBody = data.data.body;
                            if (scope._zhujimaBody.length == 0) {
                                alert('暂无该助记码相关信息！');
                            }else if(scope._zhujimaBody.length == 1){
                                scope.row = scope._zhujimaBody[0];
                                var me = scope;
                                if (me.triggerFuns) {
                                    for (var i = 0; i < me.triggerFuns.length; i++) {
                                        me.triggerFuns[i].apply(me);
                                    }
                                }
                            } else {
                                var html = '<tr> <th ng-repeat="x in _zhujimaheads" style="background-color: lightgray">{{x.colName}}</th></tr>' +
                                    '<tr ng-repeat="x in _zhujimaBody" ng-click="selRow(x)">' +
                                    '<td ng-repeat="col in _zhujimaheads">' +
                                    '<span>{{x[col.colkey]}}</span>' +
                                    '</td>' +
                                    '</tr>' +
                                    '<tr ng-if="_zhujimaBody.length==0"><td colspan="100" align="center">暂无数据…</td></tr>';
                                var _el = $compile(html)(scope);
                                $('#zhujimaModal .modal-dialog').css('width', '80%');
                                $('#zhujimaModal .modal-body table').html(_el);
                                $('#zhujimaModal').modal('show');
                            }

                        } else {
                            alert("请检查助记码数据表是否正确！");
                        }
                        NProgress.done();
                    }).error(function (err) {
                        console.log(err);
                        NProgress.start();
                    });
                };
                scope.selRow = function (row) {
                    scope.row = row;
                    var me = scope;
                    if (me.triggerFuns) {
                        for (var i = 0; i < me.triggerFuns.length; i++) {
                            me.triggerFuns[i].apply(me);
                        }
                    }
                };
            } else {
                scope.$execute();
            }
        }
    }
});
/*
 billuibarcode
 */

myDirectiveModule.directive('billuibarcode', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            datatablekey: "@",
            key: "@",
            renderid: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }


            element.keydown(function (event) {
                var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
                switch (myEvent.keyCode) {
                    case 13://delete事件
                        scope.loadTest();
                        break;
                }
            });

            scope.loadTest = function () {
                var req = {
                    billkey: "shangpinjibenxinxi",
                    params: element.val()
                };
                scope._ngModel = _parent._model[scope.datatablekey].co[scope.key];
                $http.post("/bill/data/dic-eq", $.param(req), {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }).success(function (data) {
                    if (data.data && data.data.head !== '') {
                        if (data.data.body && data.data.body.shangpintiaoma) {
                            element.attr('readonly', "readonly")
                            scope.row = data.data.body;
                            var me = scope;
                            if (me.triggerFuns) {
                                for (var i = 0; i < me.triggerFuns.length; i++) {
                                    me.triggerFuns[i].apply(me);
                                }
                            }
                        } else {
                            element.val('');
                            scope.ngModel.$setViewValue('');
                            alert("未找到该条形码信息！");
                        }
                    } else {
                        element.val('');
                        scope.ngModel.$setViewValue('');
                        alert("未找到该条形码信息！");
                    }
                }).error(function (err) {
                    alert(err);
                });
            };


        }
    }
});


/*
 BillUITextArea
 */

myDirectiveModule.directive('billuitextarea', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }

        }
    }
});


/*
 BillUIInteger
 */

myDirectiveModule.directive('billuiinteger', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }

            //禁止输入数字以及负数
            // ngModel.$viewChangeListeners.push(
            //     function () {
            //         if (ngModel.$viewValue.toString().indexOf(".") > -1) {
            //             ngModel.$setViewValue(ngModel.$viewValue.substring(0, ngModel.$viewValue.indexOf(".")));
            //             ngModel.$render();
            //         }
            //     }
            // );

        }
    }
});


/**
 * virtualField
 * outputMode:
 *
 */

myDirectiveModule.directive('billuiexpression', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            key: "@",
            renderid: "@",
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            element.addClass('exprInput disabled');
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            mixer(exprLib, scope)
            scope.$execute();
            scope._$parentScope = _$util.getParent(scope).scope;
            //this["$"+data._models[i]]=dataObject;
            //_$parentScope["$"+key].co.

            var _array = eval(BASE64.decoder(scope.properties));
            scope.expr = _array[0].value;
            scope.findRowObject = function () {
                var parent = scope.$parent;
                while (!parent.row) {
                    parent = parent.$parent;
                    if (!parent) {
                        return;
                    }
                }
                return parent.row;
            };

            scope.findCoObject = function () {
                var parent = scope.$parent;
                while (!parent.co) {
                    parent = parent.$parent;
                    if (!parent) {
                        return;
                    }
                }
                return parent.co;
            };

            scope.row = scope.findRowObject();

            if (!scope.row) {
                scope.row = scope.findCoObject();
            };
            var preParse = function (expr) {
                var p = /#(\w*?)[.]/g;
                var p_g = expr.match(p);
                if (p_g) {

                    for (var i = 0; i < p_g.length; i++) {
                        var item = p_g[i];//#ssss.

                        var entityKey = item.substr(1, item.length - 2);
                        expr = expr.replace(item, "row.");
                    }
                }
                expr = expr.replace(/\s/g, '');
                return expr.replaceAll("@", "_$parentScope.").substr(1);
            };

            if (scope.expr.indexOf("=") > -1) {
                scope.expred = preParse(scope.expr);
                scope.$watch(scope.expred, function (newValue, oldValue) {
                    // scope.row = scope.findRowObject();
                    if(isNaN(newValue)==true){
                        newValue=0;
                    }
                    scope.row[attrs.key] = newValue;
                    scope.row.shadowObj[attrs.key] = newValue;
                    ngModel.$modelValue = newValue;
                    element.val(newValue);
                    //触发trigger代码
                    var me = scope;
                    if (me.triggerFuns) {
                        for (var i = 0; i < me.triggerFuns.length; i++) {
                            me.triggerFuns[i].apply(me);
                        }
                    }
                    ;
                    //判断有没有隐藏列
                    if (scope.row.shadowCol && !scope.row.shadowCol.contains(attrs.key)) {
                        scope.row.shadowCol.push(attrs.key);
                    }
                });
            } else {
                element.html(scope.expr);
            }
            ;
        }
    }
});

/*
 BillUILong
 */

myDirectiveModule.directive('billuilong', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            required: "@"
        },


        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }
            ngModel.$viewChangeListeners.push(
                function () {
                    if (ngModel.$viewValue.indexOf(".") > -1) {
                        ngModel.$setViewValue(ngModel.$viewValue.substring(0, ngModel.$viewValue.indexOf(".")));
                        ngModel.$render();
                    }
                }
            );
        }
    }
});

/*
 BillUIDecimal
 */

myDirectiveModule.directive('billuidecimal', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }
            scope.$execute();
            // ngModel.$viewChangeListeners.push(
            //     function () {
            //         var _check = /^(?:[1-9]+\d*|0)(\.\d+)?$/
            //         if (!_check.test(ngModel.$viewValue)) {
            //             ngModel.$setViewValue('0');
            //             ngModel.$render();
            //         }
            //     }
            // );
        }
    }
});


myDirectiveModule.directive('stringToNumber', function () {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ngModel) {
            ngModel.$parsers.push(function (value) {
                return '' + value;
            });
            ngModel.$formatters.push(function (value) {
                    return parseFloat(value);
            });
        }
    };
});
/*
 BillUILabel
 */

myDirectiveModule.directive('billuilabel', function ($http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();

            element.bind("click", function () {
                var me = scope;
                if (me.triggerFuns) {
                    for (var i = 0; i < me.triggerFuns.length; i++) {
                        me.triggerFuns[i].apply(me);
                    }
                }

            });
        }
    }
});

/*
 BillUIButton
 */

myDirectiveModule.directive('billuibutton', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            exportsheets: "@",
            importsheets: "@",
            filename: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            $('html').append()

            var _parent = _$util.getParent(scope).scope;

            if (scope.exportsheets && scope.exportsheets !== '') {
                var envExportsheets = {
                    'exportsheets': scope.exportsheets
                };
                clone(envExportsheets, _parent.$parent._env);

            }
            if (scope.importsheets && scope.importsheets !== '') {
                var envImportSheets = {
                    'importsheets': scope.importsheets
                };
                clone(envImportSheets, _parent.$parent._env);

            }
            if (scope.filename && scope.filename !== '') {
                var envFileName = {
                    'filename': scope.filename
                };
                clone(envFileName, _parent.$parent._env);

            }

            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
                if ($(element).attr('key') == 'goback' || $(element).attr('key') == 'process'|| $(element).attr('key') == 'print'|| $(element).attr('key') == 'prePrint') {
                    $(element).removeAttr("disabled");
                }
            }

            scope.setGridValue = function (datakey, key, value) {
                if (datakey && key && _parent._model[datakey]) {
                    for (var i = 0; i < _parent._model[datakey].cos.length; i++) {
                        _parent._model[datakey].cos[i][key] = value;
                    }
                }
            };
            scope.search = function (entityKey) {
                NProgress.start();
                var $parentScope = _$util.getParent(scope).scope;
                var url = "/bill/data/refresh"
                var req = {};

                clone($parentScope._env, req);
                if (entityKey) {
                    var mode = {};
                    mode[entityKey] = {
                        page: $parentScope._model[entityKey].page,
                        params: $parentScope._model[entityKey].params
                    };
                    req["model"] = angular.toJson(mode);
                }
                req.fullKey = $parentScope._model[entityKey].fullDatasetKey;
                req.entityKey = entityKey;
                this.$http.post(url, $.param(req),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }).success(function (ret) {
                    if (ret.status == 0) {
                        alert(ret.error);
                    } else if (ret.status == 1) {
                        scope.__bind($parentScope, entityKey, ret.data);
                    } else if (ret.status == 2) {//正常返回，但没有数据
                        console.log("no data.")
                    } else {
                        alert("未知异常");
                    }
                    NProgress.done();
                }).error(function (err) {
                    alert(error);
                    NProgress.done();
                });

            },
                //cos,page
                scope.__bind = function ($parentScope, entityKey, data) {
                    var dataObject = $parentScope._model[entityKey];
                    $parentScope._model._models = [];
                    $parentScope._model._models.push(entityKey);
                    dataObject.cos.clear();
                    dataObject.cos.addAll(data.cos);
                    clone(data.page, dataObject.page);
                    dataObject.co = null;
                    dataObject.sos.clear();
                    dataObject.dels.clear();
                    if (dataObject.head) {
                        dataObject.co = dataObject.cos[0];
                        $parentScope[entityKey] = dataObject.co;
                        $parentScope["$" + entityKey] = dataObject;
                    } else {
                        $parentScope["__$" + entityKey + "$__"] == dataObject.cos;
                        $parentScope["$" + entityKey] = dataObject;
                    }
                    //发送修改广播
                    $parentScope.$broadcast('$_MODEL_CHANGE_$', $parentScope._model);

                }
            //    打印
            scope.basePath="http://127.0.0.1:9111/";
            scope.print = function (params,  type) {
                scope._index = 0;
                scope.tottle;
                scope.src;
                scope.srcArr;
                if(params.body && params.head){
                	if(params.body.sqlCmd.parameters.length<1 || params.head.sqlCmd.parameters.length<1){
                		alert("请选择打印单据！");
                        return;
                	}
                }else if( params.ids==''){
                	alert("请选择打印单据！");
                    return;
                }else {
                    NProgress.start();
                    $http.post(scope.basePath+"print/buildPrintJob", $.param({
                            printDataContext: JSON.stringify(params),
                        }),
                        {
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                            }
                        }
                    ).success(function (data) {
                        //打印预览分支
                        if(!data || JSON.stringify(data)=='{}'){
                            alert('打印任务生成失败！');
                            NProgress.done();
                            return;
                        };
                        //打印预览分支
                        if (type == false) {
                            var ret = data.printViewImgs;
                            if (JSON.stringify(ret) == "{}") {
                                alert('打印任务生成失败！');
                                return;
                            }
                            scope.imgDir=ret.imgDir;
                            scope._billid=ret.billid;
                            scope.tottle = ret.imgsCount;
                            scope._index=1;
                            scope.src ="http://127.0.0.1:9111/print/viewPrintImg?imgPath="+scope.imgDir+"p-"+scope._billid+"-"+scope._index+".jpg";
                            scope.srcArr = ret;
                            var btn = '<div class="fastEditRows">' +
                                '<button class="btn btn-primary btn-sm" ng-click="prePage()" ng-disabled="_index==1">上一个打印任务</button>' +
                                '<button class="btn btn-primary btn-sm" ng-click="nextPage()" ng-disabled="_index==tottle">下一个打印任务</button>' +
                                '</div> '
                            var dom = '<div><img  ng-src="{{src | trustUrl}}" id="printView" style="width: 100%"/></div>'
//                         var dom = '<iframe ng-src="{{src | trustUrl}}" index="" id="printView" width="100%" style="height: 100%;" frameborder="no" border="0" marginwidth="0" ' +
//                             'marginheight="0" scrolling="auto" allowtransparency="yes" ></iframe>';
                            var el = $compile(dom)(scope);
                            var btn = $compile(btn)(scope);
                            $('#myModal .modal-header h4').html('打印预览');
                            $('#myModal .modal-body').html(el);
                            $('#myModal .modal-footer').html(btn);
                            $('#myModal .modal-dialog').css('width', '80%');
                            $('#myModal .modal-body').css({'height':'600px'});
                            $('#myModal').modal('show');
                        } else {
                            //    打印分支
                            alert("打印任务已发送至打印机！");
                        }
                        NProgress.done();
                    }).error(function (data) {
                        alert("打印预览失败：" + data);
                        NProgress.done();
                    });
                }


            };
            //    翻页
            scope.prePage = function () {
                if (scope._index > 0) {
                    scope._index = scope._index - 1;
                    scope.src = scope.srcArr[scope._index];

                }
            };

            scope.nextPage = function () {
                if (scope._index < scope.tottle) {
                    scope._index = scope._index + 1;
                    scope.src = scope.srcArr[scope._index];
                }
            };
            element.bind("click", function () {
                var me = scope;
                if (me.triggerFuns) {
                    for (var i = 0; i < me.triggerFuns.length; i++) {
                        me.triggerFuns[i].apply(me);
                    }
                }

            });
            scope.setEnvModel = function (model) {
                var _model = eval(model);
                clone(model, window.$env);
            };

            scope.openWindow = function (viewKey, tableKey, data,field) {
                scope.condition = data.condition;
                clone(data.condition, window.$env);
                var _cosTmp = window.BASE64.encoder(JSON.stringify(data.cos));
                var _html = "<div billuiview src='" + viewKey + "'  field='" + field + "'  cosarchetype=" + _cosTmp + " />";
                var dom = $compile(_html)(scope);
                var _btn = "<button class='btn btn-success' ng-click=\"cancelfn()\" data-dismiss=\"modal\">取消</button> <button class='btn btn-success' ng-click=\"okfn()\">上引</button>";
                scope._btn = $compile(_btn)(scope);

                $('#myModal .modal-dialog').css('width', '80%');
                $('#myModal .modal-header h4').html('上引单据');
                $('#myModal .modal-body').html(dom);
                $('#myModal .modal-footer').html(scope._btn);
                $('#myModal').modal('show');
                scope.vpTableKey = tableKey;
                scope.resolveFn = data;
            };

            scope.okfn = function () {
                var data = getSos(scope.vpTableKey);
                scope.resolveFn.ok(data);
                $('#myModal').modal('hide');
                //    删除环境中传进来的参数
                for (var key in scope.condition) {
                    delEnvByKey(key);
                }
            };
            scope.cancelfn = function () {
                scope.resolveFn.cancel();
                //    删除环境中传进来的参数
                for (var key in scope.condition) {
                    delEnvByKey(key);
                }
            };
        }
    }
});


/*
 BillUIDateTime
 */

myDirectiveModule.directive('billuidatetime', function ($http, $q, Common ,$timeout,$interval) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            dateFormat: "@",
            required: "@"
        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.attr("disabled", "true");
            }
            //更新模型，注册为了让模型更新的事件在angularjs上下文中工作，

            var add0 = function (m) {
                return m < 10 ? '0' + m : m
            };
            scope.formatDate = function (date) {
                var now = new Date(date);
                var y = now.getFullYear();
                var m = now.getMonth() + 1;
                var d = now.getDate();
                var h = now.getHours();
                var mm = now.getMinutes();
                var s = now.getSeconds();
                if (scope.dateFormat == 'yyyy-mm-dd hh:ii:ss') {
                    return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) + ':' + add0(s);
                } else if (scope.dateFormat == 'yyyy-mm') {
                    return y + '-' + add0(m)
                } else {
                    return y + '-' + add0(m) + '-' + add0(d)
                }

            };

            var updateModel = function (dateTxt) {
                scope.$apply(function () {
                    ngModel.$setViewValue(dateTxt);
                });
            };
            if (ngModel.$viewValue) {
                updateModel(scope.formatDate(ngModel.$viewValue))
            }

            if (!attrs.readonly) {
                var datepickerHandler = {
                    init: function () {
                        var _self = this;
                        if (typeof element.datetimepicker == 'function') {
                            Common.loadScript('/lib/js/locales/bootstrap-datetimepicker.zh-CN.js', null);
                            if (scope.dateFormat == 'yyyy-mm-dd hh:ii:ss') {
                                scope.startView = 'month';
                                scope.minView = 'hour';
                            } else if (scope.dateFormat == 'yyyy-mm') {
                                scope.startView = 'year';
                                scope.minView = 'year';
                            } else {
                                scope.startView = 'month';
                                scope.minView = 'month';
                            }
                            element.datetimepicker({
                                language: 'cn',
                                format: scope.dateFormat,//显示格式
                                weekStart: 1,
                                todayBtn: 1,
                                autoclose: 1,
                                todayHighlight: 1,
                                forceParse: 0,
                                showMeridian: 1,
                                startView: scope.startView,
                                minView: scope.minView,
                            });
                            element.on('click', function () {
                                element.datetimepicker('show');
                            });

                            //绑定changeDate事件，当界面变化时通知模型
                            element.datetimepicker().on('changeDate', function (ev) {
                                //将中国标准时间格式化为时间戳；
                                // var _date = Math.round(ev.date.getTime()/1000);
                                var _dateTime = scope.formatDate(ev.date);
                                updateModel(_dateTime);
                            });
                        } else {
                            Common.loadCss('/lib/css/bootstrap-datetimepicker.min.css', null);
                            Common.loadScript('/lib/js/bootstrap-datetimepicker.min.js', null);
                            Common.loadScript('/lib/js/locales/bootstrap-datetimepicker.zh-CN.js', function () {
                                _self.init();
                            });
                        }
                    }
                }
                datepickerHandler.init();
            }
        }
    }
});


/*
 billuigrid
 billuigrid的两种操作模式：
 1.编辑模式；

 2.查看模式：
 */

myDirectiveModule.directive('billuigrid', function ($http, $q, $compile, Common, $filter) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        templateUrl: "/Views/_gridview.html",
        scope: {
            triggers: "@",
            checkrules: "@",
            init: "@",
            key: "@",
            renderid: "@",
            optMode: "@",
            selectMode: "@",
            tableKey: "@",//实体key
            heads: "@",
            rowId: "@",
            rowCheckRule: "@",
            sorts: "@",  //排序方式
            doubleClickHandler: "@",
            onClickHandler: "@",
            delHandler:"@deltriggers",
            tableType: "@",
            rules: "@",  //规则
            hideAddRow: "@",
            isfull: "@"

        },
        controller: function ($scope, $element, $attrs) {
            $scope.filter;
            $scope.exitsCol = function (row, col) {
                for (var p in row) {//遍历 src对象,并将所有属性赋值给target，从而实现对象的混合
                    if (p == col) {
                        return true;
                    }
                }
                return false;
            };
            //新增行
            $scope.addRow = function () {
                if ($scope.template) {
                    var _model = $scope.rows;
                    var _value = 0;

                    for (var i = 0; i < _model.length; i++) {
                        if (_model[i].SN > _value) {
                            _value = _model[i].SN;
                        }
                    }
                    $scope.template.SN = _value + 1;
                    $scope.template.isNullRow = true;
                    $scope.rows.push(clone($scope.template, {}));
                } else {
                    alert("还没有绑定数据模型.");
                }
            };

            //删除行
            $scope.delRow = function () {
                if ($scope.co) {
                    for (var i = 0; i < $scope.rows.length; i++) {
                        if ($scope.rows[i] == $scope.co) {
                            if ($scope.co.BillDtlID) {
                                $scope.$parent._model[$scope.tableKey].dels.push($scope.co);
                            }
                            $scope.rows.splice(i, 1);
                            break;
                        }
                    }
                } else {
                    alert("请选择行.");
                }
            };

            $scope.rowCheckRuleFun = null;
            $scope.rowCheckRuleFunMeta = null;
            $scope._parseRowCheckRuleFun = function () {
                if ($scope.rowCheckRule) {
                    try {
                        var base64expr = BASE64.decoder($scope.rowCheckRule);
                        var exprs = base64expr.split(",");
                        if (exprs.length != 3) {
                            return;
                        }
                        $scope.rowCheckRuleFunMeta = {
                            expr: exprs[0],
                            tClass: exprs[1],
                            fClass: exprs[2],
                        };
                        $scope.rowCheckRuleFun = new Function('row', "return " + $scope.rowCheckRuleFunMeta.expr);
                        $scope.rowCheckRuleFun.apply(row);
                    } catch (e) {
                        //alert(e);
                    }
                }
            };

            $scope.setStyle = function (row, index) {
                $scope.buildRowIndex(row, index);
                if ($scope.co != null) {
                    if (row == $scope.co) {
                        return "info";
                    }
                }
                if (!$scope.rowCheckRuleFun) {
                    $scope._parseRowCheckRuleFun();
                } else {
                    var result = $scope.rowCheckRuleFun.apply(row);
                    if (result) {
                        return $scope.rowCheckRuleFunMeta.tClass;
                    } else {
                        return $scope.rowCheckRuleFunMeta.fClass;
                    }
                }

            };
            $scope._rowIndex = [];
            $scope.buildRowIndex = function (row, index) {
                $scope._rowIndex[index] = row;
            };

            $scope.as = false;
            //全选
            $scope.allSelect = function () {
                $scope.as = !$scope.as;
                var _cos = $scope.$parent._model[$scope.tableKey].cos;
                if ($scope.as) {
                    for (var i = 0; i < $scope.sos.length; i++) {
                        //从sos中清除当前页的cos;
                        for (var j = 0; j < $scope.rows.length; j++) {
                            if ($scope.sos[i].rowID == $scope.rows[j].rowID) {
                                $scope.sos.delByValue($scope.sos[i]);
                            }
                        }
                    }

                    for (var i = 0; i < $scope.rows.length; i++) {
                        if (!$scope.rows[i].isDisable) {
                            //没有被禁用的行才可以被选中
                            $scope.sos.push($scope.rows[i]);
                        }
                    }
                    $scope.$parent._model[$scope.tableKey].sos = $scope.sos;
                } else {
                    for (var i = 0; i < $scope.sos.length; i++) {
                        //从sos中清除当前页的cos;
                        for (var j = 0; j < $scope.rows.length; j++) {
                            if ($scope.sos[i].rowID == $scope.rows[j].rowID) {
                                $scope.sos.delByValue($scope.sos[i]);
                            }
                        }
                    }
                    $scope.$parent._model[$scope.tableKey].sos = $scope.sos;
                }

                var retModel = {
                    entityKey: $scope.tableKey,
                    rows: $scope.sos
                };
                $scope.$emit("_$popreturnmodel$_", retModel);
            };
            $scope.currSos = [];
            $scope.sos = [];
            $scope.inSos = function (row) {
                for (var i = 0; i < $scope.sos.length; i++) {
                    if ($scope.sos[i].rowID == row.rowID) {
                        return true;
                    }

                }
                return false;
            };
            //清空选择SOS
            $scope.clearSos=function(){
            	$scope.sos.clear();
            };
            //选择行
            $scope.select = function (row) {
                if ($scope.selectMode == "radio") {
                    $scope.sos.clear();
                }
                $scope.sos.push(row);
                $scope.$parent._model[$scope.tableKey].sos = $scope.sos;
            };

            $scope.cancelSelect = function (row) {
                for (var i = 0; i < $scope.sos.length; i++) {
                    if ($scope.sos[i].rowID == row.rowID) {
                        $scope.sos.delByValue(row);
                        $scope.$parent._model[$scope.tableKey].sos = $scope.sos;
                    }

                }
            };
            //    筛选列
            $scope.delHead = function (item) {
                if (item.isChecked == true) {
                    item.isChecked = false;
                    item.isHide = true;
                } else {
                    item.isChecked = true;
                    item.isHide = false;
                }
            }

            //    显示历史信息
            $scope.showHistoryInfo = function (col, model, event) {
                event.stopPropagation();
                if (col.viewType == 'Windows') {
                    var _req = {
                        [col.col]: model[col.col]
                    };
                    clone(_req, $scope.$parent._env);
                    return;
                }
                ;


                $($element).find(".glyphicon-time").bind('mousedown', function () {
                    var _req = {
                        [col.col]: model[col.col]
                    }
                    clone(_req, $scope.$parent._env);

                    var $this = $(this);
                    var placement = "right";
                    var windowHeight = $(window).height();
                    var windowWidth = $(window).width();
                    $scope.meR = windowWidth - $this.offset().left;//
                    $scope.meT = $this.offset().top;//元素到头部距离
                    $scope.meB = windowHeight - $scope.meT;//元素到头部距离


                    if ($scope.meR > 600) {
                        placement = "right";
                    } else if ($scope.meR < 600 && $scope.meB > 300) {
                        placement = "bottom";
                    } else if ($scope.meR < 600 && $scope.meT > 300) {
                        placement = "top";
                    } else {
                        placement = "auto";
                    }
                    ;
                    $scope.HistoryInfo = "<div src='" + col.targetView + "'billuiview  />";
                    var dom = $compile($scope.HistoryInfo)($scope);

                    $this.popover({
                        trigger: 'hover', //触发方式
                        html: true, // 为true的话，data-content里就能放html代码了
                        content: dom,//这里可以直接写字符串，也可以 是一个函数，该函数返回一个字符串；
                        placement: placement,//
                    });
                    $this.mouseenter(function () {
                        $this.popover('show');
                    });
                });


            };
            //  求和  品均值等公示
            $scope.getCalc = function (keyarr) {
                var _arr = {}
                $scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                    var _model = model[$scope.tableKey].cos;
                    if (_model && _model.length == 0) {
                        return;
                    }
                    angular.forEach(keyarr, function (data, index, array) {
                        var type = keyarr[index].formular;
                        var key = keyarr[index].key;
                        var _value = null;
                        if (type == 'Sum') {
                            angular.forEach(_model, function (data, index, array) {
                                _value += _model[index][key];
                            });
                            _value = "合计：" + _value
                        } else if (type == 'Avg') {
                            angular.forEach(_model, function (data, index, array) {
                                _value += _model[index][key];
                            });
                            _value = "平均值：" + _value / _model.length;
                        } else if (type == 'Max') {
                            var _value = _model[0][key];
                            angular.forEach(_model, function (data, index, array) {
                                if (_model[index][key] > _value) {
                                    _value = _model[index][key];
                                }
                            });
                            _value = "最大值：" + _value
                        } else if (type == 'Min') {
                            var _value = _model[0][key];
                            angular.forEach(_model, function (data, index, array) {
                                if (_model[index][key] < _value) {
                                    _value = _model[index][key];
                                }
                            });
                            _value = "最小值：" + _value
                        }
                        _arr[key] = _value;
                    });
                    model[$scope.tableKey].formulaCols.push(_arr);
                });
            };

        },

        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用

            //混合器，指令初始代码
            mixer(abstractDirective, scope);

            var parent = _$util.getParent(scope);
            var entityKey = scope.tableKey;
            scope.$execute();//混合
            if (scope.rules && scope.rules !== '') {
                scope.rules = BASE64.decoder(scope.rules);
                parent.scope._env.rules[entityKey] = eval('(' + scope.rules + ')');
            };
            //
            scope._watchHodler = null;
            scope.setPage = function (num) {
                NProgress.start();
                if (num < 1) {
                    alert('目标页数不能小于1');
                    return;
                } else if (num > scope.pageInfo.totalPage) {
                    alert('目标页数大于最大页');
                    return;
                }
                var _pageInfo = scope.pageInfo;
                if (num == 'next') {
                    if (scope.pageInfo.pageNumber < scope.pageInfo.totalPage) {
                        _pageInfo.pageNumber = scope.pageInfo.pageNumber + 1;
                    } else {
                        return;
                    }
                } else if (num == 'prev') {
                    if (scope.pageInfo.pageNumber !== 1) {
                        _pageInfo.pageNumber = scope.pageInfo.pageNumber - 1;
                    } else {
                        return;
                    }
                } else if (num == 'begin') {
                    _pageInfo.pageNumber = 1;
                } else if (num == 'end') {
                    _pageInfo.pageNumber = scope.pageInfo.totalPage;
                } else {
                    _pageInfo.pageNumber = num;
                }
                scope.goToPage(scope.tableKey, _pageInfo);
                NProgress.done();
            };

            scope.goToPage = function (entitykey, _pageInfo) {
                var me = _$util.getParent(scope).scope;
                // /收集参数
                var entityKey = entitykey;
                var fullKey = me._model[entityKey].fullDatasetKey;
                var page = {
                    pageNumber: _pageInfo.pageNumber,
                    pageSize: _pageInfo.pageSize,
                    totalPage: _pageInfo.totalPage,
                    totalRow: _pageInfo.totalRow,
                };
                //var params=me._model[entityKey].params;
                var mode = {};
                mode[entityKey] = {
                    page: page,
                    params: me._model[entityKey].params
                };
                //发送ajax请求
                var req = {entityKey: entitykey, fullKey: fullKey, page: angular.toJson(page)};
                req["model"] = angular.toJson(mode);
                clone(me._env, req);
                scope.$http.post("/bill/data/refresh", $.param(req),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }).success(function (ret) {
                    //
                    if (ret.status == 0) {
                        alert(ret.error);
                    } else if (ret.status == 1) {
                        //me.refresh(entitykey,data.data);
                        var entity = me._model[entitykey];
                        if (entity) {
                            entity.cos.splice(0, entity.cos.length);
                            for (var i = 0; i < ret.data.cos.length; i++) {
                                entity.cos.push(ret.data.cos[i]);
                            }
                            clone(ret.data.page, entity.page);
                        }
                        me.$broadcast('$_MODEL_CHANGE_$', me._model);
                    } else {
                        alert("unknown error.");
                    }

                }).error(function (err) {
                    alert("分页失败,原因:" + err);
                });
            }
            //快速导航
            scope.fastEdit = function () {
                if (!scope.co || Object.getOwnPropertyNames(scope.co).length == 0) {
                    scope.co = scope.rows[0];[]
                     m
                    scope._index = 0;
                }
                if (scope.co && Object.getOwnPropertyNames(scope.co).length !== 0) {
                    var btn = '<div class="fastEditRows">' +
                        // '<button class="btn btn-primary btn-sm" ng-click="setPage(\'prev\')"  ng-disabled="pageInfo.pageNumber===1">上一页</button>' +
                        '<button class="btn btn-primary btn-sm" ng-click="preLine()" ng-disabled="_index==0">上一行</button>' +
                        '<button class="btn btn-primary btn-sm" ng-click="nextLine()" ng-disabled="_index==rows.length - 1">下一行</button>' +
                        // '<button class="btn btn-primary btn-sm" ng-click="setPage(\'next\')" ng-disabled="pageInfo.pageNumber===pageInfo.totalPage">下一页</button>' +
                        '</div> '
                    var dom = '<ul class="fastEdit clearfix">' +
                        '<li class="clearfix" ng-repeat="x in _$heads | filter: {isShow:true}">' +
                        '<label class="pull-left" >{{x.caption}}</label> ' +
                        '<div class="pull-left" widget="{{x.widget}}" ng-if="optMode==\'edit\' && !isExpred(x.col)" metawidget model="co[x.col]"></div>' +
                        '<div class="pull-left" widget="{{x.widget}}" ng-if="optMode==\'view\' || isExpred(x.col)">{{co.shadowObj[x.col]}}</div>' +
                        '</li></ul>';
                    var el = $compile(dom)(scope);
                    var btn = $compile(btn)(scope);
                    $('#myModal .modal-header h4').html('快速导航');
                    $('#myModal .modal-body').html(el);
                    $('#myModal .modal-footer').html(btn);
                    $('#myModal').modal('show');
                } else {
                    alert('暂无数据');
                }
            };
            if (scope.optMode == 'view') {
                $("#myModal").keydown(function (event) {
                    var myEvent = event || window.event; //解决不同浏览器获取事件对象的差异
                    switch (myEvent.keyCode) {
                        case 37:// 左
                            scope.preLine();
                            break;
                        case 39:// →
                            scope.nextLine();
                            break;
                    }
                });
            }
            //判断是不是虚拟列
            scope.isExpred = function (col) {
                if (scope.co.shadowCol) {
                    for (var i = 0; i < scope.co.shadowCol.length; i++) {
                        if (col == scope.co.shadowCol[i]) {
                            return true;
                        }
                    }
                }
            };
            scope.preLine = function () {
                // scope._index
                if (scope._index > 0) {
                    scope._index = scope._index - 1
                } else {
                    // scope.setPage('prev');
                    // scope._index = scope.rows.length - 1;
                }
                scope.co = scope._rowIndex[scope._index];
            };
            scope.nextLine = function () {
                // scope._index
                if (scope._index == scope.rows.length - 1 || scope._index > scope.rows.length) {
                    // scope._index = scope.rows.length - 1;
                    // scope.setPage('next');
                    // scope._index = 0;
                } else {
                    scope._index = scope._index + 1;
                }
                scope.co = scope._rowIndex[scope._index];
            };


            //排序数据集

            if (scope.sorts && scope.sorts != '') {
                var sortOpt = BASE64.decoder(scope.sorts);
                scope.sortOpt = eval(sortOpt);
                for (var i = 0; i < scope.sortOpt.length; i++) {
                    scope.sortOpt[i]._type = 1;
                }
                ;
            }
            //排序model
            scope.sortType = '';
            scope.sortName = '';
            scope.setSortType = function (x, t) {
                scope.sortName = x.caption;
                if (t == 1) {
                    //正
                    x._type = 1;
                    scope.sortType = x.col;
                } else if (t == 0) {
                    scope.sortType = '-' + x.col;
                    x._type = 0;
                }
            };

            /**
             * 绑定行模型
             */
            scope.rows = [];
            scope.bindRowsModel = function () {
                //寻找我的父
                scope.currSos = [];

                if (!parent.scope._model) {
                    alert("grid的父模型构建不完整.");
                }

                if (parent.scope._model[entityKey]) {
                    //绑定行数据；
                    // scope.rows = parent.scope._model[entityKey].cos;
                    scope.rows = scope.__initFormatters(parent.scope._model[entityKey].cos, scope.formatterObj);
                    //原型信息；
                    scope.template = parent.scope._model[entityKey].archetype;
                    //行汇总信息
                    scope.formulaCols = parent.scope._model[entityKey].formulaCols;

                    scope.pageInfo = parent.scope._model[entityKey].page;  //分页信息。
                    var _totalPage = parent.scope._model[entityKey].page.totalPage;
                    scope.pageInfo.pageItems = [];
                    var _pageStart = 1;
                    scope.skipPage = scope.pageInfo.pageNumber;
                    if (scope.pageInfo.pageNumber < 3) {
                        var _pageStart = 1;
                    } else if (scope.pageInfo.pageNumber > 5) {
                        if (_totalPage - scope.pageInfo.pageNumber <= 2) {
                            var _pageStart = _totalPage - 4;
                        } else {
                            var _pageStart = scope.pageInfo.pageNumber - 2;
                        }
                    } else {
                        var _pageStart = scope.pageInfo.pageNumber - 2;
                    }

                    for (var i = _pageStart; i <= _totalPage; i++) {
                        scope.pageInfo.pageItems.push(i);
                    }

                    //
                    if (scope._watchHodler) {
                        scope._watchHodler();
                    }
                    ;

                    scope.$watch("co", function (newValue, oldValue) {//
                        if ((oldValue && !isEmptyObject(oldValue)) && (newValue && !isEmptyObject(newValue))) {
                            scope.co;
                            var isSame = cmp(newValue, oldValue);
                            if (isSame == false) {
                                scope.co.isNullRow = false;
                            }
                        }
                    }, true);

                    scope._watchHodler = scope.$watch("rows", function () {//
                        //检测行模型改变的时候是否需要格式化？
                        scope.rows = scope.__initFormatters(scope.rows, scope.formatterObj);
                        var me = scope;
                        if (me.triggerFuns) {
                            for (var i = 0; i < me.triggerFuns.length; i++) {
                                me.triggerFuns[i].apply(me);
                            }
                        }
                    }, true);
                }
                ;
                NProgress.done();
            };
            //选中行
            scope.curRowSelect = function (row, index) {
                if (parent.scope._env && parent.scope._env.alldisabled) {
                    return
                }
                ;
                if (row.isDisable) {
                    alert("当前行已禁用，无法被选中！");
                    return;
                }
                scope._index = index;
                scope.co = row;
                scope.co._$index=index;
                parent.scope._model[entityKey].co = row;
                if (scope.optMode == "view" && scope.selectMode == "single") {
                    scope.sos.clear();
                    scope.sos.push(row);
                    parent.scope._model[entityKey].sos = scope.sos;
                }
                var retModel = {
                    entityKey: entityKey,
                    rows: scope.sos
                };
                scope.$emit("_$popreturnmodel$_", retModel);

            };
            //取消选中
            scope.cancelClick = function () {
                scope.co = {};
                scope.readonly = true;
                // $('input,textarea').popover('hide');
            };


            scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                NProgress.start();
                if (scope.$parent._env && scope.$parent._env.alldisabled) {
                    element.find('input,button,select').attr("disabled", "true");
                    element.find('.shaixuan *,.fastEditBtn').removeAttr("disabled");
                }
                scope.bindRowsModel();

                //
                //往体模型中塞是否可以保存空的标识
                if (scope.isfull && scope.isfull == "true") {
                    window.$body[scope.tableKey].canSaveNull = true;
                }
                ;
            });
            /**
             * 绑定头部模型
             */
            scope.bindHeadModel = function () {
                //渲染表头信息；
                var _heads = BASE64.decoder(scope.heads);
                scope.formatterObj = [];
                try {
                    scope._$heads = JSON.parse(_heads);
                    scope.keyArr = [];

                    angular.forEach(scope._$heads, function (data, index, array) {
                        scope._$heads[index].isChecked = true;
                        scope._$heads[index].isHide = false;
                        if (scope._$heads[index].formularType && scope._$heads[index].formularType == 'Page') {
                            scope.keyArr.push({
                                'key': scope._$heads[index].col,
                                'formular': scope._$heads[index].formularMode
                            });
                        }
                        ;
                        var _widget = BASE64.decoder(data.widget);
                        if ($(_widget).children().length > 0) {
                            var _key = $(_widget).children().attr('key');
                            var _formatter = $(_widget).children().attr('formatters');
                        } else {
                            var _key = $(_widget).attr('key');
                            var _formatter = $(_widget).attr('formatters');
                        }
                        var currArr = {};
                        currArr.key = _key;
                        currArr.formatter = _formatter;
                        scope.formatterObj.push(currArr);
                    });
                    if (scope.keyArr && scope.keyArr.length > 0) {
                        // 当需要计算的列>1的时候才计算；
                        scope.getCalc(scope.keyArr);
                    }
                    ;

                } catch (error) {
                    alert("grid head error:" + scope.key);
                }
            };
            scope.bindHeadModel();
            // scope.bindRowsModel();

            //删除触发器
            scope.delClickHandler = function () {
                var me = scope;
                if (!(me.delHandler == null || me.delHandler == "")) {
                    var fun_str = BASE64.decoder(me.delHandler);
                    //分析脚本中@调用
                    try {
                        var fun_def = JSON.parse(fun_str);
                        for (var i = 0; i < fun_def.length; i++) {
                            if (!me.delHandlerFuns) {
                                me.delHandlerFuns = [];//触发器数组
                            }
                            var trigger = me.__resolve(fun_def[i]);
                            me.delHandlerFuns.clear();
                            me.delHandlerFuns.push(new Function('scope', trigger));
                        }
                    } catch (error) {
                        alert(me.key + " 双击事件语法错误，请核对.");
                    }
                }
            };
            //双击事件语法解析
            scope.dblClickHandler = function () {
                var me = scope;
                if (!(me.doubleClickHandler == null || me.doubleClickHandler == "")) {
                    var fun_str = BASE64.decoder(me.doubleClickHandler);
                    //分析脚本中@调用
                    try {
                        var fun_def = JSON.parse(fun_str);
                        for (var i = 0; i < fun_def.length; i++) {
                            if (!me.dblclickFuns) {
                                me.dblclickFuns = [];//触发器数组
                            }
                            var trigger = me.__resolve(fun_def[i]);
                            me.dblclickFuns.clear();
                            me.dblclickFuns.push(new Function('scope', trigger));
                        }
                    } catch (error) {
                        alert(me.key + " 双击事件语法错误，请核对.");
                    }
                }
            };

            //单击事件语法解析
            scope.clickHandler = function () {
                var me = scope;
                if (!(me.onClickHandler == null || me.onClickHandler == "")) {
                    var fun_str = BASE64.decoder(me.onClickHandler);
                    //分析脚本中@调用
                    try {
                        var fun_def = JSON.parse(fun_str);
                        for (var i = 0; i < fun_def.length; i++) {
                            if (!me.onclickFuns) {
                                me.onclickFuns = [];//触发器数组
                            }
                            var trigger = me.__resolve(fun_def[i]);
                            me.onclickFuns.clear();
                            me.onclickFuns.push(new Function('scope', trigger));
                        }
                    } catch (error) {
                        alert(me.key + " 单击事件语法错误，请核对.");
                    }
                }
            };

            //删除行事件
            scope.delHook = function (row) {
                scope.delClickHandler(row);
                scope.row = row;
                var me = scope;
                if (me.delHandlerFuns) {
                    for (var i = 0; i < me.delHandlerFuns.length; i++) {
                        me.delHandlerFuns[i].apply(me);
                    }
                }
            };
            //行双击钩子事件
            scope.trHook = function (row) {
                scope.dblClickHandler(row);
                scope.row = row;
                var me = scope;
                if (me.dblclickFuns) {
                    for (var i = 0; i < me.dblclickFuns.length; i++) {
                        me.dblclickFuns[i].apply(me);
                    }
                }
            };
            //    单击事件钩子
            scope.clickHook = function (row) {
                scope.clickHandler(row);
                scope.row = row;
                var me = scope;
                if (me.onclickFuns) {
                    for (var i = 0; i < me.onclickFuns.length; i++) {
                        me.onclickFuns[i].apply(me);
                    }
                }
            };
            //

            //    拖拽

            scope.dropComplete = function (index, obj) {
                var idx = scope._$heads.indexOf(obj);

                scope._$heads.splice(idx, 1);
                scope._$heads.splice(index, 0, obj);
                // scope._$heads[idx] = scope._$heads[index];
                // scope._$heads[index] = obj;
            };
        }
    }
});

/**
 * 动态根据widget元信息，创建widget的指令
 */
myDirectiveModule.directive('metawidget', function ($compile) {
    return {
        restrict: 'EA',
        scope: false,
        link: function (scope, element, attrs) {//链接端函数
            var widget = attrs.widget;
            var model = attrs.model;
            var dom = BASE64.decoder(widget);
            dom = dom.replace("__$d_model$__", model);
            var el = $compile(dom)(scope);//编译，这里需要获取视图的，当时视图需要获取自己的渲染id
            element.append(el);
            scope.$broadcast('$_MODEL_CHANGE_$', this._model);
        }
    }
});

/**
 * billdataset指令，用于加载数据集
 * ---定义主数据集
 *       <dataset key="" bill-type="Bill,MultiBill,Dictionary,Report" bill-key="" bill-id=""
 *         binding="view|controller"
 *       />
 *
 * ---定义应用数据集
 * <dataset  key=""  />
 *
 */
myDirectiveModule.directive('dataset', function ($http, $q, $compile) {
    return {
        restrict: 'E',
        require: '?ngModel',
        scope: {
            triggers: "@",
            checkrules: "@",
            init: "@",
            key: "@",
            renderid: "@",
        },
        controller: function ($scope, $element, $attrs) {

        },
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();//混合


        }
    }
});
// 列指令
myDirectiveModule.directive('colummeta', function ($compile) {
    return {
        restrict: 'EA',
        scope: false,
        link: function (scope, element, attrs) {//链接端函数

        }
    }
});


//docker 容器指令
myDirectiveModule.directive('docker', function ($http, $q, $compile) {
    return {
        restrict: 'EA',
        require: '?ngModel',
        scope: {
            triggers: "@",
            checkrules: "@",
            init: "@",
            key: "@",
            renderid: "@",
            tableKey: "@"
        },
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();//混合
            // 判断是否具有子元素，如果没有子元素，display为none；
            if (element.children.length == 0) {
                element.css('display', 'none');
            }
        }
    }
});

//模版指令
myDirectiveModule.directive('billuitemplate', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        templateUrl: '/Views/_popup.html',
        scope: false,
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();
            scope.insertCos = function () {
                scope.$broadcast("_$popupSave$_");
                $('#myModal').modal('hide');
            }
        }
    }
});

//弹窗框  BillUIPopupWindow
//billuipopupwindow
myDirectiveModule.directive('billuipopupwindow', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            viewsize: "@",
            rules: "@", //  引用后置验证规则
            mappings: "@",
            required: "@",
            onClickHandler: "@",
        },
        link: function (scope, element, attrs, ngModel) {//链接端函数

            scope.$on("__$_view_created__$__", function (event, obj) {
                scope._$childview = obj.scope;
            });
            scope._$$popwin$$ = "_$$popwin$$";
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            scope.$flag = "POPWIN";
            mixer(abstractDirective, scope);
            scope.$executePopup();
            scope.__initPopwin();
            //alert(attrs.src);
            var parent = _$util.getParent(scope);
            if (!parent.scope._model) {
                alert("父模型构建不完整.");
            };

            if (parent.scope._env && !parent.scope._env.alldisabled) {
                element.addClass("whiteBg");
            }else  if(parent.scope._env && parent.scope._env.alldisabled && (!attrs.withouticon || attrs.withouticon!=="withoutIcon" || attrs.class!=="glyphicon glyphicon-time") ){
                return;
            };

            scope._model = new $Model(parent.scope._model);
            scope._env = new $Env(parent.scope._env);
            //
            if (!attrs.withouticon) {
                var _icon = $compile('<i class="searchIcon glyphicon glyphicon-search" ng-click="searchzjm()"></i>')(scope);
                element.parent().append(_icon);
            }


            //关联关系
            if (scope.mappings && scope.mappings !== '') {
                scope.map = JSON.parse(BASE64.decoder(scope.mappings));
                scope.dataTableKey = scope.map[0].dataTableKey;
                scope.mapList = scope.map[0].data;
            }
            ;
            //塞值入行
            scope.setRows = function (data) {
                scope._cos = data.rows;
                if ((scope.mappings && scope.mappings !== '') && (scope._cos && scope._cos !== '')) {
                    var _arrCache = {};
                    for (var j = 0; j < scope._cos.length; j++) {
                        clone(parent.scope._model[scope.dataTableKey].archetype, _arrCache);
                        for (var i = 0; i < scope.mapList.length; i++) {
                            _arrCache[scope.mapList[i].tk] = scope._cos[j][scope.mapList[i].sk];
                            if (i == scope.mapList.length - 1) {
                                scope.insertRows(scope.dataTableKey, _arrCache);
                                _arrCache = {};
                            }
                        }
                    }
                } else {
                    scope.rows = data.rows;
                    var me = scope;
                    if (me.triggerFuns) {
                        for (var i = 0; i < me.triggerFuns.length; i++) {
                            me.triggerFuns[i].apply(me);
                        }
                    }
                }
            };
            //后置验证规则
            scope.checkSaveRules = function (data) {
                if (scope.rules && scope.rules !== '') {
                    var postData = {};
                    var rules = eval('(' + BASE64.decoder(scope.rules) + ')');
                    var _errMsg = rules[0].errorMsg;
                    var _warnMsg = "";
                    if (rules[0].waring != null) {
                        _warnMsg = rules[0].waring.waringMsg;
                    }
                    postData[data.entityKey] = {models: [data.rows[0]], rules: rules};
                    scope.$http.post("/bill/data/rule", $.param({postData: JSON.stringify(postData)}),
                        {
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                            }
                        }).success(function (ret) {
                        for (i in ret.result) {
                            var table = ret.result[i];
                            if (table.errorModels.length > 0 || table.warnModels.length > 0) {
                                if (table.mes && table.mes !== '') {
                                    alert(table.mes);
                                } else {
                                    if (table.warnFlag) {
                                        alert(_errMsg);
                                    } else {
                                        alert(_warnMsg);
                                    }
                                }
                                return false;
                            } else {
                                scope.setRows(data);
                            }
                        }
                    }).error(function (err) {
                        alert("请求失败" + err);
                    });
                } else {
                    scope.setRows(data);
                }
            };


            scope.$on("_$popreturnmodel$_", function (event, data) {
                scope.__data = data;
                if (!scope.mappings) {
                    scope.checkSaveRules(data);
                }
                //点击行的时候回传模型
            });

            scope.okfn = function () {
                if (scope.mappings && scope.mappings !== '') {
                    scope.checkSaveRules(scope.__data);
                } else {
                    $('#myModal').modal('hide');
                }
            };
            //混合器，指令初始代码
            scope.setEnvModel = function (model) {
                var _model = eval(model);
                clone(model, window.$env);
            };
            scope.template = "<div src='" + attrs.src + "'billuiview  />";
            element.on('click', function () {
                var me = scope;
                if (me.onclickFuns) {
                    for (var i = 0; i < me.onclickFuns.length; i++) {
                        me.onclickFuns[i].apply(me);
                    }
                };
                //
                if (scope._$childview) {
                    scope._$childview.$destroy();
                }
                if (scope.viewsize == 'lg') {
                    $('#myModal .modal-dialog').css('width', '80%');
                } else if (scope.viewsize == 'normal') {
                    $('#myModal .modal-dialog ').css('width', '60%');
                } else if (scope.viewsize == 'sm') {
                    $('#myModal .modal-dialog ').css('width', '40%');
                } else if (scope.viewsize !== '' && scope.viewsize % 1 === 0) {
                    $('#myModal .modal-dialog').css('width', scope.viewsize + '%');
                } else {
                    $('#myModal .modal-dialog').css('width', '80%');
                }
                ;
                var dom = $compile(scope.template)(scope);
                var _btn = "<button class='btn btn-default' ng-click=\"cancelfn()\" data-dismiss=\"modal\">关闭</button> <button class='btn btn-primary' ng-click=\"okfn()\">确定</button>";
                scope._btn = $compile(_btn)(scope);
                $('#myModal .modal-header h4').html('&nbsp;');
                $('#myModal .modal-footer').html(scope._btn);
                $('#myModal .modal-body').html(dom);
                // $('#myModal .modal-body').addClass('modalBodyStyle');
                $('#myModal').modal('show');
            });
        }
    }
});
//checkbox   billuicheckbox
myDirectiveModule.directive('billuicheckbox', function ($http, $q, $compile) {
    return {
        restrict: 'EA',
        require: '?^ngModel',
        scope: {
            triggers: "@",
            checkrules: "@",
            properties: "@",
            init: "@",
            key: "@",
            renderid: "@",
            tableKey: "@",
            sourceSrc: "@",
            required: "@"
        },
        template: " <label class=\"uicheckbox\" ng-repeat=\"x in ds_array\" ng-class=\"{'checked':x.selected}\"><input  type=\"checkbox\"  name=\"{{checkboxname}}\"  ng-value=\"x\"  ng-checked=\"x.selected\"  ng-click=\"toggleSelection(x)\">{{x.name}} </label>",
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();//混合
            var parent = _$util.getParent(scope);
            var entityKey = parent.scope._env.billkey;
            scope.ds_array = {};    // 完整模型
            scope.selection = [];  //选中的值
            scope.selectioncn = [];  //选中的值
            scope.checkboxname = attrs.key;
            element.addClass("checkboxWrap");


            scope.findRowObject = function () {
                var _parent = scope.$parent;
                while (!_parent.row) {
                    _parent = _parent.$parent;
                    if (!_parent) {
                        return;
                    }
                }
                return _parent.row;
            };

            scope.row = scope.findRowObject();

            //初始化数据
            scope._initCheckbox = function () {
                scope.selection.clear();
                scope.selectioncn.clear();
                try {
                    if(attrs.ngModel.indexOf("$")==0){
                        scope.selection = parent.scope._model[entityKey].params[attrs.key].split(",");
                    }else{
                        if (scope.row) {
                            scope.selection = scope.row[attrs.key].split(",");
                        } else  {
                            if(parent.scope._model[entityKey].co[attrs.key]){
                                scope.selection = parent.scope._model[entityKey].co[attrs.key].split(",");
                            }
                        }
                    }


                    for (var i = 0; i < scope.ds_array.length; i++) {
                        for (var j = 0; j < scope.selection.length; j++) {
                            if (scope.ds_array[i].value == scope.selection[j]) {
                                scope.ds_array[i].selected = true;
                                scope.selectioncn.push(scope.ds_array[i].name);
                            }
                        }
                    };
                } catch (e) {
                    console.log('初始化选中状态错误：' + e)
                }
                //初始化选中
            };
            //

            scope.$on('$_MODEL_CHANGE_$', function () {
                scope._initCheckbox();
            });


            scope.loadDataSource = function (src) {
                if (src && src !== '') {
                    scope.selection.clear();
                    $http.get(src).success(function (result, status, headers, config) {
                        var _ds = [];
                        var data = result.data;
                        if (data && data !== '') {
                            for (var i = 0; i < data.length; i++) {
                                _ds.push({
                                    "name": data[i].v,
                                    "value": data[i].k,
                                    "selected": false
                                });
                                scope.ds_array = _ds;
                            }
                            ;
                            window.sessionStorage.setItem(entityKey + '-' + scope.key, JSON.stringify(_ds));
                            scope._initCheckbox();
                        }
                    }).error(function (data, status, headers, config) {
                        console.log(status);
                    });
                } else {
                    var _cacheSelect = window.sessionStorage.getItem(entityKey + '-' + scope.key);
                    if (_cacheSelect && _cacheSelect !== '') {
                        scope.ds_array = JSON.parse(_cacheSelect);
                        scope._initCheckbox();
                    } else if (scope.sourceSrc && scope.sourceSrc !== "") {
                        var _src = scope.sourceSrc;
                        $http.get(_src).success(function (result, status, headers, config) {
                            var _ds = [];
                            var data = result.data;
                            if (data && data !== '') {
                                for (var i = 0; i < data.length; i++) {
                                    _ds.push({
                                        "name": data[i].v,
                                        "value": data[i].k,
                                        "selected": false
                                    });
                                    scope.ds_array = _ds;
                                }
                                ;
                                window.sessionStorage.setItem(entityKey + '-' + scope.key, JSON.stringify(_ds));
                                scope._initCheckbox();
                            }
                        }).error(function (data, status, headers, config) {
                            console.log(status);
                        });

                    } else if (scope.properties && scope.properties !== "") {
                        var ds_array = eval(BASE64.decoder(scope.properties));
                        scope.ds_array = ds_array[0].value;
                        scope._initCheckbox();
                    } else {
                        alert("没有设置数据源properties属性，checkbox控件无法工作");
                    }
                    ;
                }
            };
            scope.toggleSelection = function (item) {
                if (scope.$parent._env && scope.$parent._env.alldisabled) {
                    console.log("当前不可编辑~~~");
                    return;
                }
                //先判断已经选中的节点
                var idx;
                var _index;
                for (var i = 0; i < scope.selection.length; i++) {
                    if (scope.selection[i] == item.value) {
                        idx = true;
                        _index = i;
                    }
                }

                if (item.selected == false) {
                    item.selected = true;
                    scope.selection.push(item.value);
                    scope.selectioncn.push(item.name);
                } else {
                    item.selected = false;
                    if (idx) {
                        scope.selection.splice(_index, 1);
                        scope.selectioncn.splice(_index, 1);
                    }
                }//
                var __viewValue = scope.selection.join(",");
                scope.ngModel.$setViewValue(__viewValue);
            };

            scope.getSelectionName = function () {
                return scope.selectioncn.join(",");
            };
        },

    }
});


//radio   billuiradio
myDirectiveModule.directive('billuiradio', function ($http, $q, $compile) {
    return {
        restrict: 'EA',
        require: '?ngModel',
        scope: {
            triggers: "@",
            checkrules: "@",
            properties: "@",
            init: "@",
            key: "@",
            renderid: "@",
            tableKey: "@",
            sourceSrc: "@",
            required: "@"
        },
        template: " <label class=\"uicheckbox\" ng-repeat=\"x in ds_array\" ng-class=\"{'checked':x.value==selection}\"><input  type=\"radio\"  name=\"{{checkboxname}}\"  ng-value=\"x\"  ng-checked=\"x.value==selection\"  ng-click=\"toggleSelection(x)\">{{x.name}} </label>",
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            scope.$execute();//混合
            // 判断是否具有子元素，如果没有子元素，display为none；
            scope.selection = '';  //选中的值
            scope.selectioncn = '';  //选中的值
            scope.checkboxname = attrs.key;


            var parent = _$util.getParent(scope);
            var entityKey = parent.scope._env.billkey;
            element.addClass("checkboxWrap");

            scope.findRowObject = function () {
                var _parent = scope.$parent;
                while (!_parent.row) {
                    _parent = _parent.$parent;
                    if (!_parent) {
                        return;
                    }
                }
                return _parent.row;
            };

            scope.row = scope.findRowObject();
            //初始化数据
            scope._initRadio = function () {
                try {

                    if(attrs.ngModel.indexOf("$")==0){
                        scope.selection = parent.scope._model[entityKey].params[attrs.key];
                    }else{
                        if (scope.row) {
                            scope.selection = scope.row[attrs.key];
                        } else  {
                            scope.selection = parent.scope._model[entityKey].co[attrs.key];
                        }
                    }
                    for (var i = 0; i < scope.ds_array.length; i++) {
                        if (scope.ds_array[i].value == scope.selection) {
                            scope.ds_array[i].selected = true;
                            scope.selectioncn=scope.ds_array[i].name;
                        }
                    };
                } catch (e) {
                    console.log('初始化选中状态错误：' + e)
                }
                //初始化选中
            };
            //

            scope.$on('$_MODEL_CHANGE_$', function () {
                scope._initRadio();
            });

            scope.loadDataSource = function () {
                if (scope.sourceSrc && scope.sourceSrc !== "") {
                    scope.ds_array = {};
                    // var _src = eval(BASE64.decoder(scope.sourceSrc));
                    var _src = scope.sourceSrc;
                    $http.get(_src).success(function (result, status, headers, config) {
                        var _ds = [];
                        var data = result.data;
                        for (var i = 0; i < data.length; i++) {
                            _ds.push({
                                "name": data[i].v,
                                "value": data[i].k,
                                "selected": false
                            });
                        }
                        scope.ds_array = _ds;
                    }).error(function (data, status, headers, config) {
                        console.log(status);
                    });

                } else if (scope.properties && scope.properties !== "") {
                    var ds_array = eval(BASE64.decoder(scope.properties));
                    scope.ds_array = ds_array[0].value;
                    scope.selection = scope.ngModel.$viewValue;
                    //初始化选中
                } else {
                    alert("没有设置数据源properties属性，radio控件无法工作");
                };
            };


            scope.toggleSelection = function (item) {
                if (scope.$parent._env && scope.$parent._env.alldisabled) {
                    console.log("当前不可编辑~~~");
                    return;
                }

                scope.selection=item.value;
                scope.selectioncn=item.name;
                scope.ngModel.$setViewValue(item.value);
            };

            scope.getSelectionName = function () {
                return scope.selectioncn;
            };
        },

    }
});

//zhujimatemplate
myDirectiveModule.directive('zhujimatemplate', function ($http, $compile) {
    return {
        restrict: 'EA',
        require: '?ngModel',
        scope: false,
        templateUrl: "/Views/_zhujima.html",
        link: function (scope, element, attrs, ngModel) {//链接端函数


        }
    }
});


/**
 *      选择框指令
 *      参数说明如下：
 *      1.action为请求后端数据的action,其中controller为api
 *      2.key   为数据的实际值
 *      3.value  为数据的表现值 *
 *      4.check-model为绑定的模型，这个模型一般为父控制器中的模型值
 *      5.fields:为需要显示的字段
 *      6.heads:为需要显示字段的中文名称
 *      <div ng-controller="testupController as main" >
 *              <div checkboxgroup  check-model="mychecks"  action="checks"  key="id" value="name"  fields="" heads=""/>
 *       </div>
 *
 */
myDirectiveModule.directive('checkboxgroup', function () {
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
        templateUrl: "/Views/_checkboxgroup.html",
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
            if ($scope.searchs != undefined) {
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
                    var t_checkes = $scope.checkModel.split(",");
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
                $element.find("#" + $scope.pid).on("change", "input[type='checkbox']", function () {
                    var $this = $(this);
                    var result = $this.prop("checked");
                    var value = $this.attr("value");
                    if (result) {//选择
                        $scope.check_arr.push(value);
                    } else {//取消选择
                        $scope.check_arr.remove(value);
                    }
                    $scope.checkModel = $scope.check_arr.join(",");
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
                $("#" + $scope.pid).modal('show');
            }

            $scope.close = function () {
                $("#" + $scope.pid).modal('hide');
            }

            $scope.sure = function () {
                var sear_code = "";
                var sear_value = "";

                for (var i = 0; i < $scope.search_list.length; i++) {

                    sear_code += $scope.search_list[i].split(',')[1] + ",";
                    sear_value += $("#" + $scope.search_list[i].split(',')[1]).val() + ",";

                }
                //$scope.check_arr = [];
                //$element.find("#" + $scope.tid).empty();
                $scope.check_arr.length = 0;
                $scope.searchCondition = {
                    sear_code: sear_code,
                    sear_value: sear_value,
                };
                var postData = $scope.searchCondition;

                common_service.list2(postData, $scope.action).success(
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

            $scope.mySplit = function (string, nb) {
                var array = string.split(',');
                return array[nb];
            }
        }
    }
});


myDirectiveModule.directive('radiogroup', function () {
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
            if ($scope.searchs != undefined) {
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
                    var t_checkes = $scope.checkModel.split(",");
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

                    $scope.checkModel = value;
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
                $("#" + $scope.pid).modal('show');
            }

            $scope.close = function () {
                $("#" + $scope.pid).modal('hide');
            }

            $scope.sure = function () {
                var sear_code = "";
                var sear_value = "";

                for (var i = 0; i < $scope.search_list.length; i++) {

                    sear_code += $scope.search_list[i].split(',')[1] + ",";
                    sear_value += $("#" + $scope.search_list[i].split(',')[1]).val() + ",";

                }
                //$scope.check_arr = [];
                //$element.find("#" + $scope.tid).empty();
                $scope.check_arr.length = 0;
                $scope.searchCondition = {
                    sear_code: sear_code,
                    sear_value: sear_value,
                };
                var postData = $scope.searchCondition;

                common_service.list2(postData, $scope.action).success(
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

            $scope.mySplit = function (string, nb) {
                var array = string.split(',');
                return array[nb];
            }
        }
    }
});

/*
 shangyin
 */

myDirectiveModule.directive('billuibuttongroup', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            key: "@",
            renderid: "@",
            src: "@",
        },
        controller: function ($scope, $element, $attrs, $transclude) {//指令的控制器
            $scope.showindex = function (index) {
                var me = $scope;
                if (me.triggerFuns && index <= me.triggerFuns.length) {
                    me.triggerFuns[index].apply(me);
                }
            };
        },
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            //解析property
            scope.$execute();
            scope._parent = _$util.getParent(scope);
            scope.tid = "T" + GUID();

            var _billkey = scope._parent.scope.$parent._env.billkey;
            var _parent = _$util.getParent(scope).scope;
            if (_parent._env && _parent._env.alldisabled) {
                element.find('button').attr("disabled", "true");
            }
            scope.getEnvFromCache();

            element.find("li").bind("click", function () {
                var _index = $(this).attr('index');
                var me = scope;
                if (me.triggerFuns && _index < me.triggerFuns.length) {
                    me.triggerFuns[_index].apply(me);
                }
            });
            //    测试openwind
            scope.openWindow = function (viewKey, tableKey, data,field) {
                scope.condition = data.condition;
                clone(data.condition, window.$env);
                var _cosTmp = window.BASE64.encoder(JSON.stringify(data.cos));
                var _html = "<div src='" + viewKey + "' billuiview  field='" + field + "'   cosarchetype=" + _cosTmp + " />";
                var dom = $compile(_html)(scope);
                var _btn = "<button class='btn btn-success' ng-click=\"cancelfn()\" data-dismiss=\"modal\">取消</button> <button class='btn btn-success' ng-click=\"okfn()\">上引</button>";
                scope._btn = $compile(_btn)(scope);

                $('#myModal .modal-dialog').css('width', '80%');
                $('#myModal .modal-header h4').html('上引单据');
                $('#myModal .modal-body').html(dom);
                $('#myModal .modal-footer').html(scope._btn);
                $('#myModal').modal('show');
                scope.vpTableKey = tableKey;
                scope.resolveFn = data;
            };
            scope.okfn = function () {
                var data = getSos(scope.vpTableKey);
                scope.resolveFn.ok(data);
                $('#myModal').modal('hide');
                //    删除环境中传进来的参数
                for (var key in scope.condition) {
                    delEnvByKey(key);
                }
            };
            scope.cancelfn = function () {
                scope.resolveFn.cancel();
                //    删除环境中传进来的参数
                for (var key in scope.condition) {
                    delEnvByKey(key);
                }
            };

        }
    }
});

myDirectiveModule.directive('xiatui', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: false,
        link: function (scope, element, attrs, ngModel) {
            element.removeAttr("disabled");
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            //解析property
            scope.$execute();

//            var _parent = _$util.getParent(scope).scope;
//            if (_parent._env && _parent._env.alldisabled) {
//                element.find('button').attr("disabled", "true");
//            }

            scope._parent = _$util.getParent(scope);
            var _billkey = scope._parent.scope._env.billkey;
            var _rulekey = '';
            $http.post(attrs.src + _billkey).success(function (ret) {
                if (ret.status == 0) {
                    element.hide();
                } else {
                    _rulekey = ret.ruleKey;
                    element.removeAttr("disabled");
                }

            });

            element.on('click', function () {
                scope._parent.scope.refCreateTarget("PUSH", {
                    'billKey': _billkey,
                    'ruleKey': _rulekey
                });
            })

        }


    }
});


/**
 * 文件上传控件：注意：files与控制器中的myfile绑定
 *    <div ng-controller=;"testupController" >
 <div upload  files="main.myfile" count="1"/>


 </div>
 *
 */
myDirectiveModule.directive('billuiattachment', function (Common, $http, $q) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        transclude: false,
        scope: {
            bound: "@",//
            triggers: "@",
            checkrules: "@",
            init: "@",
            formatters: "@",
            properties: "@",
            datatablekey: "@",
            key: "@",
            renderid: "@",
            count: "@",
            parseExcel: "@parseexcel",
            files: "=",//必须指定这个属性，文件采用“|”方式分隔，因为文件命名规则中不能使用这个，而逗号可能会导致错误
            templetUrl: "@templeturl",
            importsheets:"@"

        },
        templateUrl: "/Views/_upload.html",
        controller: function ($scope, $element, $attrs, $transclude) {//指令的控制器
            $scope.files_arr = [];
            $scope.tid = "T" + GUID();
            $scope.fid = "F" + GUID();
            $scope.modalLoading = "M" + GUID();
            var actionUrl;
            $scope.importFn = function (data, fn) {
                //data: 数据
                //fn：  function，成功执行的方法；
                data.importsheets=$scope.importsheets;
                $scope.resolveFn = fn;
                $scope._args=$.param(data);
                if ($scope.parseExcel) {
                    actionUrl = '/bill/parseExcel?optExcel=upload&'+$scope._args;
                } else {
                    actionUrl = '/common/uploadFile';
                }
            };


            $scope.openUploadModel = function (event) {
                var me = $scope;
                if (me.triggerFuns) {
                    for (var i = 0; i < me.triggerFuns.length; i++) {
                        me.triggerFuns[i].apply(me);
                    }
                };

                $('#model-' + $scope.tid).modal("show");
                event.stopPropagation();
            };
            if ($scope.parseExcel) {
                var actionUrl = '/bill/parseExcel?optExcel=upload&parms='+$scope._args;
                $scope.attachInfo = {
                    title: '导入附件',
                    btnName: '导入',
                    tmpUrl: $scope.templetUrl
                };

            } else {
                $scope.attachInfo = {
                    title: '管理附件',
                    btnName: '上传',
                };
                var actionUrl = '/common/uploadFile';
            }
            $scope.stopEvent = function (event) {
                event.stopPropagation();
            };
            $scope.test = function () {
                var fileSize = $('#' + $scope.fid)[0].files;
                var fileSizeKb = 0;
                for (var i = 0; i < fileSize.length; i++) {
                    fileSizeKb += fileSize[i].size;
                }
                var fileSizeMb = fileSizeKb / 1024 / 1024;//转换为kb
                console.log(fileSizeMb);
                if (fileSizeMb > 9.9) {
                    alert('当前文件大小超过10mb，请重新选择！');
                    return;
                };

                if (typeof __$AjaxUpload != 'undefined') {
                    __$AjaxUpload($scope.tid, $scope.fid, $scope.preSend, $scope.sucess, $scope.fail, $scope.del, actionUrl);
                } else {
                    try {
                        Common.loadScript('/lib/js/workflow/fileUpload.js', function () {
                            __$AjaxUpload($scope.tid, $scope.fid, $scope.preSend, $scope.sucess, $scope.fail, $scope.del, actionUrl);
                        });
                    } catch (e) {
                        alert('上传附件资源没有正确加载！');
                    }
                }
            };

            $scope.preSend = function () {
                $("#" + $scope.modalLoading).modal("show");
            };
            $scope.sucess = function (result) {
                $("#" + $scope.modalLoading).modal("hide");
                if (result.status == 1) {
                    if ($scope.parseExcel) {
                        //导入附件分支
                        $scope.resolveFn.ok(result);
                    } else {
                        //附件上传分支
                            var fileObj = {
                                fileName: result.fileName,
                                filePath: result.filePath
                            };
                            $scope.files_arr.push(fileObj);
                            $scope.files = $scope.files_arr;
                        }
                }else {
                    var er=JSON.parse(result.error);
                    alert(er[0].message);//打印出错误信息
                }

            };


            $scope.fail = function () {
                alert("网络问题，文件发送失败.");
                $("#" + $scope.modalLoading).modal("hide");
            };
            $scope.delfiles = function (filename) {
                var _fileArr = filename.filePath.split('\\');

                $http.post("/common/deleteAttachment?fileName=" + _fileArr[_fileArr.length - 1],
                    $.param({}),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }
                ).success(function (ret) {
                    if (ret.status == '1') {
                        $scope.files_arr.delByValue(filename);
                        $scope.files = $scope.files_arr;
                    } else {
                        alert("删除失败：" + ret.error);
                    }
                }).error(function (data) {
                    alert("删除失败:" + error);
                });

            };

        },
        link: function ($scope, $element, $attrs, ngModel) {
            $scope.files_arr = [];
            $scope.url = BASE64.decoder($attrs.url);
            $scope.ngModel = ngModel;//模型应用
            $scope.$q = $q;//$q引用
            $scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, $scope);
            $scope.$execute();

            $scope.disabled = false;
            $scope._parent = _$util.getParent($scope);
            $scope._env = $scope._parent.scope._env;
            if ($scope._env && ($scope._env.alldisabled == 'true' || $scope._env.billtype == 'Bills')) {
                $scope.disabled = true;
            };
            $($element).css('display', 'inline-block');

            //当有数据变化时，添加对应的DOM列表
            $scope.$on('$_MODEL_CHANGE_$', function (e, model) {
                $scope.$watch("files", function (newValue, oldValue, scope) {
                    if ($scope.ngModel.$modelValue) {
                        $scope.files = eval($scope.ngModel.$modelValue);
                        $scope.files_arr = $scope.files;
                    }
                    $scope.ngModel.$setViewValue($scope.files);
                });
                //初始时，链接对应的DOM结构
            });

        }

    };
});


/**
 * billwfoperatorform
 *   工作流操作表单
 *
 *
 */
myDirectiveModule.directive('billwfoperatorform', function ($http, $q, $compile, $rootScope) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        template: "<div class=\"workbooksubNav clearfix\"><ul><li ng-repeat=\"x in dataArray\" ng-class=\"{'curr':x.ngcontroller==isActive}\"><a href=''  ng-click=\"changeTab(x)\">{{x.caption}}</a> </li></ul></div>",
        scope: {
            taskInstance: "@"
        },
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            //taskInstance   task-instance
            if (!scope.taskInstance) {
                alert("taskInstance null.");
            }
            scope.requestOperatorForm = function () {
                $http.post("/bill/view/workform",
                    $.param({taskInstance: scope.taskInstance}),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }
                ).success(function (ret) {
                    if (ret && ret != '') {
                        scope.dataArray = new Array();
                        for (var i = 0; i < ret.length; i++) {
                            var html = '<div class="workbookWrap">' + ret[i].html + '</div>';
                            var _caption = $(html).find("div[Caption]");
                            var _ngcontroller = $(html).find("div[ng-controller]");
                            var dataAttr = {
                                caption: $(_caption).attr('Caption'),
                                ngcontroller: $(_ngcontroller).attr('ng-controller')
                            };
                            scope.dataArray.push(dataAttr);

                            var fun = new Function("$scope", "$http", "$interval", "$rootScope", "$compile", "$q", ret[i].controller);
                            controllerProvider.register("xyyerp." + ret[i].controllerName, fun);
                            var inject = element.injector();
                            inject.invoke(function ($compile, $rootScope) {
                                element.append($compile(html)($rootScope));//追加dom结构
                            });
                            $('div').remove('#toolbox');
                            $("#operatorFormDiv div[BillType]").hide();
                            $("#operatorFormDiv div[BillType]").eq(0).show();
                        }
                        scope.isActive = scope.dataArray[0].ngcontroller;
                    } else {
                        var _html = "<div class='noData'>暂无操作表单</div>"
                        element.append(_html);
                    }
                }).error(function (data) {
                    alert("请求数据表单失败." + data);
                });
            };
            scope.requestOperatorForm();

            scope.changeTab = function (tab) {
                scope.isActive = tab.ngcontroller;
                $("#operatorFormDiv div[BillType]").hide();
                $("#operatorFormDiv div[ng-controller='" + tab.ngcontroller + "']").show();
            };
            //
        }
    }
});

/**
 * billwfdataforms
 * 工作流数据表单
 */
myDirectiveModule.directive('billwfdataforms', function ($http, $q, $compile) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        template: "<div class=\"workbooksubNav clearfix\"><ul><li ng-repeat=\"x in dataArray\" ng-class=\"{'curr':x.ngcontroller==isActive}\"><a href=''  ng-click=\"changeTab(x.ngcontroller)\">{{x.caption}}</a> </li></ul></div>",
        scope: {
            taskInstance: "@"
        },
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            //taskInstance   task-instance
            if (!scope.taskInstance) {
                alert("taskInstance null.");
            }
            scope.requestOperatorForm = function () {
                $http.post("/bill/view/dataforms",
                    $.param({taskinstance: scope.taskInstance}),
                    {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    }
                ).success(function (ret) {
                    if (ret && ret != '') {
                        scope.dataArray = new Array();
                        for (var i = 0; i < ret.length; i++) {
                            var html = '<div class="workbookWrap">' + ret[i].html + '</div>';
                            var _caption = $(html).find("div[Caption]");
                            var _ngcontroller = $(html).find("div[ng-controller]");
                            var dataAttr = {
                                caption: $(_caption).attr('Caption'),
                                ngcontroller: $(_ngcontroller).attr('ng-controller')
                            };
                            scope.dataArray.push(dataAttr);

                            var fun = new Function("$scope", "$http", "$interval", "$rootScope", "$compile", "$q", ret[i].controller);
                            controllerProvider.register("xyyerp." + ret[i].controllerName, fun);
                            var inject = element.injector();
                            inject.invoke(function ($compile, $rootScope) {
                                element.append($compile(html)($rootScope));//追加dom结构
                            });
                            $('div').remove('#toolbox');//清除叙事薄工具栏
                            $('div').remove('#dictionary_tools');//清除叙事薄工具栏
                            $('div').remove('#bills_tools');//清除叙事薄查询面板
                            $("#dataFormDiv div[BillType]").hide();
                            $("#dataFormDiv div[BillType]").eq(0).show();
                        }
                        scope.isActive = scope.dataArray[0].ngcontroller;
                    } else {
                        var _html = "<div class='noData'>暂无数据表单</div>"
                        element.append(_html);
                    }
                }).error(function (data) {
                    alert("请求数据表单失败." + data);
                });
            };
            scope.requestOperatorForm();

            scope.changeTab = function (tab) {
                scope.isActive = tab;
                $("#dataFormDiv div[BillType]").hide();
                $("#dataFormDiv div[ng-controller='" + tab + "']").show();
            }
        }
    }
});


//jsdecoder
myDirectiveModule.directive('jsdecoder', function ($http, $q, $compile, Common) {
    return {
        require: '?ngModel',
        restrict: 'EA',
        scope: false,
        link: function (scope, element, attrs, ngModel) {//链接端函数
            scope.ngModel = ngModel;//模型应用
            scope.$q = $q;//$q引用
            scope.$http = $http;//$http引用
            //混合器，指令初始代码
            mixer(abstractDirective, scope);
            //解析property
            scope.$execute();

            scope._parent = _$util.getParent(scope);

            var jsdecoder;
            var code = '';
            scope.ngModel.$viewChangeListeners.push(function () {
                jsdecoder = new JsDecoder();
                jsdecoder.s = ngModel.$modelValue;
                try {
                    code = jsdecoder.decode();
                    console.log(code);
                    ngModel.$setViewValue(code);
                    ngModel.$render();
                } catch (e) {
                    alert(JSON.stringify(e));
                }
            });

        }
    }
});

// 树 xyyztree
myDirectiveModule.directive('xyyztree', function ($http, $q, Common) {
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
            onSelection: "&",
            broadcastName: "@"
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
                scope.$emit(scope.broadcastName, _treeNode);
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
                var parent = _$util.getParent(scope);
                var entityKey = "$t_" + scope.tableKey;
                parent.scope._model[entityKey] = scope.tree.getNodes();
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
            }
            ;
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
myDirectiveModule.directive('paging', function () {
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


myDirectiveModule.directive('ngDrag', ['$rootScope', '$parse', '$document', '$window', 'ngDraggable', function ($rootScope, $parse, $document, $window, ngDraggable) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            scope.value = attrs.ngDrag;
            var offset, _centerAnchor = false, _mx, _my, _tx, _ty, _mrx, _mry;
            var _hasTouch = ('ontouchstart' in window) || window.DocumentTouch && document instanceof DocumentTouch;
            var _pressEvents = 'touchstart mousedown';
            var _moveEvents = 'touchmove mousemove';
            var _releaseEvents = 'touchend mouseup';
            var _dragHandle;

            // to identify the element in order to prevent getting superflous events when a single element has both drag and drop directives on it.
            var _myid = scope.$id;
            var _data = null;

            var _dragOffset = null;

            var _dragEnabled = false;

            var _pressTimer = null;

            var onDragStartCallback = $parse(attrs.ngDragStart) || null;
            var onDragStopCallback = $parse(attrs.ngDragStop) || null;
            var onDragSuccessCallback = $parse(attrs.ngDragSuccess) || null;
            var allowTransform = angular.isDefined(attrs.allowTransform) ? scope.$eval(attrs.allowTransform) : true;

            var getDragData = $parse(attrs.ngDragData);

            // deregistration function for mouse move events in $rootScope triggered by jqLite trigger handler
            var _deregisterRootMoveListener = angular.noop;

            var initialize = function () {
                element.attr('draggable', 'false'); // prevent native drag
                // check to see if drag handle(s) was specified
                // if querySelectorAll is available, we use this instead of find
                // as JQLite find is limited to tagnames
                if (element[0].querySelectorAll) {
                    var dragHandles = angular.element(element[0].querySelectorAll('[ng-drag-handle]'));
                } else {
                    var dragHandles = element.find('[ng-drag-handle]');
                }
                if (dragHandles.length) {
                    _dragHandle = dragHandles;
                }
                toggleListeners(true);
            };

            var toggleListeners = function (enable) {
                if (!enable)return;
                // add listeners.

                scope.$on('$destroy', onDestroy);
                scope.$watch(attrs.ngDrag, onEnableChange);
                scope.$watch(attrs.ngCenterAnchor, onCenterAnchor);
                // wire up touch events
                if (_dragHandle) {
                    // handle(s) specified, use those to initiate drag
                    _dragHandle.on(_pressEvents, onpress);
                } else {
                    // no handle(s) specified, use the element as the handle
                    element.on(_pressEvents, onpress);
                }
                // if(! _hasTouch && element[0].nodeName.toLowerCase() == "img"){
                if (element[0].nodeName.toLowerCase() == "img") {
                    element.on('mousedown', function () {
                        return false;
                    }); // prevent native drag for images
                }
            };
            var onDestroy = function (enable) {
                toggleListeners(false);
            };
            var onEnableChange = function (newVal, oldVal) {
                _dragEnabled = (newVal);
            };
            var onCenterAnchor = function (newVal, oldVal) {
                if (angular.isDefined(newVal))
                    _centerAnchor = (newVal || 'true');
            };

            var isClickableElement = function (evt) {
                return (
                    angular.isDefined(angular.element(evt.target).attr("ng-cancel-drag"))
                );
            };
            /*
             * When the element is clicked start the drag behaviour
             * On touch devices as a small delay so as not to prevent native window scrolling
             */
            var onpress = function (evt) {
                // console.log("110"+" onpress: "+Math.random()+" "+ evt.type);
                if (!_dragEnabled)return;

                if (isClickableElement(evt)) {
                    return;
                }

                if (evt.type == "mousedown" && evt.button != 0) {
                    // Do not start dragging on right-click
                    return;
                }

                var useTouch = evt.type === 'touchstart' ? true : false;


                if (useTouch) {
                    cancelPress();
                    _pressTimer = setTimeout(function () {
                        cancelPress();
                        onlongpress(evt);
                        onmove(evt);
                    }, ngDraggable.touchTimeout);
                    $document.on(_moveEvents, cancelPress);
                    $document.on(_releaseEvents, cancelPress);
                } else {
                    onlongpress(evt);
                }

            };

            var cancelPress = function () {
                clearTimeout(_pressTimer);
                $document.off(_moveEvents, cancelPress);
                $document.off(_releaseEvents, cancelPress);
            };

            var onlongpress = function (evt) {
                if (!_dragEnabled)return;
                evt.preventDefault();

                offset = element[0].getBoundingClientRect();
                if (allowTransform)
                    _dragOffset = offset;
                else {
                    _dragOffset = {left: document.body.scrollLeft, top: document.body.scrollTop};
                }


                element.centerX = element[0].offsetWidth / 2;
                element.centerY = element[0].offsetHeight / 2;

                _mx = ngDraggable.inputEvent(evt).pageX;//ngDraggable.getEventProp(evt, 'pageX');
                _my = ngDraggable.inputEvent(evt).pageY;//ngDraggable.getEventProp(evt, 'pageY');
                _mrx = _mx - offset.left;
                _mry = _my - offset.top;
                if (_centerAnchor) {
                    _tx = _mx - element.centerX - $window.pageXOffset;
                    _ty = _my - element.centerY - $window.pageYOffset;
                } else {
                    _tx = _mx - _mrx - $window.pageXOffset;
                    _ty = _my - _mry - $window.pageYOffset;
                }

                $document.on(_moveEvents, onmove);
                $document.on(_releaseEvents, onrelease);
                // This event is used to receive manually triggered mouse move events
                // jqLite unfortunately only supports triggerHandler(...)
                // See http://api.jquery.com/triggerHandler/
                // _deregisterRootMoveListener = $rootScope.$on('draggable:_triggerHandlerMove', onmove);
                _deregisterRootMoveListener = $rootScope.$on('draggable:_triggerHandlerMove', function (event, origEvent) {
                    onmove(origEvent);
                });
            };

            var onmove = function (evt) {
                if (!_dragEnabled)return;
                evt.preventDefault();

                if (!element.hasClass('dragging')) {
                    _data = getDragData(scope);
                    element.addClass('dragging');
                    $rootScope.$broadcast('draggable:start', {
                        x: _mx,
                        y: _my,
                        tx: _tx,
                        ty: _ty,
                        event: evt,
                        element: element,
                        data: _data
                    });

                    if (onDragStartCallback) {
                        scope.$apply(function () {
                            onDragStartCallback(scope, {$data: _data, $event: evt});
                        });
                    }
                }

                _mx = ngDraggable.inputEvent(evt).pageX;//ngDraggable.getEventProp(evt, 'pageX');
                _my = ngDraggable.inputEvent(evt).pageY;//ngDraggable.getEventProp(evt, 'pageY');

                if (_centerAnchor) {
                    _tx = _mx - element.centerX - _dragOffset.left;
                    _ty = _my - element.centerY - _dragOffset.top;
                } else {
                    _tx = _mx - _mrx - _dragOffset.left;
                    _ty = _my - _mry - _dragOffset.top;
                }

                moveElement(_tx, _ty);

                $rootScope.$broadcast('draggable:move', {
                    x: _mx,
                    y: _my,
                    tx: _tx,
                    ty: _ty,
                    event: evt,
                    element: element,
                    data: _data,
                    uid: _myid,
                    dragOffset: _dragOffset
                });
            };

            var onrelease = function (evt) {
                if (!_dragEnabled)
                    return;
                evt.preventDefault();
                $rootScope.$broadcast('draggable:end', {
                    x: _mx,
                    y: _my,
                    tx: _tx,
                    ty: _ty,
                    event: evt,
                    element: element,
                    data: _data,
                    callback: onDragComplete,
                    uid: _myid
                });
                element.removeClass('dragging');
                element.parent().find('.drag-enter').removeClass('drag-enter');
                reset();
                $document.off(_moveEvents, onmove);
                $document.off(_releaseEvents, onrelease);

                if (onDragStopCallback) {
                    scope.$apply(function () {
                        onDragStopCallback(scope, {$data: _data, $event: evt});
                    });
                }

                _deregisterRootMoveListener();
            };

            var onDragComplete = function (evt) {


                if (!onDragSuccessCallback)return;

                scope.$apply(function () {
                    onDragSuccessCallback(scope, {$data: _data, $event: evt});
                });
            };

            var reset = function () {
                if (allowTransform)
                    element.css({transform: '', 'z-index': '', '-webkit-transform': '', '-ms-transform': ''});
                else
                    element.css({'position': '', top: '', left: ''});
            };

            var moveElement = function (x, y) {
                if (allowTransform) {
                    element.css({
                        transform: 'matrix3d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, ' + x + ', ' + y + ', 0, 1)',
                        'z-index': 99999,
                        '-webkit-transform': 'matrix3d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, ' + x + ', ' + y + ', 0, 1)',
                        '-ms-transform': 'matrix(1, 0, 0, 1, ' + x + ', ' + y + ')'
                    });
                } else {
                    element.css({'left': x + 'px', 'top': y + 'px', 'position': 'fixed'});
                }
            };
            initialize();
        }
    };
}])

    .directive('ngDrop', ['$parse', '$timeout', '$window', '$document', 'ngDraggable', function ($parse, $timeout, $window, $document, ngDraggable) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                scope.value = attrs.ngDrop;
                scope.isTouching = false;

                var _lastDropTouch = null;

                var _myid = scope.$id;

                var _dropEnabled = false;

                var onDropCallback = $parse(attrs.ngDropSuccess);// || function(){};

                var onDragStartCallback = $parse(attrs.ngDragStart);
                var onDragStopCallback = $parse(attrs.ngDragStop);
                var onDragMoveCallback = $parse(attrs.ngDragMove);

                var initialize = function () {
                    toggleListeners(true);
                };

                var toggleListeners = function (enable) {
                    // remove listeners

                    if (!enable)return;
                    // add listeners.
                    scope.$watch(attrs.ngDrop, onEnableChange);
                    scope.$on('$destroy', onDestroy);
                    scope.$on('draggable:start', onDragStart);
                    scope.$on('draggable:move', onDragMove);
                    scope.$on('draggable:end', onDragEnd);
                };

                var onDestroy = function (enable) {
                    toggleListeners(false);
                };
                var onEnableChange = function (newVal, oldVal) {
                    _dropEnabled = newVal;
                };
                var onDragStart = function (evt, obj) {
                    if (!_dropEnabled)return;
                    isTouching(obj.x, obj.y, obj.element);

                    if (attrs.ngDragStart) {
                        $timeout(function () {
                            onDragStartCallback(scope, {$data: obj.data, $event: obj});
                        });
                    }
                };
                var onDragMove = function (evt, obj) {
                    if (!_dropEnabled)return;
                    isTouching(obj.x, obj.y, obj.element);

                    if (attrs.ngDragMove) {
                        $timeout(function () {
                            onDragMoveCallback(scope, {$data: obj.data, $event: obj});
                        });
                    }
                };

                var onDragEnd = function (evt, obj) {

                    // don't listen to drop events if this is the element being dragged
                    // only update the styles and return
                    if (!_dropEnabled || _myid === obj.uid) {
                        updateDragStyles(false, obj.element);
                        return;
                    }
                    if (isTouching(obj.x, obj.y, obj.element)) {
                        // call the ngDraggable ngDragSuccess element callback
                        if (obj.callback) {
                            obj.callback(obj);
                        }

                        if (attrs.ngDropSuccess) {
                            $timeout(function () {
                                onDropCallback(scope, {
                                    $data: obj.data,
                                    $event: obj,
                                    $target: scope.$eval(scope.value)
                                });
                            });
                        }
                    }

                    if (attrs.ngDragStop) {
                        $timeout(function () {
                            onDragStopCallback(scope, {$data: obj.data, $event: obj});
                        });
                    }

                    updateDragStyles(false, obj.element);
                };

                var isTouching = function (mouseX, mouseY, dragElement) {
                    var touching = hitTest(mouseX, mouseY);
                    scope.isTouching = touching;
                    if (touching) {
                        _lastDropTouch = element;
                    }
                    updateDragStyles(touching, dragElement);
                    return touching;
                };

                var updateDragStyles = function (touching, dragElement) {
                    if (touching) {
                        element.addClass('drag-enter');
                        dragElement.addClass('drag-over');
                    } else if (_lastDropTouch == element) {
                        _lastDropTouch = null;
                        element.removeClass('drag-enter');
                        dragElement.removeClass('drag-over');
                    }
                };

                var hitTest = function (x, y) {
                    var bounds = element[0].getBoundingClientRect();// ngDraggable.getPrivOffset(element);
                    x -= $document[0].body.scrollLeft + $document[0].documentElement.scrollLeft;
                    y -= $document[0].body.scrollTop + $document[0].documentElement.scrollTop;
                    return x >= bounds.left
                        && x <= bounds.right
                        && y <= bounds.bottom
                        && y >= bounds.top;
                };

                initialize();
            }
        };
    }])
    .directive('ngDragClone', ['$parse', '$timeout', 'ngDraggable', function ($parse, $timeout, ngDraggable) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var img, _allowClone = true;
                var _dragOffset = null;
                scope.clonedData = {};
                var initialize = function () {

                    img = element.find('img');
                    element.attr('draggable', 'false');
                    img.attr('draggable', 'false');
                    reset();
                    toggleListeners(true);
                };


                var toggleListeners = function (enable) {
                    // remove listeners

                    if (!enable)return;
                    // add listeners.
                    scope.$on('draggable:start', onDragStart);
                    scope.$on('draggable:move', onDragMove);
                    scope.$on('draggable:end', onDragEnd);
                    preventContextMenu();

                };
                var preventContextMenu = function () {
                    //  element.off('mousedown touchstart touchmove touchend touchcancel', absorbEvent_);
                    img.off('mousedown touchstart touchmove touchend touchcancel', absorbEvent_);
                    //  element.on('mousedown touchstart touchmove touchend touchcancel', absorbEvent_);
                    img.on('mousedown touchstart touchmove touchend touchcancel', absorbEvent_);
                };
                var onDragStart = function (evt, obj, elm) {
                    _allowClone = true;
                    if (angular.isDefined(obj.data.allowClone)) {
                        _allowClone = obj.data.allowClone;
                    }
                    if (_allowClone) {
                        scope.$apply(function () {
                            scope.clonedData = obj.data;
                        });
                        element.css('width', obj.element[0].offsetWidth);
                        element.css('height', obj.element[0].offsetHeight);

                        moveElement(obj.tx, obj.ty);
                    }

                };
                var onDragMove = function (evt, obj) {
                    if (_allowClone) {

                        _tx = obj.tx + obj.dragOffset.left;
                        _ty = obj.ty + obj.dragOffset.top;

                        moveElement(_tx, _ty);
                    }
                };
                var onDragEnd = function (evt, obj) {
                    //moveElement(obj.tx,obj.ty);
                    if (_allowClone) {
                        reset();
                    }
                };

                var reset = function () {
                    element.css({left: 0, top: 0, position: 'fixed', 'z-index': -1, visibility: 'hidden'});
                };
                var moveElement = function (x, y) {
                    element.css({
                        transform: 'matrix3d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, ' + x + ', ' + y + ', 0, 1)',
                        'z-index': 99999,
                        'visibility': 'visible',
                        '-webkit-transform': 'matrix3d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, ' + x + ', ' + y + ', 0, 1)',
                        '-ms-transform': 'matrix(1, 0, 0, 1, ' + x + ', ' + y + ')'
                        //,margin: '0'  don't monkey with the margin,
                    });
                };

                var absorbEvent_ = function (event) {
                    var e = event;//.originalEvent;
                    e.preventDefault && e.preventDefault();
                    e.stopPropagation && e.stopPropagation();
                    e.cancelBubble = true;
                    e.returnValue = false;
                    return false;
                };

                initialize();
            }
        };
    }])
    .directive('ngPreventDrag', ['$parse', '$timeout', function ($parse, $timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var initialize = function () {

                    element.attr('draggable', 'false');
                    toggleListeners(true);
                };


                var toggleListeners = function (enable) {
                    // remove listeners

                    if (!enable)return;
                    // add listeners.
                    element.on('mousedown touchstart touchmove touchend touchcancel', absorbEvent_);
                };


                var absorbEvent_ = function (event) {
                    var e = event.originalEvent;
                    e.preventDefault && e.preventDefault();
                    e.stopPropagation && e.stopPropagation();
                    e.cancelBubble = true;
                    e.returnValue = false;
                    return false;
                };

                initialize();
            }
        };
    }])
    .directive('ngCancelDrag', [function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                element.find('*').attr('ng-cancel-drag', 'ng-cancel-drag');
            }
        };
    }])
    .directive('ngDragScroll', ['$window', '$interval', '$timeout', '$document', '$rootScope', function ($window, $interval, $timeout, $document, $rootScope) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var intervalPromise = null;
                var lastMouseEvent = null;

                var config = {
                    verticalScroll: attrs.verticalScroll || true,
                    horizontalScroll: attrs.horizontalScroll || true,
                    activationDistance: attrs.activationDistance || 75,
                    scrollDistance: attrs.scrollDistance || 15
                };


                var reqAnimFrame = (function () {
                    return window.requestAnimationFrame ||
                        window.webkitRequestAnimationFrame ||
                        window.mozRequestAnimationFrame ||
                        window.oRequestAnimationFrame ||
                        window.msRequestAnimationFrame ||
                        function (/* function FrameRequestCallback */ callback, /* DOMElement Element */ element) {
                            window.setTimeout(callback, 1000 / 60);
                        };
                })();

                var animationIsOn = false;
                var createInterval = function () {
                    animationIsOn = true;

                    function nextFrame(callback) {
                        var args = Array.prototype.slice.call(arguments);
                        if (animationIsOn) {
                            reqAnimFrame(function () {
                                $rootScope.$apply(function () {
                                    callback.apply(null, args);
                                    nextFrame(callback);
                                });
                            })
                        }
                    }

                    nextFrame(function () {
                        if (!lastMouseEvent) return;

                        var viewportWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
                        var viewportHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

                        var scrollX = 0;
                        var scrollY = 0;

                        if (config.horizontalScroll) {
                            // If horizontal scrolling is active.
                            if (lastMouseEvent.clientX < config.activationDistance) {
                                // If the mouse is on the left of the viewport within the activation distance.
                                scrollX = -config.scrollDistance;
                            }
                            else if (lastMouseEvent.clientX > viewportWidth - config.activationDistance) {
                                // If the mouse is on the right of the viewport within the activation distance.
                                scrollX = config.scrollDistance;
                            }
                        }

                        if (config.verticalScroll) {
                            // If vertical scrolling is active.
                            if (lastMouseEvent.clientY < config.activationDistance) {
                                // If the mouse is on the top of the viewport within the activation distance.
                                scrollY = -config.scrollDistance;
                            }
                            else if (lastMouseEvent.clientY > viewportHeight - config.activationDistance) {
                                // If the mouse is on the bottom of the viewport within the activation distance.
                                scrollY = config.scrollDistance;
                            }
                        }


                        if (scrollX !== 0 || scrollY !== 0) {
                            // Record the current scroll position.
                            var currentScrollLeft = ($window.pageXOffset || $document[0].documentElement.scrollLeft);
                            var currentScrollTop = ($window.pageYOffset || $document[0].documentElement.scrollTop);

                            // Remove the transformation from the element, scroll the window by the scroll distance
                            // record how far we scrolled, then reapply the element transformation.
                            var elementTransform = element.css('transform');
                            element.css('transform', 'initial');

                            $window.scrollBy(scrollX, scrollY);

                            var horizontalScrollAmount = ($window.pageXOffset || $document[0].documentElement.scrollLeft) - currentScrollLeft;
                            var verticalScrollAmount = ($window.pageYOffset || $document[0].documentElement.scrollTop) - currentScrollTop;

                            element.css('transform', elementTransform);

                            lastMouseEvent.pageX += horizontalScrollAmount;
                            lastMouseEvent.pageY += verticalScrollAmount;

                            $rootScope.$emit('draggable:_triggerHandlerMove', lastMouseEvent);
                        }

                    });
                };

                var clearInterval = function () {
                    animationIsOn = false;
                };

                scope.$on('draggable:start', function (event, obj) {
                    // Ignore this event if it's not for this element.
                    if (obj.element[0] !== element[0]) return;

                    if (!animationIsOn) createInterval();
                });

                scope.$on('draggable:end', function (event, obj) {
                    // Ignore this event if it's not for this element.
                    if (obj.element[0] !== element[0]) return;

                    if (animationIsOn) clearInterval();
                });

                scope.$on('draggable:move', function (event, obj) {
                    // Ignore this event if it's not for this element.
                    if (obj.element[0] !== element[0]) return;

                    lastMouseEvent = obj.event;
                });
            }
        };
    }])
    .filter('trustUrl', function ($sce) {
        return function(url) {
            return $sce.trustAsResourceUrl(url);
        };
    });
function GUID() {
    var S4 = function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
};