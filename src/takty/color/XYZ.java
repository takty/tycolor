package takty.color;

/**
 * This class converts the CIE 1931 XYZ color system.
 * @author Takuto Yanagida
 * @version 2018-04-18
 */
public class XYZ extends ColorSpace {

	/*
	 * Inverse conversion method
	 */

	/**
	 * Convert CIE 1931 XYZ to Linear RGB.
	 * @param src XYZ color
	 * @param dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] toLRGB(double[] src, double[] dest) {
		return LRGB.fromXYZ(src, dest);
	}

	/**
	 * Convert CIE 1931 XYZ to Linear RGB destructively.
	 * @param obj XYZ color
	 * @return Linear RGB color (Converted obj)
	 */
	static public double[] toLRGB$(double[] obj) {
		return LRGB.fromXYZ$(obj);
	}

	/**
	 * Convert Linear RGB to CIE 1931 XYZ.
	 * @param src Linear RGB color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] fromLRGB(double[] src, double[] dest) {
		return LRGB.toXYZ(src, dest);
	}

	/**
	 * Convert Linear RGB to CIE 1931 XYZ destructively.
	 * @param obj Linear RGB color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] fromLRGB$(double[] obj) {
		return LRGB.toXYZ$(obj);
	}

	/**
	 * Convert CIE 1931 XYZ to Yxy.
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest Yxy color
	 * @return Yxy color (dest)
	 */
	static public double[] toYxy(double[] src, double[] dest) {
		return Yxy.fromXYZ(src, dest);
	}

	/**
	 * Convert CIE 1931 XYZ to Yxy destructively.
	 * @param obj XYZ color
	 * @return Yxy color (Converted obj)
	 */
	static public double[] toYxy$(double[] obj) {
		return Yxy.fromXYZ$(obj);
	}

	/**
	 * Convert Yxy to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src Yxy color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] fromYxy(double[] src, double[] dest) {
		return Yxy.toXYZ(src, dest);
	}

	/**
	 * Convert Yxy to CIE 1931 XYZ destructively.
	 * @param obj LMS color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] fromYxy$(double[] obj) {
		return Yxy.toXYZ$(obj);
	}

	/**
	 * Convert CIE 1931 XYZ to CIE 1976 (L*, a*, b*).
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest CIELAB color
	 * @return CIELAB color (dest)
	 */
	static public double[] toLab(double[] src, double[] dest) {
		return Lab.fromXYZ(src, dest);
	}

	/**
	 * Convert CIE 1931 XYZ to CIE 1976 (L*, a*, b*) destructively.
	 * @param obj XYZ color
	 * @return CIELAB color (Converted obj)
	 */
	static public double[] toLab$(double[] obj) {
		return Lab.fromXYZ$(obj);
	}

	/**
	 * Convert CIE 1976 (L*, a*, b*) to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] fromLab(double[] src, double[] dest) {
		return Lab.toXYZ(src, dest);
	}

	/**
	 * Convert CIE 1976 (L*, a*, b*) to CIE 1931 XYZ destructively.
	 * @param obj CIELAB color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] fromLab$(double[] obj) {
		return Lab.toXYZ$(obj);
	}

	/**
	 * Convert CIE 1931 XYZ to LMS.
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest LMS color
	 * @return LMS color (dest)
	 */
	static public double[] toLMS(double[] src, double[] dest) {
		return LMS.fromXYZ(src, dest);
	}

	/**
	 * Convert CIE 1931 XYZ to LMS destructively.
	 * @param obj XYZ color
	 * @return LMS color (Converted obj)
	 */
	static public double[] toLMS$(double[] obj) {
		return LMS.fromXYZ$(obj);
	}

	/**
	 * Convert LMS to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src LMS color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] fromLMS(double[] src, double[] dest) {
		return LMS.toXYZ(src, dest);
	}

	/**
	 * Convert LMS to CIE 1931 XYZ destructively.
	 * @param obj LMS color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] fromLMS$(double[] obj) {
		return LMS.toXYZ$(obj);
	}

	/*
	 * Conversion of standard illuminant
	 */

	/**
	 * Convert CIE 1931 XYZ of standard illuminant C to CIE 1931 XYZ of standard illuminant D65.
	 * Reference: http://www.brucelindbloom.com/index.html?MunsellCalculator.html (Von Kries method)
	 * This method works even if src and dest are the same object.
	 * @param src XYZ of standard illuminant C
	 * @param dest XYZ of standard illuminant D65
	 * @return XYZ of standard illuminant D65 (dest)
	 */
	static public double[] fromIlluminantC(final double[] src, double[] dest) {
		final double d0 =  0.9972812 * src[0] + -0.0093756 * src[1] + -0.0154171 * src[2];
		final double d1 = -0.0010298 * src[0] +  1.0007636 * src[1] +  0.0002084 * src[2];
		final double d2 =                                              0.9209267 * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ of standard illuminant D65 to CIE 1931 XYZ of standard illuminant C.
	 * Reference: http://www.brucelindbloom.com/index.html?MunsellCalculator.html (Von Kries method)
	 * This method works even if src and dest are the same object.
	 * @param src XYZ of standard illuminant D65
	 * @param dest XYZ of standard illuminant C
	 * @return XYZ of standard illuminant C (dest)
	 */
	static public double[] toIlluminantC(final double[] src, double[] dest) {
		final double d0 =  1.0027359 * src[0] +  0.0093941 * src[1] +  0.0167846 * src[2];
 		final double d1 =  0.0010319 * src[0] +  0.9992466 * src[1] + -0.0002089 * src[2];
 		final double d2 =                                              1.0858628 * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

}
