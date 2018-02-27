package com.xyy.workflow.definition;

import java.util.Date;

import com.jfinal.plugin.activerecord.entity.BaseEntity;
import com.jfinal.plugin.activerecord.entity.Entity;
import com.jfinal.plugin.activerecord.entity.GeneratedValue;
import com.jfinal.plugin.activerecord.entity.GenericGenerator;
import com.jfinal.plugin.activerecord.entity.Id;
import com.jfinal.plugin.activerecord.entity.Table;

/**
 * 流程执行异常监控类
 * 
 * @author evan
 *
 */

@Entity(name = "ProcessException")
@Table(name = "tb_pd_exception")
public class ProcessException extends BaseEntity<ProcessException> {
	public static ProcessException dao=new ProcessException();
	/**
	 * 
	 */
	private static final long serialVersionUID = 6434553824304274557L;
	public final static int PROCESS_EXCEPTION_STATIS_PENDING = 0;
	public final static int PROCESS_EXCEPTION_STATIS_PROCESSED = 1;
	public final static int PROCESS_EXCEPTION_STATIS_NEEDNOT = 2;

	private String id;
	private Integer errNumber;
	private String errInfo;//json格式
	private String message;
	private String printStack;
	private Integer status;
	private String remark;
	private Date createTime;
	private Date updateTime;

	public ProcessException() {
		
	}

	@Id
	@GeneratedValue(name = "guid")
	@GenericGenerator(name = "guid", strategy = "com.xyy.workflow.jfinal.UUIDGenerator")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public Integer getErrNumber() {
		return errNumber;
	}

	public void setErrNumber(Integer errNumber) {
		this.errNumber = errNumber;
	}

	public String getErrInfo() {
		return errInfo;
	}

	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getPrintStack() {
		return printStack;
	}

	public void setPrintStack(String printStack) {
		this.printStack = printStack;
	}

	
	
}
