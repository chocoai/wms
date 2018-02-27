package com.xyy.erp.platform.system.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.entity.EntityStatus;
import com.mysql.jdbc.Connection;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.model.WorkflowRecord;
import com.xyy.util.DateTimeUtil;
import com.xyy.workflow.definition.ActivityController;
import com.xyy.workflow.definition.ActivityDefinition;
import com.xyy.workflow.definition.EventDefinition;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessException;
import com.xyy.workflow.definition.ProcessForm;
import com.xyy.workflow.definition.ProcessHistory;
import com.xyy.workflow.definition.ProcessInstance;
import com.xyy.workflow.definition.ProcessTypeDefinition;
import com.xyy.workflow.definition.ProcessUser;
import com.xyy.workflow.definition.TaskInstance;

public class ApiService {

	private static String _SQL_ALL = "select * ";

	public Record findDirector(String id, String table) {
		return Db.findById(table, id);
	}

	public ProcessTypeDefinition getProcessTypeDefinition(String id) {
		return ProcessTypeDefinition.dao.findById(id);
	}

	public User findFrontUser(String id) {
		return User.dao.findById(id);
	}

	/*
	 * public Task getTask(String id) { return Task.dao.findById(id); }
	 */
	public List<WorkflowRecord> getRecordByPIid(String pi_id) {
		// return WorkflowRecord.dao.find("select * from cdt_rj_workflow_record
		// where activityId=? order by createTime desc",ai_id);
		return WorkflowRecord.dao
				.find("select * from cdt_rj_workflow_record where proInstanceId=? order by createTime desc", pi_id);
	}

	public List<ProcessTypeDefinition> getProcessTypeDefinitionAll() {
		return ProcessTypeDefinition.dao.find(" select * from tb_pd_processtypedefinition ");
	}

	public List<ProcessTypeDefinition> getProcessTypeDefinitionExcept(String id) {
		return ProcessTypeDefinition.dao.find(" select * from tb_pd_processtypedefinition where id not in(?)", id);
	}

	public List<ActivityDefinition> getActivityDefinition() {
		return ActivityDefinition.dao.find(" select * from tb_pd_activitydefinition ");
	}

	public List<ProcessDefinition> geProcessdefinitions() {
		return ProcessDefinition.dao.find(" select * from tb_pd_processdefinition where status=2");
	}

	/**
	 * 查询所有taskinstance
	 * 
	 * @param piid
	 * @return
	 */
	public List<TaskInstance> findTaskinstance(String piid) {
		return TaskInstance.dao.find("select * from tb_pd_taskinstance  where piId=? order by createTime desc", piid);
	}

	@SuppressWarnings("unused")
	private String getFriendSql(String name, int status) {
		StringBuilder sb = new StringBuilder();
		if (name != null && !"".equals(name)) {
			sb.append("and name like  '%").append(name).append("%'  ");
		}
		if (status != -1) {
			sb.append("and status=").append(status).append(" ");
		}
		return sb.toString();
	}

	private static String _FrontUserSQL = "select count(*) as count from tb_sys_user where 1=1  ";

	public int getFrontUserTotal(String userName, String tel, int status, int type) {
		return Db
				.findFirst(
						_FrontUserSQL + this.getFrontUserSql(userName, tel, status, type) + " order by createTime desc")
				.getLong("count").intValue();
	}

	private String getFrontUserSql(String userName, String tel, int status, int type) {
		StringBuilder sb = new StringBuilder();
		if (userName != null && !"".equals(userName)) {
			sb.append("and userName like  '%").append(userName).append("%'  ");
		}
		if (tel != null && !"".equals(tel)) {
			sb.append("and tel like  '%").append(tel).append("%'  ");
		}
		if (status != -1) {
			sb.append("and status=").append(status).append(" ");
		}
		if (type != -1) {
			sb.append("and type=").append(type).append(" ");
		}
		return sb.toString();
	}

	public List<com.xyy.erp.platform.system.model.User> getFrontUserApplies(String userName, String tel, int status,
			int type, int pageIndex, int pageSize) {
		Page<com.xyy.erp.platform.system.model.User> result = com.xyy.erp.platform.system.model.User.dao
				.paginate(pageIndex, pageSize, _SQL_ALL, "from tb_sys_user where 1=1  "
						+ this.getFrontUserSql(userName, tel, status, type) + " order by createTime desc");
		return result.getList();
	}

	// 获取总订单量
	private static String _OrderTotalSQL = "select count(*) as count from cdt_rj_order where 1=1  ";

	public int getOrderTotal(int status, int type, int days, String inviteCodeType, String tel, String code,
			String executeNodeName, String userName, String productName, String source, String startTime,
			String endTime, int amount, String codeType) {
		return Db
				.findFirst(_OrderTotalSQL + this.getOrderSql(status, type, days, inviteCodeType, tel, code,
						executeNodeName, userName, productName, source, startTime, endTime, amount, codeType))
				.getLong("count").intValue();
	}

	// 查询订单总量、申请金额、贷款金额


