window.ERPDic={
	/*
	 文件导出
	 */
		downloadExcel: function (entityKey) {
	        var me = this;
	        var reqParams = {};
	        clone(this._env, reqParams);
	        var source = [];
	        for(model in me._model){
	        	if(model=='_$parent') continue;
	        	if(me._model[model].sos && me._model[model].sos.length>0){
	        		for(var i=0;i<me._model[model].sos.length;i++){
	        			me._model[model].sos[i].$key=model;
	        		}
	        		source.push(me._model[model].sos);
	        	}
	        }
	        if(entityKey){
	          	 var mode={};
	          	 mode[entityKey]={
	               		page:me._model[entityKey].page,
	               		params:me._model[entityKey].params
	               };
	          	reqParams["model"]=angular.toJson(mode);
	          }
            NProgress.start();
	        reqParams._source = angular.toJson(source);
	        reqParams.optExcel = "download";
	        var code = reqParams.billtype.toLowerCase();
	        if(reqParams.billtype.toLowerCase() == 'dicsitem'){
	        	code = 'dics-item';
	        }
	        this.$http.post("/bill/parseExcel/"+code, $.param(reqParams),
	            {
	                headers: {
	                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
	                }
	            }).success(function (data) {
                NProgress.done();
	            if (data.status == '1') {
	                window.location.href = "/bill/download?path=" + data.path;
	                alert("文件导出成功！")
	            } else {
	                alert("文件导出失败,原因" + data.error);
	                return;
	            }
	        }).error(function (err) {
	            alert("文件导出失败,原因:" + err);
	            return;
	        });
	    },
    //流程工作查看器
    process:function(){
        var me=this;
        var parent = _$util.getParent(this);
        if (!parent.scope._model) {
            alert("父模型构建不完整.");
            return;
        }
        var ti = me._env.id;
        var req={};
        clone(me._env,req);
        this.$http.post("/bill/data/process", $.param(req),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            }).success(function (data) {
            if(data.status=='1'){
                window.location.href = "process#/view/doTask/" + data.ti+"/false";
            }else{
                alert(data.error);
            }
        }).error(function (err) {
            alert("打开失败,原因:" + err);
        });
    },
	/*
		 ***List字典***
		 新增
	 */
	$add:function(){
        var me=this;
        var parent = _$util.getParent(this);
        if (!parent.scope._model) {
            alert("父模型构建不完整.");
            return;
        }
        window.location.href='/bill/view/dics-item?billKey='+me._env.billkey;
    },
	/*
		 ***List字典***
		 编辑
	 */
	$edit:function(){
        var me=this;
        var parent = _$util.getParent(this);
        if (!parent.scope._model) {
            alert("父模型构建不完整.");
            return;
        }
        var Bills=me._model[me._env.billkey].sos;
        if(Bills.length!=1){
            alert("请选择一行记录");
            return ;
        }else{
            var bill=Bills[0];
            if(bill.status==25||bill.status==30){
                alert("该数据已经进入业务流程，不可编辑!!!");
                return;
            }
            window.location.href='/bill/view/dics-item?billKey='+me._env.billkey+'&ID='+bill.ID;
        }


    },
	/*
		 ***List字典***
		 删除
	 */
	$del:function(){
		var me=this;
		var reqParams={};
        clone(this._env,reqParams);
        var dics=me._model[reqParams.billkey].sos;
        if(dics.length==0){
            alert("请选择需要删除记录");
            return;
        }else{
            for(var i=0;i<dics.length;i++){
                if(dics[i].status!=40){
                    alert('该数据已经进入业务流程，不可删除!');
                    return;
                }
            }
        }
        if(window.confirm('确认删除？')){
            reqParams.dics=angular.toJson(dics);
            reqParams.status=0;
            reqParams.billID=dics[0].ID;
            this.$http.post("bill/data/dics-item-delete",$.param(reqParams),
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    }
                }).success(function (data) {
                    if(data.status==1){
                        window.location.href="/bill/view/dics?billKey="+me._env.billkey;
                        alert("删除成功！");
                    }else{
                        alert("删除失败！");
                    }

            }).error(function (err) {
                alert("单据删除失败,原因:" + err);
            });
        }else{
            return ;
        }
        
        
        
        /*reqParams.model=angular.toJson(this._model);
		this.$http.post("/bill/data/dic-item-delete",$.param(reqParams),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function(data){
				if(data.status == 1){
					me.__load();
					me.__clear();
					me._env.ID = '';
					me.parentId="";
					alert("删除成功！");
				}else{
					alert("删除失败！");
				}
		}).error(function(err){
				alert(err);
		});*/
	},
	/*
		 ***List字典***
		 返回
	 */
	$reBack:function(){
    	window.location.href='/bill/view/dics?billKey='+this._env.billkey;
    },
	/*
		 保存
	 */


	save:function(type,fn){
        NProgress.start();
		var me=this;

        function delErr(_err) {
            var i = me.errList.length;
            while (i--) {
                if (me.errList[i].key == _err.key) {
                    me.errList.splice(i, 1);
                }
            }
        }

        for(var i=0;i<me.checkRuleLists.length;i++){
            var _key=me.checkRuleLists[i].key;
            var _value=getHeadValue(_key);
            try {
                var _rule=eval(me.checkRuleLists[i].rule[0].rule);
                var _rst=_rule.test(_value);
			}catch (e){
            	alert(JSON.stringify(e));
			}
			if((_rst==false || _value == undefined) ){
			$("[key="+_key+"]").focus();
            }else{
                delErr(me.checkRuleLists[i]);
			};
        };
        if(me.errList.length>0){
            var __key=me.errList[0].key;
            alert("请填写正确信息");
            $("[key=" + __key + "]").focus();
            NProgress.done();
            return;
        }

		//保存之前执行before事件
        //解析fn方法
        if(fn.before){
            var _before=fn.before;
            for(var p in _before){
                var _fn=p+"("+_before[p].join(",")+")";
                eval(_fn);
            }
        };

        var postData = {};
        var tag = true;
        var _errMsg=[],
            _warnMsg=[]


        angular.forEach(me._model, function (data) {
            if(data.head==false){
                _errMsg[data.key]=[];
                _warnMsg[data.key]=[];

                //保存之前过滤空行信息
                var i=data.cos.length;
                while (i--) {
                    if(data.cos[i].isNullRow==true){
                        data.cos.delByValue(data.cos[i]);
                    }
                };

                //保存之前过验证是否可以保存空行
                if(!data.canSaveNull || data.canSaveNull==false){
                    if(data.cos.length<=0){
                        alert('数据明细不能为空');
                        tag = false;
                        NProgress.done();
                        return false;
                    }
                };

                //获取规则验证错误信息
                if (me._env.rules&&me._env.rules[data.key]&&me._env.rules[data.key]!==''){
                    for(var i=0;i<me._env.rules[data.key].length;i++){
                        _errMsg[data.key].push(me._env.rules[data.key][i].errorMsg);
                        if(me._env.rules[data.key][i].waring){
                            _warnMsg[data.key].push(me._env.rules[data.key][i].waring.waringMsg);
                        }
                    };
                    postData[data.key] = {models: data.cos,rules:me._env.rules[data.key]};
                }
            }
        });


        if(!tag || tag==false){
        	return;
		};
		var reqParams={};
        clone(this._env,reqParams);
        reqParams.model=angular.toJson(this._model);
		this.$http.post("/bill/data/dic-item-save",$.param(reqParams),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function(data){
            NProgress.done();
				if (data.status==1) {

                    //执行after方法
                    if(fn.before){
                        var _after=fn.after;
                        for(var p in _after){
                            var _fn=p+"("+_after[p].join(",")+")";
                            eval(_fn);
                        }
                    };

					var billType = me._env.billtype;
					if(billType=="Dictionary"){
						me.__clear();
						me.__load();
						me._env.ID = '';
			        }else if(billType=="DicsItem"){
//			        	me.__loadDics();
			        	window.location.href='/bill/view/dics?billKey='+me._env.billkey;
			        }
					alert("保存成功!");


				}else{
					alert("保存失败！原因:" + data.error);
				}
		}).error(function(err){
            NProgress.done();
			alert("保存失败,原因:" + err);
		});
	},
	/*
		 ***Tree字典***
		 编辑
	 */
	edit:function (){
		var me=this;
		var data = {
				isParent:me.isParent,
				hasChild:me.hasChild
		}
		return data;
	},
	/*
		 ***Tree字典***
		 删除
	 */
	del:function(){
		var me=this;
		var reqParams={};
        clone(this._env,reqParams);
        reqParams.model=angular.toJson(this._model);
		this.$http.post("/bill/data/dic-item-delete",$.param(reqParams),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function(data){
				if(data.status == 1){
					me.__load();
					me.__clear();
					me._env.ID = '';
					me.parentId="";
					alert("删除成功！");
				}else{
					alert("删除失败！");
				}
		}).error(function(err){
				alert(err);
		});
	},
	/*
		 ***Tree字典***
		 新增
	 */
	add:function(){
		var me=this;
		var parent = _$util.getParent(this);
		if (!parent.scope._model) {
            alert("父模型构建不完整.");
            return;
        }
		me._env.parentId = me._env.ID;
//		me._env.ID = '';
		this.__clear();
	},
	
	validAdd:function(){
		var me=this;
		if(me._env.ID==''||me._env.ID==null){
			return false;
		}else{
			return true;
		}
	},
	
	__clear: function () {
		for (var i = 0; i < this._model.tableKeys.length; i++) {
			if (this._model[this._model.tableKeys[i]].head) {
				this[this._model.tableKeys[i]] = clone(this._model[this._model.tableKeys[i]].archetype,{});
				this._model[this._model.tableKeys[i]].cos[0] =this[this._model.tableKeys[i]];
			}else {
				this._model[this._model.tableKeys[i]].cos =[];
			}
		};
		this.$broadcast('$_MODEL_CHANGE_$',this._model);
	},

	 pageInit: function () {
         var cacheSkin=localStorage.getItem('skin');
         if(cacheSkin && cacheSkin!==''){
             this.skinClass=cacheSkin;
         }
        //寻找我的父
        var parent = _$util.getParent(this);
        if (!parent.scope._model) {
            alert("父模型构建不完整.");
        }
         NProgress.start();
        this.__initCtrMethodCallback();
		this.__initViewStatus();
        this.__load();
    },
    
    dicsInit: function(){
        var cacheSkin=localStorage.getItem('skin');
        if(cacheSkin && cacheSkin!==''){
            this.skinClass=cacheSkin;
        }
        //寻找我的父
        var parent = _$util.getParent(this);
        if (!parent.scope._model) {
            alert("父模型构建不完整.");
        }

        this.__initCtrMethodCallback();

        this.__loadDics();
    },

	__loadDics : function (entityKey) {
		var me = this;
		var url = '/bill/data/dics';
		var req={};
		clone(me._env,req);
		if(entityKey){
			var mode={};
			mode[entityKey]={
				page:me._model[entityKey].page,
				params:me._model[entityKey].params
			};


			req["model"]=angular.toJson(mode);
		}
		this.$http.post(url, $.param(req),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function (ret) {
			if (ret.status == 0) {
				alert(ret.error);
			} else if (ret.status == 1) {
				me.__bind(ret.data);
			} else if (ret.status == 2) {//正常返回，但没有数据
				console.log("no data.")
			} else {
				alert("位置异常");
			}
		}).error(function (err) {
			alert(err);
		});

	},
    
	__initViewStatus:function(){
		this.viewStatus=0;
	},

	getViewStatus:function(){
		if(this.viewStatus){
			return this.viewStatus;
		}
		return 0;
	},

    /**
     * 视图中调用控制器的方法：
     *        @$this.ctrlMethod();
     *    视图中调用主界面widget
     *        @$.widgetKey.method();
     * @private
     */
    __initCtrMethodCallback: function () {
        var me = this;
		this._$CACHE=createCache();

		//调用队列
		this.$on('$_INSTRUCTION_METHOD_CALL', function (e, param) {
			me._$CACHE.pushQueue({id: me._$CACHE.count(), data: param, count: 0});
		});

		//消息循环
		this.$interval(function () {
			var dels = [];
			for (var i = 0; i < me._$CACHE.queue().length; i++) {
				if (me._$CACHE.queue()[i].count > 1800) {
					dels.push(me._$CACHE.queue()[i]);
				} else {
					me._$CACHE.queue()[i].count += 1;
					me.$broadcast('$_INSTRUCTION_METHOD_CALLBACK',me. _$CACHE.queue()[i]);
				}
			}
			for (var i = 0; i < dels.length; i++) {
				me._$CACHE.delQueue(dels[i]);
			}
		}, 10);


        //事件：$_INSTRUCTION_METHOD_CALLBACK
        this.$on('$_INSTRUCTION_METHOD_CALLBACK', function (e, param) {
            if (param.data.key == "$this") {//调用控制器中的方法
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
				me._$CACHE.delQueue(param);
            } else if (param.data.key == "$") {//调用主界面中widget中的方法
                var subkey = param.data.body.substring(1, param.data.body.indexOf("."));
                param.data.key = subkey;
                param.data.body = param.data.body.substring(subkey.length + 2);
                param.data.renderid = me.renderId;
            }
        });
    },

	__load:function(){
		var me=this;
		var billType = me._env.billtype;
		if(billType=="Dictionary"){
            url="/bill/data/dic";
        }else if(billType=="Dics"){
            url="/bill/data/dic-item";
        }else if(billType=="DicsItem"){
        	url="/bill/data/dics-item";
        }else{
            alert("页面类型异常");
            return;
        }
		this.$http.post(url,$.param(me._env),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function(data){
				if(data.status==1){
					if(billType=="Dictionary"){
						me.__bindTree(data.data);
			        }else if(billType=="Dics" || billType=="DicsItem"){
			        	me.__bind(data.data);
			        }
					
				}else{
					alert(data.error);
				}

		}).error(function(err){
				alert(err);
		});
	},

	click:function(data){
		var me=this;
		var req={};
		if(data){
			var id=data[0].ID;
			this._env.ID = id;
			if(data[0].nodeType==1){
				me.isParent = true;
			}else{
				me.isParent = false;
			}
			if(data[0].isParent==true){
				me.hasChild = true;
			}else{
				me.hasChild = false;
			}
		}
		clone(this._env,req);
		this.$http.post("/bill/data/dic-item",$.param(req),
			{
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).success(function(data){
				me.__bind(data.data);
		}).error(function(err){
				alert(err);
		});

	},
	/*
		 ***List字典***
		 初始化
	 */
	__bind:function(data){
		this._model.tableKeys = [];
        window.$body=[];
		for(var i=0;i<data._models.length;i++){
			this._model.tableKeys.push(data._models[i]);
			var dataObject=data[data._models[i]];
			this._model[data._models[i]]=dataObject;
			dataObject.co=dataObject.cos[0];
			dataObject.sos=[];
			dataObject.dels=[];
            dataObject.archetype.shadowCol = [];
			if(dataObject.head){
				this[data._models[i]]=dataObject.co;
				this["$"+data._models[i]]=dataObject;
                window.$head= dataObject.co;
			}
			else{
                if(dataObject.cos && dataObject.cos.length>0){
                    for(var p =0; p<dataObject.cos.length; p++){
                        dataObject.cos[p].shadowCol=[];
                    }
                }
                window.$body[data._models[i]]= dataObject;
				this["__$"+data._models[i]+"$__"]=dataObject.cos;
				this["$"+data._models[i]]=dataObject; 
			}
		}
        window.$model= this._model;
        NProgress.done();
		//发送修改广播
		this.$broadcast('$_MODEL_CHANGE_$',this._model);

	},
	/*
		 ***Tree字典***
		 初始化
	 */
	__bindTree:function(data){
		for(var i=0;i<data._models.length;i++){
			var dataObject=data[data._models[i]];
			this._model["$t_"+data._models[i]]=dataObject.cos;
			//this["$t_"+data._models[i]]=dataObject.cos;
		}
        NProgress.done();
		//发送修改广播
		this.$broadcast('$_MODEL_CHANGE_$',this._model);

	}

};