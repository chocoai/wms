package com.xyy.erp.platform.system.controller;


import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.xyy.erp.platform.render.AppCaptchaRender;
import com.xyy.erp.platform.render.MyCaptchaRender;


/**
 * 验证码
 * @author
 */
public class AuthImgController extends Controller {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AuthImgController.class);
	
	public void index() {
		Render render = new MyCaptchaRender();
		render(render);
	}

	public void appLogin() {
		Render render = new AppCaptchaRender();
		render(render);
	}
}


