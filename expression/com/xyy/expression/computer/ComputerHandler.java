package com.xyy.expression.computer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.xyy.expression.TreatComp;
import com.xyy.util.ReflectUtil;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ListReader;
import com.xyy.util.xml.NotReadableException;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
/**
 * 计算处理器构建器
 * @author evan
 *
 */
public class ComputerHandler {
	private static List<TreatComp> treatComps;
	static {
		treatComps = new ArrayList<TreatComp>();
	}
	private Computer context;

	public ComputerHandler(Computer context) {
		this.context = context;
	}

	public synchronized List<TreatComp> buildHandlers() {
		this.builderComputers();
		return treatComps;
	}

	public static final String COMPUTER_ROOT = "computer";
	public static final String COMPUTER_ELEMENT = "handler";
	public static final String COMPUTER_CONFIG_URL = "/com/xyy/expression/computer/computer_config.xml";

	private void builderComputers() {
		URL config = Computer.class.getResource(COMPUTER_CONFIG_URL);
		Element root = XMLUtil.readXMLFile(config);
		List<CompHandler> compHandlers = new ArrayList<ComputerHandler.CompHandler>();
		if (root != null && root.getName().equals(COMPUTER_ROOT)) {
			ListReader lr = new ListReader(CompHandler.class, COMPUTER_ELEMENT);
			try {
				compHandlers = (List<CompHandler>) lr.readXML(root);
			} catch (NotReadableException e) {
				e.printStackTrace();
			}
		}
		for (CompHandler handler : compHandlers) {
			try {
				treatComps.add((TreatComp)ReflectUtil.instance(handler.clazz, this.context));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@XmlComponent(tag = "handler", type = CompHandler.class)
	public static class CompHandler {
		private String name;
		private String clazz;

		public CompHandler() {

		}

		@XmlAttribute(name = "name", tag = "name", type = String.class)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@XmlAttribute(name = "clazz", tag = "type", type = String.class)
		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public TreatComp buildTreatComp(Computer context) {
			if (this.clazz != null) {
				Object o;
				try {
					o = Class.forName(this.clazz)
							.getConstructor(new Class[] { Computer.class })
							.newInstance(context);
					return (TreatComp) o;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}
}
