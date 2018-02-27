(function ($) {
    /*
     *继承方法：
     //定义基类构造函数
     function Person(name) {this.name = name; }
     //定义基本的方法
     $.extend(Person.prototype,{  })
     //子类构造函数
     function Author(name, books) {
     //必须调用父类的构造函数，否则，父类的特权级方法和属性将继承不到哟
     Author.superclass.constructor.call(this, name);
     this.books = books;
     }
     //扩展父
     Author.extend(Person, { });
     */
    Function.prototype.extend = function (parent, overrides) {
        if (typeof parent != 'function') return this;
        //保存对父类的引用
        this.base = parent.prototype;
        this.base.constructor = parent;
        //继承
        var f = function () {
        };
        f.prototype = parent.prototype;
        this.prototype = new f();
        this.prototype.constructor = this;
        //附加属性方法
        if (overrides) $.extend(this.prototype, overrides);
    };

    //延时加载
    Function.prototype.defer = function (o, defer, args) {
        var fn = this;
        return setTimeout(function () {
            fn.apply(o, args || []);
        }, defer);
    };
    var bool = navigator.userAgent.indexOf("Mobile");

    /**
     核心为一个邮局协议，其他模块通过这个邮局进行订阅与发布消息
     */
    $.postOffice = ( function () {
        /**订阅消息
         * @param {Object} channel:订阅的频道
         * @param {Object} fn：回调函数
         */
        var subscribe = function (channel, fn, owner) {
                if (!$.postOffice.channels[channel]) {
                    $.postOffice.channels[channel] = [];
                }
                if (owner) {
                    $.postOffice.channels[channel].push({
                        context: owner, //这里的owner是订阅消息的对象，函数fn是其中的一个函数
                        callback: fn
                    });
                    return this;
                    //注意，默认情况这里的this为邮局对象本身
                } else {
                    $.postOffice.channels[channel].push({
                        context: this,
                        callback: fn
                    });
                    return this;
                }

            },
            /**
             * 向某个频道发送消息，消息一旦发布，会立马查看订阅了该频道消息的的对象，并执行其回调函数
             * @param {Object} channel 频道
             */
            publish = function (channel) {
                if (!$.postOffice.channels[channel]) {
                    return false;
                }

                //注意，发布消息时，除了第一个参数外，其他的参数作为回调函数的输入参数被输入
                var args = Array.prototype.slice.call(arguments, 1);

                //获取除chanel外的参数
                //扫描这个频道的订阅者
                for (var i = 0, l = $.postOffice.channels[channel].length; i < l; i++) {
                    var subscription = $.postOffice.channels[channel][i];
                    subscription.callback.apply(subscription.context, args);
                }
                return this;
            };

        /*
         *返回对应的对象，
         */
        return {
            channels: {},
            publish: publish,
            subscribe: subscribe,
            /**
             *  将消息安装到obj对象上，这样obj对象即为当前的工作上下文，等效于subscribe中的owner代理
             * @param {Object} obj
             */
            installTo: function (obj) {
                obj.subscribe = subscribe;
                obj.publish = publish;
            }
        };

    }());


    /*
     核心对象workflow,这里采用的为单态模式
     */
    window.workflow = $.workflow = {
        //定义对象属性
        version: "1.0.0",
        bkContainer: new HashTable(),//背景容器
        container: new HashTable(),//容器
        lineContainer: new HashTable(),//连接线容器
        noteContainer: new HashTable(),//注解容器
        operatorHandler: [],//操作模式处理器对象
        processName: "",//流程名称
        processVersion: -1,//流程版本
        pdId: null,
        shapes: [],
        processLoadStatus: false,
        deletedObjects: [],//删除队列
        cid2oid: {},//CID与OID的映射关系表，该表用于同步流程数据库中的实例存储
        stepX: 20,
        stepY: 20,
        designMode:false,//是否为设置模式，默认为false，非设置模式不允许添加和删除节点
        lock:"",//流程锁，主要用于设置时
       
        /**
         * 设置操作模式
         * @param mode
         */
        setOperatorMode: function (mode) {
            var oldMode = this.mode;
            this.mode = mode;
            if (this.operatorHandler[oldMode]) {
                this.operatorHandler[oldMode].unBindHandler();
            }
            if (this.operatorHandler[this.mode]) {
                this.operatorHandler[this.mode].bindHander();
            }
        },

        /**
         * 获取操作模式
         * @returns {*}
         */
        getOperatorMode: function () {
            if (this.mode) {
                return this.mode;
            }
            return 1;
        },
        
               

        /**
         * 删除组件
         * @param component
         */
        remove: function (component) {
            //CID:组件id
            if (component.__getType() == "workflow.line") {
                this.lineContainer.remove(component.__getCID());//删除自身
                this.deletedObjects.push(component);//加入删除队列
            } else {
                this.container.remove(component.__getCID());//删除组件自身
                this.deletedObjects.push(component);//加入删除队列
                var lcs = workflow.lineContainer.values();
                for (var i = 0; i < lcs.length; i++) {//删除组件的入连接线和出连接线
                    if (lcs[i].target.CID == component.CID || lcs[i].source.CID == component.CID) {
                        this.lineContainer.remove(lcs[i].CID);//从线图层删除线
                        this.deletedObjects.push(lcs[i]);//放入删除队列
                    }
                }
            }
        },

        /**
         * 删除所有的对象，注意这个方法没有与远程对象进行同步
         */
        removeAll: function () {//该方法不会与后端持久层同步，注意保存
            if (this.container) {
                this.bkContainer.removeAll();
                this.lineContainer.removeAll();
                this.container.removeAll();
                this.noteContainer.removeAll();
                this.render();
            }
        },

        /**
         * 删除选择的对象
         */
        removeSelectedObject: function () {
            if(!this.designMode){
                alert("非设计模式，不能删除对象");
            }

            if (this.selected && this.designMode) {
                this.remove(this.selected);//删除选择的对象
                this.selected = null;
                this.render();
            }
        },


        buildProcessSaveParameters: function () {
            var result = {};
            result.pdId = this.pdId;//流程ID
            result.lock=this.lock;
            result.designMode=this.designMode
            var ads = workflow.container.values();
            var ads_array = [];
            for (var i = 0; i < ads.length; i++) {//活动节点信息
            	ads_array.push({
                    name: ads[i].__getText(),
                    type: ads[i].__getType(),
                    cid: ads[i].__getCID(),
                    oid: this.cid2oid[ads[i].__getCID()],
                    shape: ads[i].toJSON()
                });
            }
            result.ads = JSON.stringify(ads_array);

            var trans = workflow.lineContainer.values();
            var trans_array = [];
            for (var i = 0; i < trans.length; i++) {//连接线信息
                trans_array.push({
                    name: trans[i].__getText(),
                    type: trans[i].__getType(),
                    cid: trans[i].__getCID(),
                    oid: this.cid2oid[trans[i].__getCID()],
                    shape: trans[i].toJSON(),
                    s_cid: trans[i].source.__getCID(),
                    s_oid:workflow.cid2oid[trans[i].source.__getCID()],
                    t_cid:trans[i].target.__getCID(),
                    t_oid:workflow.cid2oid[trans[i].target.__getCID()]
                });
            }
            result.trans=JSON.stringify(trans_array);

            var delObjs_array=[];
            for(var i=0;i<this.deletedObjects.length;i++){//删除组件的入连接线和出连接线
                delObjs_array.push({
                    name:this.deletedObjects[i].__getText(),
                    type: this.deletedObjects[i].__getType(),
                    cid: this.deletedObjects[i].__getCID(),
                    oid: workflow.cid2oid[this.deletedObjects[i].__getCID()]
                });
            }

            result.deletedObjects=JSON.stringify(delObjs_array);
            return result;
        },

        /**
         * 遍历并保存流程图的缓存副本
         action:/process/saveProcessDefinitionViaCanvas
         格式：
         pdId:pdId,
         "lock":this.lock,
         "designMode":this.designMode,
         ads:[{name:"",type:"",cid:"",oid:"",shape:""}],
         trans:[{name:"",type:"",cid:"",oid:"",shape:"",s_cid:"",s_oid:"",t_cid:"",t_oid:""}],
         deletedObjects:[{name:"",type:"",cid:"",oid:""}]
         */
        saveProcess: function () {
        	
            if (this.processLoadStatus) {
                //加载流程图形，基础格式为：cid2oid:{cid与对象的映射关系表},shapes:[图形元信息]
            	
            	//var reloadProcessFunc=this.reloadProcess;
            	//var buildPara=this.buildProcessSaveParameters;
            	
                $.ajax({
                    type: "POST",
                    url: "/process/saveProcessDefinitionViaCanvas",
                    async: false,
                    data: workflow.buildProcessSaveParameters(),
                    dataType: "json",
                    beforeSend: function (xmlHttpRequest) {
                        if(bool>0){
                            $('#loadingToast').show();
                        }else{
                           NProgress.start();//遮罩层弹出
                        }
                    },
                    success: function (data) {
                        if (data.status == 1) {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                               // NProgress.done();  //遮罩层弹出
                            }
                            //保存成功，重新加载流程
                            workflow.reloadProcess();
                            //this.reloadProcess();

                        } else {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                //NProgress.done();  //遮罩层弹出
                            }
                            alert("流程保存失败" + data.errorMsg);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus) {  //加载失败
                        alert("调用接口报错,请检查网络后重试.");
                        if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                    }
                });


            } else {//没有加载过流程
                alert("你还没有加载流程，请加载流程后再试");
            }
        },


        /**
         * 挂接工作流对象
         * @param element：容器DOM元素
         * @param name:工作流名称
         * @param width：画布宽度
         * @param height：画布高度
         */
        mount: function (element, name, width, height) {
            this.name = name;
            this.width = width;
            this.height = height;
            //<canvas id="cvs" width="1000" height="800"></canvas>
            this.canvasEle = $("<canvas></canvas>");
            this.canvasEle.attr("width", width);
            this.canvasEle.attr("height", height);
            //this.canvasEle.css("position", "absolute");
            this.canvasEle.attr("id", name);
            //border:1px solid #ddd;
            this.element = element;
            $(this.element).append(this.canvasEle);
            this.canvasEle = this.canvasEle[0];
            this.canvas = this.canvasEle.getContext('2d');

        },


        /**
         * 加载流程
         *          传入参数：
         *          ｛
         *              processName:"",
         *              processVersion:"",
         *              designMode:true,
         *              lock:"lock"
         *          ｝
         * 返回JSON对象：{
		 * 				pdId:pdId,
		 * 				processName:processName,
		 * 				processVersion:processVersion,
		 * 			    lock:"",//流程锁，仅在设置模式时处理该参数
		 * 				cid2oid:{cid与对象的映射关系表},
		 * 				shapes:[图形元信息]
		 * 			}
         *
         */
        loadProcess: function (processName, processVersion) {
          	if (this.processName) {//清空历史记录
                this.removeAll();
                this.processName = null;
                this.processVersion = -1;
                this.cid2oid = {};
                this.deletedObjects = [];//初始化删除队列
            }
            if (processName) {
                this.processName = processName;
                this.processVersion = processVersion;
                this.deletedObjects = [];//初始化删除队列
                //加载流程图形，基础格式为：cid2oid:{cid与对象的映射关系表},shapes:[图形元信息]
                $.ajax({
                    type: "POST",
                    url: "/process/loadProcessCanvas",
                    async: false,
                    data: {'processName': this.processName, "processVersion": this.processVersion,designMode:this.designMode,lock:this.lock},
                    dataType: "json",
                    beforeSend: function (xmlHttpRequest) {
                        if(bool>0){
                            $('#loadingToast').show();
                        }else{
                           NProgress.start();//遮罩层弹出
                        }
                    },
                    success: function (data) {
                        if (data.status == 1) {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            //加载成功
                            workflow.processLoadStatus = true;
                            workflow.cid2oid = data.cid2oid;
                            workflow.pdId = data.pdId;
                            workflow.processName = data.processName;
                            workflow.processVersion = data.processVersion;
                            workflow.shapes = data.shapes;
                            workflow.lock=data.lock;
                            workflow.__restore();//恢复图形
                            workflow.render();

                        } else {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            workflow.processLoadStatus = false;//流程加载失败
                            alert("流程加载失败【"+data.errorMsg+"】");
                            history.back(-1);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus) {  //加载失败
                        alert("调用接口报错,请检查网络后重试.");
                        workflow.processLoadStatus = false;//流程加载失败
                        if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                    }
                });
            } else {
                alert("流程名称或流程版本号不存在.");
            }
        },


        __restore: function () {
            if (workflow.processLoadStatus && workflow.shapes) {
                var lines = [];
                //绘制活动，缓存绘制线
                for (var i = 0; i < workflow.shapes.length; i++) {
                    var _type = workflow.shapes[i].type;
                    var _cid = workflow.shapes[i].cid;
                    var _text = workflow.shapes[i].text;
                    if (_type == "workflow.node.task") {//任务节点
                        //var _labelName =workflow.shapes[i].labelName;
                        var task = new workflow.shape.Task(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});

                        task.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[2].count++;
                        workflow.container.add(_cid, task);

                    }  else if (_type == "workflow.node.java") {//任务节点
                        //var _labelName =workflow.shapes[i].labelName;
                        var task = new workflow.shape.Java(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});
                        task.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[2].count++;
                        workflow.container.add(_cid, task);

                    }
                    else if (_type == "workflow.node.decision") {//决策节点
                        var decision = new workflow.shape.Decision(workflow.canvas, _text, 0, 0, {
                            newCID: true,
                            CID: _cid
                        });
                        decision.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[5].count++;
                        workflow.container.add(_cid, decision);

                    } else if (_type == "workflow.node.fork") {//分支节点
                        var fork = new workflow.shape.Fork(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});
                        workflow.operatorHandler[6].count++;
                        fork.fromJSON(workflow.shapes[i]);
                        workflow.container.add(_cid, fork);
                    } else if (_type == "workflow.node.join") {//合并节点
                        var join = new workflow.shape.Join(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});
                        join.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[7].count++;
                        workflow.container.add(_cid, join);
                    } else if (_type == "workflow.node.start") {//开始节点
                        var start = new workflow.shape.Start(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});

                        start.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[9].count++;
                        workflow.container.add(_cid, start);
                    } else if (_type == "workflow.node.end") {//结束节点
                        var end = new workflow.shape.End(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});
                        end.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[10].count++;
                        workflow.container.add(_cid, end);
                    } else if (_type == "workflow.node.status") {//状态节点
                        var ststst = new workflow.shape.Status(workflow.canvas, _text, 0, 0, {newCID: true, CID: _cid});
                        ststst.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[3].count++;
                        workflow.container.add(_cid, ststst);
                    } else if (_type == "workflow.node.meeting") {//会签节点
                        var meeting = new workflow.shape.Meeting(workflow.canvas, _text, 0, 0, {
                            newCID: true,
                            CID: _cid
                        });
                        meeting.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[4].count++;
                        workflow.container.add(_cid, meeting);
                    } else if (_type == "workflow.node.subprocess") {//子流程节点
                        var subprocess = new workflow.shape.SubProcess(workflow.canvas, _text, 0, 0, {
                            newCID: true,
                            CID: _cid
                        });
                        subprocess.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[8].count++;
                        workflow.container.add(_cid, subprocess);
                    } else if (_type == "workflow.node.dockprocess") {//挂靠流程节点
                        var dockprocess = new workflow.shape.DockProcess(workflow.canvas, _text, 0, 0, {
                            newCID: true,
                            CID: _cid
                        });
                        dockprocess.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[12].count++;
                        workflow.container.add(_cid, dockprocess);
                    } else if (_type == "workflow.node.associationprocess") {//挂靠流程节点
                        var associationprocess = new workflow.shape.DockProcess(workflow.canvas, _text, 0, 0, {
                            newCID: true,
                            CID: _cid
                        });
                        associationprocess.fromJSON(workflow.shapes[i]);
                        workflow.operatorHandler[13].count++;
                        workflow.container.add(_cid, associationprocess);
                    } else if (_type == "workflow.line") {//延迟画线
                        lines.push(workflow.shapes[i]);
                    } else {
                        alert("不支持的图形类型");
                    }
                }
                //绘制线,必须提前绘制图形
                for (var j = 0; j < lines.length; j++) {
                    //canvas,text,source,target,options,drawType
                    var _cid = lines[j].cid;
                    var _text = lines[j].text;
                    var line = new workflow.shape.Line(workflow.canvas, _text, null, null, {newCID: true, CID: _cid});
                    line.fromJSON(lines[j]);
                    workflow.operatorHandler[11].count++;
                    workflow.lineContainer.add(_cid, line);

                }


            } else {
                alert("没有成功加载流程或流程图形为空");
            }
        },

        /**
         * 重新加载流程
         *
         */
        reloadProcess: function () {
            if (this.processName && this.processLoadStatus) {
                this.processLoadStatus = false;
                this.cid2oid = {};
                this.shapes = [];
                this.removeAll();
                this.deletedObjects = [];//初始化删除队列
                this.loadProcess(this.processName, this.processVersion);//重新加载,哈哈
            } else {
                alert("没有加载流程或流程加载失败，不能重新加载.");
            }
        },

        /**
         *  添加活动定义，传入的参数包括：
         *      {'pdId':this.pdId,"name":name,"type":type,"cid":cid,designMode:"",lock:"",shape:{}}
         *
         *    返回的数据包括：
         *        status:
         *        cid:
         *        oid:
         *
         *
         */
        addActivityDefinition: function (component) {
        	if(!workflow.designMode){
                alert("非设计模式，不能添加活动.");
                return false;
            }
            var result = true;
            if (!this.cid2oid[component.CID] && this.processLoadStatus) {//这是一个持久化映射表中没有流程实例，并且流程加载成功能
                var name = component.__getText();//名称
                var type = component.__getType();//类型
                var cid = component.__getCID();//CID
                //保存名为name的activity，
                $.ajax({
                    type: "POST",
                    url: "/process/addActivityDefinitionViaCanvas",
                    data: {
                        'pdId': this.pdId,
                        "name": name,
                        "type": type,
                        "cid": cid,
                        "lock":this.lock,
                        "designMode":this.designMode,
                        shape: JSON.stringify(component.toJSON())
                    },
                    dataType: "json",
                    async: false,//同步
                    beforeSend: function (xmlHttpRequest) {
                        if(bool>0){
                            $('#loadingToast').show();
                        }else{
                           NProgress.start();//遮罩层弹出
                        }
                    },
                    success: function (data) {
                        if (data.status == 1) {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            //加载成功
                            workflow.cid2oid[cid] = data.oid;
                            result = true;
                        } else {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            result = false;
                            alert(data.errorMsg);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus) {  //加载失败
                        alert("调用接口报错,请检查网络后重试.");
                        result = false;
                        if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                    }
                });
                return result;
            }

            return result;//这本身就是一个持久化组件，这里不做远程处理
        },

        /*
         添加转移
         action:/process/addTransitionViaCanvas
         *  添加转移的定义，传入的参数包括：
         *      {'pdId':this.pdId,"name":name,"type":type,"cid":cid,shape:{},s_cid:"",s_oid:"",t_cid:"",t_oid:""}
         *
         *    返回的数据包括：
         *        statu
         *        cid:，
         *        oid:
         *
         */
        addTransition: function (component) {
            if(!workflow.designMode){
                alert("非设计模式，不能添加活动.");
                return false;
            }
            var result = true;
            if (!this.cid2oid[component.CID] && this.processLoadStatus) {//这是一个没有持久化的转移，并且流程加载成功能
                var name = component.__getText();//转移名称
                var type = component.__getType();//类型
                var cid = component.__getCID();//转移图形标识ID
                var s_cid = component.source.__getCID();
                var s_oid = workflow.cid2oid[s_cid];
                if (!s_cid || !s_oid) {
                    alert("源控件没有找到");
                    return false;
                }
                var t_cid = component.target.__getCID();
                var t_oid = workflow.cid2oid[t_cid];
                if (!t_cid || !t_oid) {
                    alert("目标控件没有找到");
                    return false;
                }

                //保存名为name的transition，
                $.ajax({
                    type: "POST",
                    url: "/process/addTransitionViaCanvas",
                    data: {
                        'pdId': this.pdId,
                        "name": name,
                        "type": type,
                        "cid": cid,
                        "lock":this.lock,
                        "designMode":this.designMode,
                        shape: JSON.stringify(component.toJSON()),
                        s_cid: s_cid,
                        s_oid: s_oid,
                        t_cid: t_cid,
                        t_oid: t_oid
                    },
                    dataType: "json",
                    async: false,//同步

                    beforeSend: function (xmlHttpRequest) {
                        if(bool>0){
                            $('#loadingToast').show();
                        }else{
                           NProgress.start();//遮罩层弹出
                        }
                    },
                    success: function (data) {
                        if (data.status == 1) {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            //加载成功
                            workflow.cid2oid[cid] = data.oid;
                            result = true;
                        } else {
                            if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                            result = false;
                            alert(data.errorMsg);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus) {  //加载失败
                        alert("调用接口报错,请检查网络后重试.");
                        result = false;
                        if(bool>0){
                                $('#loadingToast').hide();
                            }else{
                                NProgress.done();  //遮罩层弹出
                            }
                    }
                });
                return result;
            }
            return result;//这本身就是一个持久化组件，这里不做远程处理
        },


      
        /*
         *添加组件
         */
        add: function (component) {
            //CID:组件id
            if (component.__getType() == "workflow.line") {
                this.lineContainer.add(component.__getCID(), component);
                this.render();
                if (!this.addTransition(component)) {
                    //添加转移失败
                    this.lineContainer.remove(component.__getCID());
                    this.render();
                }
            } else {
                this.container.add(component.__getCID(), component);
                this.render();
                if (!this.addActivityDefinition(component)) {
                    //添加组件失败
                    this.container.remove(component.__getCID());
                    this.render();
                }

            }
        },
        /*
         *添加组件到背景图层
         */
        addBk: function (component) {
            this.bkContainer.add(component.__getCID(), component);
        },

        /*
         *添加组件到背景图层
         */
        addLine: function (component) {
            this.lineContainer.add(component.__getCID(), component);
        },
        /*
         *添加组件到背景图层
         */
        addNote: function (component) {
            this.noteContainer.add(component.__getCID(), component);
        },


        /**
         * 渲染方法
         */
        render: function () {
            this.clearCanvas();
            this.renderBk();
            this.renderLine();
            this.renderContent();
            this.renderNote();
            this.renderSelectedNode();
        },


        /*
         * 渲染内容
         */
        renderContent: function () {
            var values = this.container.values();
            for (var i = 0; i < values.length; i++) {
                values[i]._render();//渲染之
            }
        },
        /**
         *  清空画布
         */
        clearCanvas: function () {
            this.canvas.clearRect(0, 0, this.width, this.height);
        },

        /**
         * 渲染线连接图层
         */
        renderLine: function () {
            var values = this.lineContainer.values();
            for (var i = 0; i < values.length; i++) {
                values[i]._render();//渲染之
            }
        },


        /**
         * 渲染背景图层
         */
        renderBk: function () {
            var values = this.bkContainer.values();
            for (var i = 0; i < values.length; i++) {
                values[i]._render();//渲染之
            }
            //绘制网格
            this.drawGrid();
        },
        drawGrid: function () {
            this.canvas.save();
            this.canvas.strokeStyle = "lightgray";
            //设置线宽为0.5
            this.canvas.lineWidth = 0.5;
            //绘制x轴网格
            //注意：canvas在两个像素的边界处画线
            //由于定位机制，1px的线会变成2px
            //于是要+0.5
            for (var i = this.stepX + 0.5; i < this.width; i = i + this.stepX) {
                //开启路径
                this.canvas.beginPath();
                this.canvas.moveTo(i, 0);
                this.canvas.lineTo(i, this.height);
                this.canvas.stroke();
            }

            //绘制y轴网格
            for (var i = this.stepY + 0.5; i < this.height; i = i + this.stepY) {
                this.canvas.beginPath();
                this.canvas.moveTo(0, i);
                this.canvas.lineTo(this.width, i);
                this.canvas.stroke();
            }
            this.canvas.restore();
        },

        /**
         * 渲染注解图层
         */
        renderNote: function () {
            var values = this.noteContainer.values();
            for (var i = 0; i < values.length; i++) {
                values[i]._render();//渲染之
            }
        },


        renderSelectedNode: function () {
            if (this.selected) {
                var points = this.selected.getLinkedLinePointTo();
                for (var i = 0; i < points.length; i++) {
                    this.canvas.save();
                    this.canvas.beginPath();
                    this.canvas.fillStyle = "yellow";
                    this.canvas.strokeStyle = "#355878";
                    this.canvas.lineWidth = 1;
                    this.canvas.shadowOffsetX = -2;
                    this.canvas.shadowOffsetY = -2;
                    this.canvas.shadowColor = "gray";
                    this.canvas.shadowBlur = 15;
                    this.canvas.arc(points[i].x, points[i].y, 3, 0, 2 * Math.PI);
                    this.canvas.fill();
                    this.canvas.stroke();
                    this.canvas.restore();
                }
            }
        },

        getTarget: function (point) {
            var values = this.container.values();
            for (var i = 0; i < values.length; i++) {
                if (values[i]._containPoint(point)) {
                    return values[i];
                }
            }
            var values = this.lineContainer.values();
            for (var i = 0; i < values.length; i++) {
                if (values[i]._containPoint(point)) {
                    return values[i];
                }
            }
            return null;
        },
        //命名空间
        //核心控件,封装了一些常用方法
        core: {},
        //命名空间
        //核心图形命名空间
        shape: {},

        /*
         获取Canvas在屏幕上,相对坐标位置
         */
        getPointOnCanvas: function (x, y) {
            var _box = this.canvasEle.getBoundingClientRect();
            return {
                x: x - _box.left * (this.width / _box.width),
                y: y - _box.top * (this.height / _box.height)
            };
        },
        /*
         * addEventListener:监听Dom元素的事件
         *
         *  target：监听对象
         *  type：监听函数类型，如click,mouseover
         *  func：监听函数
         */
        addEventHandler: function (target, type, func) {
            if (target.addEventListener) {
                //监听IE9，谷歌和火狐
                target.addEventListener(type, func, false);
            } else if (target.attachEvent) {
                target.attachEvent("on" + type, func);
            } else {
                target["on" + type] = func;
            }
        },
        /*
         * removeEventHandler:移除Dom元素的事件
         *
         *  target：监听对象
         *  type：监听函数类型，如click,mouseover
         *  func：监听函数
         */
        removeEventHandler: function (target, type, func) {
            if (target.removeEventListener) {
                //监听IE9，谷歌和火狐
                target.removeEventListener(type, func, false);
            } else if (target.detachEvent) {
                target.detachEvent("on" + type, func);
            } else {
                delete target["on" + type];
            }
        },
        /**
         * 查看source与target之间是否有连接线
         * @param source
         * @param target
         */
        hasLinkedLine: function (source, target) {
            var values = this.lineContainer.values();
            for (var i = 0; i < values.length; i++) {
                if (values[i].source.CID == source.CID && values[i].target.CID == target.CID) {
                    return true;
                }
            }
            return false;
        }


    };

    /**
     * 模式处理器基础框架
     * @param mode
     * @constructor
     */
    workflow.core.ModeHandler = function (mode) {
        this.mode = mode;
    }
    $.extend(workflow.core.ModeHandler.prototype, {
        //获取模式类型
        _getModeType: function () {
            return this.mode;
        },
        //绑定handler
        bindHander: function () {


        },
        //解绑handler
        unBindHandler: function () {

        }

    });


    /**
     * 拖动模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.DragModeHandler = function (mode) {
        workflow.core.DragModeHandler.base.constructor.call(this, mode);

    };
    workflow.core.DragModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },


        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var curPoint = workflow.getPointOnCanvas(event.clientX, event.clientY);
            me.selected = workflow.getTarget(curPoint);
            workflow.selected = me.selected;//选择的对象
            if (me.selected) {
                if (me.selected.__getType() == "workflow.line") {

                    $.postOffice.publish("workflow.line.selected");
                } else {
                    $.postOffice.publish("workflow.activity.selected");
                }
                me.draggable = !me.draggable;
                if (me.draggable) {
                    workflow.canvasEle.style.cursor = "move";
                } else {
                    workflow.canvasEle.style.cursor = "default";
                }
            } else {
                $.postOffice.publish("workflow.process.selected");
            }
            workflow.render();
        },
        mouseMove: function (event) {
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            if (me.draggable) {
                var curPoint = workflow.getPointOnCanvas(event.clientX, event.clientY);
                if (me.selected) {
                    me.selected.moveTo(curPoint);
                    workflow.render();
                }
            }
        },
        mouseUp: function (event) {
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            me.draggable = false;
            me.selected = null;
            workflow.canvasEle.style.cursor = "default";
        }
    });


    /**
     * 任务绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.TaskModeHandler = function (mode) {
        workflow.core.TaskModeHandler.base.constructor.call(this, mode);
        this.count = 1;
        this.defaultName = "任务";
    };
    workflow.core.TaskModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {
            return this.mode;
        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        /**
         * 鼠标按下事件
         * @param event
         */
        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Task(workflow.canvas, me.defaultName + me.count, p.x, p.y);//生成组件
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }


    });


    /**
     * 状态绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.StatusModeHandler = function (mode) {
        workflow.core.StatusModeHandler.base.constructor.call(this, mode);
        this.defaultName = "状态";
        this.count = 1;

    };
    workflow.core.StatusModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Status(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * 会签绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.MeetingModeHandler = function (mode) {
        workflow.core.MeetingModeHandler.base.constructor.call(this, mode);
        this.defaultName = "会签";
        this.count = 1;

    };
    workflow.core.MeetingModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Meeting(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * 决策绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.DecisionModeHandler = function (mode) {
        workflow.core.DecisionModeHandler.base.constructor.call(this, mode);
        this.defaultName = "决策";
        this.count = 1;

    };
    workflow.core.DecisionModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Decision(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();

            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * fork绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.ForkModeHandler = function (mode) {
        workflow.core.ForkModeHandler.base.constructor.call(this, mode);
        this.defaultName = "分支";
        this.count = 1;

    };
    workflow.core.ForkModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Fork(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * join绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.JoinModeHandler = function (mode) {
        workflow.core.JoinModeHandler.base.constructor.call(this, mode);
        this.defaultName = "合并";
        this.count = 1;

    };
    workflow.core.JoinModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }


            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Join(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * subProcessMode绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.SubProcessModeHandler = function (mode) {
        workflow.core.SubProcessModeHandler.base.constructor.call(this, mode);
        this.defaultName = "子流程";
        this.count = 1;

    };
    workflow.core.SubProcessModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }


            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.SubProcess(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * dockProcessMode绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.DockProcessModeHandler = function (mode) {
        workflow.core.DockProcessModeHandler.base.constructor.call(this, mode);
        this.defaultName = "挂靠子流程";
        this.count = 1;

    };
    workflow.core.DockProcessModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }


            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.DockProcess(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * AssociationProcessMode绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.AssociationProcessModeHandler = function (mode) {
        workflow.core.AssociationProcessModeHandler.base.constructor.call(this, mode);
        this.defaultName = "关联子流程";
        this.count = 1;

    };
    workflow.core.AssociationProcessModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }


            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.AssociationProcess(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });

    /**
     * JavaProcessModeHandler绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.JavaProcessModeHandler = function (mode) {
        workflow.core.AssociationProcessModeHandler.base.constructor.call(this, mode);
        this.defaultName = "JAVA节点";
        this.count = 1;

    };
    workflow.core.JavaProcessModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }


            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            var meObj = new workflow.shape.Java(workflow.canvas, me.defaultName + me.count, p.x, p.y);
            workflow.add(meObj);
            me.count++;
            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });

    /**
     * 开始绘制模式处理器
     * @param mode
     * @constructor
     */
    workflow.core.StartModeHandler = function (mode) {
        workflow.core.StartModeHandler.base.constructor.call(this, mode);
        this.defaultName = "start";
    };
    workflow.core.StartModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var meObj = new workflow.shape.Start(workflow.canvas, "start", p.x, p.y);

            var conTable = workflow.container.values();
            for (var i = 0; i < conTable.length; i++) {
                if (conTable[i].__getType() == 'workflow.node.start') {
                	alert("开始节点已经存在");
                    console.log("开始节点已经存在");
                    delete meObj;
                    return;
                } else {
                    workflow.startNode = meObj;
                }

            }
            workflow.add(meObj);


            workflow.render();
            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });


    /**
     * 结束结点绘制模式
     * @param mode
     * @constructor
     */
    workflow.core.EndModeHandler = function (mode) {
        workflow.core.EndModeHandler.base.constructor.call(this, mode);
        this.defaultName = "end";
    };
    workflow.core.EndModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }
            var p = workflow.getPointOnCanvas(event.clientX, event.clientY);
            var meObj = new workflow.shape.End(workflow.canvas, "end", p.x, p.y);
            var conTable = workflow.container.values();
            for (var i = 0; i < conTable.length; i++) {

                if (conTable[i].__getType() == 'workflow.node.end') {
                    console.log("结束节点已经存在");
                    alert("结束节点已经存在");
                    delete meObj;
                    return;
                } else {
                    workflow.endNode = meObj;
                }

            }
            workflow.add(meObj);
            workflow.render();

            workflow.setOperatorMode(1);
        },
        mouseMove: function (event) {

        },
        mouseUp: function (event) {

        }
    });

    /**
     * 连接线模式
     * @param mode
     * @constructor
     */
    workflow.core.LinkedLineModeHandler = function (mode) {
        workflow.core.LinkedLineModeHandler.base.constructor.call(this, mode);
        this.drawableLine = false;
        this.defaultName = "转移";
        this.count = 1;

    };
    workflow.core.LinkedLineModeHandler.extend(workflow.core.ModeHandler, {
        //获取模式类型
        _getModeType: function () {

        },
        //绑定handler
        bindHander: function () {
            workflow.addEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.addEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.addEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },
        //解绑handler
        unBindHandler: function () {
            workflow.removeEventHandler(workflow.canvasEle, "mousedown", this.mouseDown);
            workflow.removeEventHandler(workflow.canvasEle, "mousemove", this.mouseMove);
            workflow.removeEventHandler(workflow.canvasEle, "mouseup", this.mouseUp);
        },

        mouseDown: function (event) {
            if (event.button != 0) {
                return;
            }

            var me = workflow.operatorHandler[workflow.getOperatorMode()];
            if (!me.drawableLine) {
                var curPoint = workflow.getPointOnCanvas(event.clientX, event.clientY);
                //判断当前按下鼠标地方是那个对象
                me.source = workflow.getTarget(curPoint);
                //绘制链接线
                if (me.source && me.source.__getType() != "workflow.line") {
                    me.drawableLine = true;
                }
            } else {
                var curPoint = workflow.getPointOnCanvas(event.clientX, event.clientY);
                me.target = workflow.getTarget(curPoint);
                if (me.target && me.target.__getType() != "workflow.line" && me.source && me.target.CID != me.source.CID) {//绘制线
                    //查看源和目标之间是否有连接线，如果没有创建之，有则不构建
                    if (!workflow.hasLinkedLine(me.source, me.target)) {
                        var meObj = new workflow.shape.Line(workflow.canvas, me.defaultName + me.count, me.source, me.target);
                        workflow.add(meObj);
                        me.count++;
                    }
                    me.target = null;
                    me.source = null;
                    me.drawableLine = false;
                    workflow.render();
                }
            }
        },
        mouseMove: function (event) {


        },
        mouseUp: function (event) {


        }
    });


    /** 操作模式绑定
     *      1==》dragMode
     *      2==》taskMode
     *      3==》statusMode
     *      4==》meetingMode
     *      5==》decisionMode
     *      6==》forkMode,
     *      7==》joinMode
     *      8==》subProcessMode
     *      9==》startMode
     *      10==》endMode
     *      11==》linkedLineMode
     *      12==》DockProcessMode
     *      13==》AssociationProcessMode
     */
    workflow.operatorHandler[1] = new workflow.core.DragModeHandler(1);
    workflow.operatorHandler[2] = new workflow.core.TaskModeHandler(2);
    workflow.operatorHandler[3] = new workflow.core.StatusModeHandler(3);
    workflow.operatorHandler[4] = new workflow.core.MeetingModeHandler(4);
    workflow.operatorHandler[5] = new workflow.core.DecisionModeHandler(5);
    workflow.operatorHandler[6] = new workflow.core.ForkModeHandler(6);
    workflow.operatorHandler[7] = new workflow.core.JoinModeHandler(7);
    workflow.operatorHandler[8] = new workflow.core.SubProcessModeHandler(8);
    workflow.operatorHandler[9] = new workflow.core.StartModeHandler(9);
    workflow.operatorHandler[10] = new workflow.core.EndModeHandler(10);
    workflow.operatorHandler[11] = new workflow.core.LinkedLineModeHandler(11);
    workflow.operatorHandler[12] = new workflow.core.DockProcessModeHandler(12);
    workflow.operatorHandler[13] = new workflow.core.AssociationProcessModeHandler(13);

    workflow.operatorHandler[15] = new workflow.core.JavaProcessModeHandler(15);
    //组件基类,该基类
    //1,完成定义参数处理方法和参数属性初始化的工作
    //2,完成定义事件处理方法和事件属性初始化的工作
    workflow.core.Component = function (options) {
        //组件配置参数
        this.options = options || {};
        if (options && options.newCID) {
            this.CID = options.CID;
        } else {
            this.CID = newGuid();
        }
        this._customEventListeners = {};
    };


    //定义组件的方法
    $.extend(workflow.core.Component.prototype, {
        __getCID: function () {
            return this.CID;
        },
        __getType: function () {
            return 'workflow.core.Component';
        },
        __getOID: function () {
            return this.OID;
        },
        __getText: function () {
            return this.text;
        },
        /*
         *   自定义事件：添加事件
         */
        addEvent: function (type, func) {
            if (!this._customEventListeners) //如果事件类型列表为空，建立事件类型列表，数据方式 为：类型名：函数名
                this._customEventListeners = {};
            var funcs = this._customEventListeners;
            //将事件类型与处理函数相关联，注意，这里每种类型可存入多个事件，是一个二维数组
            funcs[type] ? funcs[type].push(func) : funcs[type] = [func];
        },


        /*
         * 自定义事件删除指定类型事件
         */
        delEvent: function (type, func) {
            var funcs = this._customEventListeners[type];
            if (funcs) {
                for (var i = funcs.length - 1; i >= 0; i--) {
                    if (funcs[i] == func) {
                        funcs[i] = null;
                        break;
                    }
                }
            }
        },


        /*
         * 自定义事件：引爆指定类型的事件，第一个参数为事件类型，后面的参数为事件参数
         */
        fireEvent: function (type) {
            if (!this._customEventListeners[type]) //不支持的话，直接返回
                return;
            var funcs = this._customEventListeners[type], s = this, ars = $A(arguments);
            ars.shift();
            for (var i = funcs.length - 1; i >= 0; i--) {
                if (funcs[i])
                    funcs[i].apply(s, ars);
            }
        },

        /*
         * 组件的销毁方法
         */
        destroy: function () {
            workflow.remove(this);
        }
    });


    /**
     * 流程组件的图形化基础类组件
     * @param type
     * @param options
     * @constructor
     */
    workflow.core.Shape = function (canvas, text,options) {
        workflow.core.Shape.base.constructor.call(this, options);//调用基类构造函数
        this.canvas = canvas;//画布
        this.text = text;//文本
        this.labelName = text;//label
        this.options = {};
        this._init();//初始化方法
    };

    workflow.core.Shape.extend(workflow.core.Component, {
        //初始化事件
        _initEvents: function () {

        },
        __getText: function () {
            return this.text;
        },

        __getLabelName: function () {
            return this.labelName;
        },
        __getType: function () {
            //子类实现之
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //渲染方法
        _render: function () {

        },

        moveTo: function (point) {

        },
        /**
         * 判断当鼠标点击点是否在当前图形上
         * @param point
         * @private
         */
        _containPoint: function (point) {

        },
        /**
         * 获取连接线点起点，为图形中形点
         */
        getLinkedLinePointFrom: function () {

        },
        /**
         * 获取连接点入口点，一个图形在正交方向默认有四个正交点
         */
        getLinkedLinePointTo: function () {

        },
        /**
         *
         */
        toJSON: function () {

        },
        /**
         * fromJSON
         */
        fromJSON: function (json) {

        },


    });
    /**
     * save
     */

    //连接线工具
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Line = function (canvas, text, source, target, options, drawType) {
        workflow.shape.Line.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.source = source;
        this.target = target;
        this.drawType = drawType;

    };

    workflow.shape.Line.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.line";
        },
        //初始化方法，子类实现之
        _init: function () {


        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();

        },

        getLinkedLinePointTo: function () {
            return [this.cp];
        },

        getLinkedLinePointFrom: function () {
            return this.cp;
        },

        /**
         * 线的移动，以中间的控制点为中心进行移动
         * @param point
         */
        moveTo: function (point) {
            var offsetX = Math.abs(point.x - this.cp.x);
            var offserY = Math.abs(point.y - this.cp.y);
            if (offsetX <= 30 && offserY <= 30) {
                this.cp = point;
            }

        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            if (this.sp && this.cp && this.tp) {
                var d_sp_cp = getPointDistance(this.sp, this.cp);
                var d_tp_cp = getPointDistance(this.cp, this.tp);
                var p_sp_cp = getPointDistance(point, this.sp) + getPointDistance(point, this.cp);
                var p_tp_cp = getPointDistance(point, this.tp) + getPointDistance(point, this.cp);
                if (p_sp_cp - d_sp_cp <= 0.5) {
                    return true;
                }
                if (p_tp_cp - d_tp_cp <= 0.5) {
                    return true;
                }
            }
            return false;
        },


        /**
         * 画线
         * @private
         */
        __renderBox: function () {
            if (this.drawType == 1) {
                this.canvas.save();
                this.canvas.strokeStyle = "gray";
                this.canvas.fillStyle = "gray";
                this.canvas.lineWidth = 1;
                this.sp = this.source.getLinkedLinePointFrom();//源端点
                this.tp = getNearestPoint(this.sp, this.target.getLinkedLinePointTo());//目标点，动态点，从控制点中动态获取
                if (!this.cp)
                    this.cp = {x: (this.sp.x + this.tp.x) / 2, y: (this.sp.y + this.tp.y) / 2};//控制点，可以控制二次贝塞尔曲线
                //绘制2次贝塞尔曲线
                this.canvas.beginPath();
                this.canvas.moveTo(this.sp.x, this.sp.y);
                this.canvas.lineTo(this.cp.x, this.cp.y);
                this.tp = getNearestPoint(this.cp, this.target.getLinkedLinePointTo());
                this.canvas.quadraticCurveTo(this.cp.x, this.cp.y, this.tp.x, this.tp.y);
                //this.canvas.strokeStyle = "red";
                this.canvas.stroke();
                // draw the ending arrowhead
                var endRadians = Math.atan((this.tp.y - this.cp.y) / (this.tp.x - this.cp.x));
                endRadians += ((this.tp.x > this.cp.x) ? 90 : -90) * Math.PI / 180;
                this.__drawArrowhead(this.tp.x, this.tp.y, endRadians);
            } else {
                this.canvas.save();
                this.canvas.strokeStyle = "gray";
                this.canvas.fillStyle = "gray";
                this.canvas.lineWidth = 1;
                this.sp = this.source.getLinkedLinePointFrom();//源端点
                this.tp = getNearestPoint(this.sp, this.target.getLinkedLinePointTo());//目标点，动态点，从控制点中动态获取

                if (!this.cp)
                    this.cp = {x: (this.sp.x + this.tp.x) / 2, y: (this.sp.y + this.tp.y) / 2};//控制点，可以控制二次贝塞尔曲线
                //绘制2次贝塞尔曲线
                this.canvas.beginPath();
                this.canvas.moveTo(this.sp.x, this.sp.y);
                this.canvas.quadraticCurveTo(this.cp.x, this.cp.y, this.tp.x, this.tp.y);
                //this.canvas.strokeStyle = "red";
                this.canvas.stroke();
                // draw the ending arrowhead
                var endRadians = Math.atan((this.tp.y - this.cp.y) / (this.tp.x - this.cp.x));
                endRadians += ((this.tp.x > this.cp.x) ? 90 : -90) * Math.PI / 180;
                this.__drawArrowhead(this.tp.x, this.tp.y, endRadians);
            }
        },

        __drawArrowhead: function (x, y, radians) {
            this.canvas.beginPath();
            this.canvas.translate(x, y);
            this.canvas.rotate(radians);
            this.canvas.moveTo(0, 0);
            this.canvas.lineTo(6, 18);
            this.canvas.lineTo(-6, 18);
            this.canvas.closePath();
            this.canvas.fill();
            this.canvas.restore();
        },

        __renderText: function () {
            this.canvas.save();
            this.canvas.fillStyle = "black";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, (this.tp.x + this.cp.x) / 2, (this.tp.y + this.cp.y) / 2);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), (this.tp.x + this.cp.x) / 2, (this.tp.y + this.cp.y) / 2);
            }
            this.canvas.restore();
        },
        toJSON: function () {
            var lineResult = {};
            lineResult.cp = this.cp;//控制点
            lineResult.source = this.source.__getCID();//指向源端点的指针
            lineResult.target = this.target.__getCID();//指向目标端点指标
            lineResult.drawType = this.drawType;//绘画类型
            lineResult.cid = this.CID;
            lineResult.type = this.__getType();
            lineResult.text = this.text;
            lineResult.labelName=this.labelName;
            return lineResult;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.cp = json.cp;
            this.drawType = json.drawType;
            this.source = workflow.container.get(json.source);
            if (this.source == null) {
                alert("源活动为空，请检查");
            }
            this.target = workflow.container.get(json.target);
            if (this.target == null) {
                alert("目标活动为空，请检查");
            }
            return this;
        }
    });
