package com.xyy.erp.platform.system.service;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xyy.erp.platform.common.tools.CommonTools;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.MD5Util;
import com.xyy.erp.platform.common.tools.RandomUtil;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.erp.platform.common.tools.TimeUtil;
import com.xyy.erp.platform.system.model.Role;
import com.xyy.erp.platform.system.model.User;

/**
 * 系统管理-用户信息服务层
 * 
 * @author caofei
 *
 */
public class UserService {

	private static final String PREFIX_SQL = "select *";
	
	private static final String INIT_PASSWORD = "123456";

	public Page<User> paginate(int pageNumber, int pageSize, User user) {
		StringBuilder buffer = new StringBuilder("from tb_sys_user as u where 1 = 1");
		if (null != user) {
			if(!StringUtil.isEmpty(user.getRoleId())){
				Role role = Role.dao.findById(user.getRoleId());
				if(!StringUtil.isEmpty(role.getParentId())){
					buffer.append(" and u.id in (select userId from tb_sys_role_user_relation where roleId = '"+user.getRoleId()+"')");
				}
			}
			if (!StringUtil.isEmpty(user.getLoginName())) {
				buffer.append(" and u.loginName like '%" + user.getLoginName() + "%'");
			}
			if (!StringUtil.isEmpty(user.getMobile())) {
				buffer.append(" and u.mobile = " + user.getMobile());
			}
			if (user.getState() != null) {
				buffer.append(" and u.state = " + user.getState());
			}
		}
		buffer.append(" order by u.sortNo");
		return User.dao.paginate(pageNumber, pageSize, PREFIX_SQL, buffer.toString());
	}

	public boolean save(User user) {
		Date curDate = new Date();
		user.setCreateTime(curDate);
		user.setUpdateTime(curDate);
		user.setPassword(MD5Util.getMD5Str(INIT_PASSWORD));
		String head=CommonTools.getPinYinHeadChar(user.getRealName());
		user.setUserId(head+TimeUtil.format(new Date(), "yyyyMMddHHmmssss")+RandomUtil.getRandomCode(3));
		user.setCode(head+TimeUtil.format(new Date(), "yyyyMMddHHmmssss")+RandomUtil.getRandomCode(3));
		user.setZhujima(CommonTools.getPinYinChar(user.getRealName()));
		boolean result=user.save();
		if(result){
			doWork(user.getUserId(),true);
		}
		return result;
	}
	public boolean update(User user) {
		user.setUpdateTime(new Date());
		user.setZhujima(CommonTools.getPinYinChar(user.getRealName()));
		boolean result=user.update();
		if(result){
			doWork(user.getUserId(),false);
		}
		return result;
	}
	
	public void doWork(String UserId,boolean add){
		Record res=Db.findFirst("select * from tb_sys_user where userId=?",UserId);
		Record wms=new Record();
		wms.set("DZYID", res.get("userId"));
		wms.set("DZYCODE",res.get("code"));//目前没有
		wms.set("DZYNAME", res.get("realName"));
		wms.set("ZJM", res.get("zhujima"));//没有
		wms.set("DENGLRQ", res.get("createTime"));
		
		wms.set("DELRQ", res.get("updateTime"));
		wms.set("BEACTIVE", res.getInt("state")==1?"Y":"N");
		wms.set("dmrq", res.get("updateTime"));
		wms.set("YZID", "O0Z8M62IK57");
		
		wms.set("WMFLG", "N");
		wms.set("lastmodifytime", res.get("updateTime"));
		if(add){
			Db.use(DictKeys.db_dataSource_wms_mid).save("int_inf_zhiydoc", wms);
		}else{
			Db.use(DictKeys.db_dataSource_wms_mid).update("int_inf_zhiydoc","DZYID", wms);
		}
	}
	
	public boolean delete(String id) {
		return User.dao.deleteById(id);
	}
	
	public User findById(String id){
		return User.dao.findById(id);
	}

	public List<User> queryUserByName(String userName) {
		StringBuilder buffer = new StringBuilder("select u.* from tb_sys_user as u where 1 = 1 and u.state = '1' ");
		if (!StringUtil.isEmpty(userName)) {
			buffer.append(" and (u.realName like '%"+userName+"%' or u.mobile like '%"+userName+"%')");
		}
		List<User> userList = User.dao.find(buffer.toString());
		return userList;
	}
	
}
