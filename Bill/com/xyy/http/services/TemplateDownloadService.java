package com.xyy.http.services;

import java.io.File;
import java.util.Arrays;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.xyy.erp.platform.common.tools.FileUtil;
import com.xyy.http.service.Service;
import com.xyy.http.util.Envelop;
import com.xyy.util.StringUtil;

public class TemplateDownloadService implements Service {

	@Override
	public Envelop service(Envelop request) throws Exception {
		// 从ERP中获取打印模板
		JSONArray templateNames = request.getBody().getJSONArray("templateNames");
		Envelop result = new Envelop();
		String webRootPath = PathKit.getWebRootPath() + "/WEB-INF/config/Print";
		if (templateNames != null && templateNames.size() > 0) {
			File file = new File(webRootPath);
			File[] listTemplateFiles = file.listFiles();
			if (listTemplateFiles == null || listTemplateFiles.length == 0) {
				result.getHead().put("status", 0);
				result.getBody().put("msg", "没有文件");
				return result;
			}
			for (Object object : templateNames) {
				// 得到每个模板的名字
				String templateName = (String) object;
				// 得到模板名字后，在ERP指定目录之内寻找相应的模板文件
				File templateFile = Arrays.stream(listTemplateFiles)
						.filter(f -> f.getName().substring(6).equals(templateName + ".xml")).findFirst().get();
				if (templateFile == null)
					continue;
				JSONObject temp = new JSONObject();
				String templateText = FileUtil.readTxt(templateFile.getPath(), "UTF-8");
				if (StringUtil.isEmpty(templateText) || !templateFile.getName().endsWith(".xml")) {
					continue;
				}
				temp.put("fileText", templateText);
				temp.put("fileName", templateFile.getName());
				result.getBody().put(templateName, temp);
			}
			result.getHead().put("status", 1);
		} else {
			result.getHead().put("status", 0);
			result.getBody().put("msg", "模板名称不能为空");
		}
		return result;
	}
}
