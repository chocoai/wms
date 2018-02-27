package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.erp.platform.common.tools.StringUtil;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class DicRqzlwhPreHandler implements PreSaveEventListener {

    @Override
    public void execute(PreSaveEvent event) {
        DataSetInstance dsi = (DataSetInstance) event.getContext().get("$model");
        Record head = getHead(dsi);
        //获取组织机构id
        String orgId=event.getContext().getString("orgId");
        String id=head.getStr("ID");
        int sf=head.getInt("shifouqiyong");
        String rongqibianhao=head.getStr("rongqibianhao");
        int rongqileixing=head.get("rongqileixing");
       if (rongqibianhao==null){
            event.getContext().addError("容器资料维护","容器编号信息不完整,请检查后再提交");
            return;
        }
        if (rongqileixing==0){
            event.getContext().addError("容器资料维护","容器类型信息不完整,请检查后再提交");
            return;
        }

        String[] rqParams={rongqibianhao};
        if (StringUtil.isEmpty(id)){
            String sql="select * from xyy_wms_dic_rqzlwh l where l.rongqibianhao=? and orgId='"+orgId+"'";
            Record record= Db.findFirst(sql,rqParams);
            if (record!=null){
                event.getContext().addError("容器资料维护","容器编号已经存在");
                return;
            }
        }
    }

}
