package takty.color;

/**
 * This class converts the LMS color system.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class LMS extends ColorSpace {

	/*
	 * Reference: F. Vienot, H. Brettel, and J.D. Mollon,
	 * Digital video colourmaps for checking the legibility of displays by dichromats,
	 * COLOR research and application, vol.24, no.4, pp.243-252, Aug. 1999.
	 */
	static public final double[][] SMITH_POKORNY = {
		{ 0.15514, 0.54312, -0.03286},
		{-0.15514, 0.45684,  0.03286},
		{ 0.0,     0.0,      0.01608}
	};

	static public final double[][] SMITH_POKORNY_INV = {
		{2.944812906606763, -3.500977991936487, 13.17218214714747},
		{1.000040001600064,  1.000040001600064,  0.0             },
		{0.0,                0.0,               62.18905472636816}
	};

	static public final double[][] BRADFORD = {
		{ 0.8951000,  0.2664000, -0.1614000},
		{-0.7502000,  1.7135000,  0.0367000},
		{ 0.0389000, -0.0685000,  1.0296000}
	};

	static public final double[][] BRADFORD_INV = {
		{ 0.9869929, -0.1470543,  0.1599627},
		{ 0.4323053,  0.5183603,  0.0492912},
		{-0.0085287,  0.0400428,  0.9684867}
	};

	static public final double[][] VON_KRIES = {
		{ 0.4002400, 0.7076000, -0.0808100},
		{-0.2263000, 1.1653200,  0.0457000},
		{ 0.0000000, 0.0000000,  0.9182200}
	};

	static public final double[][] VON_KRIES_INV = {
		{1.8599364, -1.1293816,  0.2198974},
		{0.3611914,  0.6388125, -0.0000064},
		{0.0000000,  0.0000000,  1.0890636}
	};

	static public double[][] matrix = SMITH_POKORNY;

	static public double[][] matrixInverse = SMITH_POKORNY_INV;

	/**
	 * Convert CIE 1931 XYZ to LMS.
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest LMS color
	 * @return LMS color (dest)
	 */
	static public double[] fromXYZ(double[] src, double[] dest) {
		final double d0 = matrix[0][0] * src[0] + matrix[0][1] * src[1] + matrix[0][2] * src[2];
		final double d1 = matrix[1][0] * src[0] + matrix[1][1] * src[1] + matrix[1][2] * src[2];
		final double d2 = matrix[2][0] * src[0] + matrix[2][1] * src[1] + matrix[2][2] * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ to LMS destructively.
	 * @param obj XYZ color
	 * @return LMS color (Converted obj)
	 */
	static public double[] fromXYZ$(double[] obj) {
		return fromXYZ(obj, obj);
	}

	/**
	 * Convert LMS to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src LMS color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(double[] src, double[] dest) {
		final double d0 = matrixInverse[0][0] * src[0] + matrixInverse[0][1] * src[1] + matrixInverse[0][2] * src[2];
		final double d1 = matrixInverse[1][0] * src[0] + matrixInverse[1][1] * src[1] + matrixInverse[1][2] * src[2];
		final double d2 = matrixInverse[2][0] * src[0] + matrixInverse[2][1] * src[1] + matrixInverse[2][2] * src[2];
		dest[0] = d0; dest[1] = d1; dest[2] = d2;
		return dest;
	}

	/**
	 * Convert LMS to CIE 1931 XYZ destructively.
	 * @param obj LMS color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(double[] obj) {
		return toXYZ(obj, obj);
	}

}
