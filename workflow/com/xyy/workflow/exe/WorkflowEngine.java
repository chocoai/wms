package com.xyy.workflow.exe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jdom.Element;

import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.ClassFactory;
import com.xyy.util.XMLUtil;
import com.xyy.workflow.definition.ActivityInstance;
import com.xyy.workflow.definition.ProcessDefinition;
import com.xyy.workflow.inf.IEvents;
import com.xyy.workflow.inf.INodeProcessor;
import com.xyy.workflow.inf.ITaskAssignment;

public class WorkflowEngine {
	//初始化脚本管理器
	public static ScriptEngineManager manager=new ScriptEngineManager();
	//调用脚本引擎
	public static ScriptEngine nashornEngine = manager.getEngineByName("nashorn");
	private static WorkflowEngine engine;
	private final static String config = "workflow.cfg.xml";
	private IEvents defaultProcessStartEventListener;  //流程开始事件hook
	private IEvents defaultProcessEndEventListener;    //流程结束事件hook
	private IEvents defaultActivityStartEventListener; //活动开始事件hook
	private IEvents defaultActivityEndEventListener;   //活动结束事件hook
	private ITaskAssignment defaultTaskAssignmentor;   //任务分配器
	private String defaultTempalteDir;                 //默认模板路径

	private Map<String, INodeProcessor> np_caches = new HashMap<String, INodeProcessor>();

	public INodeProcessor getNodeProcessor(ActivityInstance ai) {
		String nodeType = ai.getActivityDefinition().getType();// 获取节点类型
		if (np_caches.containsKey(nodeType)) {
			return np_caches.get(nodeType);
		}
		return null;
	}

	public boolean isSusportedActivityNode(String nodeName) {
		return this.np_caches.containsKey(nodeName);
	}

	public static WorkflowEngine instance() throws Exception{
		if (engine == null) {
			synchronized (WorkflowEngine.class) {
				if (engine == null) {
					engine = new WorkflowEngine();
					try {
						engine.configuration();
					} catch (Exception e) {
						
					}
					
				}
			}
		}
		return engine;
	}

	/*
	 * 私有默认构造函数
	 */
	private WorkflowEngine() {

	}

	/**
	 * 流程引擎对象的配置
	 */
	public void configuration() throws Exception {
		Element root = XMLUtil.readXMLFile(ProcessDefinition.class
				.getResource("/" + config));
		if (root == null) {
			throw new Exception("process engine config file not found.");
		}

		Element e1 = root.getChild("default-process-start-event-listener");
		if (e1 != null && !StringUtil.isEmpty(e1.getAttributeValue("class"))) {
			this.defaultProcessStartEventListener = (IEvents) ClassFactory
					.CreateObject(e1.getAttributeValue("class"));
		}

		Element e2 = root.getChild("default-process-end-event-listener");
		if (e2 != null && !StringUtil.isEmpty(e2.getAttributeValue("class"))) {
			this.defaultProcessEndEventListener = (IEvents) ClassFactory
					.CreateObject(e2.getAttributeValue("class"));
		}

		Element e3 = root.getChild("default-activity-start-event-listener");
		if (e3 != null && !StringUtil.isEmpty(e3.getAttributeValue("class"))) {
			this.defaultActivityStartEventListener = (IEvents) ClassFactory
					.CreateObject(e3.getAttributeValue("class"));
		}

		Element e4 = root.getChild("default-activity-end-event-listener");
		if (e4 != null && !StringUtil.isEmpty(e4.getAttributeValue("class"))) {
			this.defaultActivityEndEventListener = (IEvents) ClassFactory
					.CreateObject(e4.getAttributeValue("class"));
		}

		Element e5 = root.getChild("default-task-assignment-handler");
		if (e5 != null && !StringUtil.isEmpty(e5.getAttributeValue("class"))) {
			this.defaultTaskAssignmentor = (ITaskAssignment) ClassFactory
					.CreateObject(e5.getAttributeValue("class"));
		}

		// default-template-dir
		Element e6 = root.getChild("default-template-dir");
		if (e6 != null) {
			this.defaultTempalteDir = e6.getAttributeValue("dir");
		}
		// 获取活动元素列表
		Element aes = root.getChild("activities");
		if (aes == null) {
			throw new Exception("activities element is null.");
		}

		@SuppressWarnings("unchecked")
		List<Element> activityListEle = aes.getChildren("activity");
		if (activityListEle == null) {
			throw new Exception("activity element is null.");
		}

		for (Element activityEle : activityListEle) {
			String name = activityEle.getAttributeValue("name");
			String nodeProcessClazz = activityEle
					.getAttributeValue("node-process");
			if (StringUtil.isEmpty(name)
					|| StringUtil.isEmpty(nodeProcessClazz)) {
				throw new Exception("activity element definition error.");
			}

			INodeProcessor np = (INodeProcessor) ClassFactory
					.CreateObject(nodeProcessClazz);
			if (np == null) {
				throw new Exception("INodeProcessor create failed.");
			}
			this.np_caches.put(name, np);
		}

	}

	public IEvents getDefaultProcessStartEventListener() {
		return defaultProcessStartEventListener;
	}

	public void setDefaultProcessStartEventListener(
			IEvents defaultProcessStartEventListener) {
		this.defaultProcessStartEventListener = defaultProcessStartEventListener;
	}

	public IEvents getDefaultActivityStartEventListener() {
		return defaultActivityStartEventListener;
	}

	public void setDefaultActivityStartEventListener(
			IEvents defaultActivityStartEventListener) {
		this.defaultActivityStartEventListener = defaultActivityStartEventListener;
	}

	public IEvents getDefaultActivityEndEventListener() {
		return defaultActivityEndEventListener;
	}

	public void setDefaultActivityEndEventListener(
			IEvents defaultActivityEndEventListener) {
		this.defaultActivityEndEventListener = defaultActivityEndEventListener;
	}

	public ITaskAssignment getDefaultTaskAssignmentor() {
		return defaultTaskAssignmentor;
	}

	public void setDefaultTaskAssignmentor(
			ITaskAssignment defaultTaskAssignmentor) {
		this.defaultTaskAssignmentor = defaultTaskAssignmentor;
	}

	public IEvents getDefaultProcessEndEventListener() {
		return defaultProcessEndEventListener;
	}

	public void setDefaultProcessEndEventListener(
			IEvents defaultProcessEndEventListener) {
		this.defaultProcessEndEventListener = defaultProcessEndEventListener;
	}

	public String getDefaultTempalteDir() {
		return defaultTempalteDir;
	}

	public void setDefaultTempalteDir(String defaultTempalteDir) {
		this.defaultTempalteDir = defaultTempalteDir;
	}

}
