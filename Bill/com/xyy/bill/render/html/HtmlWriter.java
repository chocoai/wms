package com.xyy.bill.render.html;

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
public class HtmlWriter extends StringWriter {
	private String renderID = UUIDUtil.newUUID();
	// 脚本链接路径
	private List<String> scriptLinks = new ArrayList<String>();
	// css链接路径
	private List<String> cssLinks = new ArrayList<String>();
	// css block
	private List<String> cssBlocks = new ArrayList<String>();
	// meta info
	private StringBuffer metaInfo = new StringBuffer();

	private String title = "xyybill";// 标题

	// 全局包装函数
	@SuppressWarnings("unused")
	private JSFunction globalFunction = new JSFunction("_$global");
	// 主控制器函数
	private JSFunction mainControllerFunction = new JSFunction("mainCtl"+UUIDUtil.newUUID(),
			"$scope, $http, $interval, $rootScope,$compile,$q");
	
	// 需要管理的其他注册函数
	private Map<String, JSFunction> jsFunctionMap = new HashMap<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private Stack<TagState> stack = new Stack<TagState>();

	private static String TAG_SYMBOL = "<%TAG%>";

	public String getRenderID() {
		return renderID;
	}

	public void setRenderID(String renderID) {
		this.renderID = renderID;
	}

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

	public void registerScriptLink(String scriptLink) {
		this.scriptLinks.add(scriptLink);
	}

	public void registerEmbedCssBlock(String cssBolck) {
		this.cssBlocks.add(cssBolck);
	}

	public void registerCssLink(String cssLink) {
		this.cssLinks.add(cssLink);
	}

	public void registerMetaInfo(String meta) {
		this.metaInfo.append(meta);
	}



	public JSFunction getMainControllerFunction() {
		return mainControllerFunction;
	}

	public Map<String, JSFunction> getJsFunctionMap() {
		return jsFunctionMap;
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

	/**
	 * 输出HTML的头部
	 * 
	 * @return
	 */
	protected String getHtmlHead() {
		StringBuffer sb = new StringBuffer();
		sb.append("<head>");
		// 相对的路径
		sb.append("<base href=\"/\" />");
		// 编码元信息
		sb.append("<meta charset=\"UTF-8\">").append(this.metaInfo.toString());
		// viewport元信息
		sb.append(
				"<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no\">");

		// 标题
		sb.append("<title>");
		sb.append(this.getTitle());
		sb.append("</title>");
		// 样式部分
		sb.append("<link rel='stylesheet' href='../lib/css/bootstrap.css'>");
		sb.append("<link rel='stylesheet' href='../lib/css/css.css'>");
		// css链接块
		for (String css : this.cssLinks) {
			sb.append("<link rel='stylesheet' type='text/css' href='").append(css).append("' />");
		}
		sb.append("<script src=\"//cdn.bootcss.com/jquery/1.11.1/jquery.min.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/bootstrap/3.3.0/js/bootstrap.min.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/angular.js/1.5.10/angular.min.js\"></script>");
		sb.append("<script src=\"/lib/js/util.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/angular-ui-bootstrap/2.4.0/ui-bootstrap-tpls.min.js\"></script>");

		sb.append(" <script src=\"/Shared/myDirective.js\"></script>");
		sb.append("<script src=\"/Shared/SharedService.js\"></script>");

		// 通过标识引入不同业务js文件
		sb.append("<script src=\"/lib/js/dictionary.js\"></script>");
		sb.append("<script src=\"/lib/js/bill.js\"></script>");
		sb.append("<script src=\"/lib/js/multiBill.js\"></script>");
		

		// 脚本链接块
		for (String script : this.scriptLinks) {
			sb.append("<script ").append("src='").append(script).append("'></script>");
		}
		// css注入块
		sb.append("<style>");
		for (String css : this.cssBlocks) {
			sb.append(css);
		}
		sb.append("</style>");
		sb.append("</head>");

		return sb.toString();

	}

	/**
	 * 输出HTML
	 * 
	 * @return
	 */
	public String toHtml() {
		// ------------写html元素--------------
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"cn\"  ng-app=\"xyyerp\">");
		sb.append(this.getHtmlHead());
		sb.append("<body>");
		sb.append(this.toString());
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * 输出视图HTML，局部页面
	 * 
	 * @return
	 */
	public String toViewportHtml() {
		// 至写入渲染的内容
		return this.toString();
	}

	public String toHtml(BillType billType) {
		// ------------写html元素--------------
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"cn\"  ng-app=\"xyyerp\">");
		sb.append(this.getHtmlHead(billType));
		sb.append("<body>");
		sb.append(this.toString());
		/**
		 * 1.链接主控制器与全局控制器 2.在渲染最后阶段做链接工作，这样渲染之前，其他代码 有机会修改全局函数与主控制器函数
		 */
		sb.append("<script>");
		sb.append("main.controller('xyyerp." + this.mainControllerFunction.getName() + "',")
				.append(this.mainControllerFunction.toString()).append(")").append(";");
		sb.append("</script>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}
	
	/**
	 * 输出单据的裸html(仅包含html)
	 * @return
	 */
	public String toBillBareHtml() {
		// ------------写html元素--------------
		StringBuffer sb = new StringBuffer();
		sb.append(this.toString());
		/**
		 * 1.链接主控制器与全局控制器 2.在渲染最后阶段做链接工作，这样渲染之前，其他代码 有机会修改全局函数与主控制器函数
		 */
		sb.append("<script>");
		sb.append("main.controller('xyyerp." + this.mainControllerFunction.getName() + "',")
				.append(this.mainControllerFunction.toString()).append(")").append(";");
		sb.append("</script>");
		return sb.toString();
	}
	
	
	public String toBillBareController() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.mainControllerFunction.toBareString());
		return sb.toString();
	}

	
	
