package com.xyy.expression.parser;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.xyy.expression.Operator;
import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ListReader;
import com.xyy.util.xml.NotReadableException;
/**
 * 解析器构建器
 * @author evan
 *
 */
public class ParserHandler {
	private static Map<String, Operator> operatorMap;
	static {
		operatorMap = new HashMap<String, Operator>();
	}

	public synchronized Map<String, Operator> buildTokenProcessor() {
		this.buildTokenProcessors();
		return operatorMap;
	}

	public static final String PARSER_ROOT = "parser";
	public static final String PARSER_ELEMENT = "operator";
	public static final String PARSER_CONFIG_URL = "/com/xyy/expression/parser/parser.xml";

	private void buildTokenProcessors() {
		URL config=Parser.class.getResource(PARSER_CONFIG_URL);
		Element root = XMLUtil.readXMLFile(config);
		if (root != null && root.getName().equals(PARSER_ROOT)) {
			ListReader lr = new ListReader(Operator.class, PARSER_ELEMENT);
			try {
				List<Operator> result = (List<Operator>) lr.readXML(root);
				if (result != null && !result.isEmpty()) {
					for (Operator ope : result) {
						operatorMap.put(ope.getOpe(), ope);
					}
				}
			} catch (NotReadableException e) {
				e.printStackTrace();
			}
		}
	}

}
