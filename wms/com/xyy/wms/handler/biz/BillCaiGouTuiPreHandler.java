package com.xyy.wms.handler.biz;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;



public class BillCaiGouTuiPreHandler implements PreSaveEventListener {

	@Override
	public void execute(PreSaveEvent event) {
		DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        List<Record> body = getBody(dsi);
        Map<String ,BigDecimal> mapChu = new HashMap<String ,BigDecimal>();
        Map<String ,BigDecimal> mapXiao = new HashMap<String ,BigDecimal>();
       
        for (int i = 0; i < body.size(); i++) {
        	String shangpinbianhao=body.get(i).getStr("shangpinbianhao");
        	String pihao = body.get(i).getStr("pihao");
        	BigDecimal shuliang = body.get(i).get("shuliang");
        	BigDecimal kucunshuliang = body.get(i).get("kucunshuliang");
        	String huowei = body.get(i).getStr("huoweibianhao");
        	if(kucunshuliang.compareTo(BigDecimal.ZERO)==0 || huowei.length()==0){
        		event.getContext().addError("货位或者库存数量不能为空","货位或者库存数量不能为空");
				return;
        	}
        	
        	if(shuliang.compareTo(kucunshuliang)>0){
        		event.getContext().addError("出库数量大于库存数量","出库数量大于库存数量");
				return;
        	}
        	String key = shangpinbianhao +"->"+ pihao;
        	if(mapChu.isEmpty()){
        		mapChu.put(key, shuliang);
        		}else{
        			Set<Entry<String, BigDecimal>>   entries = mapChu.entrySet();
            		for (Entry<String, BigDecimal> entry : entries) {      
            			if(key.equals(entry.getKey())){
            				BigDecimal v = entry.getValue();
            				BigDecimal value =  v.add(shuliang);
            				mapChu.put(key, value);
            		}          
        		}
        	}
        	
        }
        for(int i = 0; i < body.size(); i++){
        	String shangpinbianhao=body.get(i).getStr("shangpinbianhao");
        	String pihao = body.get(i).getStr("pihao");
        	String key = shangpinbianhao +"->"+ pihao;
        	BigDecimal xiaotuishuliang = body.get(i).get("xiaotuishuliang");
        	mapXiao.put(key, xiaotuishuliang);
        }
        Set<Entry<String, BigDecimal>>   entries = mapChu.entrySet();
		for (Entry<String, BigDecimal> entry : entries) {      
			String entryKey = entry.getKey();
			BigDecimal val = entry.getValue();
			BigDecimal map2Value = mapXiao.get(entryKey);
			if(map2Value.compareTo(val)==-1){
				event.getContext().addError("出库数量大于采购退出数量","出库数量大于采购退出数量");
				return;
			}
			if(map2Value.compareTo(val)!=0){
				event.getContext().addError("出库数量和采购退出数量不相等不能出库","出库数量和采购退出数量不相等不能出库");
				return;
			}
			
		}
    }
}
