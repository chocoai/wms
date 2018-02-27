package com.xyy.bill.meta;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.xyy.util.ColorUtil;
import com.xyy.util.StringUtil;
import com.xyy.util.xml.annotation.XmlAttribute;
import com.xyy.util.xml.annotation.XmlComponent;
import com.xyy.util.xml.annotation.XmlList;

@XmlComponent(tag = "Style", type = Style.class)
public class Style {
	private String key;
	private String caption;
	private Background background;
	private Align align;
	private List<Border> borders = new ArrayList<>();
	private Font font;

	public Style() {
		super();
	}

	@XmlAttribute(name = "key", tag = "Key", type = String.class)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlAttribute(name = "caption", tag = "Caption", type = String.class)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@XmlComponent(name = "background", tag = "Background", type = Background.class)
	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	@XmlComponent(name = "align", tag = "Align", type = Align.class)
	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	@XmlList(name = "borders", tag = "Borders", subTag = "Border", type = Border.class)
	public List<Border> getBorders() {
		return borders;
	}

	public void setBorders(List<Border> borders) {
		this.borders = borders;
	}

	@XmlComponent(name = "font", tag = "Font", type = Font.class)
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.background != null) {
			sb.append(this.background.toString());
		}
		if (this.align != null) {
			sb.append(this.align.toString());
		}
		if (this.font != null) {
			sb.append(this.font.toString());
		}
		for (Border border : this.borders) {
			sb.append(border.toString());
		}

		return sb.toString();
	}

	@XmlComponent(tag = "Font", type = Font.class)
	public static class Font {
		private String family;
		private int size = -1;
		private Color color;
		private boolean bold = false;
		private boolean italic = false;

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (!StringUtil.isEmpty(family)) {
				// font-family: "宋体";
				sb.append("font-family:").append("\"").append(family).append("\"").append(";");
			}
			if (size > 0) {
				// font-size:12px;
				sb.append("font-size:").append(size).append("px").append(";");
			}
			if (color != null) {
				// color: #000;
				sb.append("color:").append(ColorUtil.toString(color)).append(";");
			}
			if (bold) {
				// font-weight: bold; /*bold加粗 normal默认*/
				sb.append("font-weight:").append("bold").append(";");
			}
			if (italic) {
				// font-style:italic; /*italic斜体 normal默认 */
				sb.append("font-style:").append("italic").append(";");
			}

			return sb.toString();
		}

		public Font() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Font(String family, int size, Color color, boolean bold, boolean italic) {
			super();
			this.family = family;
			this.size = size;
			this.color = color;
			this.bold = bold;
			this.italic = italic;
		}

		@XmlAttribute(name = "family", tag = "Family", type = String.class)
		public String getFamily() {
			return family;
		}

		public void setFamily(String family) {
			this.family = family;
		}

		@XmlAttribute(name = "size", tag = "Size", type = int.class)
		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		@XmlAttribute(name = "color", tag = "Color", type = Color.class)
		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		@XmlAttribute(name = "bold", tag = "Bold", type = boolean.class)
		public boolean getBold() {
			return bold;
		}

		public void setBold(boolean isBold) {
			this.bold = isBold;
		}

		@XmlAttribute(name = "italic", tag = "Italic", type = boolean.class)
		public boolean getItalic() {
			return italic;
		}

		public void setIsItalic(boolean isItalic) {
			this.italic = isItalic;
		}

	}

	@XmlComponent(tag = "Border", type = Border.class)
	public static class Border {
		private BorderDir dir;
		private int width;
		private Color color;
		private LineStyle lineStyle;

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (dir != null && dir != null && color != null && lineStyle != null) {
				switch (dir) {
				case left:
					sb.append("border-left: ").append(width).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(ColorUtil.toString(color)).append(";");
					break;
				case top:
					sb.append("border-top: ").append(width).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(ColorUtil.toString(color)).append(";");
					break;
				case right:
					sb.append("border-right: ").append(width).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(ColorUtil.toString(color)).append(";");
					break;
				case bottom:
					sb.append("border-bottom: ").append(width).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(ColorUtil.toString(color)).append(";");
					break;
				case all:
					sb.append("border: ").append(width).append("px").append(" ").append(lineStyle.toString())
							.append(" ").append(ColorUtil.toString(color)).append(";");
					break;
				default:
					break;
				}
			}
			return sb.toString();
		}

		public Border() {
			super();
		}

		@XmlAttribute(name = "dir", tag = "Dir", type = BorderDir.class)
		public BorderDir getDir() {
			return dir;
		}

		public void setDir(BorderDir dir) {
			this.dir = dir;
		}

		@XmlAttribute(name = "width", tag = "Width", type = int.class)
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		@XmlAttribute(name = "color", tag = "Color", type = Color.class)
		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		@XmlAttribute(name = "lineStyle", tag = "LineStyle", type = LineStyle.class)
		public LineStyle getLineStyle() {
			return lineStyle;
		}

		public void setLineStyle(LineStyle lineStyle) {
			this.lineStyle = lineStyle;
		}

	}

	public static enum LineStyle {
		solid
	}

	public static enum BorderDir {
		left, top, right, bottom, all
	}

	@XmlComponent(tag = "Align", type = Align.class)
	public static class Align {
		private HAlign hAlign = HAlign.left;
		private VAlign vAlign;

		public Align() {
			super();
		}

		public Align(HAlign hAlign, VAlign vAlign) {
			super();
			this.hAlign = hAlign;
			this.vAlign = vAlign;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (hAlign != null) {
				sb.append(" text-align:").append(hAlign.toString()).append(";");
			}
			// 水平对齐不做处理

			return sb.toString();
		}

		@XmlAttribute(name = "hAlign", tag = "HAlign", type = HAlign.class)
		public HAlign getHAlign() {
			return hAlign;
		}

		public void setHAlign(HAlign hAlign) {
			this.hAlign = hAlign;
		}

		@XmlAttribute(name = "vAlign", tag = "VAlign", type = VAlign.class)
		public VAlign getVAlign() {
			return vAlign;
		}

		public void setVAlign(VAlign vAlign) {
			this.vAlign = vAlign;
		}

	}

	public static enum HAlign {
		left, center, right
	}

	public static enum VAlign {
		top, middle, bottom
	}

	@XmlComponent(name = "background", tag = "Background", type = Background.class)
	public static class Background {
		private Color color;
		private String imgUrl;
		private BackgroundRepeat repeat = BackgroundRepeat.no_repeat;

		public Background() {
			super();
		}

		public Background(Color color, String imgUrl, BackgroundRepeat repeat) {
			super();
			this.color = color;
			this.imgUrl = imgUrl;
			this.repeat = repeat;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (color != null) {
				sb.append("background-color:").append(ColorUtil.toString(color)).append(";");
			}

			if (!StringUtil.isEmpty(imgUrl)) {
				sb.append("background-image:").append("url(").append(imgUrl).append(")").append(";");
				switch (this.repeat) {
				case no_repeat:
					sb.append("background-repeat:no-repeat").append(";");
					break;
				case repeat:
					sb.append("background-repeat:repeat").append(";");
					break;
				case repeat_x:
					sb.append("background-repeat:repeat-x").append(";");
					break;
				case repeat_y:
					sb.append("background-repeat:repeat-y").append(";");
					break;
				default:
					break;
				}
			}

			return sb.toString();
		}

		@XmlAttribute(name = "color", tag = "Color", type = Color.class)
		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		@XmlAttribute(name = "imgUrl", tag = "ImgUrl", type = String.class)
		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		@XmlAttribute(name = "repeat", tag = "background_repeat", type = BackgroundRepeat.class)
		public BackgroundRepeat getRepeat() {
			return repeat;
		}

		public void setRepeat(BackgroundRepeat repeat) {
			this.repeat = repeat;
		}

	}

	/**
	 * background-repeat : repeat | no-repeat | repeat-x | repeat-y
	 * 默认值。背景图像在纵向和横向上平铺 no-repeat : 背景图像不平铺 repeat-x : 背景图像仅在横向上平铺 repeat-y :
	 * 背景图像仅在纵向上平铺
	 * 
	 * @author evan
	 *
	 */
	public static enum BackgroundRepeat {
		repeat, no_repeat, repeat_x, repeat_y
	}
}
