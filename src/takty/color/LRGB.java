package takty.color;

/**
 * This class converts the Linear RGB color system.
 * It is targeted for Linear RGB which converted sRGB (D65).
 * Reference: http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public abstract class LRGB extends ColorSpace {

	/**
	 * Convert Linear RGB to CIE 1931 XYZ.
	 * @param src Linear RGB color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(double[] src, double[] dest) {
		double d0 = 0.4124564 * src[0] + 0.3575761 * src[1] + 0.1804375 * src[2];
		double d1 = 0.2126729 * src[0] + 0.7151522 * src[1] + 0.0721750 * src[2];
		double d2 = 0.0193339 * src[0] + 0.1191920 * src[1] + 0.9503041 * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert Linear RGB to CIE 1931 XYZ destructively.
	 * @param obj Linear RGB color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(double[] obj) {
		return toXYZ(obj, obj);
	}

	/**
	 * Convert CIE 1931 XYZ to Linear RGB.
	 * @param src XYZ color
	 * @param dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] fromXYZ(double[] src, double[] dest) {
		double d0 =  3.2404542 * src[0] + -1.5371385 * src[1] + -0.4985314 * src[2];
		double d1 = -0.9692660 * src[0] +  1.8760108 * src[1] +  0.0415560 * src[2];
		double d2 =  0.0556434 * src[0] + -0.2040259 * src[1] +  1.0572252 * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ to Linear RGB destructively.
	 * @param obj XYZ color
	 * @return Linear RGB color (Converted obj)
	 */
	static public double[] fromXYZ$(double[] obj) {
		return fromXYZ(obj, obj);
	}

	/*
	 * Inverse conversion methods
	 */

	/**
	 * Convert Linear RGB to sRGB (Gamma 2.2).
	 * This method works even if src and dest are the same object.
	 * @param src Linear RGB color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] toRGB(double[] src, double[] dest) {
		return RGB.fromLRGB(src, dest);
	}

	/**
	 * Convert Linear RGB to sRGB (Gamma 2.2) destructively.
	 * @param src Linear RGB color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] toRGB$(double[] obj) {
		return RGB.fromLRGB$(obj);
	}

	/**
	 * Convert sRGB (Gamma 2.2) to Linear RGB.
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @return dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] fromRGB(double[] src, double[] dest) {
		return RGB.toLRGB(src, dest);
	}

	/**
	 * Convert Linear RGB to sRGB (Gamma 2.2) destructively.
	 * @param src Linear RGB color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] fromRGB$(double[] obj) {
		return RGB.toLRGB$(obj);
	}

	/**
	 * Convert Linear RGB to YIQ.
	 * This method works even if src and dest are the same object.
	 * @param src Linear RGB color
	 * @param dest YIQ color
	 * @return YIQ color (dest)
	 */
	static public double[] toYIQ(double[] src, double[] dest) {
		return YIQ.fromLRGB(src, dest);
	}

	/**
	 * Convert Linear RGB to YIQ destructively.
	 * @param obj Linear RGB color
	 * @return YIQ color (Converted obj)
	 */
	static public double[] toYIQ$(double[] obj) {
		return YIQ.fromLRGB$(obj);
	}

	/**
	 * Convert YIQ to Linear RGB.
	 * This method works even if src and dest are the same object.
	 * @param src YIQ color
	 * @param dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] fromYIQ(double[] src, double[] dest) {
		return YIQ.toLRGB(src, dest);
	}

	/**
	 * Convert Linear RGB to YIQ destructively.
	 * @param obj Linear RGB color
	 * @return YIQ color (Converted obj)
	 */
	static public double[] fromYIQ$(double[] obj) {
		return YIQ.fromLRGB$(obj);
	}

}
