package com.xyy.util;



import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xyy.erp.platform.system.model.Attach;
import com.xyy.erp.platform.system.model.Forceendform;
import com.xyy.erp.platform.system.model.Suspendform;
import com.xyy.erp.platform.system.model.WorkflowRecord;
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
import com.xyy.workflow.jfinal.WorkflowPlugin;
import com.xyy.workflow.service.imp.RepositoryServiceImp;

public class Test {
	private static WorkflowPlugin workflowPlugin;
	private static RepositoryServiceImp service=new RepositoryServiceImp();
	
	public static DruidPlugin druidPlugin;

	public static ActiveRecordPlugin arpMain;
	
	/*
	 * mysql.jdbcUrl = jdbc:mysql://rm-bp1505z7znbz68p07.mysql.rds.aliyuncs.com:3306/xyy_erp?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull
mysql.userName = xyy_erp
mysql.passWord = b9~oh-T23sUFp65J6lPgrLGIfW6z0cuR
	 * 
	 */
	public static final String jdbcUrl = "jdbc:mysql://rm-bp1505z7znbz68p07.mysql.rds.aliyuncs.com:3306/xyy_erp?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
	
	
	public static final String userName = "xyy_erp";
	public static final String passWord = "b9~oh-T23sUFp65J6lPgrLGIfW6z0cuR";
	public static final String driverClass = null;
	public static final int db_initialSize = 3;
	public static final int db_minIdle =1;
	public static final int db_maxActive =5;

	public static void main(String[] args) {
		initPlugin();
		ProcessDefinition pd=service.findProcessDefinitionByName("testorm");
		
		ProcessInstance pi=new ProcessInstance(pd);
	
		try {
			pi.startProcessInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	
	private static void initPlugin() {
		DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, userName, passWord, driverClass);

		druidPlugin.set(db_initialSize,db_minIdle,db_maxActive);

		ActiveRecordPlugin arpMain = new ActiveRecordPlugin(druidPlugin);
		// arpMain.setTransactionLevel(4);//事务隔离级别
		arpMain.setDevMode(true); // 设置开发模式
		arpMain.setShowSql(true); // 是否显示SQL
		arpMain.setDialect(new MysqlDialect());// 设置方言

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

		arpMain.addMapping("tb_sys_station", "id", com.xyy.erp.platform.system.model.Station.class);
		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));

		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));
		// =======
		arpMain.addMapping("tb_sys_station_user_relation", "id",
				com.xyy.erp.platform.system.model.StationUserRelation.class);
		arpMain.addMapping("tb_sys_menu", "id", com.xyy.erp.platform.system.model.Menu.class);
		arpMain.addMapping("tb_sys_systems", "id", com.xyy.erp.platform.system.model.Systems.class);
		// arpMain.setContainerFactory(new PropertyNameContainerFactory(false));

		/* 工作流引擎相关表*/
		
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
		/* 工作流引擎相关表 end*/
		//流程附件
		arpMain.addMapping("tb_pd_attach", "id", Attach.class);
		//流程记录
		arpMain.addMapping("tb_pd_workflow_record", "id", WorkflowRecord.class);
		//否单表单
		arpMain.addMapping("tb_pd_forceendform", "id", Forceendform.class);
		//挂起表单
		arpMain.addMapping("tb_pd_suspendform", "id", Suspendform.class);
		//备注
		//arpMain.addMapping("tb_pd_orderform", "id", Orderform.class);
		
		druidPlugin.start();
		arpMain.start();

		
		
		workflowPlugin=new WorkflowPlugin();		
		workflowPlugin.start();
	}

}
