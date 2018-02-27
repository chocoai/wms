package com.xyy.erp.platform.system.task;

import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.util.UUIDUtil;
import org.apache.log4j.Logger;

/**
 * 串行执行拦截
 *
 * @author
 */
public class AccountManagerPlugin implements IPlugin {

    private static Logger LOGGER = Logger.getLogger(AccountManagerPlugin.class);

    private static final int MAX_QUEUE_SIZE = Integer.MAX_VALUE;
    private static final BlockingQueue<AccountTaskDescr> blockingQueue = new LinkedBlockingQueue<>();
    private static boolean stop = true;
    private static ExecutorService worker_shangping = Executors.newFixedThreadPool(1);
    private static ExecutorService worker_wanglai = Executors.newFixedThreadPool(1);
    private static ExecutorService monitor = Executors.newSingleThreadExecutor();

    private static Map<Integer, Record> _wanglai_maps = new HashMap<>();//往来账页
    private static Map<Integer, Record> _goods_maps = new HashMap<>();//商品账页

    private static class Monitor implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                try {
                    //获取任务
                    AccountTaskDescr task = blockingQueue.take();

                    for (SQLTarget key : task.getMap().keySet()) {
                        if (key.getTableName().equals("xyy_wms_bill_shangpinzhangye")) {
                            worker_shangping.execute(new AccountTask(key, task.getMap().get(key)));
                        } else if (key.getTableName().equals("xyy_erp_bill_wanglaizhangye")) {
                            worker_wanglai.execute(new AccountTask(key, task.getMap().get(key)));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
    }

    @Override
    public boolean start() {
        stop = false;
        monitor.execute(new Monitor());
        return true;
    }

    @Override
    public boolean stop() {
        stop = true;
        if (!monitor.isShutdown())
            monitor.shutdown();
        if (!worker_shangping.isShutdown())
            worker_shangping.shutdown();
        if (!worker_wanglai.isShutdown())
            worker_wanglai.shutdown();
        return true;
    }

    /**
     * 添加队列
     *
     * @param taskDescr
     */
    public static void addAccountTaskDescr(AccountTaskDescr taskDescr) {
        try {
            blockingQueue.put(taskDescr);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static class AccountTaskDescr {

        private Map<SQLTarget, List<Record>> map = new HashMap<>();

        public AccountTaskDescr() {
            super();
        }

        public void addTask(Map<SQLTarget, List<Record>> tasklist) {
            map.putAll(tasklist);
        }

        public Map<SQLTarget, List<Record>> getMap() {
            return map;
        }

    }

    public static class AccountTask implements Runnable {
        private String taskId;
        private SQLTarget target;
        private List<Record> records;

        @Override
        public void run() {

            Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    LogKit.error(e.getMessage(), e);

                }
            });

            if (target.getTableName().equals("xyy_wms_bill_shangpinzhangye")) {
                this.processShangping();
            } else if (target.getTableName().equals("xyy_erp_bill_wanglaizhangye")) {
                this.processWanglai();
            }

        }

        /**
         * 往来账处理
         */
        private void processWanglai() {
            //获取最新的往来账页
            try {
                for (Record record : records) {
                    BigDecimal yue = BigDecimal.ZERO;
                    Integer hashCode = record.getStr("wlId").hashCode();
                    Record r = new Record();
                    if (!_wanglai_maps.containsKey(hashCode)) {
                        r = Db.findFirst("select yue from xyy_erp_bill_wanglaizhangye where wlId=? order by ID DESC limit 1", record.getStr("wlId"));
                    } else {
                        r = _wanglai_maps.get(hashCode);
                    }

                    if (r != null) {
                        yue = r.getBigDecimal("yue");
                    }
                    BigDecimal jiefang = record.get("jiefang") == null ? BigDecimal.ZERO : new BigDecimal(record.get("jiefang").toString());
                    BigDecimal daifang = record.get("daifang") == null ? BigDecimal.ZERO : new BigDecimal(record.get("daifang").toString());

                    yue = yue.add(jiefang).subtract(daifang).setScale(2, BigDecimal.ROUND_HALF_UP);
                    record.set("yue", yue);
                    //加入内存
                    _wanglai_maps.put(hashCode, record);
                }
                //保存往来账信息
                Db.batchSave("xyy_erp_bill_wanglaizhangye", records, 30);
                _wanglai_maps.clear();//清除缓存

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * 商品账页处理
         */
        private void processShangping() {
            LOGGER.info("商品往来账页开始...");
            //获取最新的商品账页
            try {
                // 查询参数
                String[] params = null;
                LOGGER.info("商品往来账页条目数:" + (records == null ? 0 : records.size()));
                for (Record record : records) {
                    // 商品总结余
                    BigDecimal spzjy = BigDecimal.ZERO;
                    // 商品批号结余
                    BigDecimal spphjy = BigDecimal.ZERO;
                    // 商品批号货位结余
                    BigDecimal spphhwjy = BigDecimal.ZERO;
                    // 商品
                    Record shangPin = new Record();
                    // 商品批号
                    Record shangPinPiHao = new Record();
                    // 商品批号货位
                    Record shangPinPiHaoHuoWei = new Record();

                    BigDecimal rukushuliang = record.get("rukushuliang")==null?BigDecimal.ZERO :record.getBigDecimal("rukushuliang");
                    BigDecimal chukushuliang = record.get("chukushuliang")==null?BigDecimal.ZERO:record.getBigDecimal("chukushuliang");
                    /*
                     *  摘要 1.入库，2.购退，3.出库，4.销退，5.移库，6.损溢，7.补货
                     *  补货 和 移库 的业务，不需要更新 商品结余、商品批号结余
                     */
                    int zhaiyao = record.get("zhaiyao")==null?0:record.getInt("zhaiyao");

                    LOGGER.debug("商品往来账页对象:" + record.toString());

                    // 防止record中没有这两个字段
                    if(record.get("rukushuliang") == null) record.set("rukushuliang", BigDecimal.ZERO);
                    if(record.get("chukushuliang") == null) record.set("chukushuliang", BigDecimal.ZERO);

                    // 商品id，用来更新 "商品总结余"
                    String shangpinId = record.getStr("shangpinId");
                    String pihaoId = record.getStr("pihaoId");
                    String huoweiId = record.getStr("huoweiId");
                    
                    Integer shangPinLevelHashCode = shangpinId.hashCode();
                    Integer piHaoLevelHashCode = (shangpinId + pihaoId).hashCode();
                    Integer huoWeiLevelHashCode = (shangpinId + pihaoId + huoweiId).hashCode();

                    // 商品结余
                    if (!_goods_maps.containsKey(shangPinLevelHashCode)) {
                        // 有历史记录
                        shangPin = Db.findFirst("select spzjy from xyy_wms_bill_shangpinzhangye "
                                + "where shangpinId=? ORDER BY ID DESC limit 1", shangpinId);

                        /*
                         * 计算商品结余
                         * 如果有历史记录，结余数据通过历史记录计算
                         * 如果没有历史记录，读相应的 库存表(商品总库存、商品批号库存、商品批号货位库存) 来更新当前结余
                         */
                        if(shangPin != null) {
                            // 更新 商品结余、商品批号结余 需要过滤的业务
                            if(zhaiyao != 5 && zhaiyao != 7) spzjy = shangPin.getBigDecimal("spzjy").add(rukushuliang).subtract(chukushuliang);
                            else spzjy = shangPin.getBigDecimal("spzjy");

                            LOGGER.debug("1.商品总结余:" + spzjy);
                        } else {
                            /*// 无历史记录
                            Record shangPinKuCun = Db.findFirst("select kucunshuliang from xyy_wms_bill_shangpinzongkucun "
                                    + "where shangpinId=? ORDER BY createTime DESC limit 1", shangpinId);
                            // 没有历史记录的情况下，此时查出数据在记账页之前已经更新过，所以不需要计算，直接赋值即可。
                            if(shangPinKuCun != null) spzjy = shangPinKuCun.get("kucunshuliang") == null ? BigDecimal.ZERO : shangPinKuCun.getBigDecimal("kucunshuliang");*/
                            // 无历史记录则代表是新商品，从零开始计算
                            if(zhaiyao != 5 && zhaiyao != 7) spzjy = BigDecimal.ZERO.add(rukushuliang).subtract(chukushuliang);
                            else spzjy = BigDecimal.ZERO;

                            LOGGER.debug("2.商品总结余:" + spzjy);
                        }
                    } else {
                        shangPin = _goods_maps.get(shangPinLevelHashCode);
                        // 判断 业务 类型
                        if(zhaiyao != 5 && zhaiyao != 7) spzjy = shangPin.getBigDecimal("spzjy").add(rukushuliang).subtract(chukushuliang);
                        else spzjy = shangPin.getBigDecimal("spzjy");

                        LOGGER.debug("3.商品总结余:" + spzjy);
                    }

                    // 商品批号结余
                    if (!_goods_maps.containsKey(piHaoLevelHashCode)) {
                        params = new String[]{shangpinId, pihaoId};
                        shangPinPiHao = Db.findFirst("select spphjy from xyy_wms_bill_shangpinzhangye "
                                + "where shangpinId=? and pihaoId=? ORDER BY ID DESC limit 1", params);

                        /*
                         * 计算商品批号结余
                         */
                        if (shangPinPiHao != null) {
                            // 判断 业务 类型
                            if(zhaiyao != 5 && zhaiyao != 7) spphjy = shangPinPiHao.getBigDecimal("spphjy").add(rukushuliang).subtract(chukushuliang);
                            else spphjy = shangPinPiHao.getBigDecimal("spphjy");

                            LOGGER.debug("1.商品批号结余:" + spphjy);
                        } else {
                            /*params = new String[]{shangpinId, pihaoId};
                            Record shangPinPiHaoKuCun = Db.findFirst("select kucunshuliang from xyy_wms_bill_shangpinpihaokucun "
                                    + "where shangpinId=? and pihaoId=? ORDER BY createTime DESC limit 1", params);
                            if(shangPinPiHaoKuCun != null) spphjy = shangPinPiHaoKuCun.get("kucunshuliang") == null ? BigDecimal.ZERO : shangPinPiHaoKuCun.getBigDecimal("kucunshuliang");*/
                            // 无历史记录则代表是新商品，从零开始计算
                            if(zhaiyao != 5 && zhaiyao != 7) spphjy = BigDecimal.ZERO.add(rukushuliang).subtract(chukushuliang);
                            else spphjy = BigDecimal.ZERO;

                            LOGGER.debug("2.商品批号结余:" + spphjy);
                        }

                    } else {
                        shangPinPiHao = _goods_maps.get(piHaoLevelHashCode);
                        // 判断 业务 类型
                        if(zhaiyao != 5 && zhaiyao != 7) spphjy = spphjy = shangPinPiHao.getBigDecimal("spphjy").add(rukushuliang).subtract(chukushuliang);
                        else spphjy = shangPinPiHao.getBigDecimal("spphjy");

                        LOGGER.debug("3.商品批号结余:" + spphjy);
                    }

                    // 商品批号货位结余
                    if (!_goods_maps.containsKey(huoWeiLevelHashCode)) {
                        params = new String[]{shangpinId, pihaoId, huoweiId};
                        shangPinPiHaoHuoWei = Db.findFirst("select spphhwjy from xyy_wms_bill_shangpinzhangye "
                        		+ "where shangpinId=? and pihaoId=? and huoweiId=? ORDER BY ID DESC limit 1", params);
                        /*
                         * 计算商品批号货位结余
                         */
                        if (shangPinPiHaoHuoWei != null) {
                            spphhwjy = shangPinPiHaoHuoWei.getBigDecimal("spphhwjy").add(rukushuliang).subtract(chukushuliang);

                            LOGGER.debug("1.商品批号货位结余:" + spphhwjy);
                        } else {
                            /*params = new String[]{shangpinId, pihaoId, huoweiId};
                            Record shangPinPiHaoHuoWeiKuCun = Db.findFirst("select kucunshuliang from xyy_wms_bill_shangpinpihaohuoweikucun "
                                    + "where shangpinId=? and pihaoId=? and huoweiId=? ORDER BY createTime DESC limit 1", params);
                            if(shangPinPiHaoHuoWeiKuCun != null) spphhwjy = shangPinPiHaoHuoWeiKuCun.get("kucunshuliang") == null ? BigDecimal.ZERO : shangPinPiHaoHuoWeiKuCun.getBigDecimal("kucunshuliang");*/
                            // 无历史记录则代表是新商品，从零开始计算
                            spphhwjy = BigDecimal.ZERO.add(rukushuliang).subtract(chukushuliang);
                            LOGGER.debug("2.商品批号货位结余:" + spphhwjy);
                        }
                    } else {
                        shangPinPiHaoHuoWei = _goods_maps.get(huoWeiLevelHashCode);
                        spphhwjy = shangPinPiHaoHuoWei.getBigDecimal("spphhwjy").add(rukushuliang).subtract(chukushuliang);

                        LOGGER.debug("3.商品批号货位结余:" + spphhwjy);
                    }

                    record.set("spzjy", spzjy);
                    record.set("spphjy", spphjy);
                    record.set("spphhwjy", spphhwjy);
                    //加入内存
                    _goods_maps.put(shangPinLevelHashCode, record);
                    _goods_maps.put(piHaoLevelHashCode, record);
                    _goods_maps.put(huoWeiLevelHashCode, record);

                }
                //保存商品往来账
                Db.batchSave("xyy_wms_bill_shangpinzhangye", records, 30);
                _goods_maps.clear();//清除缓存

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public AccountTask(SQLTarget target, List<Record> records) {
            super();
            this.taskId = UUIDUtil.newUUID();
            this.target = target;
            this.records = records;
        }

        public String getTaskId() {
            return taskId;
        }

        public SQLTarget getTarget() {
            return target;
        }

        public List<Record> getRecords() {
            return records;
        }

    }

}
