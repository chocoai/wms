package com.xyy.erp.platform.config.launch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.xyy.bill.controller.CommonController;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.engine.BillRoutes;
import com.xyy.bill.migration.BillMigratePlugin;
import com.xyy.bill.plugin.PullWMSDataPlugin;
import com.xyy.bill.plugin.SysSelectCachePlug;
import com.xyy.bill.print.job.PrintController;
import com.xyy.bill.print.meta.Print;
import com.xyy.bill.services.util.OMSTaskManagerPlugin;
import com.xyy.edge.engine.BillEdgeRoutes;
import com.xyy.edge.engine.service.BillDesignController;
import com.xyy.erp.platform.app.controller.AppController;
import com.xyy.erp.platform.app.model.AppMenu;
import com.xyy.erp.platform.common.plugin.I18NPlugin;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.plugin.SynDataPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.FileUtil;
import com.xyy.erp.platform.interceptor.AuthenticationInterceptor;
import com.xyy.erp.platform.interceptor.ExceptionInterceptor;
import com.xyy.erp.platform.system.controller.AuthImgController;
import com.xyy.erp.platform.system.controller.CaiwuController;
import com.xyy.erp.platform.system.controller.DeptController;
import com.xyy.erp.platform.system.controller.ImageController;
import com.xyy.erp.platform.system.controller.IndexController;
import com.xyy.erp.platform.system.controller.LoginController;
import com.xyy.erp.platform.system.controller.MenuController;
import com.xyy.erp.platform.system.controller.ModuleController;
import com.xyy.erp.platform.system.controller.OperationController;
import com.xyy.erp.platform.system.controller.OperationDeskController;
import com.xyy.erp.platform.system.controller.OrganizationController;
import com.xyy.erp.platform.system.controller.PermissionController;
import com.xyy.erp.platform.system.controller.RoleController;
import com.xyy.erp.platform.system.controller.SelectController;
import com.xyy.erp.platform.system.controller.ServiceController;
import com.xyy.erp.platform.system.controller.SpecialController;
import com.xyy.erp.platform.system.controller.StationController;
import com.xyy.erp.platform.system.controller.SystemsController;
import com.xyy.erp.platform.system.controller.UserController;
import com.xyy.erp.platform.system.model.Attach;
import com.xyy.erp.platform.system.model.Forceendform;
import com.xyy.erp.platform.system.model.Suspendform;
import com.xyy.erp.platform.system.model.WorkflowRecord;
import com.xyy.erp.platform.system.task.AccountManagerPlugin;
import com.xyy.erp.platform.system.task.EDBInterceptor;
import com.xyy.http.services.BillAndDicPrintTask;
import com.xyy.wms.basicData.controller.*;
import com.xyy.wms.outbound.controller.PickingController;
import com.xyy.wms.outbound.controller.WaveExecuteController;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.ActivityForm;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.DynamicUser;
import com.xyy.workflow.definition.EventDefinition;
import com.xyy.workflow.definition.ProcessController;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessException;
import com.xyy.workflow.definition.ProcessForm;
import com.xyy.workflow.definition.ProcessHistory;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.ProcessTypeDefinition;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.TaskInstance;
import com.xyy.workflow.definition.Token;
import com.xyy.workflow.definition.Transition;
import com.xyy.workflow.definition.VariantInstance;
import com.xyy.workflow.jfinal.ActiveRecordEntitySessionInterceptor;
import com.xyy.workflow.jfinal.WorkflowPlugin;

/**
 * API引导式配置
 */
public class JfinalConfig extends JFinalConfig {

	@Override
	public void configEngine(Engine me) {

	}

	private static final Log LOG = Log.getLog(JfinalConfig.class);

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		LOG.info("configConstant 缓存 properties");
		new PropertiesPlugin(loadPropertyFile("init.properties")).start();

		LOG.info("configConstant 设置字符集");
		me.setEncoding(getProperty(DictKeys.config_encoding));

		LOG.info("configConstant 设置是否开发模式");
		me.setDevMode(getPropertyToBoolean(DictKeys.config_devMode, false));

		LOG.info("configConstant 设置上传文件路径");
		me.setBaseUploadPath(PathKit.getWebRootPath());