	private Object getHtmlHead(BillType billType) {
		StringBuffer sb = new StringBuffer();
		sb.append("<head>");
		// 相对的路径
		sb.append("<base href=\"/\" />");
		// 编码元信息
		sb.append("<meta charset=\"UTF-8\">").append(this.metaInfo.toString());
		// viewport元信息
		sb.append(
				"<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no\">");
		// 标题
		sb.append("<title>");
		sb.append(this.getTitle());
		sb.append("</title>");
		// 样式部分
		sb.append("<link rel='stylesheet' href='../lib/css/bootstrap.css'>");
		sb.append("<link rel='stylesheet' href='../lib/css/css.css'>");
		// css链接块
		for (String css : this.cssLinks) {
			sb.append("<link rel='stylesheet' type='text/css' href='").append(css).append("' />");
		}
		sb.append("<script src=\"//cdn.bootcss.com/jquery/1.11.1/jquery.min.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/bootstrap/3.3.0/js/bootstrap.min.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/angular.js/1.5.10/angular.min.js\"></script>");
		sb.append("<script src=\"/lib/js/util.js\"></script>");
		//应用入口模块配置函数
		sb.append("<script src=\"/lib/js/xyyErpApp.js\"></script>");
		sb.append("<script src=\"//cdn.bootcss.com/angular-ui-bootstrap/2.4.0/ui-bootstrap-tpls.min.js\"></script>");
		sb.append(" <script src=\"/Shared/myDirective.js\"></script>");
		sb.append("<script src=\"/Shared/SharedService.js\"></script>");
		sb.append("<script src=\"/lib/js/custom.js\"></script>");
		sb.append("<script src=\"/lib/js/coreLib.js\"></script>");
		
		// 通过标识引入不同业务js文件
		switch(billType){
			case Bill:
			case Bills:
				sb.append("<script src=\"/lib/js/bill.js\"></script>");
				break;
			case Dictionary:
			case Dics:
				sb.append("<script src=\"/lib/js/dictionary.js\"></script>");
				break;
			case MultiBill:
				sb.append("<script src=\"/lib/js/multiBill.js\"></script>");
				break;
			case Report:
				sb.append("<script src=\"/lib/js/report.js\"></script>");
				break;
		}
		
		
		// 脚本链接块
		for (String script : this.scriptLinks) {
			sb.append("<script ").append("src='").append(script).append("'></script>");
		}
		// css注入块
		sb.append("<style>");
		for (String css : this.cssBlocks) {
			sb.append(css);
		}
		sb.append("</style>");
		sb.append("</head>");
		return sb.toString();
	}
}
