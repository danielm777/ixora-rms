package com.ixora.common.ui;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/*
 * Created on 09-Dec-03
 */
/**
 * Metal theme whose properties are taken from an input stream.<br>
 * Based on Swing samples.
 * @author Daniel Moraru
 */
public class PropertiesTheme extends DefaultMetalTheme {
	protected ColorUIResource primary1;
	protected ColorUIResource primary2;
	protected ColorUIResource primary3;
	protected ColorUIResource secondary1;
	protected ColorUIResource secondary2;
	protected ColorUIResource secondary3;
	protected ColorUIResource black;
	protected ColorUIResource white;
	protected FontUIResource userText;
	protected FontUIResource menuText;
	protected FontUIResource windowTitle;
	protected FontUIResource controlText;
	protected FontUIResource systemText;
	protected FontUIResource subText;

	/**
	 * Metal theme whose properties are taken from an input stream.
	 * @param is
	 * @throws IOException
	 */
	public PropertiesTheme(InputStream is) throws IOException {
		super();
		primary1 = super.getPrimary1();
		primary2 = super.getPrimary2();
		primary3 = super.getPrimary3();
		secondary1 = super.getSecondary1();
		secondary2 = super.getSecondary2();
		secondary3 = super.getSecondary3();
		black = super.getBlack();
		white = super.getWhite();
		userText = super.getUserTextFont();
		menuText = super.getMenuTextFont();
		windowTitle = super.getWindowTitleFont();
		controlText = super.getControlTextFont();
		systemText = super.getSystemTextFont();
		subText = super.getSubTextFont();
		loadFromProps(is);
	}

	protected ColorUIResource getPrimary1() {
		return primary1;
	}

	protected ColorUIResource getPrimary2() {
		return primary2; }

	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	protected ColorUIResource getBlack() {
		return black;
	}

	protected ColorUIResource getWhite() {
		return white;
	}

	public FontUIResource getUserTextFont() {
		return userText;
	}

	public FontUIResource getMenuTextFont() {
		return menuText;
	}

	public FontUIResource getWindowTitleFont() {
		return windowTitle;
	}

	public FontUIResource getControlTextFont() {
		return controlText;
	}

	public FontUIResource getSystemTextFont() {
		return systemText;
	}

	public FontUIResource getSubTextFont() {
		return subText;
	}

	/**
	 * Override the default color combination
	 */
    public ColorUIResource getFocusColor() { return getPrimary2(); }

    public  ColorUIResource getDesktopColor() { return getPrimary2(); }

    public ColorUIResource getControl() { return getSecondary3(); }
    public ColorUIResource getControlShadow() { return getSecondary2(); }
    public ColorUIResource getControlDarkShadow() { return getSecondary1(); }
    public ColorUIResource getControlInfo() { return getBlack(); }
    public ColorUIResource getControlHighlight() { return getWhite(); }
    public ColorUIResource getControlDisabled() { return getSecondary2(); }

    public ColorUIResource getPrimaryControl() { return getPrimary3(); }
    public ColorUIResource getPrimaryControlShadow() { return getPrimary2(); }
    public ColorUIResource getPrimaryControlDarkShadow() { return getPrimary1(); }
    public ColorUIResource getPrimaryControlInfo() { return getBlack(); }
    public ColorUIResource getPrimaryControlHighlight() { return getWhite(); }

    /**
     * Returns the color used, by default, for the text in labels
     * and titled borders.
     */
    public ColorUIResource getSystemTextColor() { return getBlack(); }
    public ColorUIResource getControlTextColor() { return getControlInfo(); }
    public ColorUIResource getInactiveControlTextColor() { return getControlDisabled(); }
    public ColorUIResource getInactiveSystemTextColor() { return getSecondary2(); }
    public ColorUIResource getUserTextColor() { return getBlack(); }
    public ColorUIResource getTextHighlightColor() { return getPrimary3(); }
    public ColorUIResource getHighlightedTextColor() { return getUserTextColor(); }

    public ColorUIResource getWindowBackground() { return getWhite(); }
    public ColorUIResource getWindowTitleBackground() { return getPrimary3(); }
    public ColorUIResource getWindowTitleForeground() { return getBlack(); }
    public ColorUIResource getWindowTitleInactiveBackground() { return getSecondary3(); }
    public ColorUIResource getWindowTitleInactiveForeground() { return getBlack(); }

