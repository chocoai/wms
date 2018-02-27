package com.xyy.http.services;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.bill.def.ChartDataSetDef;
import com.xyy.bill.def.PrintTplDef;
import com.xyy.bill.engine.BillPlugin;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.instance.DataTableInstance;
import com.xyy.bill.instance.ParamSQL;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.print.meta.PageOutputMode;
import com.xyy.bill.print.meta.Print;
import com.xyy.bill.print.meta.Print.TargetType;
import com.xyy.http.service.Service;
import com.xyy.http.util.Envelop;

/**
 * 打印服务的实现
 * 
 * @author evan
 *
 */
public class PrintService implements Service {

	/**
	 * 打印服务,请求封套信息为： envolop={ head:{ service:"PrintService",
	 * code:""//请求码，可以一一匹配 }, body:{ job:"",//作业key key:"",//目标对象key
	 * ids:[id1,id2,id3],//目标对象标识列表 tpl:"",//打印模版key
	 * params:{param:value,param:value}//参数 } } 相应封套信息为： head:{
	 * status:1,//1.成功，0.失败 code:""//相应码 } body:{ job:"",//作业key
	 * jobTotal:1//总作业量，如果是单据或字典，则作业量取决预需要打印的单据或字典数量
	 * //如果为数据集中的table，则打印数量取决预记录条数和打印模式； * } job-jobkey-jobnumber.xml构成打印描述符
	 * 
	 */

	@Override
	public Envelop service(Envelop request) throws Exception {
		Envelop result = new Envelop();
		// 获取参数
		String code = request.getHead().getString("code");// 操作码
		String job = request.getBody().getString("job");// 作业编码
		String key = request.getBody().getString("key");// 目标对象key
		JSONArray ids = request.getBody().getJSONArray("ids");// 标识列表
		String tpl = request.getBody().getString("tpl");// 打印模版key
		JSONObject params = request.getBody().getJSONObject("params");// 参数
		PrintTplDef printTplDef = BillPlugin.engine.getPrintTplDef(tpl);
		if (printTplDef == null || printTplDef.getPrint() == null) {
			result.getHead().put("status", 0);
			result.getHead().put("code", code);
			result.getHead().put("error", "PrintTplDef null.");
			return result;
		} else {
			Print print = printTplDef.getPrint();
			if (!print.getTargetKey().equals(key)) {
				result.getHead().put("status", 0);
				result.getHead().put("code", code);
				result.getHead().put("error", "PrintTplDef target key error.");
				return result;
			}
			TargetType targetType = print.getTargetType();
			switch (targetType) {
			case BILL:// 打印单据
			case DIC:// 打印字典
				if (ids.size() > 0) {
					result.getHead().put("status", 1);
					result.getHead().put("code", code);
					result.getBody().put("job", job);
					result.getBody().put("jobTotal", ids.size());
				} else {
					result.getHead().put("status", 0);
					result.getHead().put("code", code);
					result.getHead().put("error", "ids列表为空");
				}
				this.doPrintJob(print, job, ids);
				return result;
			case DATASET:// 打印数据集
				// 获取对应的数据集定义key
				ChartDataSetDef datasetDef = BillPlugin.engine.getChartDataSetDef(key);
				if (datasetDef == null || datasetDef.getDataSet() == null
						|| datasetDef.getDataSet().getDataTableMetas().size() != 1) {
					result.getHead().put("status", 0);
					result.getHead().put("code", code);
					result.getHead().put("error", "数据集定义为空或有多个数据表");
					return result;
				} else {
					DataTableMeta dtm = datasetDef.getDataSet().getDataTableMetas().get(0);// 获取数据表的定义
					// 开始处理
					BillContext context = new BillContext();
					for (String param : params.keySet()) {
						context.set(param, params.get(param));
					}
					DataTableInstance dti = new DataTableInstance(context, dtm);
					// 预处理SQL
					ParamSQL paramSQL = dtm.preProcessSQL(dti);
					// 计算SQL参数的值
					Object[] paramValues = paramSQL.calParamExpr(dti);
					String query = paramSQL.getSql();
					if (query.indexOf("|") > -1) {
						query = query.replace("|", " ");
					}
					List<Record> rs = Db.find(query, paramValues);
					dti.setRecords(rs);
					if (rs.size() <= 0) {
						result.getHead().put("status", 0);
						result.getHead().put("code", code);
						result.getHead().put("error", "数据集查询结果为空");
						return result;
					} else {
						PageOutputMode pom = print.getHead().getPageOutputMode();
						if (pom.getMode() != PageOutputMode.Mode.MultiBillPerPage) {
							result.getHead().put("status", 0);
							result.getHead().put("code", code);
							result.getHead().put("error", "打印模式错误");
							return result;
						} else {// 必须为多页打印模式
							int rCount = pom.getBillHorCount();
							int cCount = pom.getBillVerCount();
							int count = rCount * cCount;// 每页打印的数量
							int total = (rs.size() + count - 1) / count;
							result.getHead().put("status", 1);
							result.getHead().put("code", code);
							result.getBody().put("job", job);
							result.getBody().put("jobTotal", total);
							this.doPrintJob(print, job, dti);
							return result;
						}
					}
				}
			default:
				result.getHead().put("status", 0);
				result.getHead().put("code", code);
				result.getHead().put("error", "PrintTplDef target key error.");
				return result;
			}

		}
	}

	/**
	 * 构建打印作业 ----数据集打印作业
	 * 
	 * @param print
	 *            打印模版对象
	 * @param job
	 *            打印作业
	 * @param dti
	 *            数据表实例
	 */
	private void doPrintJob(Print print, String job, DataTableInstance dti) {
		PrintServicePool.go(new DataTablePrintTask(print, job, dti));
	}

	/**
	 * 构建打印作业任务 ---单据或字典的打印任务
	 * 
	 * @param print
	 *            打印模版对象
	 * @param job
	 *            打印作业
	 * @param ids
	 *            打印单据或字典列表
	 */
	private void doPrintJob(Print print, String job, JSONArray ids) {
		PrintServicePool.go(new BillAndDicPrintTask(print, job, ids));
	}

}
