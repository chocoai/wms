package com.xyy.wms.handler.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.ext.directive.Str;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import com.xyy.util.StringUtil;

import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getBody;
import static com.xyy.bill.util.RecordDataUtil.getHead;

public class DicLjqhwgxwhHandler implements PreSaveEventListener{
    @Override
    public void execute(PreSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null || StringUtil.isEmpty(dsi.getFullKey())) return;
        // 单据头数据
        Record record = getHead(dsi);
        // 单据体数据
        List<Record> body = getBody(dsi);
        //判断是否启用
        int sf = record.getInt("shifouqiyong");
        String ljqbh = record.getStr("ljqbh");
        String id = record.getStr("ID");
        String sql = "select * from xyy_wms_dic_ljqhwgxwh  l where l.ljqbh=?";
        Record result = Db.findFirst(sql, ljqbh);
        //1:判断操作(新增；保存)2：若为新增，判断逻辑区是否重复3：绑定逻辑区或为关系
        if (StringUtil.isEmpty(id)) {
            if (result!=null){
                event.getContext().addError("逻辑区资料维护","该仓库已经存在相同逻辑区");
                return;
            }else if (result == null){
                if (sf == 1) {
                    //循环单据体锁定货位
                    for (Record r : body) {
                        String huoweibianhao = r.getStr("huoweibianhao");
                        String sql1 = "update xyy_wms_dic_huoweiziliaoweihu set shifousuoding=1 where huoweibianhao=?";
                        Db.update(sql1, huoweibianhao);
                    }
                } else if (sf == 0) {
                    for (Record r : body) {
                        String huoweibianhao = r.getStr("huoweibianhao");
                        String sql2 = "update xyy_wms_dic_huoweiziliaoweihu set shifousuoding=0 where huoweibianhao=?";
                        Db.update(sql2, huoweibianhao);
                    }
                }
            }
        }else if (!StringUtil.isEmpty(id)){
            if (sf==0){
                for (Record r : body) {
                    String huoweibianhao = r.getStr("huoweibianhao");
                    String sql2 = "update xyy_wms_dic_huoweiziliaoweihu set shifousuoding=0 where huoweibianhao=?";
                    Db.update(sql2, huoweibianhao);
                }
            }
        }
    }

}
