package com.xyy.expression;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdom.Element;
import org.xml.sax.helpers.DefaultHandler;

import com.xyy.util.XMLUtil;
import com.xyy.util.xml.ListReader;
import com.xyy.util.xml.NotReadableException;
/**
 * 语法规则构建器
 * @author evan
 *
 */
public class GrammarHandler extends DefaultHandler {
	private static List<Grammar> grammars;

	static {
		grammars = new ArrayList<Grammar>();
	}

	public GrammarHandler() {

	}

	public synchronized List<Grammar> buildGrammars() {
		this.buildGrammarTable();
		// 对集合按优先级进行排序t
		Collections.sort(grammars, new Comparator<Grammar>() {
			@Override
			public int compare(Grammar o1, Grammar o2) {
				int p1 = o1.getPriority();
				int p2 = o2.getPriority();
				if (p1 > p2) {
					return 1;
				} else if (p1 == p2) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		return grammars;
	}

	public static final String GRAMMAR_ROOT = "grammars";
	public static final String GRAMMAR_ELEMENT = "grammar";
	public static final String GRAMMAR_CONFIG_URL = "/com/xyy/expression/grammar.xml";

	private void buildGrammarTable() {
		URL config = GrammarHandler.class.getResource(GRAMMAR_CONFIG_URL);
		Element root = XMLUtil.readXMLFile(config);
		if (root != null && root.getName().equals(GRAMMAR_ROOT)) {
			ListReader lr = new ListReader(Grammar.class, GRAMMAR_ELEMENT);
			try {
				grammars = (List<Grammar>) lr.readXML(root);
			} catch (NotReadableException e) {
				e.printStackTrace();
			}
		}
	}
}
