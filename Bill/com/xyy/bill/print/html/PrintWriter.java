package com.xyy.bill.print.html;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.xyy.bill.meta.BillFormMeta.BillType;
import com.xyy.util.StringUtil;
import com.xyy.util.UUIDUtil;

/**
 * html输出辅助类
 * 
 * @author evan
 *
 */
public class PrintWriter extends StringWriter {
	// css block
	private List<String> cssBlocks = new ArrayList<String>();
	private Stack<TagState> stack = new Stack<TagState>();

	private static String TAG_SYMBOL = "<%TAG%>";

	/**
	 * 写如开始标记
	 * 
	 * @param tag
	 */
	public void writeBeginTag(String tag) {
		// 如果是第二次写入开始标记
		if (!this.stack.isEmpty()) {
			List<TagState> csses = new ArrayList<TagState>();
			List<TagState> attrs = new ArrayList<TagState>();
			// 获取上一个压入堆栈的TagState，直到遇到上一个开始标记
			TagState temp = stack.pop();
			while (true) {
				if (temp.getPrefix().equals(TAG_SYMBOL)) {// 如果其为上一个标记，停止循环
					break;
				} else {
					if (temp.getPrefix().equals(CSS_SYMBOL)) {// css写入csses样式列表
						csses.add(temp);
					} else if (temp.getPrefix().equals(ATTR_SYMBOL)) {// 属性写入attrs属性列表
						attrs.add(temp);
					}
				}

				temp = stack.pop();// 循环出堆栈，直到上一个开始标记为止
			}

			this.printBeginTag(temp, csses, attrs);

			temp.isWriter = true;// 标记上衣个开始标记已经输出到字符流了

			// 压回到堆栈中
			stack.push(temp);
		}

		// 当前标记入栈
		stack.push(new TagState(TAG_SYMBOL, tag, false));
	}

	private void printBeginTag(TagState tag, List<TagState> csses, List<TagState> attrs) {
		if (tag.isWriter) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		sb.append(tag.value);
		if (!attrs.isEmpty()) {
			for (TagState attr : attrs) {
				sb.append(" ");
				sb.append(attr.value);
				sb.append(" ");
			}
		} else {
			sb.append(" ");
		}
		if (!csses.isEmpty()) {
			sb.append("style=\"");
			for (TagState css : csses) {
				sb.append(css.value);
			}

			sb.append("\"");
		}

		sb.append(">");
		tag.isWriter = true;
		this.append(sb);
	}

	private static String CSS_SYMBOL = "<%CSS%>";

	/**
	 * 写如CSS样式
	 * 
	 * @param css
	 * @param value
	 */
	public void writeStyle(String css, String value) {
		stack.push(new TagState(CSS_SYMBOL, css + ":" + value + ";", false));
	}

	private static String ATTR_SYMBOL = "<%ATT%>";

	/**
	 * 写入属性值
	 */
	public void writeProperty(String property, Object value) {
		if (value == null || StringUtil.isEmpty(value.toString())) {
			stack.push(new TagState(ATTR_SYMBOL, property, false));
		} else {
			stack.push(new TagState(ATTR_SYMBOL, property + "=\"" + value + "\"", false));
		}

	}

	/**
	 * 写入文本内容
	 * 
	 * @param text
	 */
	public void writeText(String text) {
		if (!this.stack.isEmpty()) {
			List<TagState> csses = new ArrayList<TagState>();
			List<TagState> attrs = new ArrayList<TagState>();
			TagState temp = stack.pop();
			while (true) {
				if (temp.getPrefix().equals(TAG_SYMBOL)) {
					break;
				} else {
					if (temp.getPrefix().equals(CSS_SYMBOL)) {
						csses.add(temp);
					} else if (temp.getPrefix().equals(ATTR_SYMBOL)) {
						attrs.add(temp);
					}
				}
				temp = stack.pop();
			}
			this.printBeginTag(temp, csses, attrs);
			stack.push(temp);// 重新入栈,只有endTag能真正弹出它
			this.append(text);
		}
	}

	/**
	 * 写入结束标记
	 * 
	 * @param tag
	 */
	public void writeEndTag(String tag) {

		if (!this.stack.isEmpty()) {// 写结束标记是，堆栈不能为空
			List<TagState> csses = new ArrayList<TagState>();
			List<TagState> attrs = new ArrayList<TagState>();
			// 反复弹出堆栈中的元素，直到入到遇到一个标记元素
			TagState temp = stack.pop();
			while (true) {
				if (temp.getPrefix().equals(TAG_SYMBOL)) {
					break;
				} else {
					if (temp.getPrefix().equals(CSS_SYMBOL)) {
						csses.add(temp);
					} else if (temp.getPrefix().equals(ATTR_SYMBOL)) {
						attrs.add(temp);
					}
				}
				temp = stack.pop();
			}
			// 首先输出开始标记
			this.printBeginTag(temp, csses, attrs);
			// 输出结束标记
			this.append("</");
			this.append(tag);
			this.append(">");
		}

	}

	/**
	 * 写入结束标记,用与input之类不需要明确结束的
	 * 
	 * @param tag
	 */
	public void writeEndTag() {

		if (!this.stack.isEmpty()) {// 写结束标记是，堆栈不能为空
			List<TagState> csses = new ArrayList<TagState>();
			List<TagState> attrs = new ArrayList<TagState>();
			// 反复弹出堆栈中的元素，直到入到遇到一个标记元素
			TagState temp = stack.pop();
			while (true) {
				if (temp.getPrefix().equals(TAG_SYMBOL)) {
					break;
				} else {
					if (temp.getPrefix().equals(CSS_SYMBOL)) {
						csses.add(temp);
					} else if (temp.getPrefix().equals(ATTR_SYMBOL)) {
						attrs.add(temp);
					}
				}
				temp = stack.pop();
			}

			// 首先输出开始标记
			this.printBeginTag(temp, csses, attrs);

		}

	}

	public void registerEmbedCssBlock(String cssBolck) {
		this.cssBlocks.add(cssBolck);
	}

	private class TagState {
		private String prefix;
		private String value;
		private boolean isWriter = false;

		public TagState(String prefix, String value, boolean isWriter) {
			super();
			this.prefix = prefix;
			this.value = value;
			this.isWriter = isWriter;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	public String getEmbedCssStyle() {
		StringBuffer styles=new StringBuffer();
		styles.append("<style>");
		for(String css:this.cssBlocks){
			styles.append(css);
		}
		styles.append("</style>");
		return styles.toString();
	}




}
