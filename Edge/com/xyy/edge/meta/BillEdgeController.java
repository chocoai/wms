package com.xyy.edge.meta;

import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;

/**
 * 转换规则控制器实体
 * @author caofei
 *
 */
@XmlComponent(tag="BillEdgeController",type=BillEdgeController.class)
public class BillEdgeController {
	
	private MultiConvertCtrl multiConvertCtrl;
	
	private DisplayController displayController;
	
	private AutoSaveController autoSaveController;
	
	private BackWriteController backWriteController;
	
	@XmlComponent(name = "multiConvertCtrl", tag = "MultiConvertCtrl", type = MultiConvertCtrl.class)
	public MultiConvertCtrl getMultiConvertCtrl() {
		return multiConvertCtrl;
	}

	public void setMultiConvertCtrl(MultiConvertCtrl multiConvertCtrl) {
		this.multiConvertCtrl = multiConvertCtrl;
	}

	@XmlComponent(name = "displayController", tag = "DisplayController", type = DisplayController.class)
	public DisplayController getDisplayController() {
		return displayController;
	}

	public void setDisplayController(DisplayController displayController) {
		this.displayController = displayController;
	}

	@XmlComponent(name = "autoSaveController", tag = "AutoSaveController", type = AutoSaveController.class)
	public AutoSaveController getAutoSaveController() {
		return autoSaveController;
	}

	public void setAutoSaveController(AutoSaveController autoSaveController) {
		this.autoSaveController = autoSaveController;
	}

	@XmlComponent(name = "backWriteController", tag = "BackWriteController", type = BackWriteController.class)
	public BackWriteController getBackWriteController() {
		return backWriteController;
	}

	public void setBackWriteController(BackWriteController backWriteController) {
		this.backWriteController = backWriteController;
	}

	@XmlComponent(tag = "MultiConvertCtrl", type = MultiConvertCtrl.class)
	public static class MultiConvertCtrl{
		
		private CtryType ctrType;
		
		public MultiConvertCtrl() {
			super();
		}

		public MultiConvertCtrl(CtryType ctrType) {
			super();
			this.ctrType = ctrType;
		}

		@XmlAttribute(name = "ctrType", tag = "CtrType", type=CtryType.class)
		public CtryType getCtrType() {
			return ctrType;
		}

		public void setCtrType(CtryType ctrType) {
			this.ctrType = ctrType;
		}
		
		public static enum CtryType {
			AllowAndWarning, AllowAndNotWarning, Forbid
		}
		
	}
	
	@XmlComponent(tag = "DisplayController", type = DisplayController.class)
	public static class DisplayController{
		
		private CtryType ctrType;
		
		public DisplayController() {
			super();
		}

		public DisplayController(CtryType ctrType) {
			super();
			this.ctrType = ctrType;
		}

		@XmlAttribute(name = "ctrType", tag = "CtrType", type=CtryType.class)
		public CtryType getCtrType() {
			return ctrType;
		}

		public void setCtrType(CtryType ctrType) {
			this.ctrType = ctrType;
		}
		
		public static enum CtryType {
			NoGo, GoEditor, GoList
		}
		
	}
	
	@XmlComponent(tag = "AutoSaveController", type = AutoSaveController.class)
	public static class AutoSaveController {
		
		private CtryType ctrType;
		
		public AutoSaveController() {
			super();
		}

		public AutoSaveController(CtryType ctrType) {
			super();
			this.ctrType = ctrType;
		}

		@XmlAttribute(name = "ctrType", tag = "CtrType", type=CtryType.class)
		public CtryType getCtrType() {
			return ctrType;
		}

		public void setCtrType(CtryType ctrType) {
			this.ctrType = ctrType;
		}
		
		public static enum CtryType {
			NoSave, AutoSave, AutoSaveAndSubmit
		}
		
	}
	
	@XmlComponent(tag = "BackWriteController", type = BackWriteController.class)
	public static class BackWriteController {
		
		private CtryType ctrType=CtryType.Save;
		
		public BackWriteController() {
			super();
		}

		public BackWriteController(CtryType ctrType) {
			super();
			this.ctrType = ctrType;
		}

		@XmlAttribute(name = "ctrType", tag = "CtrType", type=CtryType.class)
		public CtryType getCtrType() {
			return ctrType;
		}

		public void setCtrType(CtryType ctrType) {
			this.ctrType = ctrType;
		}
		
		public static enum CtryType {
			Save, Submit, SaveAndSubmit
		}
		
	}
	
	

}
