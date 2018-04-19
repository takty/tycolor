package takty.color;

/**
 * This class converts the sRGB color system.
 * Reference: http://www.w3.org/Graphics/Color/sRGB.html
 * @author Takuto Yanagida
 * @version 2018-04-18
 */
public abstract class RGB extends ColorSpace {

	static public boolean isSaturated = false;

	// Scale to [0, 1], and convert sRGB to Linear RGB (gamma correction).
	static private double func(double x) {
		x /= 255.0;
		return (x < 0.03928) ? (x / 12.92) : Math.pow((x + 0.055) / 1.055, 2.4);
	}

	// Convert Linear RGB to sRGB (inverse gamma correction), and scale to [0, 255].
	static private double invFunc(double x) {
		x = (x > 0.00304) ? (Math.pow(x, 1.0 / 2.4) * 1.055 - 0.055) : (x * 12.92);
		return x;
	}

	/**
	 * Convert sRGB (Gamma 2.2) to Linear RGB.
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @param dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] toLRGB(final double[] src, final double[] dest) {
		dest[0] = func(src[0]);
		dest[1] = func(src[1]);
		dest[2] = func(src[2]);
		return dest;
	}

	/**
	 * Convert sRGB (Gamma 2.2) to Linear RGB destructively.
	 * @param obj sRGB color
	 * @return Linear RGB color (Converted obj)
	 */
	static public double[] toLRGB$(final double[] obj) {
		return toLRGB(obj, obj);
	}

	/**
	 * Convert Linear RGB to sRGB (Gamma 2.2).
	 * This method works even if src and dest are the same object.
	 * @param src Linear RGB color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] fromLRGB(final double[] src, final double[] dest) {
		dest[0] = invFunc(src[0]);
		dest[1] = invFunc(src[1]);
		dest[2] = invFunc(src[2]);

		final int r = (int)(dest[0] * 255.0), g = (int)(dest[1] * 255.0), b = (int)(dest[2] * 255.0);
		isSaturated = (r < 0 || 255 < r || g < 0 || 255 < g || b < 0 || 255 < b);

		dest[0] = Math.max(Math.min(dest[0], 1.0), 0.0) * 255.0;
		dest[1] = Math.max(Math.min(dest[1], 1.0), 0.0) * 255.0;
		dest[2] = Math.max(Math.min(dest[2], 1.0), 0.0) * 255.0;
		return dest;
	}

	/**
	 * Convert Linear RGB to sRGB (Gamma 2.2) destructively.
	 * @param src Linear RGB color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] fromLRGB$(final double[] obj) {
		return fromLRGB(obj, obj);
	}

	/*
	 * Utility methods
	 */

	/**
	 * Convert color integer to sRGB.
	 * @param src Color integer
	 * @param dest sRGB color
	 * @return Color vector (dest)
	 */
	static public double[] fromColorInteger(final int src, final double[] dest) {
		dest[0] = (src >> 16) & 0xFF;
		dest[1] = (src >> 8) & 0xFF;
		dest[2] = (src) & 0xFF;
		return dest;
	}

	/**
	 * Convert color integer to sRGB.
	 * @param src Color integer
	 * @return Color vector (dest)
	 */
	static public double[] fromColorInteger(final int src) {
		return fromColorInteger(src, new double[3]);
	}

	/**
	 * Convert sRGB to color integer.
	 * @param src sRGB color
	 * @return Color integer
	 */
	static public int toColorInteger(final double[] src) {
		return ((int)src[0] << 16) | ((int)src[1] << 8) | ((int)src[2]) | 0xff000000;
	}

	/**
	 * Convert sRGB to color integer.
	 * @param red Red
	 * @param green Green
	 * @param blue Blue
	 * @return Color integer
	 */
	static public int toColorInteger(final int red, final int green, final int blue) {
		return (red << 16) | (green << 8) | blue | 0xff000000;
	}

	/**
	 * Convert sRGB (Gamma 2.2) to CIELAB (L*a*b*).
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @param dest CIELAB color
	 * @return CIELAB color (dest)
	 */
	static public double[] toLab(final double[] src, final double[] dest) {
		return Lab.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB(src, dest)));
	}

	/**
	 * Convert sRGB (Gamma 2.2) to CIELAB (L*a*b*) destructively.
	 * @param obj sRGB color
	 * @return CIELAB color (Converted obj)
	 */
	static public double[] toLab$(final double[] obj) {
		return Lab.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB$(obj)));
	}

	/**
	 * Convert CIELAB (L*a*b*) to sRGB (Gamma 2.2).
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] fromLab(final double[] src, final double[] dest) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromLab(src, dest)));
	}

	/**
	 * Convert CIELAB (L*a*b*) to sRGB (Gamma 2.2) destructively.
	 * @param obj CIELAB color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] fromLab$(final double[] obj) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromLab$(obj)));
	}

	/**
	 * Convert sRGB to CIE 1931 XYZ.
	 * @param src sRGB color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(double[] src, double[] dest) {
		return LRGB.toXYZ$(LRGB.fromRGB(src, dest));
	}

	/**
	 * Convert sRGB to CIE 1931 XYZ destructively.
	 * @param obj sRGB color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(double[] obj) {
		return LRGB.toXYZ$(LRGB.fromRGB$(obj));
	}

	/**
	 * Convert CIE 1931 XYZ to sRGB.
	 * @param src XYZ color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] fromXYZ(double[] src, double[] dest) {
		return RGB.fromLRGB$(LRGB.fromXYZ(src, dest));
	}

	/**
	 * Convert CIE 1931 XYZ to sRGB destructively.
	 * @param obj XYZ color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] fromXYZ$(double[] obj) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(obj));
	}

	/**
	 * Convert sRGB (Gamma 2.2) to Yxy.
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @param dest Yxy color
	 * @return Yxy color (dest)
	 */
	static public double[] toYxy(final double[] src, final double[] dest) {
		return Yxy.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB(src, dest)));
	}

	/**
	 * Convert sRGB (Gamma 2.2) to Yxy destructively.
	 * @param obj sRGB color
	 * @return Yxy color (Converted obj)
	 */
	static public double[] toYxy$(final double[] obj) {
		return Yxy.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB$(obj)));
	}

	/**
	 * Convert Yxy to sRGB (Gamma 2.2).
	 * This method works even if src and dest are the same object.
	 * @param src Yxy color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] fromYxy(final double[] src, final double[] dest) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromYxy(src, dest)));
	}

	/**
	 * Convert Yxy to sRGB (Gamma 2.2) destructively.
	 * @param obj Yxy color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] fromYxy$(final double[] obj) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromYxy$(obj)));
	}

	/*
	 * Color vision characteristics conversion method (utility)
	 */

	/**
	 * Convert sRGB to Lightness-only sRGB.
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @param dest Lightness-only sRGB color
	 * @return Lightness-only sRGB color (dest)
	 */
	static public double[] toLightness(final double[] src, final double[] dest) {
		dest[0] = Lab.lightnessFromXYZ(XYZ.fromLRGB$(LRGB.fromRGB(src, dest)));
		dest[1] = dest[2] = 0.0;
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromLab$(dest)));
	}

	/**
	 * Convert sRGB to Lightness-only sRGB destructively.
	 * @param obj sRGB color
	 * @return Lightness-only sRGB color (Converted obj)
	 */
	static public double[] toLightness$(final double[] obj) {
		return toLightness(obj, obj);
	}

}