//    决策
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Decision = function (canvas, text, x, y, options) {
        workflow.shape.Decision.base.constructor.call(this, canvas, text, options);//调用基类构造函数
         
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";
    };
    workflow.shape.Decision.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.decision";
        },
        //初始化方法，子类实现之
        _init: function () {

        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },


        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },


        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },
        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },
        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 4;

            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.rhombus(this.x, this.y, this.width, this.height);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();
        },

        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }
            this.canvas.restore();
        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStype = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;

        }
    });
//    开始节点
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Start = function (canvas, text, x, y, options) {
        workflow.shape.Start.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.radius = 15;
        this.fillStyle = "#5eb95e";
        this.strokeStyle = "#5eb95e";
    };

    workflow.shape.Start.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.start";
        },


        //初始化方法，子类实现之
        _init: function () {

        },

        moveTo: function (point) {
            this.x = point.x;
            this.y = point.y;
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var distance = getPointDistance(point, {x: this.x, y: this.y});
            return this.radius >= distance;
        },
        getLinkedLinePointFrom: function () {
            return {x: this.x, y: this.y}
        },

        getLinkedLinePointTo: function () {
            return [
                {x: this.x, y: this.y - this.radius},
                {x: this.x + this.radius, y: this.y},
                {x: this.x, y: this.y + this.radius},
                {x: this.x - this.radius, y: this.y}
            ];
        },

        __renderBox: function () {
            this.canvas.save();
            this.canvas.beginPath();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;

            this.canvas.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();
        },
        //将图形状态转为json对象
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, radius: this.radius};
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle};
            result.text = this.text;//{text:this.text,cid:this.CID,oid:this.OID}
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },

        //通过图形状态json对象还原图形（注意：type,,cid为对象构造时生成，这里不需要处理这两个对象）
        fromJSON: function (json) {
            this.x = json.point.x;
            this.y = json.point.y;
            this.radius = json.point.radius;
            this.fillStype = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            this.text = json.text;
            this.labelName = json.labelName;
            return this;
        }
    });