		// LOG.info("configConstant 设置是否开发模式");
		// me.setBaseViewPath("/WEB-INF/views");

	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/service", com.xyy.http.service.ServiceController.class);
		me.add("/print", PrintController.class);
		me.add("/", IndexController.class);
		me.add("/login", LoginController.class);
		me.add("/authimg", AuthImgController.class);
		me.add("/index", IndexController.class);
		me.add("/user", UserController.class);
		me.add("/role", RoleController.class);
		me.add("/department", DeptController.class);
		me.add("/module", ModuleController.class);
		me.add("/operation", OperationController.class);
		me.add("/permission", PermissionController.class);
		me.add("/station", StationController.class);
		me.add("/systems", SystemsController.class);
		me.add("/menu", MenuController.class);
		me.add("/org", OrganizationController.class);
		me.add("/select", SelectController.class);
		me.add("/api", ServiceController.class); // 第三个参数为该Controller的视图存放路径
		me.add("/process", OperationDeskController.class);
		me.add("/billdesign", BillDesignController.class);
		me.add("/common", CommonController.class);
		me.add("/image", ImageController.class);
		me.add("/app", AppController.class);
		me.add("/caiwu", CaiwuController.class);
		me.add("/spe", SpecialController.class);
		me.add("/wmsBasic", BasicController.class);
		me.add("/waveExecute",WaveExecuteController.class);
		me.add("/wmsCangku",CangkuSpecialController.class);
		me.add("/wmsRkSj",RFRuKuShangJiaSpecialController.class);
		me.add("/wmsShangPin",ShangpingSpecialController.class);
		me.add("/wmsKuqu",KuQuSpecialController.class);
		me.add("/wmsXianlu",XianLuSpecialController.class);
		me.add("/wmsNeiFuHe",NeiFuSpecialController.class);
		me.add("/wmsWaiFuHe",RFWaiFuSpecialController.class);
		me.add("/wmsPicking",PickingController.class);
		// 单据路由
		me.add(new BillRoutes());

