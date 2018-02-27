package com.xyy.erp.platform.system.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.xyy.erp.platform.system.model.Organization;
import com.xyy.erp.platform.system.model.User;

/**
 * 系统管理-用户信息服务层
 * 
 * @author caofei
 *
 */
public class LoginService {
	
	/**
	 * 
	 * @param mobile
	 * @param isMoblie
	 * @return 登录用户
	 * @throws UnsupportedEncodingException 
	 */
	
	public List<User> login(String mobile,boolean isMoblie) throws UnsupportedEncodingException{
		List<User> users=new ArrayList<User>();
		if(isMoblie){
			users=User.dao.find("select * from tb_sys_user user where user.mobile =?",mobile);
		}else{
			users=User.dao.find("select * from tb_sys_user user where user.loginName =?",mobile);
		}
		return users;
	}
	/**
	 * @param username
	 * @return 返回机构ID,机构orgName
	 */
	public List<Organization> org(String username,boolean isMobile){
		StringBuffer sql=new StringBuffer("select * from tb_sys_organization where state = 1 and id in(");
		sql.append("select orgId from tb_sys_org_user_relation  where  userId =");
		if(isMobile){
			sql.append("(select id FROM tb_sys_user where mobile=?))");
		}else{
			sql.append("(select id FROM tb_sys_user where loginName=?))");
		}
		List<Organization> list=Organization.dao.find(sql.toString(),username);
		return list;
	}
}
