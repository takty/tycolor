package takty.color;

/**
 * This class represents the color system.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class ColorSpace {

	static public enum Type {
		RGB, LRGB, LAB, XYZ, YXY, LMS, YIQ,
	}

	public static void checkRange(double[] v, double min, double max) {
		if(v[0] > max) v[0] = max;
		if(v[0] < min) v[0] = min;
		if(v[1] > max) v[1] = max;
		if(v[1] < min) v[1] = min;
		if(v[2] > max) v[2] = max;
		if(v[2] < min) v[2] = min;
	}

	/**
	 * Convert the primitive double array (size 3) to an array of object Double.
	 * @param cd Primitive array
	 * @return Object array
	 */
	public static Double[] primitiveToObject(double[] cd) {
		return new Double[] {cd[0], cd[1], cd[2]};
	}

	/**
	 * Convert the object Double array (size 3) to an array of primitive double.
	 * @param cd Object array
	 * @return Primitive array
	 */
	public static double[] objectToPrimitive(Double[] cd) {
		return new double[] {cd[0], cd[1], cd[2]};
	}

}
