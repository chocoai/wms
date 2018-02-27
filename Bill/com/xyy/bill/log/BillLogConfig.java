package com.xyy.bill.log;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ListReader;

public class BillLogConfig {
	public static final String BILL_LOG_CONFIG="bill_log_config.xml";
	private static BillLogConfig config;
	
	private List<BillLog> billLogs=new ArrayList<>();
	
	
	public BillLog getBillOptLog(){
		for(BillLog log:billLogs){
			if(log.getLogType()==BillLogType.BillOptLog){
				return log;
			}
		}
		return null;
	}
	
	
	public BillLog getBillFeebackLog(){
		for(BillLog log:billLogs){
			if(log.getLogType()==BillLogType.BillFeedbackLog){
				return log;
			}
		}
		return null;
	}
	
	
	public BillLog getBillMigrationLog(){
		for(BillLog log:billLogs){
			if(log.getLogType()==BillLogType.BillMigrationLog){
				return log;
			}
		}
		return null;
	}
	
	public BillLog getBillMigrationFailLog(){
		for(BillLog log:billLogs){
			if(log.getLogType()==BillLogType.BillMigrationFailLog){
				return log;
			}
		}
		return null;
	}
	
	private BillLogConfig(){
		
	}
	
	
	public static BillLogConfig instance(){
		if(config==null){
			synchronized (BillLogConfig.class) {
				if(config==null){
					config=new BillLogConfig();
					config.init();
				}
			}
		}
		return config;
	}


	@SuppressWarnings("unchecked")
	private void init() {
		try {
			ListReader reader=new ListReader(BillLog.class, "BillLog");
			List<BillLog> bls=(List<BillLog>)reader.readXML(XMLUtil.readXMLFile(BillLogConfig.class.getResource(BILL_LOG_CONFIG)));
			this.billLogs.addAll(bls);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
