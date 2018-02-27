package com.xyy.bill.print.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
/**
 *         <!-- 
            页面输出模式设置
            Mode页面输出模式：BillPerPage单据输出模式，每个页（或多页）输出一张单据;
                              MultiBillPerPage一个页面输出多个单据;
                              BillHorCount：一个页面输出多个单据时水平方向计数;
                              BillVerCount：一个页面输出多个单据是垂直方向计数;
        -->
        <PageOutputMode Mode="BillPerPage|MultiBillPerPage"  BillHorCount="" BollVerCount=""  />  
 * @author evan
 *
 */
@XmlComponent(tag = "PageOutputMode", type = PageOutputMode.class)
public class PageOutputMode {
	public static enum Mode{
		BillPerPage,MultiBillPerPage
	}
	
	private Mode mode=Mode.BillPerPage;
	private int billHorCount=1;
	private int billVerCount=1;
	
	public PageOutputMode() {
		super();
	}
	
	@XmlAttribute(name="mode",tag="Mode",type=Mode.class)
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	@XmlAttribute(name="billHorCount",tag="BillHorCount",type=int.class)
	public int getBillHorCount() {
		return billHorCount;
	}
	public void setBillHorCount(int billHorCount) {
		this.billHorCount = billHorCount;
	}
	
	@XmlAttribute(name="billVerCount",tag="BillVerCount",type=int.class)
	public int getBillVerCount() {
		return billVerCount;
	}
	public void setBillVerCount(int billVerCount) {
		this.billVerCount = billVerCount;
	}
	
	
	
}
