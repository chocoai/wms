package com.xyy.bill.print.meta;

import java.util.ArrayList;
import java.util.List;

import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlElement;
import com.xyy.util.xml.annotation.XmlList;

/**
 * 样式表设计
 * 
 * @author evan
 *
 */
@XmlComponent(tag = "Style", type = Style.class)
public class Style {
	public static enum Type {
		Class, Tag
	}

	private String key;// 样式key
	private String backgroundColor;// 背景颜色
	private Font font;// 字体
	private String css;
	
	
	
	
	// Padding="" Type="Class|Tag"
	private String padding;

	private Type type = Type.Class;

	@XmlAttribute(name = "padding", tag = "Padding", type = String.class)
	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	
	@XmlElement(name="css",tag="Css")
	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	@XmlAttribute(name = "type", tag = "Type", type = Type.class)
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	private List<Border> borders = new ArrayList<>();// 边框

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "backgroundColor", tag = "BackgroundColor", type = String.class)
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@XmlComponent(name = "font", tag = "Font", type = Font.class)
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	@XmlList(name = "borders", tag = "Borders", subTag = "Border", type = Border.class)
	public List<Border> getBorders() {
		return borders;
	}

	public void setBorders(List<Border> borders) {
		this.borders = borders;
	}

	/**
	 * 字体属性
	 * 
	 * @author evan
	 *         <Font Size="" Family="" Italic="true|fasle" Weight=""></Font>
	 */
	@XmlComponent(tag = "Font", type = Font.class)
	public static class Font {
		private int size = 12;
		private String color;
		private String family;
		private boolean italic;
		private String weight;

		@XmlAttribute(name = "color", tag = "Color", type = String.class)
		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		@XmlAttribute(name = "size", tag = "Size", type = int.class)
		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		@XmlAttribute(name = "family", tag = "Family", type = String.class)
		public String getFamily() {
			return family;
		}

		public void setFamily(String family) {
			this.family = family;
		}

		@XmlAttribute(name = "italic", tag = "Italic", type = boolean.class)
		public boolean getItalic() {
			return italic;
		}

		public void setItalic(boolean italic) {
			this.italic = italic;
		}

		@XmlAttribute(name = "weight", tag = "Weight", type = String.class)
		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String toCssString() {
			StringBuffer sb = new StringBuffer();
			if (!StringUtil.isEmpty(family)) {
				// font-family: "宋体";
				sb.append("font-family:").append("\"").append(family).append("\"").append(";");
			}
			if (size > 0) {
				sb.append("font-size:").append(size).append("px").append(";");
			}
			if (!StringUtil.isEmpty(color)) {
				sb.append("color:").append(color).append(";");
			}
			if (!StringUtil.isEmpty(this.weight)) {
				sb.append("font-weight:").append(this.weight).append(";");
			}
			if (italic) {
				sb.append("font-style:").append("italic").append(";");
			}
			return sb.toString();
		}
	}

	/**
	 * 边框属性 <Border Dir="Left|Top|Right|Bottom|All" LineSize="" LineColor=""
	 * LineStyle=""/>
	 * 
	 * @author evan
	 *
	 */
	@XmlComponent(tag = "Border", type = Border.class)
	public static class Border {
		private Dir dir;
		private String lineSize = "1";
		private String lineColor;
		private String lineStyle;

		@XmlAttribute(name = "dir", tag = "Dir", type = Dir.class)
		public Dir getDir() {
			return dir;
		}

		public void setDir(Dir dir) {
			this.dir = dir;
		}

		@XmlAttribute(name = "lineSize", tag = "LineSize", type = String.class)
		public String getLineSize() {
			return lineSize;
		}

		public void setLineSize(String lineSize) {
			this.lineSize = lineSize;
		}

		@XmlAttribute(name = "lineColor", tag = "LineColor", type = String.class)
		public String getLineColor() {
			return lineColor;
		}

		public void setLineColor(String lineColor) {
			this.lineColor = lineColor;
		}

		@XmlAttribute(name = "lineStyle", tag = "LineStyle", type = String.class)
		public String getLineStyle() {
			return lineStyle;
		}

		public void setLineStyle(String lineStyle) {
			this.lineStyle = lineStyle;
		}

		public String toCssString() {
			StringBuffer sb = new StringBuffer();
			if (dir != null && !StringUtil.isEmpty(this.lineColor) && !StringUtil.isEmpty(this.lineStyle)) {
				switch (dir) {
				case Left:
					sb.append("border-left: ").append(this.lineSize).append("px").append(" ")
							.append(lineStyle.toString()).append(" ").append(this.lineColor).append(";");
					break;
				case Top:
					sb.append("border-top: ").append(this.lineSize).append("px").append(" ")
							.append(lineStyle.toString()).append(" ").append(this.lineColor).append(";");
					break;
				case Right:
					sb.append("border-right: ").append(this.lineSize).append("px").append(" ")
							.append(lineStyle.toString()).append(" ").append(this.lineColor).append(";");
					break;
				case Bottom:
					sb.append("border-bottom: ").append(this.lineSize).append("px").append(" ")
							.append(lineStyle.toString()).append(" ").append(this.lineColor).append(";");
					break;
				case All:
					sb.append("border: ").append(this.lineSize).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(this.lineColor).append(";");
					break;
				default:
					break;
				}
			}
			return sb.toString();
		}

	}

	public static enum Dir {
		Left, Top, Right, Bottom, All
	}

	@Override
	public String toString() {
		return this.toCssString();
	}

	/**
	 * 输出CSS样式： 格式为：样式名称:样式值;
	 * 
	 * @return
	 */
	public String toCssString() {
		StringBuffer sb = new StringBuffer();
		if (!StringUtil.isEmpty(this.backgroundColor)) {// 背景色；
			sb.append("background-color:").append(this.backgroundColor).append(";");
		}
		if(!StringUtil.isEmpty(this.padding)){
			sb.append("padding:").append(this.padding).append(";");
		}
		if (this.font != null) {// 字体
			sb.append(this.font.toCssString());
		}
		for (Border border : this.borders) {// 边框
			sb.append(border.toCssString());
		}
		if(!StringUtil.isEmpty(this.css)){
			sb.append(this.css);
		}

		return sb.toString();
	}
}