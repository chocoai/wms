package com.xyy.wms.outbound.biz;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataSetInstance;
import org.apache.log4j.Logger;

import java.util.List;

import static com.xyy.bill.util.RecordDataUtil.getHead;

/**
 * @author zhang.wk
 * 内复核完成前的处理
 */
public class ChuKuNeiFuHePreHandler implements PreSaveEventListener {

    private static Logger logger = Logger.getLogger(ChuKuNeiFuHePreHandler.class);

    @Override
    public void execute(PreSaveEvent event) {
        BillContext context = event.getContext();
        DataSetInstance dsi = (DataSetInstance) context.get("$model");
        if (dsi == null) {
            return;
        }
        // 单据头数据
        Record head = getHead(dsi);
        // 单据体数据
        List<Record> bodyList = dsi.getBodyDataTableInstance().get(0).getRecords();

        String dingdanbianhao = head.get("dingdanbianhao");
        String neifuhedanhao = head.get("neifuhedanhao");
        String weifuherongqi = head.get("weifuherongqi");
        String rongqihao = head.get("rongqihao");
        String[] split = weifuherongqi.split(",");

        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals(rongqihao)) {
                if (i!=0&&i!=split.length-1) {
                    sbu.append(",");
                }else {
                    sbu.append("");
                }

            } else {
                sbu.append(split[i]);
            }
        }

        //更新拣货任务中的未复核容器
        Object[] objects={sbu.toString(),dingdanbianhao};
        Db.update("UPDATE  xyy_wms_bill_dabaorenwu SET weifuherongqi=? WHERE dingdanbianhao=?",objects);

        //更新內复核
        Object[] objects1={sbu.toString(),dingdanbianhao};
        Db.update("UPDATE  xyy_wms_bill_chukuneifuhe SET weifuherongqi=? WHERE dingdanbianhao=? ",objects);


        Record record = Db.findFirst("select * from  xyy_wms_bill_chukuneifuhe where dingdanbianhao=? and neifuhedanhao=?", dingdanbianhao,neifuhedanhao);
        if (record != null)

        {
            int status = record.get("status");
            if (status == 43) {
                event.getContext().addError(dingdanbianhao, "不能重复内复核");
                return;
            }
        }


        //修改状态为复核完成
        head.set("status", 43);
        head.set("weifuherongqi", sbu.toString());
    }


}
