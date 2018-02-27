package com.xyy.erp.platform.system.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.xyy.erp.platform.common.tools.MD5;
import com.xyy.erp.platform.common.tools.MD5Util;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.User;
import com.xyy.erp.platform.system.service.LoginService;
import com.xyy.erp.platform.system.service.OrganizationService;



/**
 * LoginController
 */
public class LoginController extends Controller {
	
	private LoginService loginService = Enhancer.enhance(LoginService.class);
	
	private OrganizationService orgService = Enhancer.enhance(OrganizationService.class);
	
	public void index() {
		User user = ToolContext.getCurrentUser(getRequest(), true); // cookie认证自动登陆处理
		if(null == user){//后台
			redirect("/login/login.html");
		}else{
			redirect("/index.html");
		}
	}
	/**
	 * 0 用户不存在或有重复的用户  1 判断成功 2密码错误
	 * @throws UnsupportedEncodingException
	 */
	public void vali() throws UnsupportedEncodingException {
		boolean authCode =authCode() ;//authCode();
		int result=0;//0 验证码错误 1 成功 2 用户不存在或有重复的用户 3 密码错误
		if(authCode){
			String username = getPara("username");
			String password = getPara("password");
			String orgId = getPara("orgId");
			boolean isMoblie=getParaToBoolean("isMoblie");
			if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)){
				redirect("/login/login.html");
				return;
			}
			List<User> users= loginService.login(username,isMoblie);
			if(users.size()!=1){
				result= 2;// 用户不存在或有重复的用户
			}
			User user=users.get(0);
			boolean bool=false;
			try {
				bool = user.getPassword().equals(MD5.encodeByMD5(password));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (bool) {
				// 密码验证成功
				Organization org=Organization.dao.findById(orgId);
				user.setOrgId(org.getId());
				user.setOrgName(org.getOrgName());
				user.setOrgCode(org.getOrgCode());
				String orgCodes = orgService.queryOrgCodeByUserId(user.getId());
				user.setOrgCodes(orgCodes);
				ToolContext.setCurrentUser(getRequest(), getResponse(), user, false);// 设置登录账户
				result= 1;
			}else{
				result= 3;
			}
		}
		this.setAttr("result", result);
		this.renderJson();

	}
	protected boolean authCode() {
		String authCodePram = getPara("authCode");
		String authCodeCookie = ToolContext.getAuthCode(getRequest());
		if (null != authCodePram && null != authCodeCookie) {
			authCodePram = authCodePram.toLowerCase();// 统一小写
			authCodeCookie = authCodeCookie.toLowerCase();// 统一小写
			if (authCodePram.equals(authCodeCookie)) {
				return true;
			}
		}
		return false;
	}
	
	public void edit(){
		int result=0;//0 原密码错误 1修改成功 2修改失败
		String opd = getPara("oldpwd");
		String npd = getPara("newpwd");
		String md5opd=MD5Util.getMD5Str(opd);
		String md5npd=MD5Util.getMD5Str(npd);
		User user = ToolContext.getCurrentUser(getRequest(), true);
		if(user.getPassword().equals(md5opd)){
			user.setPassword(md5npd);
			result=1;
			if(!user.update()){
				result=2;
			};
		}
		this.setAttr("result", result);
		this.renderJson();
	}
	/**
	 * 注销
	 */
	public void logout() {
		ToolContext.clear(this.getRequest(), this.getResponse());
		redirect("/login/login.html");
	}
	
	public void org(){
		String username = getPara("username");
		boolean isMoblie=getParaToBoolean("isMoblie");
		if(StringUtil.isEmpty(username)){
			redirect("/login/login.html");
			return;
		}
		List<Organization>  orgList=loginService.org(username,isMoblie);
		this.setAttr("result", orgList);
		this.renderJson();
	}
}






