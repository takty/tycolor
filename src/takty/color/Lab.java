package takty.color;

/**
 * This class converts the CIELAB (L*a*b*) color system.
 * By default, D65 is used as tristimulus value.
 * Reference: http://en.wikipedia.org/wiki/Lab_color_space
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class Lab extends ColorSpace {

	// Constants for simplification of calculation
	static private final double C1_ = Math.pow(6.0, 3.0) / Math.pow(29.0, 3.0);        // (6/29)^3 = 0.0088564516790356308171716757554635
	static private final double C2_ = Math.pow(29.0, 2.0) / Math.pow(6.0, 2.0) / 3.0;  // (1/3)*(29/6)^2 = 7.787037037037037037037037037037
	static private final double C3_ = 6.0 / 29.0;                                      // 6/29 = 0.20689655172413793103448275862069
	static private final double C4_ = Math.pow(6.0, 2.0) / Math.pow(29.0, 2.0) * 3.0;  // 3*(6/29)^2 = 0.12841854934601664684898929845422

	// Conversion function
	static private double func(double x) {
		return (x > C1_) ? Math.pow(x, 1.0 / 3.0) : (C2_ * x + 16.0 / 116.0);
	}

	// Inverse conversion function
	static private double invFunc(double x) {
		return (x > C3_) ? Math.pow(x, 3.0) : ((x - 16.0 / 116.0) * C4_);
	}

	/**
	 * D50 tristimulus value
	 * Reference: http://www.babelcolor.com/download/A%20review%20of%20RGB%20color%20spaces.pdf
	 */
	static final private double[] D50_xyz = {0.34567, 0.35850, 0.29583};
	static final private double[] D50_XYZ = {D50_xyz[0] / D50_xyz[1], 1.0, D50_xyz[2] / D50_xyz[1]};
	static public double[] D50_xyz() {return D50_xyz.clone();}
	static public double[] D50_XYZ() {return D50_XYZ.clone();}

	/**
	 * D65 tristimulus value
	 * Reference: http://www.babelcolor.com/download/A%20review%20of%20RGB%20color%20spaces.pdf
	 */
	static final private double[] D65_xyz = {0.31273, 0.32902, 0.35825};
	static final double[] D65_XYZ = {D65_xyz[0] / D65_xyz[1], 1.0, D65_xyz[2] / D65_xyz[1]};
	static public double[] D65_xyz() {return D65_xyz.clone();}
	static public double[] D65_XYZ() {return D65_XYZ.clone();}

	/**
	 * XYZ tristimulus value
	 */
	static public double[] XYZ_TRISTIMULUS_VALUES = D65_XYZ;

	/**
	 * Convert CIE 1931 XYZ to CIE 1976 (L*, a*, b*).
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest CIELAB color
	 * @return CIELAB color (dest)
	 */
	static public double[] fromXYZ(double[] src, double[] dest) {
		double fy = func(src[1] / XYZ_TRISTIMULUS_VALUES[1]);
		double d0 = 116.0 * fy - 16.0;
		double d1 = 500.0 * (func(src[0] / XYZ_TRISTIMULUS_VALUES[0]) - fy);
		double d2 = 200.0 * (fy - func(src[2] / XYZ_TRISTIMULUS_VALUES[2]));
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ to CIE 1976 (L*, a*, b*) destructively.
	 * @param obj XYZ color
	 * @return CIELAB color (Converted obj)
	 */
	static public double[] fromXYZ$(double[] obj) {
		return fromXYZ(obj, obj);
	}

	/**
	 * Convert CIE 1931 XYZ to L* of CIE 1976 (L*, a*, b*).
	 * @param src XYZ color
	 * @return L*
	 */
	static public double lightnessFromXYZ(double[] src) {
		double fy = func(src[1] / XYZ_TRISTIMULUS_VALUES[1]);
		return 116.0 * fy - 16.0;
	}

	/**
	 * Convert CIE 1976 (L*, a*, b*) to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(double[] src, double[] dest) {
		double fy = (src[0] + 16.0) / 116.0;
		double d0 = invFunc(fy + src[1] / 500.0) * XYZ_TRISTIMULUS_VALUES[0];
		double d1 = invFunc(fy) * XYZ_TRISTIMULUS_VALUES[1];
		double d2 = invFunc(fy - src[2] / 200.0) * XYZ_TRISTIMULUS_VALUES[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert CIE 1976 (L*, a*, b*) to CIE 1931 XYZ destructively.
	 * @param obj CIELAB color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(double[] obj) {
		return toXYZ(obj, obj);
	}

	/*
	 * Evaluation method =======================================================
	 */

	/**
	 * Calculate the conspicuity degree.
	 * Reference: Effective use of color conspicuity for Re-Coloring system,
	 * Correspondences on Human interface Vol. 12, No. 1, SIG-DE-01, 2010.
	 * @param lab CIELAB color
	 * @return Conspicuity degree [0, 180]
	 * TODO Consider chroma (ab radius of LAB)
	 */
	static public double conspicuityOf(double[] lab) {
		return Evaluation.conspicuityOfLab(lab);
	}

	/**
	 * Calculate the color difference between the two colors.
	 * @param v1 CIELAB color 1
	 * @param v2 CIELAB color 2
	 * @return Color difference
	 */
	static public double differenceBetween(final double[] v1, final double[] v2) {
		return Evaluation.differenceBetweenLab(v1, v2);
	}

	/*
	 * Utility methods =========================================================
	 */

	/**
	 * Convert CIELAB (L*a*b*) to sRGB (Gamma 2.2).
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color
	 * @param dest sRGB color
	 * @return sRGB color (dest)
	 */
	static public double[] toRGB(double[] src, double[] dest) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromLab(src, dest)));
	}

	/**
	 * Convert CIELAB (L*a*b*) to sRGB (Gamma 2.2) destructively.
	 * @param obj CIELAB color
	 * @return sRGB color (Converted obj)
	 */
	static public double[] toRGB$(double[] obj) {
		return RGB.fromLRGB$(LRGB.fromXYZ$(XYZ.fromLab$(obj)));
	}

	/**
	 * Convert sRGB (Gamma 2.2) to CIELAB (L*a*b*).
	 * This method works even if src and dest are the same object.
	 * @param src sRGB color
	 * @param dest CIELAB color
	 * @return CIELAB color (dest)
	 */
	static public double[] fromRGB(double[] src, double[] dest) {
		return Lab.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB(src, dest)));
	}

	/**
	 * Convert sRGB (Gamma 2.2) to CIELAB (L*a*b*) destructively.
	 * @param obj sRGB color
	 * @return CIELAB color (Converted obj)
	 */
	static public double[] fromRGB$(double[] obj) {
		return Lab.fromXYZ$(XYZ.fromLRGB$(LRGB.fromRGB$(obj)));
	}

	/**
	 * Convert CIELAB (L*a*b*) from rectangular coordinate format to polar coordinate format.
	 * This method works even if src and dest are the same object.
	 * @param src Color in rectangular coordinate format (CIELAB)
	 * @param dest  Color in polar format
	 * @return  Color in polar format (dest)
	 */
	static public double[] toPolarCoordinate(final double[] src, final double[] dest) {
		final double rad = (src[2] > 0) ? Math.atan2(src[2], src[1]) : (Math.atan2(-src[2], -src[1]) + Math.PI);
		final double c = Math.sqrt(src[1] * src[1] + src[2] * src[2]);
		final double h = rad * 360.0 / (Math.PI * 2);
		dest[0] = src[0]; dest[1] = c; dest[2] = h;
		return dest;
	}

	/**
	 * Convert CIELAB (L*a*b*) from polar coordinate format to rectangular coordinate format.
	 * This method works even if src and dest are the same object.
	 * @param src  Color in polar format (CIELAB)
	 * @param dest Color in rectangular coordinate format
	 * @return Color in rectangular coordinate format (dest)
	 */
	static public double[] toOrthogonalCoordinate(final double[] src, final double[] dest) {
		final double rad = src[2] * (Math.PI * 2) / 360.0;
		final double a = Math.cos(rad) * src[1];
		final double b = Math.sin(rad) * src[1];
		dest[0] = src[0]; dest[1] = a; dest[2] = b;
		return dest;
	}

}
