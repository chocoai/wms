package com.xyy.workflow.service.inf;

import java.util.List;

import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.definition.ProcessHistory;

/**
 * 流程资源仓库服务，主要用于流程定义的发布，查找，删除等流程定义相关的工作 
 * 
 * @author evan
 *
 */
public interface RepositoryService {
	/**
	 * 发布草稿状态的流程
	 * 
	 * @param tpd:草稿流程定义
	 * @return
	 */
	public ProcessDefinition deployProcess(ProcessDefinition tpd);

	/**
	 * 清空所有的流程定义，这将导致所有的流程实例被删除
	 */
	public void ClearAllProcessDefinition();
	
	/**
	 * 清空所有的草稿流程定义
	 */
	public void ClearAllSketchProcessDefinition();
	

	/**
	 * 查寻所有已经发布的流程定义
	 * 
	 * @return
	 */
	public List<ProcessDefinition> findAllDeployProcess();
	
	


	
	/**
	 * 查寻所有未发布的流程定义
	 * 
	 * @return
	 */
	public List<ProcessDefinition> findAllSketchProcess();
	
	

	
	
	
    /**
     * 按名称查找流程定义，默认返回流程定义的最新版本(已经发布的)
     * @param name
     * @return
     */
    public  ProcessDefinition findProcessDefinitionByName(String name);
	
	
    /**
     * 按名称查找流程的指定版本
     * @param name
     * @return
     */
    public  ProcessDefinition findProcessDefinitionByName(String name,int version);
	
    /**
     * 删除名为name的流程定义，这将删除其所有的流程版本及实例
     * @param name
     */
    public  void deleteDeployProcess(String name);
    
    
 
    public ProcessDefinition getProcessDefinition(String pid);
    
    public ProcessHistory getNearestProcessHistory(String pid);
    
    
    /**
     *利用pid删除流程实例 
     * @param pid
     */
    public  void deleteDeployProcessById(String pid);  
    
   
    
    /**
     * 删除名称name的流程的草稿
     * @param name
     */
    public  void deleteSketchProcess(String name);
    
   
    /**
     * 利用pid删除草稿流程定义
     * @param pid
     */
    public  void deleteSketchProcessById(String pid);
    
    

}
