package takty.color;

/**
 * This class performs various simulations of color space.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class AgeSimulation {

	/*
	 * Color vision age-related change simulation (conversion other than lightness)
	 * Reference: Katsunori Okajima, Human Color Vision Mechanism and its Age-Related Change,
	 * IEICE technical report 109(249), 43-48, 2009-10-15.
	 */

	static private double hueDiff(final double a, final double b) {
		double p = (b > 0) ? Math.atan2(b, a) : (Math.atan2(-b, -a) + Math.PI);
		return 4.5 * Math.cos(2.0 * Math.PI * (p - 28.8) / 50.9) + 4.4;
	}

	static private double chromaRatio(final double a, final double b) {
		double c = Math.sqrt(a * a + b * b);
		return 0.83 * Math.exp(-c / 13.3) - (1.0 / 8.0) * Math.exp(-(c - 50) * (c - 50) / (3000 * 3000)) + 1;
	}

	/**
	 * Convert CIELAB (L*a*b*) to CIELAB in the color vision of elderly people (70 years old) (conversion other than lightness).
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color (young person)
	 * @param dest CIELAB color in color vision of elderly people
	 * @return CIELAB color in color vision of elderly people (dest)
	 */
	static public double[] labToElderlyAB(final double[] src, final double[] dest) {
		double h = ((src[2] > 0) ? Math.atan2(src[2], src[1]) : (Math.atan2(-src[2], -src[1]) + Math.PI)) + hueDiff(src[1], src[2]);
		double c = Math.sqrt(src[1] * src[1] + src[2] * src[2]) * chromaRatio(src[1], src[2]);
		dest[0] = src[0]; dest[1] = Math.cos(h) * c; dest[2] = Math.sin(h) * c;
		return dest;
	}

	/**
	 * Convert CIELAB (L*a*b*) to CIELAB in the color vision of young people (20 years old) (conversion other than lightness).
	 * This method works even if src and dest are the same object.
	 * @param src CIELAB color (elderly person)
	 * @param dest CIELAB color in color vision of young people
	 * @return CIELAB color in color vision of young people (dest)
	 */
	static public double[] labToYoungAB(final double[] src, final double[] dest) {
		double h = ((src[2] > 0) ? Math.atan2(src[2], src[1]) : (Math.atan2(-src[2], -src[1]) + Math.PI)) - hueDiff(src[1], src[2]);
		double c = Math.sqrt(src[1] * src[1] + src[2] * src[2]) / chromaRatio(src[1], src[2]);
		dest[0] = src[0]; dest[1] = Math.cos(h) * c; dest[2] = Math.sin(h) * c;
		return dest;
	}

}
