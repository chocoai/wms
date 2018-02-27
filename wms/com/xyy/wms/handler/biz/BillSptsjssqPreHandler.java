package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

public class BillSptsjssqPreHandler implements PreSaveEventListener{

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        for(Record r:body){
        	int tingshouleixing=r.getInt("tingshouleixing");
        	String pihao=r.getStr("pihao");
        	if(tingshouleixing==2){
    			if(StringUtil.isEmpty(pihao)){
    				event.getContext().addError("mes", " 请填写正确批号!!!");
    				return ;
    			}
    		}
        	String shangpinmingcheng=r.getStr("shangpinmingcheng");
        	if(!StringUtil.isEmpty(shangpinmingcheng)){
    			if(StringUtil.isEmpty(r.getStr("shangpinbianhao"))){
    				event.getContext().addError("mes", "请填写商品信息!!!");
    				return ;
    			}
    		}
        }
		
	}

}