	private String getOrderSql(int status, int type, int days, String inviteCodeType, String tel, String code,
			String executeNodeName, String userName, String productName, String source, String startTime,
			String endTime, int amount, String codeType) {
		StringBuilder sb = new StringBuilder();
		if (status == 4) {
			// 所有订单除开草稿订单
			sb.append("and status != -1 ").append(" ");
		} else if (status == 1) {
			// 已经完成的订单
			sb.append("and status=70 ").append(" ");
		} else if (status == 2) {
			// 拒绝的订单
			sb.append("and status in(11,21,31,51,61,91) ").append(" ");

		} else if (status == 3) {
			// 所有订单除开草稿订单
			sb.append("and status != -1 ").append(" ");

			String beforDate = DateTimeUtil.formatDate(new Date());
			String startDate = beforDate + " 00:00:00";
			String endDate = beforDate + " 23:59:59";
			sb.append("and loanTime >= '" + startDate + "' and loanTime <= '" + endDate + " '").append(" ");
			// sb.append("and
			// DATE_FORMAT(createTime,'%Y-%m-%d')>=DATE_FORMAT(NOW(),'%Y-%m-%d
			// 00:00:00') and
			// DATE_FORMAT(createTime,'%Y-%m-%d')<=DATE_FORMAT(NOW(),'%Y-%m-%d
			// 23:59:59')").append(" ");
		} else if (status == 5) {
			// 所有受理的订单
			sb.append("and status in(1,2,10,20,30,40,50,60,80,90,100) ").append(" ");
		} else {
			sb.append("and status=").append(status).append(" ");
		}
		if (type == 1) {
			// 计算前七天的数据
			String beforDate = DateTimeUtil.getBeforYDate(new Date(), days);
			String startDate = beforDate + " 00:00:00";
			String endDate = beforDate + " 23:59:59";
			sb.append("and createTime >= '" + startDate + "' and createTime <= '" + endDate + " '").append(" ");
		} else if (type == 2) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(sdf.format(new Date()));
			String endDate = DateTimeUtil.getEndDayofMonth(year, days) + " 23:59:59";
			String startDate = "";
			startDate = year + "-" + days + "-01 00:00:00";
			sb.append("and createTime >= '" + startDate + "' and createTime <= '" + endDate + "'").append(" ");
		}
		if (!StringUtil.isEmpty(codeType)) {
			if (codeType.equals("1")) {
				if (inviteCodeType.equals("05")) {
					sb.append("and FIND_IN_SET(substr(groupInviteCode,5,2),\"" + inviteCodeType + "\")").append(" ");

				} else if (inviteCodeType.equals("03")) {
					sb.append("and FIND_IN_SET(substr(groupInviteCode,5,2),\"" + inviteCodeType + "\")").append(" ");
				} else if (!StringUtil.isEmpty(inviteCodeType)) {
					sb.append("and FIND_IN_SET(groupInviteCode,\"" + inviteCodeType + "\")").append(" ");
				}
			} else if (codeType.equals("2")) {
				if (inviteCodeType.equals("05")) {
					sb.append("and FIND_IN_SET(substr(groupInviteCode2,5,2),\"" + inviteCodeType + "\")").append(" ");

				} else if (inviteCodeType.equals("03")) {
					sb.append("and FIND_IN_SET(substr(groupInviteCode2,5,2),\"" + inviteCodeType + "\")").append(" ");
				} else if (!StringUtil.isEmpty(inviteCodeType)) {
					sb.append("and FIND_IN_SET(groupInviteCode2,\"" + inviteCodeType + "\")").append(" ");
				}
			}
		}

