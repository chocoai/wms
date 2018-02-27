package com.xyy.bill.log;


import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;

/**
 * 单据日志配置
 * <BillLog  LogType="BillOptLog" LogTable="tb_system_bill_log" SharingCount="30"/>
 * @author evan
 *
 */
@XmlComponent(tag="BillLog",type=BillLog.class)
public class BillLog {
	private BillLogType logType;
	private String logTable;
	private int sharingCount;
	private String createSQL;
		
	@XmlElement(name="createSQL",tag="CreateSQL")
	public String getCreateSQL() {
		return createSQL;
	}
	public void setCreateSQL(String createSQL) {
		this.createSQL = createSQL;
	}
	@XmlAttribute(name="logType",tag="LogType",type=BillLogType.class)
	public BillLogType getLogType() {
		return logType;
	}
	public void setLogType(BillLogType logType) {
		this.logType = logType;
	}
	
	@XmlAttribute(name="logTable",tag="LogTable",type=String.class)
	public String getLogTable() {
		return logTable;
	}
	public void setLogTable(String logTable) {
		this.logTable = logTable;
	}
	
	@XmlAttribute(name="sharingCount",tag="SharingCount",type=int.class)
	public int getSharingCount() {
		return sharingCount;
	}
	public void setSharingCount(int sharingCount) {
		this.sharingCount = sharingCount;
	}
	
	
	

}