		// 单据规则路由
		me.add(new BillEdgeRoutes());
		
	}

	public static IDataSourceProvider idp;

	public static C3p0Plugin createC3p0Plugin() {
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {

		LOG.info("configPlugin 配置Druid数据库连接池连接属性");
		DruidPlugin druidPlugin = new DruidPlugin(
				(String) PropertiesPlugin.getParamMapValue(DictKeys.db_connection_jdbcUrl),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.db_connection_userName),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.db_connection_passWord),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.db_connection_driverClass));

		LOG.info("configPlugin 配置Druid数据库连接池大小");
		druidPlugin.set((Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_initialSize),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_minIdle),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_maxActive));
		LOG.info("configPlugin 配置ActiveRecord插件");
		idp = druidPlugin;
		ActiveRecordPlugin arpMain = new ActiveRecordPlugin(DictKeys.db_dataSource_main, druidPlugin);
		// arp.setTransactionLevel(4);//事务隔离级别
		arpMain.setDevMode(getPropertyToBoolean(DictKeys.config_devMode, false)); // 设置开发模式
		arpMain.setShowSql(getPropertyToBoolean(DictKeys.config_devMode, true)); // 是否显示SQL

		/********************
		 * 添加ORM映射
		 ********************/
		arpMain.addMapping("tb_sys_user", "id", com.xyy.erp.platform.system.model.User.class);
		arpMain.addMapping("tb_sys_role_user_relation", "id", com.xyy.erp.platform.system.model.RoleUserRelation.class);
		arpMain.addMapping("tb_sys_role", "id", com.xyy.erp.platform.system.model.Role.class);
		arpMain.addMapping("tb_sys_permission", "id", com.xyy.erp.platform.system.model.Permission.class);
		arpMain.addMapping("tb_sys_perm_menu_relation", "id", com.xyy.erp.platform.system.model.PermMenuRelation.class);
		arpMain.addMapping("tb_sys_module", "id", com.xyy.erp.platform.system.model.Module.class);
		arpMain.addMapping("tb_sys_dept", "id", com.xyy.erp.platform.system.model.Dept.class);
		arpMain.addMapping("tb_sys_dept_user_relation", "id", com.xyy.erp.platform.system.model.DeptUserRelation.class);
		arpMain.addMapping("tb_sys_role_perm_relation", "id", com.xyy.erp.platform.system.model.RolePermRelation.class);
		arpMain.addMapping("tb_sys_operation", "id", com.xyy.erp.platform.system.model.Operation.class);
		arpMain.addMapping("tb_sys_organization", "id", com.xyy.erp.platform.system.model.Organization.class);
		arpMain.addMapping("tb_sys_org_user_relation", "id", com.xyy.erp.platform.system.model.OrgUserRelation.class);
		arpMain.addMapping("tb_sys_data_user_relation", "id", com.xyy.erp.platform.system.model.DataUserRelation.class);

		arpMain.addMapping("tb_sys_station", "id", com.xyy.erp.platform.system.model.Station.class);
		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));

		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));
		// =======
		arpMain.addMapping("tb_sys_station_user_relation", "id",
				com.xyy.erp.platform.system.model.StationUserRelation.class);
		arpMain.addMapping("tb_sys_menu", "id", com.xyy.erp.platform.system.model.Menu.class);
		arpMain.addMapping("tb_sys_systems", "id", com.xyy.erp.platform.system.model.Systems.class);
		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));

		/* app相关表 */
		arpMain.addMapping("tb_app_menu", "id", AppMenu.class);

		/* 工作流引擎相关表 */

		arpMain.addMapping("tb_pd_processtypedefinition", "id", ProcessTypeDefinition.class);
		arpMain.addMapping("tb_pd_processcontroller", "id", ProcessController.class);
		arpMain.addMapping("tb_pd_processdefinition", "id", ProcessDefinition.class);

		arpMain.addMapping("tb_pd_activitycontroller", "id", ActivityController.class);
		arpMain.addMapping("tb_pd_activitydefinition", "id", ActivityDefinition.class);
		arpMain.addMapping("tb_pd_transitions", "id", Transition.class);

		arpMain.addMapping("tb_pd_processform", "id", ProcessForm.class);
		arpMain.addMapping("tb_pd_activityform", "id", ActivityForm.class);

		arpMain.addMapping("tb_pd_eventdefinition", "id", EventDefinition.class);

		arpMain.addMapping("tb_pd_dynamicuser", "id", DynamicUser.class);
		arpMain.addMapping("tb_pd_processuser", "id", ProcessUser.class);

		arpMain.addMapping("tb_pd_processinstance", "id", ProcessInstance.class);
		arpMain.addMapping("tb_pd_activityinstance", "id", ActivityInstance.class);
		arpMain.addMapping("tb_pd_taskinstance", "id", TaskInstance.class);
		arpMain.addMapping("tb_pd_token", "id", Token.class);
		arpMain.addMapping("tb_pd_processhistory", "id", ProcessHistory.class);
		arpMain.addMapping("tb_pd_variantinstance", "id", VariantInstance.class);

		arpMain.addMapping("tb_pd_exception", "id", ProcessException.class);
		/* 工作流引擎相关表 end */
		// 流程附件
		arpMain.addMapping("tb_pd_attach", "id", Attach.class);
		// 流程记录
		arpMain.addMapping("tb_pd_workflow_record", "id", WorkflowRecord.class);
		// 否单表单
		arpMain.addMapping("tb_pd_forceendform", "id", Forceendform.class);
		// 挂起表单
		arpMain.addMapping("tb_pd_suspendform", "id", Suspendform.class);
		// 备注
		// arpMain.addMapping("tb_pd_orderform", "id", Orderform.class);

		LOG.info("configPlugin 数据库类型判断");
		String db_type = (String) PropertiesPlugin.getParamMapValue(DictKeys.db_type_key);
		if (db_type.equals(DictKeys.db_type_postgresql)) {
			LOG.info("configPlugin 使用数据库类型是 postgresql");
			arpMain.setDialect(new PostgreSqlDialect());
			// arpMain.setContainerFactory(new
			// CaseInsensitiveContainerFactory(true));// 配置属性名(字段名)大小写不敏感容器工厂

		} else if (db_type.equals(DictKeys.db_type_mysql)) {
			LOG.info("configPlugin 使用数据库类型是 mysql");
			arpMain.setDialect(new MysqlDialect());
			// arpMain.setContainerFactory(new
			// CaseInsensitiveContainerFactory(true));// 配置属性名(字段名)大小写不敏感容器工厂
		} else if (db_type.equals(DictKeys.db_type_sqlserver)) {
			LOG.info("configPlugin 使用数据库类型是 sqlserver");
			arpMain.setDialect(new SqlServerDialect());
		}

		LOG.info("configPlugin 添加druidPlugin插件");
		me.add(druidPlugin); // 多数据源继续添加

		LOG.info("configPlugin 表扫描注册");
		Map<String, ActiveRecordPlugin> arpMap = new HashMap<String, ActiveRecordPlugin>();
		arpMap.put(DictKeys.db_dataSource_main, arpMain); // 多数据源继续添加
		// 主数据源要先添加
		me.add(arpMain); // 多数据源继续添加

		// WMS与ERP中间库配置
		//this.initWMSMidDbConfig(me, arpMap);
		// 打印中间库配置
		//this.initPrintDbConfig(me, arpMap);
		
		this.initERPDbConfig(me, arpMap);

		LOG.info("I18NPlugin 国际化键值对加载");
		me.add(new I18NPlugin());
		LOG.info("EhCachePlugin EhCache缓存");
		me.add(new EhCachePlugin());
		LOG.info("BillPlugin 单据引擎插件加载");
		String realPath = (String) PropertiesPlugin.getParamMapValue(DictKeys.app_resource_home);
		me.add(new BillPlugin(realPath));
		LOG.info("workflow插件");
		me.add(new WorkflowPlugin());