    public ColorUIResource getMenuBackground() { return getSecondary3(); }
    public ColorUIResource getMenuForeground() { return  getBlack(); }
    public ColorUIResource getMenuSelectedBackground() { return getPrimary3(); }
    public ColorUIResource getMenuSelectedForeground() { return getMenuForeground(); }
    public ColorUIResource getMenuDisabledForeground() { return getSecondary2(); }
    public ColorUIResource getSeparatorBackground() { return getWhite(); }
    public ColorUIResource getSeparatorForeground() { return getPrimary2(); }
    public ColorUIResource getAcceleratorSelectedForeground() { return getBlack(); }

	/**
	 * Loads the ui props from the given stream.
	 * @param stream
	 * @throws IOException
	 */
	protected void loadFromProps(InputStream stream) throws IOException {
		Properties prop = new Properties();
		prop.load(stream);

		Object colorString = prop.get("color.primary1");
		if (colorString != null){
			primary1 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.primary2");
		if (colorString != null) {
			primary2 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.primary3");
		if (colorString != null) {
			primary3 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.secondary1");
		if (colorString != null) {
			secondary1 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.secondary2");
		if (colorString != null) {
			secondary2 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.secondary3");
		if (colorString != null) {
			secondary3 = parseColor(colorString.toString());
		}

		colorString = prop.get("color.black");
		if (colorString != null) {
			black = parseColor(colorString.toString());
		}

		colorString = prop.get("color.white");
		if (colorString != null) {
			white = parseColor(colorString.toString());
		}

		Object fontString = prop.get("font.usertext");
		if(fontString != null) {
			userText = parseFont(fontString.toString());
		}

		fontString = prop.get("font.menutext");
		if(fontString != null) {
			menuText = parseFont(fontString.toString());
		}

		fontString = prop.get("font.systemtext");
		if(fontString != null) {
			systemText = parseFont(fontString.toString());
		}

		fontString = prop.get("font.windowtitle");
		if(fontString != null) {
			windowTitle = parseFont(fontString.toString());
		}

		fontString = prop.get("font.controltext");
		if(fontString != null) {
			controlText = parseFont(fontString.toString());
		}

		fontString = prop.get("font.subtext");
		if(fontString != null) {
			subText = parseFont(fontString.toString());
		}
	}

	/**
	 * @param line
	 * @return the color resource as described by the input line
	 */
	protected ColorUIResource parseColor(String line) {
		int red = 0;
		int green = 0;
		int blue = 0;
		StringTokenizer tok = new StringTokenizer(line, ",");
		if(tok.countTokens() != 3) {
			throw new IllegalArgumentException("invalid color string:" + line);
		}
		String tmp;
		int i = 0;
		while(tok.hasMoreTokens()) {
			++i;
			tmp = tok.nextToken();
			if(tmp.trim().length() <= 0) {
				throw new IllegalArgumentException("invalid color string:" + line);
			}
			switch(i) {
				case 1:
					red = Integer.parseInt(tmp);
				break;
				case 2:
					green = Integer.parseInt(tmp);
				break;
				case 3:
					blue = Integer.parseInt(tmp);
				break;
			}
		}
		return new ColorUIResource(red, green, blue);
	}

	/**
	 * Reads the specified line and returns the font details.
	 * @param line
	 * @return the font ui resource described by the input line
	 */
	public FontUIResource parseFont(String line) {
		StringTokenizer tok = new StringTokenizer(line, ",");
		String tmp;
		String font = "Dialog";
		int style = Font.PLAIN;
		int size = 11;
		int i = 0;
		if(tok.countTokens() != 3) {
			throw new IllegalArgumentException("invalid font string:" + line);
		}
		while(tok.hasMoreTokens()) {
			++i;
			tmp = tok.nextToken();
			if(tmp.trim().length() <= 0) {
				throw new IllegalArgumentException("invalid font string:" + line);
			}
			switch(i) {
				case 1:
					font = tmp;
				break;
				case 2:
					if("plain".equalsIgnoreCase(tmp)) {
						style = Font.PLAIN;
						break;
					}
					if("bold".equalsIgnoreCase(tmp)) {
						style = Font.BOLD;
						break;
					}
					if("italic".equalsIgnoreCase(tmp)) {
						style = Font.ITALIC;
						break;
					}
				break;
				case 3:
					size = Integer.parseInt(tmp);
				break;
				default :
				break;
			}
		}
		return new FontUIResource(new Font(font, style, size));
	}

}
