package com.xyy.bill.instance;

/**
 * HTML构建接口
 * @author evan
 *
 */
public interface IHTMLBuilder {
	public void compileHtmlView() throws BillHtmlCompileException;
	public void linkedHtmlView()  throws BillHtmlLinkedException;
	public String renderHTMl() throws HtmlRenderException;
}