//结束节点
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.End = function (canvas, text, x, y, options) {
        workflow.shape.End.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 25;
        this.offset = 10;
        this.fillStyle = "red";
        this.strokeStyle = "#355878";
    };

    workflow.shape.End.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.end";
        },
        //初始化方法，子类实现之
        _init: function () {

        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },

        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },
        moveTo: function (point) {
            this.x = point.x;
            this.y = point.y;
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
        },

        __renderBox: function () {
            this.canvas.save();
            this.canvas.beginPath();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -1;
            this.canvas.shadowOffsetY = -1;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 4;
            this.canvas.roundRect(this.x, this.y, this.width, this.height, this.offset);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();
        },

        //保存图形的状态
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height};
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle};
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },

        //通过图形的状态还原图形（注意：CID和类型不需要还原）
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });
//分支
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Fork = function (canvas, text, x, y, options) {
        workflow.shape.Fork.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.radius = 20;
        this.fillStyle = "#7CC3B1";
        this.strokeStyle = "#7CC3B1";
    };

    workflow.shape.Fork.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.fork";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var distance = getPointDistance(point, {x: this.x, y: this.y});
            return this.radius >= distance;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x, y: this.y}
        },

        getLinkedLinePointTo: function () {
            return [
                {x: this.x, y: this.y - this.radius},
                {x: this.x + this.radius, y: this.y},
                {x: this.x, y: this.y + this.radius},
                {x: this.x - this.radius, y: this.y}
            ];
        },

        moveTo: function (point) {
            this.x = point.x;
            this.y = point.y;
        },


        __renderBox: function () {
            this.canvas.save();
            this.canvas.beginPath();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();
        },
        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x - 15, this.y + 5);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x - 15, this.y + 5);
            }
            this.canvas.restore();
        },

        //保存图形对象状态
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, radius: this.radius}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },

        //从图形对象状态还原图形（注意：CID,TYPE不需要还原）
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.radius = json.point.radius;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });
//合并
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Join = function (canvas, text, x, y, options) {
        workflow.shape.Join.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.radius = 20;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";
    };

    workflow.shape.Join.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.join";
        },
        //初始化方法，子类实现之
        _init: function () {

        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var distance = getPointDistance(point, {x: this.x, y: this.y});
            return this.radius >= distance;
        },

        moveTo: function (point) {
            this.x = point.x;
            this.y = point.y;
        },
        getLinkedLinePointFrom: function () {
            return {x: this.x, y: this.y}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x, y: this.y - this.radius},
                {x: this.x + this.radius, y: this.y},
                {x: this.x, y: this.y + this.radius},
                {x: this.x - this.radius, y: this.y}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },
        __renderBox: function () {
            this.canvas.save();
            this.canvas.beginPath();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.arc(this.x, this.y, this.radius, 0, 2 * Math.PI);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();
        },
        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x - 15, this.y + 5);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x - 15, this.y + 5);
            }
            this.canvas.restore();
        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, radius: this.radius}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },

        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.radius = json.point.radius;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }

    });
