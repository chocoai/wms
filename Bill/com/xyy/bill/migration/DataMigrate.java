package com.xyy.bill.migration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.log.BillLogHelper;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.expression.NullRefObject;
import com.xyy.expression.OperatorData;
import com.xyy.expression.services.ExpService;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;


public class DataMigrate {

	public static void dataMigrate(BillContext context) {
		
		// 查找匹配的规则
		DataSetInstance model = (DataSetInstance) context.get("$model");
		if (model == null) {
			return;
		}
		
		DataTableInstance head=model.getHeadDataTableInstance();
		context.set("$head", head);
		
		String srcDataObjectKey = head.getKey();
		if (StringUtil.isEmpty(srcDataObjectKey)) {
			context.addError("999", "未找到源表数据集Key");
			return;
		}
		List<MigrationDef> migrationDefs = BillPlugin.engine.findMigrationDefs(srcDataObjectKey);// 候选迁移集合
		List<MigrationDef> tgtMigrationDefs = new ArrayList<>();// 需要执行的迁移
		for (MigrationDef md : migrationDefs) {
			if (canMigrate(context, md.getDataMigration())) {
				tgtMigrationDefs.add(md);
			}
		}
		if (tgtMigrationDefs.size()==0) return;
		billDataMigrate(context, tgtMigrationDefs);
	}

	/**
	 * 数据表迁移
	 * 
	 * @param tgtDataSetInstance
	 * @param context
	 * @param dataMigration
	 * @return
	 */
	private static void billDataMigrate(BillContext context, List<MigrationDef> tgtMigrationDefs) {
		DataSetInstance srcDsi = (DataSetInstance) context.get("$model");// 获取需要迁移的数据集实例
		Map<SourceTable, List<Record>> sourceTableGroups = new HashMap<>();
		Map<SourceTable, MigrationDef> sourceTableMapMigrationDef = new HashMap<>();
		context.set("$migrationMap", sourceTableMapMigrationDef);
		// 遍历转换对象
		for (MigrationDef migrationDef : tgtMigrationDefs) {
			List<SourceTable> sourceTables = migrationDef.getDataMigration().getSrcTableCollections();
			for (SourceTable sourceTable : sourceTables) {
				if (!sourceTableGroups.containsKey(sourceTable))
					sourceTableGroups.put(sourceTable, new ArrayList<>());
				if (!sourceTableMapMigrationDef.containsKey(sourceTable)) 
					sourceTableMapMigrationDef.put(sourceTable, migrationDef);
				if (sourceTable.getTableKey().equals(srcDsi.getHeadDataTableInstance().getKey())) {
					sourceTableGroups.get(sourceTable).addAll(srcDsi.getHeadDataTableInstance().getRecords());
				} else {
					for (DataTableInstance bodyDti : srcDsi.getBodyDataTableInstance()) {
						if (sourceTable.getTableKey().equals(bodyDti.getKey())) {
							sourceTableGroups.get(sourceTable).addAll(bodyDti.getRecords());
						}
					}
				}
			}
		}

		
		for (SourceTable sourceTable : sourceTableGroups.keySet()) {
			processDataMigrate(context, sourceTable, sourceTableGroups.get(sourceTable));
		}

	}
	
	
	private static Map<String, Lock> MIGRATE_INSERT_LOCK=new ConcurrentHashMap<>();

	/**
	 * 执行数据迁移算法
	 * @param context	
	 * @param sourceTable
	 * @param list
	 */
	private static void processDataMigrate(BillContext context, SourceTable sourceTable, List<Record> list) {
		if(list.size()==0)
			return;
		//制锁
		String lockName=sourceTable.getTargetTableKey();
		if(!MIGRATE_INSERT_LOCK.containsKey(lockName)){
			MIGRATE_INSERT_LOCK.put(lockName, new ReentrantLock());
		}
		//(2)尝试insert
		boolean result = tryInsert(context,sourceTable,list);
		if (result) {
			//(3)update
			migrateUpdate(context,sourceTable,list);
		}
	}

