package com.xyy.bill.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.meta.BillBodyMeta.UserAgent;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "BillFormMeta", type = BillFormMeta.class)
public class BillFormMeta {

	private String key;
	private String caption;
	private BillType billType;
	private String version;

	// 单据元信息部分
	private BillHeadMeta billMeta;

	private List<BillBodyMeta> billBodyMetas = new ArrayList<BillBodyMeta>();

	public BillFormMeta() {
		super();
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlAttribute(name = "billType", tag = "BillType", type = BillType.class)
	public BillType getBillType() {
		return billType;
	}

	public void setBillType(BillType billType) {
		this.billType = billType;
	}

	@XmlAttribute(name = "version", tag = "Version", type = String.class)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlComponent(name = "billMeta", tag = "BillHeadMeta", type = BillHeadMeta.class)
	public BillHeadMeta getBillMeta() {
		return billMeta;
	}

	public void setBillMeta(BillHeadMeta billMeta) {
		this.billMeta = billMeta;
	}

	@XmlList(name = "billBodyMetas", tag = "BillBody", subTag = "BillBodyMeta", type = BillBodyMeta.class)
	public List<BillBodyMeta> getBillBodyMetas() {
		return billBodyMetas;
	}

	public void setBillBodyMetas(List<BillBodyMeta> billBodyMetas) {
		this.billBodyMetas = billBodyMetas;
	}

	public static enum BillType {
		Bill, MultiBill, Dictionary, Report, Bills, Dics
	}

	public static enum SQLType {
		StandardSql, GroupSql
	}

	// CommType:与机构编码不关联，PrivateType：与机构编码关联
	public static enum PermissionType {
		CommType, PrivateType
	}

	public boolean existWebUserAgent() {
		for (BillBodyMeta body : this.getBillBodyMetas()) {
			if (body.getUserAgent() == UserAgent.web) {
				return true;
			}
		}
		return false;
	}

	public boolean existPadUserAgent() {
		for (BillBodyMeta body : this.getBillBodyMetas()) {
			if (body.getUserAgent() == UserAgent.pad) {
				return true;
			}
		}
		return false;
	}

	public boolean existMobileUserAgent() {
		for (BillBodyMeta body : this.getBillBodyMetas()) {
			if (body.getUserAgent() == UserAgent.mobile) {
				return true;
			}
		}
		return false;
	}

}