		if (tel != null && !"".equals(tel)) {
			sb.append("and tel like  '%").append(tel).append("%'  ");
		}
		if (code != null && !"".equals(code)) {
			sb.append("and code like  '%").append(code).append("%'  ");
		}
		if (executeNodeName != null && !"".equals(executeNodeName)) {
			sb.append("and executeNodeName like  '%").append(executeNodeName).append("%'  ");
		}
		if (userName != null && !"".equals(userName)) {
			sb.append("and userName like  '%").append(userName).append("%'  ");
		}
		if (productName != null && !"".equals(productName)) {
			sb.append("and productId like  '%").append(productName).append("%'  ");
		}
		/*
		 * if (source != null && !"".equals(source)) { sb.append(
		 * "and source like  '%").append(source).append("%'  "); }
		 */
		if (startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)) {
			sb.append("and DATE_FORMAT(createTime,'%Y-%m-%d')>=str_to_date('" + startTime
					+ "','%Y-%m-%d') and DATE_FORMAT(createTime,'%Y-%m-%d')<=str_to_date('" + endTime + "','%Y-%m-%d')")
					.append(" ");
		}
		if (source != null && !"".equals(source)) {
			sb.append("and source like  '%").append(source).append("%'  ");
		}
		if (amount != -1) {
			if (amount == 1) {
				sb.append(" and (amount>0 and amount<=100000)  ");
			} else if (amount == 2) {
				sb.append(" and (amount>100000 and amount<=200000) ");
			} else if (amount == 3) {
				sb.append(" and amount>200000  ");
			}
		}
		return sb.toString();
	}

	private String getTaskSql(int status, int type, int days, String executor, String userName, String tel,
			String acceptMember) {
		StringBuilder sb = new StringBuilder();
		if (status == -1) {
			sb.append("and status in(1,2,3,4,7)").append(" ");
		} else if (status == -2) {
			sb.append("and (status in(1,2,3,4,7) and find_in_set(executor,'" + acceptMember
					+ "')) or (status = 0 and find_in_set('" + acceptMember + "',mIds))").append(" ");
			return sb.toString();
		} else if (status > -1) {
			sb.append("and status=" + status).append(" ");
		}
		if (type == 1) {
			// 计算前七天的数据
			String beforDate = DateTimeUtil.getBeforYDate(new Date(), days);
			String startDate = beforDate + " 00:00:00";
			String endDate = beforDate + " 23:59:59";
			sb.append("and createTime >= '" + startDate + "' and createTime <= '" + endDate + " '").append(" ");
		} else if (type == 2) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(sdf.format(new Date()));
			String endDate = DateTimeUtil.getEndDayofMonth(year, days) + " 23:59:59";
			String startDate = "";
			startDate = year + "-" + days + "-01 00:00:00";
			sb.append("and createTime >= '" + startDate + "' and createTime <= '" + endDate + "'").append(" ");
		}
		if (null != executor && !StringUtil.isEmpty(executor) && status != 0) {
			sb.append("and find_in_set(executor,'" + executor + "')").append(" ");
		}
		if (userName != null && !"".equals(userName)) {
			sb.append("and taskNumber='").append(userName).append("' ");
		}
		if (tel != null && !"".equals(tel)) {
			sb.append("and pdName='").append(tel).append("' ");
		}
		if (acceptMember != null && !"".equals(acceptMember) && status == 0) {
			sb.append("and find_in_set('" + acceptMember + "',mIds)").append(" ");
		}
		return sb.toString();
	}

	public List<TaskInstance> getTaskListByStatus(int status, int pageIndex, int pageSize, int type, int days,
			String executor, String userName, String tel, String acceptMember) {
		Page<TaskInstance> result = TaskInstance.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_taskinstance where 1=1  "
						+ this.getTaskSql(status, type, days, executor, userName, tel, acceptMember)
						+ " order by createTime desc ");
		return result.getList();
	}

	/**
	 * 结单统计
	 */
	private static String _TaskOverTotalSQL = "select count(*) as count from tb_pd_processinstance where 1=1  ";

	public Record getTaskOverTotal(int status, int type, int days, String executor) {
		Record cord = Db.findFirst(_TaskOverTotalSQL + this.getTaskOverSql(status, type, days, executor));
		return cord;
	}

	private String getTaskOverSql(int status, int type, int days, String executor) {
		StringBuilder sb = new StringBuilder();
		if (status == -1) {

		} else if (status >= -1) {
			sb.append("and status=" + status).append(" ");
		}
		if (type == 1) {
			// 计算前七天的数据
			String beforDate = DateTimeUtil.getBeforYDate(new Date(), days);
			String startDate = beforDate + " 00:00:00";
			String endDate = beforDate + " 23:59:59";
			sb.append("and startTime >= '" + startDate + "' and startTime <= '" + endDate + " '").append(" ");
		} else if (type == 2) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(sdf.format(new Date()));
			String endDate = DateTimeUtil.getEndDayofMonth(year, days) + " 23:59:59";
			String startDate = "";
			startDate = year + "-" + days + "-01 00:00:00";
			sb.append("and startTime >= '" + startDate + "' and startTime <= '" + endDate + "'").append(" ");
		}
		if (!StringUtil.isEmpty(executor)) {
			sb.append("and id in (select distinct piId from tb_pd_taskinstance where executor = '" + executor
					+ "' group by piId)").append(" ");
		}

		return sb.toString();
	}

	public List<ProcessInstance> getTaskOverListByStatus(int status, int pageIndex, int pageSize, int type, int days,
			String executor) {
		Page<ProcessInstance> result = ProcessInstance.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_processinstance where 1=1  " + this.getTaskOverSql(status, type, days, executor)
						+ " order by startTime desc ");
		return result.getList();
	}

	private static String _ProcessTypeDefinitionSQL = "select count(*) as count from tb_pd_processtypedefinition where 1=1  ";

	public int getProcessTypeDefinitionTotal() {
		return Db.findFirst(_ProcessTypeDefinitionSQL + " order by createTime desc ").getLong("count").intValue();
	}

	public List<ProcessTypeDefinition> getProcessTypeDefinitionApplies(int pageIndex, int pageSize) {
		Page<ProcessTypeDefinition> result = ProcessTypeDefinition.dao.paginate(pageIndex, pageSize,
				"SELECT  t.id,t.name,(select name from tb_pd_processtypedefinition  where id=t.parentId ) as parentId,t.desc,t.desc,t.createTime,t.updateTime",
				" from tb_pd_processtypedefinition  t  " + " order by createTime desc ");
		return result.getList();
	}

	public String postProcessTypeDefinition(String operUser) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> processTypeDefinitionMap = (Map<String, Object>) JSON.parse(operUser);
			ProcessTypeDefinition processTypeDefinitionSave = new ProcessTypeDefinition();
			processTypeDefinitionSave._setAttrs(processTypeDefinitionMap);

			// 查找父节点名称
			if (null != processTypeDefinitionMap.get("parentId")
					&& !StringUtil.isEmpty(processTypeDefinitionMap.get("parentId").toString())) {
				ProcessTypeDefinition ptd = ProcessTypeDefinition.dao
						.findById(processTypeDefinitionMap.get("parentId").toString());
				if (null != ptd) {
					processTypeDefinitionSave.set("parentName", ptd.getName());
				}
				processTypeDefinitionSave.set("id", UUIDUtil.newUUID());
			} else {
				processTypeDefinitionSave.set("id", "root");
			}
			processTypeDefinitionSave.set("createTime", new Date());
			if (processTypeDefinitionSave.save()) {
				return "1";
			} else {
				return "0";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}
	}

	public String postProcessTypeDefinitionUpdate(String operUser) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> processTypeDefinitionMap = (Map<String, Object>) JSON.parse(operUser);
			ProcessTypeDefinition processTypeDefinitionUpdate = ProcessTypeDefinition.dao
					._setAttrs(processTypeDefinitionMap);
			// 查找父节点名称
			if (null != processTypeDefinitionMap.get("parentId")
					&& !StringUtil.isEmpty(processTypeDefinitionMap.get("parentId").toString())) {
				ProcessTypeDefinition ptd = ProcessTypeDefinition.dao
						.findById(processTypeDefinitionMap.get("parentId").toString());
				if (null != ptd) {
					processTypeDefinitionUpdate.set("parentName", ptd.getName());
				}
			}
			processTypeDefinitionUpdate.set("updateTime", new Date());
			if (processTypeDefinitionUpdate.update()) {
				return "1";
			} else {
				return "0";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}
	}

		

	public int getProcessDefineTotal(String status, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) as count from tb_pd_processdefinition where 1=1");
		if (!StringUtil.isEmpty(name)) {
			sb.append(" and name like '%").append(name).append("%'");
		}
		sb.append(" and status=").append(status);
		// 返回对应的条目数
		return Db.findFirst(sb.toString()).getLong("count").intValue();
	}

	public List<ProcessDefinition> getProcessDefineList(String status, String name, int pageIndex, int pageSize) {
		StringBuffer sb = new StringBuffer();
		if (!StringUtil.isEmpty(name)) {
			sb.append(" and name like '%").append(name).append("%'");
		}
		sb.append(" and status=").append(status);
		sb.append(" order by createTime desc");

		return ProcessDefinition.dao
				.paginate(pageIndex, pageSize, "select * ", "from tb_pd_processdefinition where 1=1" + sb.toString())
				.getList();
	}

	// @Before(RSTx.class)
	public String saveProcessDefine(String processdefinitionStr, String processcontrollerStr, String eventdefinitionStr,
			String processform) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> processdefinitionMap = (Map<String, Object>) JSON.parse(processdefinitionStr);
			// Map<String, Object> processcontrollerMap = (Map<String, Object>)
			// JSON.parse(processcontrollerStr);
			// Map<String, Object> eventdefinitionMap = (Map<String, Object>)
			// JSON.parse(eventdefinitionStr);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> processFormListMap = (List<Map<String, Object>>) JSONArray.parse(processform);

			String id = UUIDUtil.newUUID();
			ProcessDefinition pd = new ProcessDefinition();
			pd.setId(id);
			pd._setAttrs(processdefinitionMap);
			pd.setVersion(0);
			pd.setStatus(1);
			pd.setCreateTime(new Timestamp(System.currentTimeMillis()));

			/*
			 * ProcessController pc = new ProcessController();
			 * pc.setId(UUIDUtil.newUUID()); pc.setAttrs(processcontrollerMap);
			 * pc.setCreateTime(new Timestamp(System.currentTimeMillis()));
			 * pc.setPdId(pd.getId());
			 * 
			 * EventDefinition ed = new EventDefinition();
			 * ed.setId(UUIDUtil.newUUID()); ed.setAttrs(eventdefinitionMap);
			 * ed.setPid(pd.getId()); ed.setType(0);//流程事件 ed.setCreateTime(new
			 * Timestamp(System.currentTimeMillis()));
			 */

			for (Map<String, Object> processFormMap : processFormListMap) {
				// ProcessForm pfObj = ProcessForm.dao.setAttrs(processFormMap);
				ProcessForm pfObj = new ProcessForm();
				pfObj._setAttrs(processFormMap);
				// if(StringUtil.isEmpty(pfObj.getId())){
				pfObj.setId(UUIDUtil.newUUID());
				pfObj.setPdId(id);
				pfObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
				pfObj.save();
				// }
			}

			if (!pd.save()) {
				return "0";
			}
			/*
			 * if(!pc.save()){ return "0"; } if(!ed.save()){ return "0"; }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}
		return "1";
	}

	// @Before(RSTx.class)
	public String updateProcessDefine(String processdefinitionStr, String processcontrollerStr,
			String eventdefinitionStr, String processformStr) {
		@SuppressWarnings("unchecked")
		Map<String, Object> processdefinitionMap = (Map<String, Object>) JSON.parse(processdefinitionStr);
		//Map<String, Object> processcontrollerMap = (Map<String, Object>) JSON.parse(processcontrollerStr);
		//Map<String, Object> eventdefinitionMap = (Map<String, Object>) JSON.parse(eventdefinitionStr);			
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> processformMap = (List<Map<String, Object>>) JSONArray.parse(processformStr);
		ProcessDefinition pd=new ProcessDefinition();
		pd._setAttrs(processdefinitionMap);	
		pd.set__$status(EntityStatus.dirty);
		pd.setUpdateTime(new Timestamp(System.currentTimeMillis()));		
		for(Map<String, Object> process : processformMap){
			ProcessForm pfObj = new  ProcessForm();
			pfObj._setAttrs(process);
			if(StringUtil.isEmpty(pfObj.getId())){
				pfObj.setId(UUIDUtil.newUUID());
				pfObj.setPdId(pd.getId());
				pfObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
				pfObj.save();
			}else{
				pfObj.setId(process.get("id").toString());
				pfObj.set__$status(EntityStatus.dirty);
				pfObj.update();
			}
		}
		
		if(!pd.update()){
			return "0";
		}
		return "1";
	}

	public boolean postActivitys(final String activitydefinition, final String activitycontroller,
			final String eventdefinition, final String processuser) {

		boolean result = Db.tx(Connection.TRANSACTION_REPEATABLE_READ, new IAtom() {
			@Override
			public boolean run() throws SQLException {

				try {
					@SuppressWarnings("unchecked")
					Map<String, Object> activitydefinitionMap = (Map<String, Object>) JSON.parse(activitydefinition);
					@SuppressWarnings("unchecked")
					Map<String, Object> activitycontrollerMap = (Map<String, Object>) JSON.parse(activitycontroller);

					@SuppressWarnings("unchecked")
					List<Map<String, Object>> eventdefinitionMap = (List<Map<String, Object>>) JSONArray
							.parse(eventdefinition);
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> processuserMap = (List<Map<String, Object>>) JSONArray.parse(processuser);
					String uuid = UUIDUtil.newUUID();
					// 活动定义表
					ActivityDefinition activityDefinitionObj = new ActivityDefinition();
					activityDefinitionObj._setAttrs(activitydefinitionMap);
					activityDefinitionObj.setId(uuid);
					activityDefinitionObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
					activityDefinitionObj.save();

					// 活动控制器表
					ActivityController activityControllerObj = new ActivityController();
					activityControllerObj._setAttrs(activitycontrollerMap);
					activityControllerObj.setId(UUIDUtil.newUUID());
					activityControllerObj.setAdId(uuid);
					activityControllerObj.setCreateTime(new Timestamp(System.currentTimeMillis()));
					activityControllerObj.save();

					// 活动事件表
					for (Map<String, Object> eventMap : eventdefinitionMap) {
						EventDefinition eventDefinition = new EventDefinition();
						eventDefinition._setAttrs(eventMap);
						eventDefinition.setAid(uuid);
						eventDefinition.setPid(activityDefinitionObj.getPdId());
						eventDefinition.setCreateTime(new Timestamp(System.currentTimeMillis()));
						eventDefinition.save();
					}

					// 活动用户表
					String category = "";
					for (Map<String, Object> userMap : processuserMap) {

						category = userMap.get("category").toString();
						String[] categorySZ = null;
						if ("0".equals(category)) {
							categorySZ = userMap.get("category0").toString().split(",");
						} else if ("1".equals(category)) {
							categorySZ = userMap.get("category1").toString().split(",");
						} else if ("2".equals(category)) {
							categorySZ = userMap.get("category2").toString().split(",");
						} else if ("3".equals(category)) {
							categorySZ = userMap.get("category3").toString().split(",");
						} else if ("4".equals(category)) {
							categorySZ = userMap.get("category4").toString().split(",");
						}

						for (int i = 0; i < categorySZ.length; i++) {
							ProcessUser processUser = new ProcessUser();
							processUser.setId(UUIDUtil.newUUID());
							processUser.setRid(uuid);
							processUser.setType(Integer.parseInt(userMap.get("type").toString()));
							processUser.setCategory(Integer.parseInt(userMap.get("category").toString()));
							processUser.setUid(categorySZ[i]);
							processUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
							processUser.save();
						}
					}
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		});
		return result;
	}

	public boolean postActivitysUpdate(final String id, final String activitydefinition,
			final String activitycontroller, final String eventdefinition, final String processuser) {

		boolean result = Db.tx(Connection.TRANSACTION_REPEATABLE_READ, new IAtom() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean run() throws SQLException {

				try {
					Map<String, Object> activitydefinitionMap = (Map<String, Object>) JSON.parse(activitydefinition);
					Map<String, Object> activitycontrollerMap = (Map<String, Object>) JSON.parse(activitycontroller);

					List<Map<String, Object>> eventdefinitionMap = (List<Map<String, Object>>) JSONArray
							.parse(eventdefinition);
					List<Map<String, Object>> processuserMap = (List<Map<String, Object>>) JSONArray.parse(processuser);

					// 活动定义表
					ActivityDefinition activityDefinitionUpdate = ActivityDefinition.dao
							._setAttrs(activitydefinitionMap);
					activityDefinitionUpdate.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					activityDefinitionUpdate.update();
					// 活动控制器表
					ActivityController ActivityControllerUpdate = ActivityController.dao
							._setAttrs(activitycontrollerMap);
					ActivityControllerUpdate.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					ActivityControllerUpdate.update();

					// 活动事件表
					for (Map<String, Object> eventMap : eventdefinitionMap) {
						EventDefinition eventDefinition = new EventDefinition();
						eventDefinition._setAttrs(eventMap);
						if (eventMap.get("id") == null) {
							eventDefinition.set("id", UUIDUtil.newUUID());
							eventDefinition.setPid(activityDefinitionUpdate.getPdId());
							eventDefinition.setAid(id);
							eventDefinition.setCreateTime(new Timestamp(System.currentTimeMillis()));
							eventDefinition.save();
						} else {
							eventDefinition.setPid(activityDefinitionUpdate.getPdId());
							;
							eventDefinition.update();
						}
					}

					// 活动用户表
					String category = "";
					for (Map<String, Object> userMap : processuserMap) {

						if (userMap.get("id") == null) {
							category = userMap.get("category").toString();
							String[] categorySZ = null;
							if ("0".equals(category)) {
								categorySZ = userMap.get("category0").toString().split(",");
							} else if ("1".equals(category)) {
								categorySZ = userMap.get("category1").toString().split(",");
							} else if ("2".equals(category)) {
								categorySZ = userMap.get("category2").toString().split(",");
							} else if ("3".equals(category)) {
								categorySZ = userMap.get("category3").toString().split(",");
							} else if ("4".equals(category)) {
								categorySZ = userMap.get("category4").toString().split(",");
							}
							for (int i = 0; i < categorySZ.length; i++) {
								ProcessUser processUser = new ProcessUser();
								processUser.setId(UUIDUtil.newUUID());
								processUser.setRid(id);
								processUser.setType(Integer.parseInt(userMap.get("type").toString()));
								processUser.setCategory(Integer.parseInt(userMap.get("category").toString()));
								processUser.setUid(categorySZ[i]);
								processUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
								processUser.save();
							}
						}
					}
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		});
		return result;
	}

	public static class ApplyOrderProcessResult {
		private int result = 0;

		public ApplyOrderProcessResult(int result) {
			super();
			this.result = result;
		}

		public int getResult() {
			return result;
		}

		public void setResult(int result) {
			this.result = result;
		}
	}

	/************************ 流程类别管理 **************************/
	private static String _SysProcessDefinitionSQL = "select count(*) as count from tb_pd_processdefinition where 1=1 ";

	public int getProcessDefinitionTotal(String tid) {
		return Db.findFirst(_SysProcessDefinitionSQL + this.getProcessDefinitionSql(tid) + " order by createTime desc ")
				.getLong("count").intValue();
	}

	public List<ProcessDefinition> getProcessDefinitionList(int pageIndex, int pageSize, String tid) {
		Page<ProcessDefinition> result = ProcessDefinition.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_processdefinition where 1=1  " + this.getProcessDefinitionSql(tid)
						+ " order by createTime desc ");
		return result.getList();
	}

	private String getProcessDefinitionSql(String tid) {
		StringBuilder sb = new StringBuilder();
		if (tid != null && !"".equals(tid)) {
			sb.append(" and tid =  '").append(tid).append("' ");
		} else {
			sb.append(" and tid is not null ");
		}
		return sb.toString();
	}

	/**
	 * 查询流程类别树
	 * 
	 * @return
	 */
	public String queryProcessTypeTree() {
		List<ProcessTypeDefinition> deptTree = ProcessTypeDefinition.dao
				.find("select * from tb_pd_processtypedefinition order by createTime");
		ProcessTypeDefinition root = null;
		for (ProcessTypeDefinition d : deptTree) {
			if (d.getParentId() == null || StringUtil.isEmpty(d.getParentId())) {
				root = d;
				break;
			}
		}
		if(root!=null)
			return "[" + iteratorProcessTypeTree(root) + "]";
		else {
			return "[ ]";
		}
	}

	public String iteratorProcessTypeTree(ProcessTypeDefinition pt) {
		StringBuilder buffer = new StringBuilder();
		if (pt != null) {
			buffer.append("{");
			buffer.append(" 'id' : '").append(pt.getId()).append("',");
			buffer.append(" 'name' : '").append(pt.getName()).append("',");
			buffer.append(" 'chidrens' : [");
			int i = 0;
			if (null != pt.getChidrens() && pt.getChidrens().size() > 0) {
				do {

					ProcessTypeDefinition index = pt.getChidrens().get(i);
					if (index.getChidrens() != null && index.getChidrens().size() > 0) {
						buffer.append(iteratorProcessTypeTree(index));
					} else {
						buffer.append("{");
						buffer.append(" 'id' : '").append(pt.getChidrens().get(i).getId()).append("',");
						buffer.append(" 'name' : '").append(pt.getChidrens().get(i).getName()).append("',");
						buffer.append(" 'chidrens' : []}");
					}

					if (i < pt.getChidrens().size() - 1) {
						buffer.append(", ");
					}
					i++;
				} while (i < pt.getChidrens().size());
			}
			buffer.append("]");
			buffer.append("}");
		}
		return buffer.toString();
	}

	private static String _SysProcessTypeSQL = "select count(*) as count from tb_pd_processtypedefinition where 1=1  ";

	public int getProcessTypeTotal(String parentId) {
		return Db.findFirst(_SysProcessTypeSQL + this.getProcessTypeSql(parentId) + " order by createTime ")
				.getLong("count").intValue();
	}

	public List<ProcessTypeDefinition> getProcessTypeList(int pageIndex, int pageSize, String parentId) {
		Page<ProcessTypeDefinition> result = ProcessTypeDefinition.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_processtypedefinition where 1=1  " + this.getProcessTypeSql(parentId)
						+ " order by createTime ");
		return result.getList();
	}

	private String getProcessTypeSql(String parentId) {
		StringBuilder sb = new StringBuilder();
		if (parentId != null && !"".equals(parentId)) {
			sb.append("and (parentId =  '").append(parentId).append("' or id = '").append(parentId).append("') ");
		}
		return sb.toString();
	}

	/****************** 成员管理 *************************/
	private static String _SysMembersSQL = "select count(*) as count from cdt_rj_members where 1=1  ";

	public int getMembersTotal(String status, String name, String tel, String followStatus) {
		return Db.findFirst(
				_SysMembersSQL + this.getMembersSql(status, name, tel, followStatus) + " order by createTime desc ")
				.getLong("count").intValue();
	}

	public List<User> getMembersList(String status, int pageIndex, int pageSize, String name, String tel,
			String followStatus) {
		Page<User> result = User.dao.paginate(pageIndex, pageSize, _SQL_ALL, "from tb_sys_user where 1=1  "
				+ this.getMembersSql(status, name, tel, followStatus) + " order by createTime desc ");
		return result.getList();
	}

	private String getMembersSql(String status, String name, String tel, String followStatus) {
		StringBuilder sb = new StringBuilder();
		if (status != null && !"".equals(status)) {
			sb.append("and status =  ").append(status).append(" ");
		}
		if (name != null && !"".equals(name)) {
			sb.append("and realName like  '%" + name + "%'").append(" ");
		}
		if (tel != null && !"".equals(tel)) {
			sb.append("and tel like  '%" + tel + "%'").append(" ");
		}
		if (followStatus != null && !"".equals(followStatus)) {
			sb.append("and followStatus =  ").append(followStatus).append(" ");
		}
		return sb.toString();
	}

	/**
	 * 查询部门
	 */
	public User findMembersById(String id) {
		return User.dao.findById(id);
	}

	/**
	 * 获取所有部门信息
	 */
	public List<User> queryMembersList(String id) {
		String sql = "";
		if (StringUtil.isEmpty(id)) {
			sql = "select * from tb_sys_user where status=1";
		} else {
			sql = "select * from tb_sys_user where status=1 and id != '" + id + "'";
		}
		return User.dao.find(sql);
	}

	/*********** 线上订单管理 ************/
	private static String _OnlineOrderSQL = "select count(*) as count from tb_pd_taskinstance where 1=1  ";

	public int getOnlineOrderTotal(int status, String userId, String name) {

		return Db.findFirst(_OnlineOrderSQL + this.getOnlineOrderSql(userId, name, status)).getLong("count").intValue();
	}

	private String getOnlineOrderSql(String executor, String pdName, int status) {
		StringBuilder sb = new StringBuilder();
		if (executor != null && !"".equals(executor)) {
			sb.append("and executor='").append(executor).append("' ");
		}
		if (pdName != null && !"".equals(pdName)) {
			sb.append("and pdName='").append(pdName).append("' ");
		}
		if (status != -1) {
			sb.append("and status=").append(status).append(" ");
		}
		return sb.toString();
	}

	public List<TaskInstance> getOnlineOrderList(int status, String userId, String name, int pageIndex, int pageSize) {
		Page<TaskInstance> result = TaskInstance.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_taskinstance where 1=1  " + this.getOnlineOrderSql(userId, name, status));
		return result.getList();
	}

	public List<com.xyy.erp.platform.system.model.User> searchByTiaojiao(String sear_code, String sear_value) {
		String[] sear_codeSZ = sear_code.split(",", -1);
		String[] sear_valueSZ = sear_value.split(",", -1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sear_codeSZ.length - 1; i++) {
			if (sear_valueSZ[i] != null && !"".equals(sear_valueSZ[i].trim())) {
				sb.append("and ").append(sear_codeSZ[i]).append(" like  '%").append(sear_valueSZ[i]).append("%'  ");
			}
		}
		String sql = "select * from tb_sys_user where 1=1 " + sb.toString();
		return com.xyy.erp.platform.system.model.User.dao.find(sql);

	}

	public List<Record> searchDirectorByTiaojiao(String sear_code, String sear_value, String table, int status) {
		String sql = "select * from " + table + " where 1=1 and status=" + status;
		if (!StringUtil.isEmpty("sear_code") && !sear_value.contains("undefined")) {
			String[] sear_codeSZ = sear_code.split(",", -1);
			String[] sear_valueSZ = sear_value.split(",", -1);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sear_codeSZ.length - 1; i++) {
				if (sear_valueSZ[i] != null && !"".equals(sear_valueSZ[i].trim())) {
					sb.append("and ").append(sear_codeSZ[i]).append(" like  '%").append(sear_valueSZ[i]).append("%'  ");
				}
			}
			sql += sb.toString();
		}
		return Db.find(sql);
	}

	/**
	 * 异常流程管理
	 */
	private static String _ExceptionSQL = "select count(*) as count from tb_pd_exception where 1=1 ";

	public int getExceptionTotal(int status) {
		return Db.findFirst(_ExceptionSQL + this.getExceptionSql(status) + " order by createTime desc ")
				.getLong("count").intValue();
	}

	// 查询并返回异常流程信息
	public List<ProcessException> getExceptionApplies(int status, int pageIndex,
			int pageSize) {
		
		Page<ProcessException> result = ProcessException.dao.paginate(
				pageIndex,
				pageSize,
				_SQL_ALL,
				"from tb_pd_exception where 1=1  "
						+ this.getExceptionSql(status)
						+ " order by createTime desc ");
		
		return result.getList();

	}

	// 获取异常sql
	private String getExceptionSql(int status) {
		StringBuilder sb = new StringBuilder();
		if (status != -1) {
			if (status == 0) {
				sb.append("and (status is null or  status=").append(status).append(" ) ");
			} else {
				sb.append("and status=").append(status).append(" ");
			}
		}
		return sb.toString();
	}

	// 解决异常
	public boolean updateException(String id, int status, String remark) {
		int result = 0;

		result = Db.update("update tb_pd_exception set status=?,remark=?,updateTime=? where id=?", status, remark,
				new Date(), id);
		return result == 1 ? true : false;
	}

	/**
	 * 获取流程实例总数
	 */
	private static String processInstanceSQL = "select count(1) as cc from tb_pd_processinstance where 1=1 ";

	public int getProcessTotal(String name, String pdName, String id, String startTime, String endTime, int status) {
		String sql = processInstanceSQL + this.getProcessMonitorSql(name, pdName, status, id, startTime, endTime);
		int aa = Db.findFirst(sql).getLong("cc").intValue();
		return aa;
	}

	public int getTransferTaskTotal(String taskNumber, String pdName, String id, String createTime, String endTime) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select count(1) as cc from tb_pd_taskinstance where 1 = 1 and status = 1");
		if (!StringUtil.isEmpty(taskNumber)) {
			buffer.append(" and taskNumber like  '%" + taskNumber + "%'");
		}
		if (!StringUtil.isEmpty(pdName)) {
			List<ProcessDefinition> pds = ProcessDefinition.dao
					.find("select * from tb_pd_processdefinition where name like '%" + pdName + "%' ");
			if (pds != null) {
				StringBuilder pdstr = new StringBuilder();
				for (ProcessDefinition pd : pds) {
					pdstr.append("'" + pd.getId() + "',");
				}
				buffer.append(" and pdId in( " + pdstr.toString().substring(0, pdstr.toString().length() - 1) + ")");

			}
		}
		if (!StringUtil.isEmpty(id)) {
			buffer.append(" and id like '%" + id + "%'");
		}
		if (!StringUtil.isEmpty(createTime)) {
			buffer.append(" and DATE_FORMAT(startTime,'%Y-%m-%d')>=str_to_date('" + createTime + "','%Y-%m-%d')");
		}
		if (!StringUtil.isEmpty(endTime)) {
			buffer.append(" and DATE_FORMAT(startTime,'%Y-%m-%d')<=str_to_date('" + endTime + "','%Y-%m-%d')");
		}
		return Db.findFirst(buffer.toString()).getLong("cc").intValue();
	}

	public List<TaskInstance> getTransferTasklist(String taskNumber, String pdName, String id, String createTime,
			String endTime, int pageIndex, int pageSize) {

		StringBuffer buffer = new StringBuffer();
		if (!StringUtil.isEmpty(taskNumber)) {
			buffer.append(" and taskNumber like  '%" + taskNumber + "%'");
		}
		if (!StringUtil.isEmpty(pdName)) {
			List<ProcessDefinition> pds = ProcessDefinition.dao
					.find("select * from tb_pd_processdefinition where name like '%" + pdName + "%' ");
			if (pds != null) {
				StringBuilder pdstr = new StringBuilder();
				for (ProcessDefinition pd : pds) {
					pdstr.append("'" + pd.getId() + "',");
				}
				buffer.append(" and pdId in( " + pdstr.toString().substring(0, pdstr.toString().length() - 1) + ")");

			}
		}
		if (!StringUtil.isEmpty(id)) {
			buffer.append(" and id like  '%" + id + "%'");
		}
		if (!StringUtil.isEmpty(createTime)) {
			buffer.append(" and DATE_FORMAT(startTime,'%Y-%m-%d')>=str_to_date('" + createTime + "','%Y-%m-%d')");
		}
		if (!StringUtil.isEmpty(endTime)) {
			buffer.append(" and DATE_FORMAT(startTime,'%Y-%m-%d')<=str_to_date('" + endTime + "','%Y-%m-%d')");
		}

		Page<TaskInstance> result = TaskInstance.dao.paginate(pageIndex, pageSize, _SQL_ALL,
				"from tb_pd_taskinstance where 1=1 and status = 1 " + buffer.toString() + " order by createTime desc ");
		return result.getList();
	}

	/**
	 * 获取流程监控所需 sql
	 */
	private String getProcessMonitorSql(String name, String pdName, int status, String id, String startTime,
			String endTime) {

		StringBuilder sb = new StringBuilder();
		if (status >= 0) {
			sb.append("and status =  ").append(status).append(" ");
		}
		if (name != null && !"".equals(name)) {
			sb.append("and name like  '%" + name + "%'").append(" ");
		}
		if (id != null && !"".equals(id)) {
			sb.append("and id like  '%" + id + "%'").append(" ");
		}
		if (pdName != null && !"".equals(pdName)) {

			List<ProcessDefinition> pds = ProcessDefinition.dao
					.find("select * from tb_pd_processdefinition where name like '%" + pdName + "%' ");
			if (pds != null) {
				StringBuilder pdstr = new StringBuilder();
				for (ProcessDefinition pd : pds) {
					pdstr.append("'" + pd.getId() + "',");
				}
				sb.append("and pdId in( " + pdstr.toString().substring(0, pdstr.toString().length() - 1) + ")")
						.append(" ");

			}
		}

		// 格式化开始时间和结束时间
		startTime = formatData(startTime);
		endTime = formatData(endTime);
		if (startTime != null && !"".equals(startTime)) {
			sb.append("and DATE_FORMAT(startTime,'%Y-%m-%d')>=str_to_date('" + startTime + "','%Y-%m-%d')").append(" ");
		}
		if (endTime != null && !"".equals(endTime)) {
			sb.append("and DATE_FORMAT(startTime,'%Y-%m-%d')<=str_to_date('" + endTime + "','%Y-%m-%d')").append(" ");
		}

		return sb.toString();
	}

	// 时间格式化
	@SuppressWarnings("null")
	public static String formatData(String s) {
		if (null != s || "".equals(s.trim())) {
			try {
				s = s.replace("GMT", "").replaceAll("\\(.*\\)", "");
				// 将字符串转化为date类型，格式2017-01-01
				SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z", Locale.ENGLISH);
				Date date = format.parse(s);
				return new SimpleDateFormat("yyyy-MM-dd").format(date);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取流程实例列表
	 */
	public List<ProcessInstance> getProcessInstancelist(String name, String pdName, String id, String startTime,
			String endTime, int status, int pageIndex, int pageSize) {

		String sql = "from tb_pd_processinstance pi where 1=1 "
				+ this.getProcessMonitorSql(name, pdName, status, id, startTime, endTime) + " order by startTime desc";
		Page<ProcessInstance> page=ProcessInstance.dao.paginate(pageIndex, pageSize, "select * ", sql);
		
		List<ProcessInstance> list =page.getList();

		List<ProcessInstance> plist = new ArrayList<ProcessInstance>();
		for (ProcessInstance p : list) {

			ProcessInstance po = new ProcessInstance();
			po.setId(p.getId());
			po.setName(p.getName());
			po.setPdId(p.getProcessDefinition().getName());
			int v = p.getProcessDefinition().getVersion();
			po.setEntityId("" + v);
			po.setStatus(p.getStatus());
			if (p.getStartTime() != null)
				po.setStartTime(new Timestamp(p.getStartTime().getTime()));
			if (p.getEndTime() != null)
				po.setEndTime(new Timestamp(p.getEndTime().getTime()));
			if (p.getMainProcessInstance() != null)
				po.setMainProcess(p.getMainProcessInstance().getId());
			else
				po.setMainProcess("");
			plist.add(po);
		}

		return plist;
	}

	/**
	 * 获取执行链结构的头部 执行链结构： A->B B->C C->D 则其头部为A
	 * 
	 * @param t_hisList
	 * @return
	 */
	public ProcessHistory getProcessHistoryHead(List<ProcessHistory> t_hisList) {
		Map<String, ProcessHistory> pIdx = new HashMap<String, ProcessHistory>();
		for (ProcessHistory ph : t_hisList) {
			pIdx.put(ph.getNewAiId(), ph);
		}

		ProcessHistory head = null;
		for (ProcessHistory ph : t_hisList) {
			if (!pIdx.containsKey(ph.getOldAiId())) {
				head = ph;
				return head;
			}
		}
		return head;
	}

	/**
	 * 任务统计
	 */
	private static String _TaskTotalSQL = "select count(*) as count from tb_pd_taskinstance where 1=1  ";

	public Record getTaskTotal(int status, int type, int days, String executor, String userName, String tel,
			String acceptMember) {
		Record cord = Db
				.findFirst(_TaskTotalSQL + this.getTaskSql(status, type, days, executor, userName, tel, acceptMember));
		return cord;
	}

}