//    会签
    /**
     * 任务节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Meeting = function (canvas, text, x, y, options) {
        workflow.shape.Meeting.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";
    };

    workflow.shape.Meeting.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.meeting";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },


        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },

        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },


        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.quadrangle(this.x, this.y, this.width, this.height, 15);
            this.canvas.fill();
            this.canvas.stroke();
            this.canvas.restore();

        },

        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }
            this.canvas.restore();
        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });
//状态节点
    /**
     * 状态节点构造函数
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Status = function (canvas, text, x, y, options) {
        workflow.shape.Status.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";

    };

    workflow.shape.Status.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.status";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },
        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },

        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },


        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.roundRect(this.x, this.y, this.width, this.height, 10);
            this.canvas.fill();
            this.canvas.stroke();

            this.canvas.restore();

        },
        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }

            this.canvas.restore();
        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });
//    子流程节点
    /**
     * 子流程节点
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.SubProcess = function (canvas, text, x, y, options) {
        workflow.shape.SubProcess.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#fa6f57";
        this.strokeStyle = "#fa6f57";
    };

    workflow.shape.SubProcess.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.subprocess";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();

            this.__renderLBox();
            this.__renderRBox();
        },
        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.fillRect(this.x, this.y, this.width, this.height);
            this.canvas.strokeRect(this.x, this.y, this.width, this.height);
            this.canvas.restore();

        },

        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }
            this.canvas.restore();
        },
        __renderLBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x - 15, this.y, 15, this.height);
            this.canvas.strokeRect(this.x - 15, this.y, 15, this.height);
            this.canvas.restore();

        },

        __renderRBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.strokeRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.restore();

        },

        //图形状态保存
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },

        //根据状态恢复图形
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }

    });

//  挂靠流程节点
    /**
     * 挂靠流程节点
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.DockProcess = function (canvas, text, x, y, options) {
        workflow.shape.DockProcess.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#ff00ff";
        this.strokeStyle = "#ff00ff";
    };

    workflow.shape.DockProcess.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.dockprocess";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();

            this.__renderLBox();
            this.__renderRBox();
        },
        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.fillRect(this.x, this.y, this.width, this.height);
            this.canvas.strokeRect(this.x, this.y, this.width, this.height);
            this.canvas.restore();

        },

        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }
            this.canvas.restore();
        },
        __renderLBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x - 15, this.y, 15, this.height);
            this.canvas.strokeRect(this.x - 15, this.y, 15, this.height);
            this.canvas.restore();

        },

        __renderRBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.strokeRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.restore();

        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }

    });

//  关联流程节点
    /**
     * 关联流程节点
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.AssociationProcess = function (canvas, text, x, y, options) {
        workflow.shape.AssociationProcess.base.constructor.call(this, canvas, text, options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#ffa500";
        this.strokeStyle = "#ffa500";
    };

    workflow.shape.AssociationProcess.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.associationprocess";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },

        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();

            this.__renderLBox();
            this.__renderRBox();
        },
        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }
            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 40;
            this.canvas.fillRect(this.x, this.y, this.width, this.height);
            this.canvas.strokeRect(this.x, this.y, this.width, this.height);
            this.canvas.restore();

        },

        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 25);
            }
            this.canvas.restore();
        },
        __renderLBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x - 15, this.y, 15, this.height);
            this.canvas.strokeRect(this.x - 15, this.y, 15, this.height);
            this.canvas.restore();

        },

        __renderRBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            this.canvas.fillRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.strokeRect(this.x + this.width, this.y, 15, this.height);
            this.canvas.restore();

        },
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;
        },
        fromJSON: function (json) {
            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStyle = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }

    });

//任务节点
    /**
     * 任务节点
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Task = function (canvas, text, x, y, options) {
        workflow.shape.Task.base.constructor.call(this, canvas, text,options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";
    };

    workflow.shape.Task.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.task";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },
        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }

            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 30;
            this.canvas.fillRect(this.x, this.y, this.width, this.height);
            this.canvas.strokeRect(this.x, this.y, this.width, this.height);
            this.canvas.restore();

        },
        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 20);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 20);
            }
            this.canvas.restore();
        },

        //将图形的状态转转为json对象
        toJSON: function () {
        	var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;

        },
        //通过json对象还原图形对象自身
        fromJSON: function (json) {

            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStype = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });

    //JAVA节点
    /**
     * JAVA节点
     * @param canvas
     * @param text
     * @param x
     * @param y
     * @param width
     * @param hieght
     * @param options
     * @constructor
     */
    workflow.shape.Java = function (canvas, text, x, y, options) {
        workflow.shape.Java.base.constructor.call(this, canvas, text,options);//调用基类构造函数
        this._init();//初始化方法
        this.x = x;
        this.y = y;
        this.fillStyle = "#355878";
        this.strokeStyle = "#355878";
    };

    workflow.shape.Java.extend(workflow.core.Shape, {
        __getType: function () {
            return "workflow.node.java";
        },
        //初始化方法，子类实现之
        _init: function () {

        },
        moveTo: function (point) {
            //设为中间点移动到point点
            this.x = point.x - this.width / 2;
            this.y = point.y - this.height / 2;
        },
        getLinkedLinePointFrom: function () {
            return {x: this.x + this.width / 2, y: this.y + this.height / 2}
        },
        getLinkedLinePointTo: function () {
            return [
                {x: this.x + this.width / 2, y: this.y},
                {x: this.x + this.width, y: this.y + this.height / 2},
                {x: this.x + this.width / 2, y: this.y + this.height},
                {x: this.x, y: this.y + this.height / 2}
            ];
        },

        //渲染方法
        _render: function () {
            this.__renderBox();
            this.__renderText();
        },

        //判断屏幕输入点是否在当前点范围内
        _containPoint: function (point) {
            var min_x = this.x;
            var max_x = this.x + this.width;
            var min_y = this.y;
            var max_y = this.y + this.height;
            return point.x <= max_x && point.x >= min_x && point.y >= min_y && point.y <= max_y;
        },

        __renderBox: function () {
            this.canvas.save();
            this.canvas.fillStyle = this.fillStyle;
            this.canvas.strokeStyle = this.strokeStyle;
            this.canvas.lineWidth = 1;
            this.canvas.shadowOffsetX = -2;
            this.canvas.shadowOffsetY = -2;
            this.canvas.shadowColor = "gray";
            this.canvas.shadowBlur = 8;
            if(this.__getLabelName()==undefined)
            {
                var metrics = this.canvas.measureText(this.text);
            }else
            {
                var metrics = this.canvas.measureText(this.__getLabelName());
            }

            this.fontWidth = metrics.width;
            if (metrics.width < 80) {
                this.width = 80;
            } else {
                this.width = metrics.width + 40;
            }
            this.height = 30;
            this.canvas.fillRect(this.x, this.y, this.width, this.height);
            this.canvas.strokeRect(this.x, this.y, this.width, this.height);
            this.canvas.restore();

        },
        __renderText: function () {
            this.canvas.save();
            //this.canvas.font="bold 10px sans-serif";
            this.canvas.fillStyle = "white";
            if(this.__getLabelName()==undefined)
            {
                this.canvas.fillText(this.text, this.x + (this.width - this.fontWidth) / 2, this.y + 20);
            }else
            {
                this.canvas.fillText(this.__getLabelName(), this.x + (this.width - this.fontWidth) / 2, this.y + 20);
            }
            this.canvas.restore();
        },

        //将图形的状态转转为json对象
        toJSON: function () {
            var result = {};
            result.point = {x: this.x, y: this.y, w: this.width, h: this.height}
            result.style = {fillStyle: this.fillStyle, strokeStyle: this.strokeStyle}
            result.text = this.text;
            result.labelName=this.labelName;
            result.cid = this.CID;
            result.type = this.__getType();
            return result;

        },
        //通过json对象还原图形对象自身
        fromJSON: function (json) {

            this.text = json.text;
            this.labelName = json.labelName;
            this.x = json.point.x;
            this.y = json.point.y;
            this.width = json.point.w;
            this.height = json.point.h;
            this.fillStype = json.style.fillStyle;
            this.strokeStyle = json.style.strokeStyle;
            return this;
        }
    });
})(jQuery);
	
	


