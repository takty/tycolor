package takty.color;

import takty.color.Evaluation.BasicCategoricalColor;

/**
 * This class converts the Yxy color system.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public abstract class Yxy extends ColorSpace {

	static public boolean isSaturated = false;

	/**
	 * Convert CIE 1931 XYZ to Yxy.
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest Yxy color
	 * @return Yxy color (dest)
	 */
	static public double[] fromXYZ(double[] src, double[] dest) {
		double d0 = src[1];
		double d1 = src[0] / (src[0] + src[1] + src[2]);
		double d2 = src[1] / (src[0] + src[1] + src[2]);
		if(Double.isNaN(d1) || Double.isNaN(d2)) {  // When X = 0, Y = 0, Z = 0
			dest[0] = d0; dest[1] = 0.31273; dest[2] = 0.32902;  // White point D65
		} else {
			dest[0] = d0; dest[1] = d1; dest[2] = d2;
		}
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ to Yxy destructively.
	 * @param obj XYZ color
	 * @return Yxy color (Converted obj)
	 */
	static public double[] fromXYZ$(double[] obj) {
		return fromXYZ(obj, obj);
	}

	/**
	 * Convert Yxy to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src Yxy color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(double[] src, double[] dest) {
		double d0 = src[1] * src[0] / src[2];
		if(Double.isNaN(d0)) {
			dest[0] = 0.0; dest[1] = 0.0; dest[2] = 0.0;
			isSaturated = false;
			return dest;
		}
		double d1 = src[0];
		double d2 = (1 - src[1] - src[2]) * src[0] / src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		isSaturated = (Lab.D65_XYZ[0] < dest[0] || Lab.D65_XYZ[1] < dest[1] || Lab.D65_XYZ[2] < dest[2]);
		return dest;
	}

	/**
	 * Convert Yxy to CIE 1931 XYZ destructively.
	 * @param obj LMS color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(double[] obj) {
		return toXYZ(obj, obj);
	}

	/*
	 * Evaluation method =======================================================
	 */

	/**
	 * Calculate the basic categorical color of the specified color.
	 * @param yxy Yxy color
	 * @return Basic categorical color
	 */
	public static BasicCategoricalColor categoryOf(double[] yxy) {
		return Evaluation.categoryOfYxy(yxy);
	}

}
