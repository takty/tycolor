package takty.color;

/**
 * This class simulates color vision characteristics.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class ColorVisionSimulation {

	static private final double[] LMS_BASE = LMS.fromXYZ$(new double[] {1.0, 1.0, 1.0});

	/**
	 * Enum type for color vision conversion methods.
	 */
	static public enum ConversionMethod {
		/**
		 * Reference: Brettel, H.; Viénot, F. & Mollon, J. D.,
		 * Computerized simulation of color appearance for dichromats,
		 * Journal of the Optical Society of America A, 1997, 14, 2647-2655.
		 */
		BRETTEL1997,

		/**
		 * Reference: Katsunori Okajima, Syuu Kanbe,
		 * A Real-time Color Simulation of Dichromats,
		 * IEICE technical report 107(117), 107-110, 2007-06-21.
		 */
		OKAJIMA2007
	}

	/**
	 * Represents the currently selected color vision characteristic conversion method.
	 */
	static public ConversionMethod conversionMethod = ConversionMethod.BRETTEL1997;

	// Constants used in Okajima 2007
	static private final double alpha_ = 1.0, beta_ = 1.0;

	/**
	 * Convert LMS to LMS in protanopia.
	 * This method works even if src and dest are the same object.
	 * @param src LMS color
	 * @param dest LMS color in protanopia
	 * @return LMS color in protanopia (dest)
	 */
	static public double[] lmsToProtanopia(double[] src, double[] dest) {
		final double d0 = 0.0 * src[0] + 2.02344 * src[1] + -2.52581 * src[2];
		final double d1 = 0.0 * src[0] + 1.0     * src[1] +  0.0     * src[2];
		final double d2 = 0.0 * src[0] + 0.0     * src[1] +  1.0     * src[2];
		if(conversionMethod == ConversionMethod.BRETTEL1997) {
			dest[0] = d0; dest[1] = d1; dest[2] = d2;
		} else {
			final double sp1 = src[1] / LMS_BASE[1];
			final double dp0 = d0 / LMS_BASE[0], dp1 = d1 / LMS_BASE[1], dp2 = d2 / LMS_BASE[2];
			final double kp = beta_ * sp1 / (alpha_ * dp0 + beta_ * dp1);
			dest[0] = (kp * dp0) * LMS_BASE[0]; dest[1] = (kp * dp1) * LMS_BASE[1]; dest[2] = (kp * dp2) * LMS_BASE[2];

			// final double kp = beta_ * src[1] / (alpha_ * d0 + beta_ * d1);
			// dest[0] = kp * d0; dest[1] = kp * d1; dest[2] = kp * d2;
		}
		return dest;
	}

	/**
	 * Convert LMS to LMS in deuteranopia.
	 * This method works even if src and dest are the same object.
	 * @param src LMS color
	 * @param dest LMS color in deuteranopia
	 * @return LMS color in deuteranopia (dest)
	 */
	static public double[] lmsToDeuteranopia(double[] src, double[] dest) {
		final double d0 = 1.0      * src[0] + 0.0 * src[1] + 0.0     * src[2];
		final double d1 = 0.494207 * src[0] + 0.0 * src[1] + 1.24827 * src[2];
		final double d2 = 0.0      * src[0] + 0.0 * src[1] + 1.0     * src[2];
		if(conversionMethod == ConversionMethod.BRETTEL1997) {
			dest[0] = d0; dest[1] = d1; dest[2] = d2;
		} else {
			final double sp0 = src[0] / LMS_BASE[0];
			final double dp0 = d0 / LMS_BASE[0], dp1 = d1 / LMS_BASE[1], dp2 = d2 / LMS_BASE[2];
			final double kd = alpha_ * sp0 / (alpha_ * dp0 + beta_ * dp1);
			dest[0] = (kd * dp0) * LMS_BASE[0]; dest[1] = (kd * dp1) * LMS_BASE[1]; dest[2] = (kd * dp2) * LMS_BASE[2];

			// final double kd = alpha_ * src[0] / (alpha_ * d0 + beta_ * d1);
			// dest[0] = kd * d0; dest[1] = kd * d1; dest[2] = kd * d2;
		}
		return dest;
	}

	/**
	 * Convert LMS to LMS in protanopia destructively.
	 * @param obj LMS color
	 * @return LMS color in protanopia (Converted obj)
	 */
	static public double[] lmsToProtanopia$(double[] obj) {
		return lmsToProtanopia(obj, obj);
	}

	/**
	 * Convert LMS to LMS in deuteranopia destructively.
	 * @param obj LMS color
	 * @return LMS color in deuteranopia (Converted obj)
	 */
	static public double[] lmsToDeuteranopia$(double[] obj) {
		return lmsToDeuteranopia(obj, obj);
	}




	static final double BL = 17.8824    * 1.0 + 43.5161   * 1.0 + 4.11935 * 1.0;
	static final double BM =  3.45565   * 1.0 + 27.1554   * 1.0 + 3.86714 * 1.0;
	static final double BS =  0.0299566 * 1.0 +  0.184309 * 1.0 + 1.46709 * 1.0;

	static public double[] lrgbToProtanopia(double[] src, double[] dest) {
		final double r = 0.992052 * src[0] + 0.003974;
		final double g = 0.992052 * src[1] + 0.003974;
		final double b = 0.992052 * src[2] + 0.003974;

		final double l = 17.8824    * r + 43.5161   * g + 4.11935 * b;
		final double m =  3.45565   * r + 27.1554   * g + 3.86714 * b;
		final double s =  0.0299566 * r +  0.184309 * g + 1.46709 * b;

		final double l2 = 0.0 * l + 2.02344 * m + -2.52581 * s;
		final double m2 = 0.0 * l + 1.0     * m +  0.0     * s;
		final double s2 = 0.0 * l + 0.0     * m +  1.0     * s;

		double l3, m3, s3;
		if(conversionMethod == ConversionMethod.BRETTEL1997) {
			l3 = l2; m3 = m2; s3 = s2;
		} else {
			final double l2n = l2 / BL, m2n = m2 / BM, s2n = s2 / BS;
			final double k = beta_ * (m / BM) / (alpha_ * l2n + beta_ * m2n);
			l3 = (k * l2n) * BL; m3 = (k * m2n) * BM; s3 = (k * s2n) * BS;

			// final double k = beta_ * m / (alpha_ * l2 + beta_ * m2);
			// l3 = k * l2; m3 = k * m2; s3 = k * s2;
		}

		final double r2 =  0.080944    * l3 + -0.130504   * m3 +  0.116721 * s3;
		final double g2 = -0.0102485   * l3 +  0.0540194  * m3 + -0.113615 * s3;
		final double b2 = -0.000365294 * l3 + -0.00412163 * m3 +  0.693513 * s3;

		dest[0] = r2; dest[1] = g2; dest[2] = b2;
		return dest;
	}

	static public double[] lrgbToDeuteranopia(double[] src, double[] dest) {
		final double r = 0.957237 * src[0] + 0.0213814;
		final double g = 0.957237 * src[1] + 0.0213814;
		final double b = 0.957237 * src[2] + 0.0213814;

		final double l = 17.8824    * r + 43.5161   * g + 4.11935 * b;
		final double m =  3.45565   * r + 27.1554   * g + 3.86714 * b;
		final double s =  0.0299566 * r +  0.184309 * g + 1.46709 * b;

		final double l2 = 1.0      * l + 0.0 * m + 0.0     * s;
		final double m2 = 0.494207 * l + 0.0 * m + 1.24827 * s;
		final double s2 = 0.0      * l + 0.0 * m + 1.0     * s;

		double l3, m3, s3;
		if(conversionMethod == ConversionMethod.BRETTEL1997) {
			l3 = l2; m3 = m2; s3 = s2;
		} else {
			final double l2n = l2 / BL, m2n = m2 / BM, s2n = s2 / BS;
			final double k = alpha_ * (l / BL) / (alpha_ * l2n + beta_ * m2n);
			l3 = (k * l2n) * BL; m3 = (k * m2n) * BM; s3 = (k * s2n) * BS;

			// final double k = alpha_ * l / (alpha_ * l2 + beta_ * m2);
			// l3 = k * l2; m3 = k * m2; s3 = k * s2;
		}

		final double r2 =  0.080944    * l3 + -0.130504   * m3 +  0.116721 * s3;
		final double g2 = -0.0102485   * l3 +  0.0540194  * m3 + -0.113615 * s3;
		final double b2 = -0.000365294 * l3 + -0.00412163 * m3 +  0.693513 * s3;

		dest[0] = r2; dest[1] = g2; dest[2] = b2;
		return dest;
	}

	static public double[] lrgbToProtanopia$(final double[] obj) {
		return lrgbToProtanopia(obj, obj);
	}

	static public double[] lrgbToDeuteranopia$(final double[] obj) {
		return lrgbToDeuteranopia(obj, obj);
	}

}
