package com.xyy.bill.map;

import java.util.List;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ComponentReader;

public class TestMap {
	private static DataSetInstance srcDSI;
	private static DataSetMeta meta;
	private static DruidPlugin druidPlugin;
	private static ActiveRecordPlugin arpMain;
	public static final String _conURL = "jdbc:mysql://172.16.96.25:3306/xyy_erp?characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
	private static final String _username = "root";
	private static final String _pwd = "1qaz123";
	private static BillPlugin billPlugin=new BillPlugin(".");

	public static void main(String[] args) {
		Init();
		meta = buildDataSetMeta();
		srcDSI = new DataSetInstance(meta);
		buildDataSetInstanceForData();
		//System.out.println(srcDSI);
		BillContext context=new BillContext();
		DataSetInstance result=DataMap.dataMap(context,srcDSI,buildBillMap());
		System.out.println(result);
		
		

	}

	private static BillMap buildBillMap() {
		try {
			ComponentReader reader=new ComponentReader(BillMap.class);
			return (BillMap)reader.readXML(XMLUtil.readXMLFile(TestMap.class.getResource("MAP1.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//be89e5f9ccf443dca7524843700cd4de
	private static void buildDataSetInstanceForData() {
		DataTableInstance head=new DataTableInstance(meta.getHeadDataTable());
		List<Record> head_rs=Db.find("select * from "+meta.getHeadDataTable().getRealTableName()+" where billid=?", "be89e5f9ccf443dca7524843700cd4de");
		head.getRecords().addAll(head_rs);
		DataTableInstance body=new DataTableInstance(meta.getTable("caigoudingdan_details"));
		List<Record> dtl_rs=Db.find("select * from xyy_erp_bill_caigoudingdan_details  where billid=?", "be89e5f9ccf443dca7524843700cd4de");
		body.getRecords().addAll(dtl_rs);
		srcDSI.getDtis().add(head);
		srcDSI.getDtis().add(body);
	}

	private static void Init() {
		DruidPlugin druidPlugin = new DruidPlugin(_conURL, _username, _pwd, null);
		druidPlugin.set(3, 1, 5);
		arpMain = new ActiveRecordPlugin(DictKeys.db_dataSource_main, druidPlugin);
		// arp.setTransactionLevel(4);//事务隔离级别
		arpMain.setDevMode(true); // 设置开发模式
		arpMain.setShowSql(true); // 是否显示SQL
		arpMain.setDialect(new MysqlDialect());
		druidPlugin.start();
		arpMain.start();
		billPlugin.start();
		
		
	}

	// 构建采购订单的DataSetMeta
	private static DataSetMeta buildDataSetMeta() {
		try {
			ComponentReader reader=new ComponentReader(DataSetMeta.class);
			return (DataSetMeta)reader.readXML(XMLUtil.readXMLFile(TestMap.class.getResource("Bill_caigoudingdan_DS.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
