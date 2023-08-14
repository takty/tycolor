package takty.color;

/**
 * This class converts the YIQ color system.
 * Reference: http://en.wikipedia.org/wiki/YIQ
 * @author Takuto Yanagida
 * @version 2018-04-18
 */
public abstract class YIQ extends ColorSpace {

	/**
	 * Convert Linear RGB to YIQ.
	 * This method works even if src and dest are the same object.
	 * @param src Linear RGB color
	 * @param dest YIQ color
	 * @return YIQ color (dest)
	 */
	static public double[] fromLRGB(double[] src, double[] dest) {
		double d0 = 0.2990   * src[0] +  0.5870   * src[1] +  0.1140   * src[2];  // Y[0, 1]
		double d1 = 0.595716 * src[0] + -0.274453 * src[1] + -0.321263 * src[2];  // I[-0.5957, 0.5957]
		double d2 = 0.211456 * src[0] + -0.522591 * src[1] +  0.311135 * src[2];  // Q[-0.5226, 0.5226]
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert Linear RGB to YIQ destructively.
	 * @param obj Linear RGB color
	 * @return YIQ color (Converted obj)
	 */
	static public double[] fromLRGB$(double[] obj) {
		return fromLRGB(obj, obj);
	}

	/**
	 * Convert YIQ to Linear RGB.
	 * This method works even if src and dest are the same object.
	 * @param src YIQ color
	 * @param dest Linear RGB color
	 * @return Linear RGB color (dest)
	 */
	static public double[] toLRGB(double[] src, double[] dest) {
		double d0 = src[0] +  0.9563 * src[1] +  0.6210 * src[2];  // R[0, 1]
		double d1 = src[0] + -0.2721 * src[1] + -0.6474 * src[2];  // G[0, 1]
		double d2 = src[0] + -1.1070 * src[1] +  1.7046 * src[2];  // B[0, 1]
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert YIQ to Linear RGB destructively.
	 * @param obj YIQ color
	 * @return Linear RGB color (Converted obj)
	 */
	static public double[] toLRGB$(double[] obj) {
		return toLRGB(obj, obj);
	}

}