//		LOG.info("数据迁移线程启动");
//		me.add(new BillMigratePlugin());
//		LOG.info("提中间库插件启动");
//		me.add(new PullWMSDataPlugin());
		LOG.info("tb_sys_select 数据缓存插件启动");
        me.add(new SysSelectCachePlug() );
//        LOG.info("中间库定时任务启动");
//        me.add(new OMSTaskManagerPlugin() );
		LOG.info("账页插件启动");
		me.add(new AccountManagerPlugin());
        LOG.info("ERP与WMS系统数据同步");
        me.add(new SynDataPlugin());
	}
	
	/**
	 * 初始化ERP数据源信息
	 * @param me
	 * @param arpMap
	 */
	private void initERPDbConfig(Plugins me, Map<String, ActiveRecordPlugin> arpMap) {
		DruidPlugin appDruidPlugin = new DruidPlugin(
				(String) PropertiesPlugin.getParamMapValue(DictKeys.mysql_erp_jdbcUrl),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.mysql_erp_userName),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.mysql_erp_passWord),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.db_connection_driverClass));

		appDruidPlugin.set((Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_initialSize),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_minIdle),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_maxActive));
		
		ActiveRecordPlugin appMid = new ActiveRecordPlugin(DictKeys.db_dataSource_erp_mid, appDruidPlugin);
		me.add(appDruidPlugin);
		arpMap.put(DictKeys.db_dataSource_erp_mid, appMid);
		appMid.setDialect(new MysqlDialect());
		appMid.setShowSql(false);
		me.add(appMid);
	}

	/**
	 * 初始化打印中间库配置
	 * 
	 * @param me
	 * @param arpMap
	 */
	private void initPrintDbConfig(Plugins me, Map<String, ActiveRecordPlugin> arpMap) {
		DruidPlugin appDruidPlugin = new DruidPlugin(
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver_db_connection_jdbcUrl),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver_db_connection_userName),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver_db_connection_passWord),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver_db_connection_driverClass));

		appDruidPlugin.set((Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_initialSize),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_minIdle),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_maxActive));

		ActiveRecordPlugin appMid = new ActiveRecordPlugin(DictKeys.db_dataSource_print_mid, appDruidPlugin);
		me.add(appDruidPlugin);
		arpMap.put(DictKeys.db_dataSource_print_mid, appMid); // 多数据源继续添加
		appMid.setDialect(new SqlServerDialect());
		appMid.setShowSql(true);
		me.add(appMid);
	}
	

	
	/**
	 * 初始化WMS与ERP中间库配置
	 * 
	 * @param me
	 * @param arpMap
	 */
	private void initWMSMidDbConfig(Plugins me, Map<String, ActiveRecordPlugin> arpMap) {
		DruidPlugin appDruidPlugin = new DruidPlugin(
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver2_db_connection_jdbcUrl),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver2_db_connection_userName),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver2_db_connection_passWord),
				(String) PropertiesPlugin.getParamMapValue(DictKeys.sqlserver_db_connection_driverClass));

		appDruidPlugin.set((Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_initialSize),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_minIdle),
				(Integer) PropertiesPlugin.getParamMapValue(DictKeys.db_maxActive));

		ActiveRecordPlugin appMid = new ActiveRecordPlugin(DictKeys.db_dataSource_wms_mid, appDruidPlugin);
		me.add(appDruidPlugin);
		arpMap.put(DictKeys.db_dataSource_wms_mid, appMid); // 多数据源继续添加
		appMid.setDialect(new SqlServerDialect());
		appMid.setShowSql(true);
		me.add(appMid);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new ExceptionInterceptor());
		me.add(new AuthenticationInterceptor());
		me.add(new ActiveRecordEntitySessionInterceptor());
		me.add(new EDBInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {

		// this.testPrint();

		// LOG.info("configHandler 全局配置处理器，主要是记录日志和request域值处理");
		// me.add(new GlobalHandler());
		// if("true".equals((String)
		// PropertiesPlugin.getParamMapValue("performanceFinder"))){
		// me.add(new PerformanceHandler());
		// }

		// LOG.info("configHandler 全局配置处理器，主要是记录日志和request域值处理");
		// me.add(new GlobalHandler());
		// me.add(new ContextPathHandler("ctx"));

	}

	private void testPrint() {
		Print print = BillPlugin.engine.getPrintTplDef("xiaoshoudingdan").getPrint();
		JSONArray ids = new JSONArray();
		ids.add("eff416d81ced4b1b8c08b345b1e55ab5");
		ids.add("047ac96d49fc4d46991b75ed83a080c2");
		BillAndDicPrintTask task = new BillAndDicPrintTask(print, "ttt1", ids);
		task.buildPrintJobDocument();

	}

	private static final String BASE_DIC_DATA = "baseDicData";
	private static final String BASE_DIC_DATA_KEY = "baseDicDataKey";
	private static final String BASE_AREA_DATA = "baseAreaData";
	private static final String BASE_AREA_DATA_KEY = "baseAreaDataKey";

	public void afterJFinalStart() {
		initBaseDicData();
		initAreaData();
		initMaxFormContentSize();
		}

	/**
	 * 修改jetty form最大提交为10M
	 * */
		private void initMaxFormContentSize() {
			if (JFinal.me().getServletContext().getClass().getName().equals("org.eclipse.jetty.webapp.WebAppContext$Context")) {
				try {
				ServletContext ctx = JFinal.me().getServletContext();
				Method getContextHandler = ctx.getClass().getMethod("getContextHandler", null);
				Object handler = getContextHandler.invoke(ctx, null);
				Method setMax = handler.getClass().getMethod("setMaxFormContentSize", int.class);
				setMax.invoke(handler, 1024 * 1024 * 10);
				} catch (Exception e) {
				e.printStackTrace();
				}
				}
			
		}

	/**
	 * 初始化区域数据
	 */
	private void initAreaData() {
		List<Record> list = Db.find("select * from xyy_erp_dic_area");
		CacheKit.put(BASE_AREA_DATA, BASE_AREA_DATA_KEY, list);
		this.refreshAddressFile();
	}

	private void refreshAddressFile() {
		List<Map<String, Object>> retList = new ArrayList<>();
		List<Record> areaList = CacheKit.get(BASE_AREA_DATA, BASE_AREA_DATA_KEY);
		for (Record record : areaList) {
			Map<String, Object> areaMap = new HashMap<>();
			areaMap.put("areaCode", record.getInt("areaCode"));
			areaMap.put("areaName", record.getStr("areaName"));
			retList.add(areaMap);
		}
		String addressJson = JSON.toJSONString(retList);
		String filePathAndName = PathKit.getWebRootPath() + "/lib/file/addressFormat.json";
		FileUtil.createFile(filePathAndName, addressJson, "UTF-8");
	}

	/**
	 * 初始化基础源数据
	 */
	private void initBaseDicData() {
		List<Record> list = Db.find("select * from tb_sys_select where state=1");
		CacheKit.put(BASE_DIC_DATA, BASE_DIC_DATA_KEY, list);
		this.refreshBaseDicFile();
	}

	private void refreshBaseDicFile() {
		Map<String, List<Map<String, Object>>> retMap = new HashMap<>();
		List<Record> dicList = CacheKit.get(BASE_DIC_DATA, BASE_DIC_DATA_KEY);
		for (Record record : dicList) {
			String type = record.getStr("type");
			if (type != null && !"".equals(type)) {
				if (!retMap.containsKey(type)) {
					retMap.put(type, new ArrayList<>());
				}
				Map<String, Object> areaMap = new HashMap<>();
				areaMap.put("key", record.getInt("key"));
				areaMap.put("value", record.getStr("value"));
				retMap.get(type).add(areaMap);
			}
		}
		String addressJson = JSON.toJSONString(retMap);
		String filePathAndName = PathKit.getWebRootPath() + "/lib/file/selectFormat.json";
		FileUtil.createFile(filePathAndName, addressJson, "UTF-8");
	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 89, "/");
	}
}