	@SuppressWarnings("unchecked")
	private static void migrateUpdate(BillContext context, SourceTable sourceTable, List<Record> list) {
		Map<SourceTable, MigrationDef> sourceTableMapMigrationDef = (Map<SourceTable, MigrationDef>)context.get("$migrationMap");	
		DataTableInstance head = (DataTableInstance)context.get("$head");
		StringBuffer sql = new StringBuffer();
		StringBuffer UndoSql = new StringBuffer();
		//获取日志记录
		Record logRecord = BillLogHelper.getMigrateOptLog(head.getRecords().get(0).getStr("BillID"),sourceTable.getKey());
		List<SourceField> groupSrcFields = sourceTable.findGroupSrcFileds();//赋值字段
		List<SourceField> groupCalFields = sourceTable.findGroupCalFileds();//需要计算字段
		if (groupCalFields.size()==0) {
			//没有需要计算的字段
			return;
		}
		sql.append("update ").append(getSourceTableRealTableName(sourceTable,sourceTableMapMigrationDef.get(sourceTable))).append(" set ");
		UndoSql.append("update ").append(getSourceTableRealTableName(sourceTable,sourceTableMapMigrationDef.get(sourceTable))).append(" set ");
		for(SourceField field:groupCalFields){
			switch (field.getOpSign()) {
			case AddDelta://加变化量
				sql.append("`").append(field.getTargetFieldKey()).append("`").append("=")
				.append("`").append(field.getTargetFieldKey()).append("`").append("+").append("?").append(",");	
				UndoSql.append("`").append(field.getTargetFieldKey()).append("`").append("=")
				//回滚sql
				.append("`").append(field.getTargetFieldKey()).append("`").append("+").append("?").append(",");
				break;
			case Assign://赋直接值
				if (field.getIsNegtive()) {
					UndoSql.append(field.getTargetFieldKey()).append("`").append("=").append("`").append(field.getTargetFieldKey()).append("`")
					.append("+").append("?").append(",");
				}
				sql.append("`").append(field.getTargetFieldKey()).append("`").append("=")
				.append("?").append(",");	
				break;
			case AddValue://加直接量
				sql.append("`").append(field.getTargetFieldKey()).append("`").append("=")
				.append("`").append(field.getTargetFieldKey()).append("`").append("+").append("?").append(",");	
				//回滚sql
				UndoSql.append("`").append(field.getTargetFieldKey()).append("`").append("=")
				.append("`").append(field.getTargetFieldKey()).append("`").append("+").append("?").append(",");	
				break;
			default:
				break;
			}
			
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where 1=1 ");
		UndoSql.deleteCharAt(UndoSql.length()-1);
		UndoSql.append(" where 1=1 ");
		for(SourceField field:groupSrcFields){
			sql.append(" and `").append(field.getTargetFieldKey()).append("`=?");
			UndoSql.append(" and `").append(field.getTargetFieldKey()).append("`=?");
		}
		int row=0;
		int col=0;
		Object[][] params=new Object[list.size()][groupSrcFields.size()+groupCalFields.size()];
		Object[][] undoParams=new Object[list.size()][groupSrcFields.size()+groupCalFields.size()];
		//计算分组字段值
		Set<Map<String, Object>> groupValues=keepGroupValues(context,list,groupSrcFields);
		//计算变化量值
		Set<Map<String, Object>> groupCalValues=keepGroupValues(context,list,groupCalFields);
		//放置变化量，日志记录使用
		JSONArray array = new JSONArray();
			for(Map<String, Object> item:groupCalValues){
				JSONObject json = new JSONObject();
				for(SourceField field:groupCalFields){
					Object value =null;
					Object undoValue=null;
					switch (field.getOpSign()) {
					case AddDelta:// 加变化量
						if (field.getIsNegtive()) {
							value = getNegtiveValue(getVariableValue(item, field.getTargetFieldKey(), logRecord));
							undoValue = getVariableValue(item, field.getTargetFieldKey(), logRecord);
						}else{
							value = getVariableValue(item, field.getTargetFieldKey(), logRecord);
							undoValue = getNegtiveValue(getVariableValue(item, field.getTargetFieldKey(), logRecord));
						}
						break;
					case Assign:// 赋直接值
						if (field.getIsNegtive()) {
							value = getNegtiveValue(item.get(field.getTargetFieldKey()));
							undoValue = item.get(field.getTargetFieldKey());
						}else {
							value = item.get(field.getTargetFieldKey());
							undoValue = getNegtiveValue(item.get(field.getTargetFieldKey()));
						}
						break;
					case AddValue:// 加直接量
						if (field.getIsNegtive()) {
							value = getNegtiveValue(item.get(field.getTargetFieldKey()));
							undoValue = item.get(field.getTargetFieldKey());
						}else {
							value = item.get(field.getTargetFieldKey());
							undoValue = getNegtiveValue(item.get(field.getTargetFieldKey()));
						}
						break;
					default:
						break;
					}
					//记录变化量信息
					json.put(field.getTargetFieldKey(), item.get(field.getTargetFieldKey()));
					json.put("OpSign", field.getOpSign());
					json.put("IsNegtive", field.getIsNegtive());
					params[row][col]=value;
					undoParams[row][col]=undoValue;
					col++;
				}
				//放置变化量，日志记录使用
				JSONObject object = new JSONObject();
				object.put(item.get("_recordID").toString(), json);
				array.add(object);
				row++;
				col=0;
			}
			row=0;col=groupCalFields.size();
			for(Map<String, Object> item:groupValues){
				for(SourceField field:groupSrcFields){
					params[row][col]=item.get(field.getTargetFieldKey());
					undoParams[row][col]=item.get(field.getTargetFieldKey());
					col++;
				}
				row++;
				col=groupCalFields.size();
			}
			boolean result = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					try {
						//更新迁移表
						Db.batch(sql.toString(), params, 100);
						return true;
					} catch (Exception e) {
						return false;
					}
				}
			});
			if (result) {
				//保存日志信息
				saveDatamigrateLog(context,sourceTable,sql,UndoSql,params,undoParams,array);
			}else{
				//保存失败，放入 重试队列等待重试
				Integer timer = context.getInteger("$timer");
				if (timer == null) {
					context.set("$timer", 1);
				}else{
					context.set("$timer", timer+1);
				}
				BillMigratePlugin.addRetryDataTask(context);
			}
	}

	/**
	 * 保存日志信息
	 * @param context
	 * @param sourceTable
	 * @param sql
	 * @param undoSql 
	 * @param params
	 * @param undoParams
	 * @param array
	 */
	private static void saveDatamigrateLog(BillContext context, SourceTable sourceTable, StringBuffer sql, StringBuffer undoSql, Object[][] params,
			Object[][] undoParams, JSONArray array) {
		
		//处理params参数
		Record record = new Record();
		DataTableInstance head = (DataTableInstance)context.get("$head");
		record.set("sourceID", head.getRecords().get(0).getStr("BillID"));
		record.set("sourceTableKey", sourceTable.getKey());
		record.set("doSql", sql.toString());
		record.set("doParams", Arrays.deepToString(params));
		record.set("undoSql", undoSql.toString());
		record.set("undoParams", Arrays.deepToString(undoParams));
		record.set("variable", array.toJSONString());
		record.set("createtime", new Timestamp(System.currentTimeMillis()));
		record.set("longtime", System.currentTimeMillis());
		if (context.get("user") != null) {
			record.set("user", context.get("user").toString());
		}
		
		Db.save(BillLogHelper.getDataMigrationLogTableName(head.getRecords().get(0).getStr("BillID")), record);
	}

	/**
	 * 获取变化量
	 * @param item
	 * @param targetFieldKey
	 * @param logRecord
	 * @return
	 */
	private static Object getVariableValue(Map<String, Object> item, String targetFieldKey, Record logRecord) {
		JSONArray array = logRecord.get("variable");
		if (array==null) {
			return null;
		}
		Object value = null;
		for (Object object : array) {
			JSONObject json = JSONObject.parseObject(object.toString());
			JSONObject o = json.getJSONObject(item.get(item.get("_recordID")).toString());
			if(o != null){
				value = o.get(targetFieldKey);
			}
		}
		value = item.get(targetFieldKey)+"+"+getNegtiveValue(value);
		return value;
	}

	//int,long,float,double
	private static Object getNegtiveValue(Object object) {
		if(object==null)
			return null;
		if(object.getClass()==int.class || object.getClass()==Integer.class){
			return -(int)object;
		}else if(object.getClass() == float.class || object.getClass()==Float.class){
			return -(float)object;
		}else if(object.getClass() == double.class || object.getClass() == Double.class){
			return -(double)object;
		}else if(object.getClass() == long.class || object.getClass() == Long.class){
			return -(long)object;
		}else if(object.getClass() == BigDecimal.class){
			return new BigDecimal(object.toString()).negate();
		}else {
			return object;
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static boolean tryInsert(BillContext context,SourceTable sourceTable, List<Record> records) {
		boolean result = false;
		Lock lock=MIGRATE_INSERT_LOCK.get(sourceTable.getTargetTableKey());
		try {
			lock.lock();
			//尝试计算分组制	
			List<SourceField> groupSourceFields=sourceTable.findGroupSrcFileds();
			Set<Map<String, Object>> groupValues=calcGroupValues(context,records,groupSourceFields);
			
			Map<SourceTable, MigrationDef> sourceTableMapMigrationDef = (Map<SourceTable, MigrationDef>)context.get("$migrationMap");	
			StringBuffer sb=new StringBuffer();
			sb.append("select * from ").append(getSourceTableRealTableName(sourceTable,sourceTableMapMigrationDef.get(sourceTable)));
			sb.append(" where 1=1 ");
			Set<Map<String, Object>> groupInsertValues=new HashSet<>();
			
			for(Map<String, Object> map:groupValues){
				StringBuffer where=new StringBuffer();
				for(Map.Entry<String, Object> item:map.entrySet()){
					where.append(" and ").append(item.getKey()).append("='").append(item.getValue()).append("'");
				}
				Record cuRecord=Db.findFirst(sb.toString()+where.toString());
				if(cuRecord==null)
					groupInsertValues.add(map);
			}
			
			sb.delete(0, sb.length());
			sb.append("insert into ").append(getSourceTableRealTableName(sourceTable,sourceTableMapMigrationDef.get(sourceTable))).append("(");
			sb.append("OID,createTime,");
			for(SourceField field:groupSourceFields){
				
				sb.append("`").append(field.getTargetFieldKey()).append("`").append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(") ").append("values( ?,?,");
			for(SourceField field:groupSourceFields){
				sb.append("?").append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(") ");
			Object[][] params=new Object[groupInsertValues.size()][groupSourceFields.size()+2];
			int row=0;
			int col=2;
			for(Map<String, Object> item:groupInsertValues){
				params[row][0]=UUIDUtil.newUUID();
				params[row][1]=new Timestamp(System.currentTimeMillis());
				for(SourceField field:groupSourceFields){
					params[row][col]=item.get(field.getTargetFieldKey());
					col++;
				}
				row++;
				col=2;
			}
			Db.batch(sb.toString(), params, 30);
			result = true;
				
		} catch (Exception e) {
			e.printStackTrace();
			Integer timer = context.getInteger("$timer");
			if (timer == null) {
				context.set("$timer", 1);
			}else{
				context.set("$timer", timer+1);
			}
			BillMigratePlugin.addRetryDataTask(context);
			result = false;
		}
		finally {
			lock.unlock();
		}
		return result;
	}
	/**
	 * 计算字段值
	 * @param context
	 * @param records
	 * @param groupSourceFields
	 * @return
	 */
	private static Set<Map<String, Object>> calcGroupValues(BillContext context,List<Record> records, List<SourceField> groupSourceFields) {
		Set<Map<String, Object>> result = new HashSet<>();

		for(Record r:records){
			Map<String, Object> item=new HashMap<>();
			for(SourceField field:groupSourceFields){
				switch (field.getType()) {
				case Field:
					item.put(field.getTargetFieldKey(), r.get(field.getDefinition()));
					break;
				case Const:
					item.put(field.getTargetFieldKey(),field.getDefinition());
					break;
				case Formula:
					item.put(field.getTargetFieldKey(),calFormualValue(context,r,field.getDefinition()));						
					break;
				default:
					break;
				}
				
			}
			result.add(item);
		}
		return result;
	}
	
	private static Set<Map<String, Object>> keepGroupValues(BillContext context,List<Record> records, List<SourceField> groupSourceFields) {
		Set<Map<String, Object>> result = new HashSet<>();

		for(Record r:records){
			Map<String, Object> item=new HashMap<>();
			for(SourceField field:groupSourceFields){
				switch (field.getType()) {
				case Field:
					item.put(field.getTargetFieldKey(), r.get(field.getDefinition()));
					break;
				case Const:
					item.put(field.getTargetFieldKey(),field.getDefinition());
					break;
				case Formula:
					item.put(field.getTargetFieldKey(),calFormualValue(context,r,field.getDefinition()));						
					break;
				default:
					break;
				}
				if (!StringUtil.isEmpty( r.getStr("BillDtlID"))) {
					item.put("_recordID",r.getStr("BillDtlID"));
				}else if (!StringUtil.isEmpty( r.getStr("BillID"))) {
					item.put("_recordID",r.getStr("BillID"));
				}
			}
			result.add(item);
		}
		return result;
	}

	private static Object calFormualValue(BillContext context, Record r,String expr) {
		try {
			context.save();
			DataTableInstance head=(DataTableInstance)context.get("$head");
			context.set("_", head.getRecords().get(0).getColumns());
			context.addAll(r.getColumns());
			OperatorData od=ExpService.instance().calc(expr, context);
			if(od.clazz!=NullRefObject.class)
				return od.value;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			context.restore();
		}
		
		return null;
	}

	private static String getSourceTableRealTableName(SourceTable sourceTable,MigrationDef migrationDef){
		String targetTableKey=sourceTable.getTargetTableKey();
		DataTableMeta dataTableMeta=migrationDef.getDataSet().getTable(targetTableKey);

		return dataTableMeta.getRealTableName();
	}

	private static boolean canMigrate(BillContext context, DataMigration dataMigration) {
		DataSetInstance model = (DataSetInstance) context.get("$model");
		if (model != null) {
			DataTableInstance head = model.getHeadDataTableInstance();
			if (head != null && head.getRecords().size() == 1 && matchBillStatus(head, dataMigration)) {
				return true;
			}
		}
		return false;
	}

	private static boolean matchBillStatus(DataTableInstance head, DataMigration dataMigration) {
		int curStatus = head.getRecords().get(0).getInt("status");
		try {
			return Integer.parseInt(dataMigration.getBillStatus()) == curStatus;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 保存迁移失败的日志记录
	 * @param context
	 */
	public static void saveMigrateFailLog(BillContext context) {
		// 查找匹配的规则
		DataSetInstance model = (DataSetInstance) context.get("$model");
		if (model == null) {
			return;
		}
		
		DataTableInstance head=model.getHeadDataTableInstance();
		
		Record record = new Record();
		record.set("sourceID", head.getRecords().get(0).getStr("BillID"));
		record.set("sourceKey", head.getKey());
		record.set("createtime", new Timestamp(System.currentTimeMillis()));
		
		Db.save(BillLogHelper.getDataMigrationLogTableName(head.getRecords().get(0).getStr("BillID")), record);
	}
}
