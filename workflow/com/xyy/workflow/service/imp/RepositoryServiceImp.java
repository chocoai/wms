package com.xyy.workflow.service.imp;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessHistory;
import com.xyy.workflow.service.inf.RepositoryService;

public class RepositoryServiceImp implements RepositoryService {
	
	

	
	/**
	 * 发布流程
	 * 状态：1：草稿状态；2：发布状态；3：停用状态;
	 */
	@Override
	public synchronized ProcessDefinition deployProcess(ProcessDefinition pd) {
		if(pd.getStatus()!=1){
			return null;
		}
		//流程版本号
		Long count =Db.findFirst("select count(*) as count from tb_pd_processdefinition pd where pd.name=? and pd.status!=1",pd.getName())
               .getLong("count");
		if(count==null){
			count=0l;
		}
		pd.setVersion(count.intValue()+1);
		pd.setStatus(ProcessDefinition.PROCESS_DEFINITION_STATUS_PUBLISHED);
     	
		if(pd.save()){
			return pd;
		}
    	return null;
	}

	
	
	/**
	 *   复制方式发布草稿流程
	 */
	public synchronized ProcessDefinition deployProcess2(ProcessDefinition tpd) {
		if(tpd.getStatus()!=1){
			return null;
		}
		ProcessDefinition newPd=new ProcessDefinition(tpd);//用这个对象复制一份对象出来
		//流程版本号
		Long count =Db.findFirst("select count(*)  as count from tb_pd_processdefinition pd where pd.name=? and pd.status!=1",newPd.getName())
               .getLong("count");
		if(count==null){
			count=0l;
		}
		newPd.setVersion(count.intValue()+1);
		newPd.setStatus(ProcessDefinition.PROCESS_DEFINITION_STATUS_PUBLISHED);
     	if(newPd.save()){
     		return newPd;
     	}
     		
		return null;
	}
	
	
	/**
	 *   复制方式发布草稿流程
	 */
	public synchronized ProcessDefinition copyProcess(ProcessDefinition tpd,String newProcessName) {
		ProcessDefinition newPd=new ProcessDefinition(tpd);//用这个对象复制一份对象出来
 		newPd.setVersion(0);
		newPd.setName(newProcessName);
		newPd.setDesc(newProcessName);
		newPd.setStatus(ProcessDefinition.PROCESS_DEFINITION_STATUS_SKETCH);
     	if(newPd.save()){
     		return newPd;
     	}
     		
		return null;
	}

	/**
	 * 清除所有的流程定义
	 */
	@Override
	public void ClearAllProcessDefinition() {
	 		List<ProcessDefinition> pds=ProcessDefinition.dao.find("select * from tb_pd_processdefinition");
     		if(pds!=null){
     			for(ProcessDefinition pd:pds){
     				pd.delete();
     			}
     		}
     	
	}
	
	/**
	 * 清除所有的草稿流程
	 */
	@Override
	public void ClearAllSketchProcessDefinition() {
	 		List<ProcessDefinition> pds=ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.status=1");
     		if(pds!=null){
     			Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						for(ProcessDefinition pd:pds){
							if(!pd.delete()){
								return false;
							}
		     			}
						return true;
					}
				});
     			
     		}
    }

	
	/**
	 * 根据id获得流程定义
	 */
	@Override
	public ProcessDefinition getProcessDefinition(String pid) {
		List<ProcessDefinition> pds= ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.id=?",pid);
		if(pds!=null &&pds.size()>0){
			return pds.get(0);
		}
		return null;
	}
	

	/**
	 * 根据pid获得 tb_pd_processhistory(最近一条数据)
	 */
	@Override
	public ProcessHistory getNearestProcessHistory(String pid) {
		List<ProcessHistory> phs= ProcessHistory.dao.find("select * from tb_pd_processhistory ph where ph.pid=? order by ph.time desc",pid);
		if(phs!=null &&phs.size()>0){
			return phs.get(0);
		}
		return null;
	}
	
	
	/**
	 * 查找所有已经发布的汉程
	 */
	@Override
	public List<ProcessDefinition> findAllDeployProcess() {
		return ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.status=2");
	}

	/**
	 * 查找所有的草稿流程
	 */
	@Override
	public List<ProcessDefinition> findAllSketchProcess() {
		return ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.status=1");
	}

	/**
	 * 查找流程定义(这里仅查找发布的流程定义中的最大版本)
	 */
	@Override
	public ProcessDefinition findProcessDefinitionByName(String name) {
		List<ProcessDefinition> pds=ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.name=? and pd.version!=0 and pd.status=2 order by pd.version desc",name);
		if(pds!=null &&pds.size()>0){
			return pds.get(0);
		}
		return null;
	}

	/**
	 * 返回指定版本的流程
	 */
	@Override
	public ProcessDefinition findProcessDefinitionByName(String name,
			int version) {
		List<ProcessDefinition> pds= ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.name=? and pd.version=?",name,version);
		if(pds!=null &&pds.size()>0){
			return pds.get(0);
		}
		return null;
	}
	
	/**
	 * 删除指定名称的流程定义
	 */
	@Override
	public void deleteDeployProcess(String name) {
	 		List<ProcessDefinition> pds=ProcessDefinition.dao.find("select * from tb_pd_processdefinition pd where pd.name=? and pd.status=2",name);
     		if(pds!=null){
     			Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						for(ProcessDefinition pd:pds){
							if(!pd.delete()){
								return false;
							}
		     			}
						return true;
					}
				});
     		}
	}

	@Override
	public void deleteDeployProcessById(String pid) {
	
     		ProcessDefinition pd=ProcessDefinition.dao.findById(pid);
     		if(pd!=null){
     			pd.delete();
     		}
    
	}

	@Override
	public void deleteSketchProcess(String name) {
		List<ProcessDefinition> pds = ProcessDefinition.dao
				.find("select * from tb_pd_processdefinition pd where pd.name=? and (pd.status=1 or pd.status=0)",
						name);

		if (pds != null) {
			Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					for (ProcessDefinition pd : pds) {
						if(!pd.delete()){
							return false;
						}
					}
					return true;
				}
			});

		}

	}

	@Override
	public void deleteSketchProcessById(String pid) {
     		ProcessDefinition pd=ProcessDefinition.dao.findById(pid);
     		if(pd!=null){
     			pd.delete();
     		}
     
	}

}
