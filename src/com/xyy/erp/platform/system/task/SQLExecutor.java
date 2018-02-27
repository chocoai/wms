package com.xyy.erp.platform.system.task;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.*;
import com.xyy.erp.platform.common.tools.UUIDUtil;
import com.xyy.erp.platform.system.task.AccountManagerPlugin.AccountTaskDescr;
import com.xyy.erp.platform.system.task.SQLDef.SQLType;
import com.xyy.util.DateTimeUtil;
import com.xyy.util.StringUtil;

/**
 * sql执行器，用于缓存需要执行的SQL语句 insert update delete
 * 
 * @author evan
 *
 */
public class SQLExecutor {
	// _rs_inserts_map,_rs_update_map,_rs_delete_map,_uid_sql_map
	public static List<String> interrupter_table_operator = new ArrayList<>();
	static {
		interrupter_table_operator.add("xyy_wms_bill_shangpinzhangye");
		interrupter_table_operator.add("xyy_erp_bill_wanglaizhangye");
	}

	// _rs_inserts_map,_rs_update_map,_rs_delete_map,_uid_sql_map,_zhangye_inserts_map
	private Map<SQLTarget, List<Record>> _rs_inserts_map = new HashMap<>();
	private Map<SQLTarget, List<Record>> _rs_update_map = new HashMap<>();
	private Map<SQLTarget, List<Record>> _rs_delete_map = new HashMap<>();
	private Map<SQLTarget, List<SQLDef>> _uid_sql_map = new HashMap<>();
	private Map<SQLTarget, List<Record>> _zhangye_inserts_map = new HashMap<>();

	private Vector<ISQLExecuteSuccess> isqlExecuteSuccesses = new Vector<>();
	private Vector<ISQLExecuteFailed> isqlExecuteFaileds = new Vector<>();

	private Boolean status;

	public Boolean getStatus() {
		return status;
	}

	public void addEDBSucessCallback(ISQLExecuteSuccess success) {
		isqlExecuteSuccesses.add(success);

	}

	public void addEDBFailCallback(ISQLExecuteFailed failed) {
		isqlExecuteFaileds.add(failed);
	}

	// sb,用于记录系统执行情况
	private StringBuffer sb = new StringBuffer();

	public SQLExecutor() {
		super();
	}

	public String getLog() {
		if (!this.hasUpdate()) {
			return "";
		}

		this.dump();
		sb.append("=======任务执行结束==========================================\n");
		return sb.toString();
	}

	/**
	 * 输出dump部分数据
	 * 
	 * @return
	 */
	private void dump() {
		sb.append("\n");
		sb.append("-------------dump start-------------------\n");
		sb.append("【_rs_inserts_map  start】\n");
		sb.append("count:").append(_rs_inserts_map.size()).append("\n");
		for (SQLTarget target : _rs_inserts_map.keySet()) {
			sb.append("SQLTarget::").append("\n");
			sb.append(target.toFormatString());
			sb.append("\n");
			sb.append("Data::").append("\n");
			sb.append(this.formatRecordList(_rs_inserts_map.get(target))).append("】\n");
			sb.append("\n");
		}
		sb.append("【_rs_inserts_map  end】 \n");
		sb.append("【_rs_update_map start】 \n");
		sb.append("count:").append(_rs_update_map.size()).append("\n");
		for (SQLTarget target : _rs_update_map.keySet()) {
			sb.append("SQLTarget::").append("\n");
			sb.append(target.toFormatString()).append("\n");
			sb.append("Data::").append("\n");
			sb.append(this.formatRecordList(_rs_update_map.get(target)));
			sb.append("\n");
		}
		sb.append("【_rs_update_map end】 \n");
		sb.append("【_rs_delete_map start】 \n");
		sb.append("count:").append(_rs_delete_map.size()).append("\n");
		for (SQLTarget target : _rs_delete_map.keySet()) {
			sb.append("SQLTarget::").append("\n");
			sb.append(target.toFormatString());
			sb.append("\n");
			sb.append("Data::").append("\n");
			sb.append(this.formatRecordList(_rs_delete_map.get(target)));
			sb.append("\n");
		}
		sb.append("【_rs_delete_map end】 \n");
		sb.append("【_uid_sql_map start】 \n");
		sb.append("count:").append(_uid_sql_map.size()).append("\n");
		for (SQLTarget target : _uid_sql_map.keySet()) {
			sb.append("SQLTarget::").append("\n");
			sb.append(target.toFormatString()).append("\n");
			sb.append("Data::").append("\n");
			sb.append(this.formatSQLDefList(_uid_sql_map.get(target))).append("\n");
		}
		sb.append("【_uid_sql_map end】 \n");
		sb.append("--------------dump end---------------------\n");
		sb.append("\n");
	}

