package com.xyy.bill.print.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.bill.print.meta.PrintBlock.Area;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "PrintBody", type = PrintBody.class)
public class PrintBody {
	private List<PrintBlock> printBlocks=new ArrayList<>();

	@XmlList(name = "printBlocks",subTag="PrintBlock",type = PrintBlock.class)
	public List<PrintBlock> getPrintBlocks() {
		return printBlocks;
	}

	public void setPrintBlocks(List<PrintBlock> printBlocks) {
		this.printBlocks = printBlocks;
	}
	
	public List<PrintBlock> getHeaderBlocks(){
		List<PrintBlock> result=new ArrayList<>();
		for(PrintBlock printBlock:this.printBlocks){
			if(printBlock.getArea()==Area.Header){
				result.add(printBlock);
			}
		}
		
		return result;
	}

	public List<PrintBlock> getFooterBlocks(){
		List<PrintBlock> result=new ArrayList<>();
		for(PrintBlock printBlock:this.printBlocks){
			if(printBlock.getArea()==Area.Footer){
				result.add(printBlock);
			}
		}
		
		return result;
	}

	public List<PrintBlock> getDtlDataBlocks(){
		List<PrintBlock> result=new ArrayList<>();
		for(PrintBlock printBlock:this.printBlocks){
			if(printBlock.getArea()==Area.DtlData){
				result.add(printBlock);
			}
		}
		return result;
	}

	

}
