package com.xyy.http.service;

import com.jfinal.core.Controller;
import com.xyy.http.services.PrintService;
import com.xyy.http.util.Envelop;
import com.xyy.util.StringUtil;

/**
 * 服务控制器，协议如下： apiKey:对应的apiKey service:服务名称，每个服务名称都对应如服务处理器
 * 
 * 调用方式： /service/printService?apiKey=YBM100 me.add("/service",
 * ServiceController.class);
 *
 */
public class ServiceController extends Controller {
	public void index() {
		String service = this.getPara();// 获取服务名称
		if (StringUtil.isEmpty(service)) {
			this.setAttr("error", "service_name_error");
			this.renderJson();
			return;
		}
		String apiKey = this.getPara("apiKey");
		if (StringUtil.isEmpty(apiKey)) {
			this.setAttr("error", "apiKey");
			this.renderJson();
			return;
		}

		String req = this.getPara("req");
		if (StringUtil.isEmpty(req)) {
			this.setAttr("error", "request_body_content");
			this.renderJson();
			return;
		}

		// 获取对应的apiKey文件，存在于项目的classes文件夹下：名称为：apkKey。key
		Envelop request = null;
		try {
			request = Envelop.fromReqString(req, apiKey);
		} catch (Exception e) {
			this.setAttr("error", "envelop decryptor faied.");
			this.renderJson();
			return;
		}

		// 查询对应的servcie接口，
		Service srv = ServiceManager.getInstance().getService(service);
		if (srv instanceof PrintService)
			System.out.println("-----------------aaaaaaaaaaaaaaa");
		if (srv == null) {
			this.setAttr("error", "create or get service instance faied.");
			this.renderJson();
			return;
		}
		try {
			Envelop response = srv.service(request);
			this.renderText(response.toResString(apiKey));
		} catch (Exception e) {
			this.setAttr("error", "service execute faied.");
			this.renderJson();
			return;
		}

	}

}