	private String formatSQLDefList(List<SQLDef> list) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("[").append("\n");
		for (SQLDef s : list) {
			sbuf.append(JsonKit.toJson(s)).append("\n");
		}
		sbuf.append("]").append("\n");
		return sbuf.toString();
	}

	private String formatRecordList(List<Record> list) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("[").append("\n");
		for (Record r : list) {
			sbuf.append(JsonKit.toJson(r)).append("\n");
		}
		sbuf.append("]").append("\n");
		return sbuf.toString();
	}

	/**
	 * 几条EDB中所用的数据
	 * 
	 * @return
	 */
	public boolean submit() {
		if (!this.hasUpdate()) {
			this.status = false;// 什么都没做，表示状态为false
			return true;
		}
		sb.append("=======开始执行提交任务==========================================\n");
		String taskID = UUIDUtil.newUUID();
		sb.append("任务ID:").append(taskID).append("\n");
		sb.append("任务启动时间:").append(DateTimeUtil.formatDateTime(new Date())).append("\n");
		long cur = System.currentTimeMillis();
		sb.append("启动时间(毫秒):").append(cur).append("\n");
		sb.append("线程ID:").append(Thread.currentThread().getId()).append("\n");
		// 已连接为单位进行数据的提交
		Set<String> dsList = getDataSouces();
		if (dsList.size() <= 0) {
			this.status = false;
			return false;
		}
		List<SubmitResult> results = new ArrayList<>();
		for (String conn : dsList) {
			sb.append("数据源：").append(conn).append("启动预处理...\n");
			SubmitResult result = submitTransaction(conn, DbKit.DEFAULT_TRANSACTION_LEVEL);
			if (result == null) {
				this.rollback(results);
				this.status = false;
				return false;
			}
			results.add(result);
			if (!result.result) {// 结果有问题
				sb.append("数据源：").append(conn).append("预处理失败，准备回滚\n");
				this.rollback(results);
				this.status = false;// 事物提交失败，标识执行状态为false
				return false;
			} else {
				sb.append("数据源：").append(conn).append("预处理成功，等待提交\n");
			}
			sb.append("数据源：").append(conn).append("预处理结束...\n");
		}
		sb.append("所用的数据源预处理成功,启动事务提交进程\n");
		this.commits(results);// 提交所有的事务啦
		sb.append("任务ID:").append(taskID).append("执行结束").append("\n");
		long end = System.currentTimeMillis();
		sb.append("结束时间(毫秒):").append(end).append("\n");
		sb.append("本次任务共耗时(毫秒)：").append(end - cur).append("\n");
		sb.append("=====================================================\n");
		//
		this.callbackSucess();
		if (_zhangye_inserts_map.size() > 0) {
			Map<SQLTarget, List<Record>> task_list = new HashMap<>();
			for (SQLTarget target : _zhangye_inserts_map.keySet()) {
				task_list.put(target, _zhangye_inserts_map.get(target));
			}
			AccountTaskDescr task = new AccountTaskDescr();
			task.addTask(task_list);
			AccountManagerPlugin.addAccountTaskDescr(task);
		}
		this.status = true;// 提交成功
		return true;// 事务都执行成功
	}

	
	/**
	 * 开启事务，准备提交，用于重试
	 * 
	 * @param result
	 * @param connName
	 * @param transactionLevel
	 */
	public void submit(SubmitResult retrySubmitResult) {
		Boolean autoCommit = null;
		Connection connection = null;
		Config config = null;
		try {
			// 获取当前的连接
			if (SQLTarget.DEFAULT_CONN_NAME.equals(retrySubmitResult.getDataSource())) {
				config = DbKit.getConfig();
			} else {
				config = DbKit.getConfig(retrySubmitResult.getDataSource());// 获取对应的链接
			}
			connection = config.getConnection();// 获取对应的链接
			autoCommit = connection.getAutoCommit();
			connection.setTransactionIsolation(DbKit.DEFAULT_TRANSACTION_LEVEL);
			connection.setAutoCommit(false);
			config.setThreadLocalConnection(connection);
			retrySubmitResult.setConfig(config);//
			retrySubmitResult.setConnection(connection);
			retrySubmitResult.setOldAutoCommit(autoCommit);
			//预处理
			boolean flag = this.process(retrySubmitResult.getDataSource());
			if (flag) {// 处理成功
				retrySubmitResult.setResult(flag);// 处理成功，准备提交
				if (!this.commit(retrySubmitResult)) {// 提交不成功
					this.addRetryQueue(retrySubmitResult);// 追加到重试队列
				}else{
					retrySubmitResult.getSqlExecutor().clear();
				}
			} else {// 处理失败，需要回滚
				this.rollback(retrySubmitResult);
				this.addRetryQueue(retrySubmitResult);// 追加到重试队列
			}
		} catch (Exception e) {// 出现异常
			LogKit.error(e.getMessage(), e);
		}

	}
	
	
	private void callbackSucess() {
		for (ISQLExecuteSuccess ises : this.isqlExecuteSuccesses) {
			try {
				ises.execute();
			} catch (Exception e) {
				LogKit.error(e.getMessage(), e);
			}

		}
	}

	private void callbackFail() {
		for (ISQLExecuteFailed isef : this.isqlExecuteFaileds) {
			try {
				isef.execute();
			} catch (Exception e) {
				LogKit.error(e.getMessage(), e);
			}

		}
	}

	private boolean hasUpdate() {
		return _rs_inserts_map.size() > 0 || _rs_update_map.size() > 0 || _rs_delete_map.size() > 0
				|| _uid_sql_map.size() > 0;
	}

	/**
	 * 成功需要提交数据
	 * 
	 * @param results
	 */
	private void commits(List<SubmitResult> results) {
		for (SubmitResult result : results) {
			sb.append("数据源:").append(result.getDataSource()).append("开始提交事务......\n");
			boolean flag = this.commit(result);
			sb.append("数据源:").append(result.getDataSource()).append("事务执行结果:" + (flag ? "成功" : "失败") + "......\n");
		}
	}

	/**
	 * 提交最后的结果
	 *
	 * @param result
	 */
	private boolean commit(SubmitResult result) {
		Config config = result.getConfig();
		Connection connection = result.getConnection();
		Boolean autoCommit = result.getOldAutoCommit();
		try {
			connection.commit();
			return true;
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			try {
				if(result.getRetryCount()==0){//非重试
					// 出现异常
					SQLExecutor retrySqlExecutor = this.duplicate();// 复制副本
					retrySqlExecutor.pack(result.getDataSource());// 收缩副本
					SubmitResult retrySubmitResult = new SubmitResult(retrySqlExecutor);
					retrySubmitResult.setDataSource(result.getDataSource());
					this.addRetryQueue(retrySubmitResult);// 添加任务队列
				}else{//重试
					this.addRetryQueue(result);// 添加任务队列
				}
			} catch (Exception e1) {
				LogKit.error(e1.getLocalizedMessage(), e1);
				e1.printStackTrace();
			}
			return false;
		} finally {
			try {
				if (connection != null) {
					if (autoCommit != null)
						connection.setAutoCommit(autoCommit);
					connection.close();// 关闭链接
				}
			} catch (Throwable t) {
				LogKit.error(t.getMessage(), t);
			} finally {
				config.removeThreadLocalConnection();
			}
		}

	}

	private SQLExecutor duplicate() {
		SQLExecutor result = new SQLExecutor();
		result._rs_inserts_map.putAll(this._rs_inserts_map);// 复制
		result._rs_update_map.putAll(this._rs_update_map);// 复制
		result._rs_delete_map.putAll(this._rs_delete_map);// 复制
		result._uid_sql_map.putAll(this._uid_sql_map);// 复制
		return result;
	}

	/**
	 * 回滚所有的结果
	 * 
	 * @param results
	 */
	private void rollback(List<SubmitResult> results) {
		for (SubmitResult result : results) {
			this.rollback(result);
		}
		// 提交预定义的错误代码...
		this.callbackFail();
	}

	/**
	 * 回滚执行的结果
	 * 
	 * @param result
	 */
	private void rollback(SubmitResult result) {
		Config config = result.getConfig();
		Connection connection = result.getConnection();
		Boolean autoCommit = result.getOldAutoCommit();
		try {
			connection.rollback();
			sb.append("数据源：").append(result.getDataSource()).append("回滚成功\n");
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			sb.append("数据源：").append(result.getDataSource()).append("回滚失败,原因：").append(e.getLocalizedMessage());
		} finally {
			try {
				if (connection != null) {
					if (autoCommit != null)
						connection.setAutoCommit(autoCommit);
					connection.close();// 关闭链接
				}
			} catch (Throwable t) {
				LogKit.error(t.getMessage(), t);
			} finally {
				config.removeThreadLocalConnection();
			}
		}
	}

	

	private void addRetryQueue(SubmitResult result) {
		if (result.getSqlExecutor() == null)
			return;
		if (result.getSqlExecutor()._rs_delete_map.size() == 0 
				&& result.getSqlExecutor()._rs_inserts_map.size() == 0
				&& result.getSqlExecutor()._rs_update_map.size() == 0
				&& result.getSqlExecutor()._uid_sql_map.size() == 0)
			return;
		//还原对象状态
		result.setResult(null);
		result.setConfig(null);
		result.setOldAutoCommit(null);
		result.setConnection(null);
		result.setRetryCount(result.getRetryCount() + 1);// 重试计数加一
		SQLExecutorManager.instance().pushRetrySubmitResult(result);

	}

	/**
	 * 对sqlexectuor收缩，使其仅包含
	 * 
	 * @param dataSource,删除不是只想dataSouce的连接
	 * 
	 */
	private void pack(String dataSource) {
		// _rs_inserts_map,_rs_update_map,_rs_delete_map,_uid_sql_map,_zhangye_inserts_map

		for (SQLTarget target : this._rs_inserts_map.keySet()) {
			if (!target.getConnName().equals(dataSource)) {
				this._rs_inserts_map.remove(target);// 删除值
			}
		}

		for (SQLTarget target : this._rs_update_map.keySet()) {
			if (!target.getConnName().equals(dataSource)) {
				this._rs_update_map.remove(target);// 删除值
			}
		}

		for (SQLTarget target : this._rs_delete_map.keySet()) {
			if (!target.getConnName().equals(dataSource)) {
				this._rs_delete_map.remove(target);// 删除值
			}
		}

		for (SQLTarget target : this._uid_sql_map.keySet()) {
			if (!target.getConnName().equals(dataSource)) {
				this._uid_sql_map.remove(target);// 删除值
			}
		}

		this._zhangye_inserts_map.clear();// 清空之
	}

	/**
	 * 开启事务，准备提交
	 * 
	 * @param connName
	 * @param transactionLevel
	 * @return
	 */
	private SubmitResult submitTransaction(String connName, int transactionLevel) {
		Boolean autoCommit = null;
		Connection connection = null;
		Config config = null;
		SubmitResult result = new SubmitResult(this);
		result.setDataSource(connName);
		try {
			// 获取当前的连接
			if (SQLTarget.DEFAULT_CONN_NAME.equals(connName)) {
				config = DbKit.getConfig();
			} else {
				config = DbKit.getConfig(connName);// 获取对应的链接
			}
			connection = config.getConnection();// 获取对应的链接
			autoCommit = connection.getAutoCommit();
			connection.setTransactionIsolation(transactionLevel);
			connection.setAutoCommit(false);
			config.setThreadLocalConnection(connection);
			result.setConfig(config);
			result.setConnection(connection);
			result.setOldAutoCommit(autoCommit);
			/**
			 * 下面的错着都在事务中拉
			 */
			boolean flag = this.process(connName);
			result.setResult(flag);
			/**
			 * 事务执行完毕需要确保下列动作 if (connection != null) { if (autoCommit != null)
			 * connection.setAutoCommit(autoCommit); connection.close();//关闭链接
			 * config.removeThreadLocalConnection(); // prevent memory leak }
			 * 
			 */
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			return null;
		}
		return result;
	}

	private boolean process(String connName) {
		sb.append("处理:_rs_inserts_map集合\n");
		for (SQLTarget target : _rs_inserts_map.keySet()) {
			if (!target.getConnName().equals(connName))
				continue;
			if (connName == SQLTarget.DEFAULT_CONN_NAME && interrupter_table_operator.contains(target.getTableName())) {// 需要拦截的保存操作
				if (_zhangye_inserts_map.containsKey(target)) {
					_zhangye_inserts_map.get(target).addAll(_rs_inserts_map.get(target));
				} else {
					_zhangye_inserts_map.put(target, _rs_inserts_map.get(target));
				}
				continue;
			}
			if (!this.processInsertMap(target, _rs_inserts_map.get(target), connName)) {
				return false;
			}
		}
		sb.append("处理:_rs_inserts_map集合处理完成\n");

		sb.append("处理:_rs_update_map集合\n");
		for (SQLTarget target : _rs_update_map.keySet()) {
			if (!target.getConnName().equals(connName))
				continue;
			if (!this.processUpdateMap(target, _rs_update_map.get(target), connName)) {
				return false;
			}
		}
		sb.append("处理:_rs_update_map集合完成\n");

		sb.append("处理:_rs_delete_map集合\n");
		for (SQLTarget target : _rs_delete_map.keySet()) {
			if (!target.getConnName().equals(connName))
				continue;
			if (!this.processDeleteMap(target, _rs_delete_map.get(target), connName)) {
				return false;
			}
		}
		sb.append("处理:_rs_delete_map集合完成\n");

		sb.append("处理:_uid_sql_map集合\n");

		// private Map<SQLTarget, List<SQLDef>> _uid_sql_map = new HashMap<>();

		for (SQLTarget target : _uid_sql_map.keySet()) {
			if (!target.getConnName().equals(connName))
				continue;
			List<SQLDef> sqldefines = _uid_sql_map.get(target);
			// sort
			sqldefines.sort((sql1, sql2) -> {
				String s1 = sql1.getSQL().trim().toLowerCase();
				String s2 = sql2.getSQL().trim().toLowerCase();
				int f1 = -1;
				int f2 = -1;
				if (s1.startsWith("insert")) {
					f1 = 0;
				} else if (s1.startsWith("update")) {
					f1 = 1;
				} else if (s1.startsWith("delete")) {
					f1 = 2;
				}
				if (s2.startsWith("insert")) {
					f2 = 0;
				} else if (s2.startsWith("update")) {
					f2 = 1;
				} else if (s2.startsWith("delete")) {
					f2 = 2;
				}
				if (f1 > f2) {
					return 1;
				} else if (f1 == f2) {
					return 0;
				} else {
					return -1;
				}
			});

			if (!this.processUIDMap(target, sqldefines, connName)) {
				return false;
			}
		}
		sb.append("处理:_uid_sql_map集合完成\n");

		return true;
	}

	private boolean processUIDMap(SQLTarget target, List<SQLDef> list, String connName) {
		if (list.size() == 0)
			return true;
		try {
			DbPro dbPro = null;
			if (SQLTarget.DEFAULT_CONN_NAME.equals(connName)) {
				dbPro = DbPro.use();
			} else {
				dbPro = DbPro.use(connName);
			}
			if (dbPro == null)
				return false;
			// 执行业务逻辑
			for (SQLDef sqlDef : list) {
				if (sqlDef.getSqlType() == SQLType.PSQL) {// 简单参数化SQL
					dbPro.update(sqlDef.getSQL(), sqlDef.getParams());
				} else if (sqlDef.getSqlType() == SQLType.PASQL) {
					dbPro.batch(sqlDef.getSQL(), sqlDef.getaParams(), 300);
				} else if (sqlDef.getSqlType() == SQLType.MSQL) {
					dbPro.batch(sqlDef.getSqls(), 300);
				} else if (sqlDef.getSqlType() == SQLType.LSQL) {
					dbPro.batch(sqlDef.getSQL(), sqlDef.getColumns(), sqlDef.getModelOrRecordList(), 300);
				} else {
					dbPro.update(sqlDef.getSQL());
				}
			}
			return true;
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			sb.append("处理:_rs_uid_map集合处理过程中出现异常,原因:").append(e.getLocalizedMessage()).append("\n");
			return false;
		}

	}

	private boolean processDeleteMap(SQLTarget target, List<Record> list, String connName) {
		if (list.size() == 0)
			return true;
		try {
			DbPro dbPro = null;
			if (SQLTarget.DEFAULT_CONN_NAME.equals(connName)) {
				dbPro = DbPro.use();
			} else {
				dbPro = DbPro.use(connName);
			}
			if (dbPro == null)
				return false;
			for (Record r : list) {
				if (StringUtil.isEmpty(target.getKey())) {
					dbPro.delete(target.getTableName(), r);
				} else {
					dbPro.delete(target.getTableName(), target.getKey(), r);
				}
			}
			return true;
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			sb.append("处理:_rs_delete_map集合处理过程中出现异常,原因:").append(e.getLocalizedMessage()).append("\n");
			return false;
		}
	}

	private boolean processUpdateMap(SQLTarget target, List<Record> list, String connName) {
		if (list.size() == 0)
			return true;
		try {
			DbPro dbPro = null;
			if (SQLTarget.DEFAULT_CONN_NAME.equals(connName)) {
				dbPro = DbPro.use();
			} else {
				dbPro = DbPro.use(connName);
			}
			if (dbPro == null)
				return false;
			if (list.size() == 1) {
				if (StringUtil.isEmpty(target.getKey())) {
					return dbPro.update(target.getTableName(), list.get(0));
				} else {
					return dbPro.update(target.getTableName(), target.getKey(), list.get(0));
				}
			} else {
				if (StringUtil.isEmpty(target.getKey())) {
					dbPro.batchUpdate(target.getTableName(), list, 300);// (target.getTableName(),

				} else {
					dbPro.batchUpdate(target.getTableName(), target.getKey(), list, 300);// (target.getTableName(),
				}
			}
			return true;
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			sb.append("处理:_rs_updates_map集合处理过程中出现异常,原因:").append(e.getLocalizedMessage()).append("\n");
			return false;
		}
	}

	/**
	 * 处理插入
	 * 
	 * @param target
	 * @param list
	 * @param connName
	 * @return
	 */
	private boolean processInsertMap(SQLTarget target, List<Record> list, String connName) {
		if (list.size() == 0)
			return true;
		try {
			DbPro dbPro = null;
			if (SQLTarget.DEFAULT_CONN_NAME.equals(connName)) {
				dbPro = DbPro.use();
			} else {
				dbPro = DbPro.use(connName);
			}
			if (dbPro == null)
				return false;
			if (list.size() == 1) {
				if (StringUtil.isEmpty(target.getKey())) {
					return dbPro.save(target.getTableName(), list.get(0));
				} else {
					return dbPro.save(target.getTableName(), target.getKey(), list.get(0));
				}
			} else {
				dbPro.batchSave(target.getTableName(), list, 300);
				return true;
			}
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			sb.append("处理:_rs_inserts_map集合处理过程中出现异常,原因:").append(e.getLocalizedMessage()).append("\n");
			return false;
		}

	}

	/**
	 * 获取涉及的所有数据源
	 * 
	 * @return
	 */
	private Set<String> getDataSouces() {
		HashSet<String> result = new HashSet<>();
		for (SQLTarget sqlTarget : _rs_inserts_map.keySet()) {
			result.add(sqlTarget.getConnName());
		}
		for (SQLTarget sqlTarget : _rs_update_map.keySet()) {
			result.add(sqlTarget.getConnName());
		}
		for (SQLTarget sqlTarget : _rs_delete_map.keySet()) {
			result.add(sqlTarget.getConnName());
		}
		for (SQLTarget sqlTarget : _uid_sql_map.keySet()) {
			result.add(sqlTarget.getConnName());
		}
		return result;
	}

	/**
	 * 添加缓冲执行的update SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addUIDSQL(SQLTarget target, SQLDef sql) {
		if (!this._uid_sql_map.containsKey(target)) {
			this._uid_sql_map.put(target, new ArrayList<>());
		}
		this._uid_sql_map.get(target).add(sql);
	}

	/**
	 * 批量添加缓冲执行的update SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addUIDSQL(SQLTarget target, List<SQLDef> sqls) {
		if (!this._uid_sql_map.containsKey(target)) {
			this._uid_sql_map.put(target, new ArrayList<>());
		}
		this._uid_sql_map.get(target).addAll(sqls);
	}

	/**
	 * 添加缓冲执行的INSERT SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addInsertSQL(SQLTarget target, Record record) {
		if (!this._rs_inserts_map.containsKey(target)) {
			this._rs_inserts_map.put(target, new ArrayList<>());
		}
		this._rs_inserts_map.get(target).add(record);
	}

	/**
	 * 批量添加缓冲执行的INSERT SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addInsertSQL(SQLTarget target, List<Record> records) {
		if (!this._rs_inserts_map.containsKey(target)) {
			this._rs_inserts_map.put(target, new ArrayList<>());
		}
		this._rs_inserts_map.get(target).addAll(records);
	}

	/**
	 * 添加缓冲执行的UPDATE SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addUpdateSQL(SQLTarget target, Record record) {
		if (!this._rs_update_map.containsKey(target)) {
			this._rs_update_map.put(target, new ArrayList<>());
		}
		this._rs_update_map.get(target).add(record);
	}

	/**
	 * 批量添加缓冲执行的UPDATE SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addUpdateSQL(SQLTarget target, List<Record> records) {
		if (!this._rs_update_map.containsKey(target)) {
			this._rs_update_map.put(target, new ArrayList<>());
		}
		this._rs_update_map.get(target).addAll(records);
	}

	/**
	 * 添加缓冲执行的DELETE SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addDeleteSQL(SQLTarget target, Record record) {
		if (!this._rs_delete_map.containsKey(target)) {
			this._rs_delete_map.put(target, new ArrayList<>());
		}
		this._rs_delete_map.get(target).add(record);
	}

	/**
	 * 批量添加缓冲执行的DELETE SQL
	 * 
	 * @param target
	 * @param record
	 */
	public void addDeleteSQL(SQLTarget target, List<Record> records) {
		if (!this._rs_delete_map.containsKey(target)) {
			this._rs_delete_map.put(target, new ArrayList<>());
		}
		this._rs_delete_map.get(target).addAll(records);
	}

	public Map<SQLTarget, List<Record>> get_rs_inserts_map() {
		return _rs_inserts_map;
	}

	public Map<SQLTarget, List<Record>> get_rs_update_map() {
		return _rs_update_map;
	}

	public Map<SQLTarget, List<Record>> get_rs_delete_map() {
		return _rs_delete_map;
	}

	public Map<SQLTarget, List<SQLDef>> get_uid_sql_map() {
		return _uid_sql_map;
	}

	public Map<SQLTarget, List<Record>> get_zhangye_inserts_map() {
		return _zhangye_inserts_map;
	}

	/**
	 * 清理缓存池
	 */
	public void clear() {
		_rs_inserts_map.clear();
		_rs_update_map.clear();
		_rs_delete_map.clear();
		_uid_sql_map.clear();
		_zhangye_inserts_map.clear();
	}

	public static class SubmitResult {
		private Boolean oldAutoCommit;// 数据库连接上原来绑定的autocommit属性
		private Connection connection;// 数据库链接
		private Config config;// 对应的config
		private Boolean result = false;// 执行的结果
		private String dataSource;
		private SQLExecutor sqlExecutor;// 执行的环境
		private int retryCount = 0;

		public SubmitResult(SQLExecutor sqlExecutor) {
			super();
			this.sqlExecutor = sqlExecutor;
		}

		public int getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(int retryCount) {
			this.retryCount = retryCount;
		}

		public SubmitResult(Boolean oldAutoCommit, Connection connection, Config config) {
			super();
			this.oldAutoCommit = oldAutoCommit;
			this.connection = connection;
			this.config = config;
		}

		public Boolean getOldAutoCommit() {
			return oldAutoCommit;
		}

		public void setOldAutoCommit(Boolean oldAutoCommit) {
			this.oldAutoCommit = oldAutoCommit;
		}

		public Connection getConnection() {
			return connection;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}

		public Config getConfig() {
			return config;
		}

		public void setConfig(Config config) {
			this.config = config;
		}

		public Boolean getResult() {
			return result;
		}

		public void setResult(Boolean result) {
			this.result = result;
		}

		public String getDataSource() {
			return dataSource;
		}

		public void setDataSource(String dataSource) {
			this.dataSource = dataSource;
		}

		public SQLExecutor getSqlExecutor() {
			return sqlExecutor;
		}

	}

}
