package takty.color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class converts the Munsell (HVC) color system.
 * D65 is used as tristimulus value.
 * Since conversion is performed by approximation based on the distance to the sample color, the conversion result is approximate value.
 * Also, when H is -1.0, it is regarded as an achromatic color (N) in particular.
 * Reference: http://www.cis.rit.edu/mcsl/online/munsell.php
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class Munsell {

	// Hue [0.0, 100.0), 100.0 is also acceptable
	static public final double MIN_HUE = 0.0;
	static public final double MAX_HUE = 100.0;  // Same as MIN_HUE

	static public final double MONO_LIMIT_C = 0.05;

	static private final double[] TBL_V = {0.2, 0.4, 0.6, 0.8, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
	static private final int[][] TBL_MAX_C = new int[TBL_V.length][];
	static private final double[][][][] TBL = new double[TBL_V.length][][][];  // [vi][10 * h / 25][c / 2] -> [x, y]

	static public boolean isSaturated = false;

	static {
		final String pathBase = "/" + Munsell.class.getPackage().getName().replace('.','/') + "/table/";

		for(int vi = 0; vi < TBL_V.length; ++vi) {
			TBL_MAX_C[vi] = new int[1000 / 25];
			TBL[vi] = new double[1000 / 25][][];
			for(int i = 0, n = 1000 / 25; i < n; ++i) TBL[vi][i] = new double[50 / 2 + 2][];  // 2 <= C <= 51

			try {
				final InputStream is = Munsell.class.getResourceAsStream(pathBase + String.format("hc2xy(%04.1f).csv", TBL_V[vi]));
				try(final BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()))) {
					while(true) {
						final String line = br.readLine();
						if(line == null) break;
						final String[] cs = line.split(",");
						if(cs.length < 4) continue;
						try {
							final int h10 = (int)(hueNameToHueValue(cs[0]) * 10.0), c = Integer.valueOf(cs[1]);
							TBL[vi][h10 / 25][c / 2] = new double[] {Double.valueOf(cs[2]), Double.valueOf(cs[3])};
							if(TBL_MAX_C[vi][h10 / 25] < c) TBL_MAX_C[vi][h10 / 25] = c;
						} catch(NumberFormatException nfe) {
							continue;
						}
					}
				}
			} catch(IOException ex) {
				Logger.getLogger(Munsell.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	static private double[] getXy(final int vi, final int h10, final int c) {
		if(c == 0) return ILLUMINANT_C;
		return TBL[vi][h10 / 25][c / 2];
	}

	// Find Y of XYZ (C) from Munsell's V (JIS).
	static private double v2y(final double v) {
		final double v2 = v * v, v3 = v2 * v, v4 = v2 * v2, v5 = v2 * v3;
		final double y = 1.1913 * v - 0.22532 * v2 + 0.23351 * v3 - 0.020483 * v4 + 0.00081936 * v5;
		return y / 100.0;
	}

	// Munsell's V is obtained from Y of XYZ (C) (JIS, Newton's method).
	static private double y2v(final double y) {
		if(eq(y, 0.0)) return 0.0;
		double v = 0;
		while(true) {
			double f = v2y(v) * 100.0 - y * 100.0;
			final double v2 = v * v, v3 = v2 * v, v4 = v2 * v2;
			double fp = 1.1913 - 2 * 0.22532 * v + 3 * 0.23351 * v2 - 4 * 0.020483 * v3 + 5 * 0.00081936 * v4;
			double v1 = -f / fp + v;
			if(Math.abs(v1 - v) < 0.01) break;
			v = v1;
		}
		return v;
	}

	static private final double EP = 0.0000000000001;
	static private final double[] ILLUMINANT_C = {0.3101, 0.3162};  // Standard illuminant C, white point

	static private boolean eq(final double a, final double b) {
		return Math.abs(a - b) < EP;
	}

	static private boolean eq0(final double a) {
		return Math.abs(a) < EP;
	}

	// Find the Munsell value from xyY (standard illuminant C).
	static private void yxy2mun(final double Y, final double x, final double y, final double[] dest) {
		final double v = y2v(Y);  // Find Munsell lightness

		// When the lightness is maximum 10.0
		if(eq(v, TBL_V[TBL_V.length - 1])) {
			final double[] hc = interpolateHC(x, y, TBL_V.length - 1, new double[2]);
			dest[0] = hc[0]; dest[1] = v; dest[2] = hc[1];
			return;
		}
		// When the lightness is 0 or the lightness is larger than the maximum 10, or when it is an achromatic color (standard illuminant C)
		if(eq(v, 0.0) || TBL_V[TBL_V.length - 1] < v || (eq(x, ILLUMINANT_C[0]) && eq(y, ILLUMINANT_C[1]))) {
			dest[0] = 0.0; dest[1] = v; dest[2] = 0.0;
			return;
		}
		// Obtain lower side
		int vi_l = -1;
		while(TBL_V[vi_l + 1] <= v) ++vi_l;
		final double[] hc_l = new double[2];  // Hue and chroma of the lower side
		if(vi_l != -1) interpolateHC(x, y, vi_l, hc_l);

		// Obtain upper side
		final int vi_u = vi_l + 1;
		final double[] hc_u = interpolateHC(x, y, vi_u, new double[2]);

		// When the lightness on the lower side is the minimum 0.0, the hue is matched with the upper side, and the chroma is set to 0.0
		if(vi_l == -1) {
			hc_l[0] = hc_u[0]; hc_l[1] = 0.0;
		}
		final double v_l = ((vi_l == -1) ? 0.0 : TBL_V[vi_l]), v_h = TBL_V[vi_u];
		final double r = (v - v_l) / (v_h - v_l);
		double h = (hc_u[0] - hc_l[0]) * r + hc_l[0];
		if(MAX_HUE <= h) h -= MAX_HUE;
		final double c = (hc_u[1] - hc_l[1]) * r + hc_l[1];
		dest[0] = h; dest[1] = v; dest[2] = c;

		if(c < MONO_LIMIT_C) dest[2] = 0.0;
	}

	// Acquires the hue and chroma for the chromaticity coordinates (x, y) on the surface of the given lightness index.
	// If not included, -1 is returned.
	static private double[] interpolateHC(final double x, final double y, final int vi, final double[] hc) {
		int h10_l, h10_u = -1, c_l = -1, c_u = -1;
		double[] hv = null;

		out:
		for(h10_l = 0; h10_l <= 975; h10_l += 25) {  // h 0-975 step 25;
			h10_u = h10_l + 25;
			if(h10_u == 1000) h10_u = 0;

			in:
			for(c_l = 0; c_l <= 50; c_l += 2) {  // c 0-50 step 2;
				c_u = c_l + 2;

				final double[] a = getXy(vi, h10_l, c_l), d = getXy(vi, h10_l, c_u);
				final double[] b = getXy(vi, h10_u, c_l), c = getXy(vi, h10_u, c_u);
				if(a == null && b == null) break in;
				if(a == null || b == null || c == null || d == null) continue;
				//  ^
				// y| B C      ↖H (Direction of rotation) ↗C (Radial direction)
				//  | A D
				//  ------> x
				if(a[0] == b[0] && a[1] == b[1]) {
					if(isInside(a, c, d, x, y)) hv = interpolationRatio(x, y, a, d, b, c);
				} else {
					if(isInside(a, c, d, x, y) || isInside(a, b, c, x, y)) hv = interpolationRatio(x, y, a, d, b, c);
				}
				if(hv != null) break out;
			}
		}
		if(hv == null) {
			hc[0] = 0.0; hc[1] = 0.0;
			return hc;
		}
		if(h10_u == 0) h10_u = 1000;
		hc[0] = ((h10_u - h10_l) * hv[0] + h10_l) / 10.0; hc[1] = ((c_u - c_l) * hv[1] + c_l);
		return hc;
	}

	// Whether a point (x, y) exists within the interior (including the boundary) of the clockwise triangle abc
	// in the mathematical coordinate system (positive on the y axis is upward)
	static private boolean isInside(final double[] a, final double[] b, final double[] c, final double x, final double y) {
		// If x, y are on the right side of ab, the point is outside the triangle
		if(cross(x - a[0], y - a[1], b[0] - a[0], b[1] - a[1]) < 0.0) return false;
		// If x, y are on the right side of bc, the point is outside the triangle
		if(cross(x - b[0], y - b[1], c[0] - b[0], c[1] - b[1]) < 0.0) return false;
		// If x, y are on the right side of ca, the point is outside the triangle
		if(cross(x - c[0], y - c[1], a[0] - c[0], a[1] - c[1]) < 0.0) return false;
		return true;
	}

	static private double cross(final double ax, final double ay, final double bx, final double by) {
		return ax * by - ay * bx;
	}

	/*
	 * Calculate the proportion [h, v] of each point in the area surrounded by the points of the following placement (null if it is invalid).
	 *  ^
	 * y| B C      ↖H (Direction of rotation) ↗C (Radial direction)
	 *  | A D
	 *  ------> x
	 */
	static private double[] interpolationRatio(final double x, final double y, final double[] a, final double[] d, final double[] b, final double[] c) {
		// Find the ratio in the vertical direction
		double v = -1.0;

		// Solve a v^2 + b v + c = 0
		final double ea = (a[0] - d[0]) * (a[1] + c[1] - b[1] - d[1]) - (a[0] + c[0] - b[0] - d[0]) * (a[1] - d[1]);
		final double eb = (x - a[0]) * (a[1] + c[1] - b[1] - d[1]) + (a[0] - d[0]) * (b[1] - a[1]) - (a[0] + c[0] - b[0] - d[0]) * (y - a[1]) - (b[0] - a[0]) * (a[1] - d[1]);
		final double ec = (x - a[0]) * (b[1] - a[1]) - (y - a[1]) * (b[0] - a[0]);

		if(eq0(ea)) {
			if(!eq0(eb)) v = -ec / eb;
		} else {
			final double rt = Math.sqrt(eb * eb - 4.0 * ea * ec);
			final double v1 = (-eb + rt) / (2.0 * ea), v2 = (-eb - rt) / (2.0 * ea);

			if(a[0] == b[0] && a[1] == b[1]) {  // In this case, v1 is always 0, but this is not a solution.
				if(0.0 <= v2 && v2 <= 1.0) v = v2;
			} else {
				if     (0.0 <= v1 && v1 <= 1.0) v = v1;
				else if(0.0 <= v2 && v2 <= 1.0) v = v2;
			}
		}
		if(v < 0.0) return null;

		// Find the ratio in the horizontal direction
		double h = -1.0, h1 = -1.0, h2 = -1.0;
		final double deX = (a[0] - d[0] - b[0] + c[0]) * v - a[0] + b[0];
		final double deY = (a[1] - d[1] - b[1] + c[1]) * v - a[1] + b[1];

		if(!eq0(deX)) h1 = ((a[0] - d[0]) * v + x - a[0]) / deX;
		if(!eq0(deY)) h2 = ((a[1] - d[1]) * v + y - a[1]) / deY;

		if     (0.0 <= h1 && h1 <= 1.0) h = h1;
		else if(0.0 <= h2 && h2 <= 1.0) h = h2;

		if(h < 0.0) return null;

		return new double[] {h, v};
	}

	static private enum HueNames {R, YR, Y, GY, G, BG, B, PB, P, RP};  // 1R = 1, 9RP = 99, 10RP = 0

	/**
	 * Convert name-based hue expression to hue value.
	 * If the Name-based hue expression is N, -1.0 is returned.
	 * @param hueName Name-based hue expression
	 * @return Hue value
	 */
	static public double hueNameToHueValue(final String hueName) {
		if(hueName.length() == 1) return -1.0;  // In case of achromatic color N

		final int slen = Character.isDigit(hueName.charAt(hueName.length() - 2)) ? 1 : 2;  // Length of color name
		final String n = hueName.substring(hueName.length() - slen);

		double hv = Double.valueOf(hueName.substring(0, hueName.length() - slen));
		hv += HueNames.valueOf(n).ordinal() * 10;
		if(MAX_HUE <= hv) hv -= MAX_HUE;
		return hv;
	}

	/**
	 * Convert hue value to name-based hue expression.
	 * If the hue value is -1.0, or if the chroma value is 0.0, N is returned.
	 * @param hue Hue value
	 * @param chroma Chroma value
	 * @return Name-based hue expression
	 */
	static public String hueValueToHueName(double hue, final double chroma) {
		if(hue == -1.0 || eq0(chroma)) return "N";
		if(hue < 0) hue += MAX_HUE;
		int c = (int)(hue / 10.0);
		if(10 <= c) c -= 10;
		final String n = HueNames.values()[c].toString();
		return String.format("%.1f%s", hue - c * 10.0, n);
	}

	/**
	 * Convert CIE 1931 XYZ to Munsell (HVC).
	 * This method works even if src and dest are the same object.
	 * @param src XYZ color
	 * @param dest Munsell color
	 * @return Munsell color (dest)
	 */
	static public double[] fromXYZ(final double[] src, final double[] dest) {
		Yxy.fromXYZ$(XYZ.toIlluminantC(src, dest));
		yxy2mun(dest[0], dest[1], dest[2], dest);
		return dest;
	}

	/**
	 * Convert CIE 1931 XYZ to Munsell (HVC) destructively.
	 * @param obj XYZ color
	 * @return Munsell color (Converted obj)
	 */
	static public double[] fromXYZ$(final double[] obj) {
		return fromXYZ(obj, obj);
	}

	/**
	 * Convert Munsell (HVC) to CIE 1931 XYZ.
	 * This method works even if src and dest are the same object.
	 * @param src Munsell color
	 * @param dest XYZ color
	 * @return XYZ color (dest)
	 */
	static public double[] toXYZ(final double[] src, final double[] dest) {
		double h = src[0], v = src[1], c = src[2];
		if(MAX_HUE <= h) h -= MAX_HUE;
		dest[0] = v2y(v);
		isSaturated = false;

		// When the lightness is 0 or achromatic (check this first)
		if(eq(v, 0.0) || h < 0.0 || c < MONO_LIMIT_C) {
			dest[1] = ILLUMINANT_C[0]; dest[2] = ILLUMINANT_C[1];
			isSaturated = eq(v, 0.0) && 0.0 < c;
			return XYZ.fromIlluminantC(Yxy.toXYZ$(dest), dest);
		}
		// When the lightness is the maximum value 10.0 or more
		if(TBL_V[TBL_V.length - 1] <= v) {
			final double[] xy = new double[2];
			interpolateXY(h, c, TBL_V.length - 1, xy);
			dest[1] = xy[0]; dest[2] = xy[1];
			isSaturated = (TBL_V[TBL_V.length - 1] < v);
			return XYZ.fromIlluminantC(Yxy.toXYZ$(dest), dest);
		}
		int vi_l = -1;
		while(TBL_V[vi_l + 1] <= v) ++vi_l;
		final int vi_u = vi_l + 1;

		// Obtain lower side
		final double[] xy_l = new double[2];
		if(vi_l != -1) {
			if(!interpolateXY(h, c, vi_l, xy_l)) isSaturated = true;
		} else {  // When the lightness of the lower side is the minimum 0.0, use standard illuminant.
			xy_l[0] = ILLUMINANT_C[0]; xy_l[1] = ILLUMINANT_C[1];
			isSaturated = true;
		}
		// Obtain upper side
		final double[] xy_u = new double[2];
		if(!interpolateXY(h, c, vi_u, xy_u)) isSaturated = true;

		final double v_l = ((vi_l == -1) ? 0.0 : TBL_V[vi_l]), v_h = TBL_V[vi_u];
		final double r = (v - v_l) / (v_h - v_l);
		final double x = (xy_u[0] - xy_l[0]) * r + xy_l[0], y = (xy_u[1] - xy_l[1]) * r + xy_l[1];
		dest[1] = x; dest[2] = y;

		return XYZ.fromIlluminantC(Yxy.toXYZ$(dest), dest);
	}

	// Obtain the hue and chroma for the chromaticity coordinates (h, c) on the surface of the given lightness index.
	// Return false if it is out of the range of the table.
	static boolean interpolateXY(final double h, final double c, final int vi, final double[] xy) {
		final double h10 = h * 10.0;
		int h10_l = (int)Math.floor(h10 / 25.0) * 25, h10_u = h10_l + 25;
		final int c_l = (int)Math.floor(c / 2.0) * 2, c_u = c_l + 2;

		final double rh = (h10 - h10_l) / (h10_u - h10_l);
		final double rc = (c - c_l) / (double)(c_u - c_l);

		if(h10_u == 1000) h10_u = 0;
		final int maxC_hl = TBL_MAX_C[vi][h10_l / 25], maxC_hu = TBL_MAX_C[vi][h10_u / 25];

		if(maxC_hl <= c_l || maxC_hu <= c_l) {
			double[] xy_hl = new double[2], xy_hu = new double[2];

			if(c_l < maxC_hl) {
				double[] a = getXy(vi, h10_l, c_l), d = getXy(vi, h10_l, c_u);
				xy_hl[0] = (d[0] - a[0]) * rc + a[0]; xy_hl[1] = (d[1] - a[1]) * rc + a[1];
			} else {
				xy_hl = getXy(vi, h10_l, maxC_hl);
			}
			if(c_l < maxC_hu) {
				double[] a = getXy(vi, h10_u, c_l), d = getXy(vi, h10_u, c_u);
				xy_hu[0] = (d[0] - a[0]) * rc + a[0]; xy_hu[1] = (d[1] - a[1]) * rc + a[1];
			} else {
				xy_hu = getXy(vi, h10_u, maxC_hu);
			}
			xy[0] = (xy_hu[0] - xy_hl[0]) * rh + xy_hl[0]; xy[1] = (xy_hu[1] - xy_hl[1]) * rh + xy_hl[1];
			return false;
		}
		if(c_l == 0) {
			final double[] o = ILLUMINANT_C, d = getXy(vi, h10_l, c_u), C = getXy(vi, h10_u, c_u);
			final double cd_x = (C[0] - d[0]) * rh + d[0], cd_y = (C[1] - d[1]) * rh + d[1];
			xy[0] = (cd_x - o[0]) * rc + o[0]; xy[1] = (cd_y - o[1]) * rc + o[1];
		} else {
			double[] a = getXy(vi, h10_l, c_l), d = getXy(vi, h10_l, c_u);
			double[] b = getXy(vi, h10_u, c_l), C = getXy(vi, h10_u, c_u);
			double ab_x = (b[0] - a[0]) * rh + a[0], ab_y = (b[1] - a[1]) * rh + a[1];
			double cd_x = (C[0] - d[0]) * rh + d[0], cd_y = (C[1] - d[1]) * rh + d[1];
			xy[0] = (cd_x - ab_x) * rc + ab_x; xy[1] = (cd_y - ab_y) * rc + ab_y;
		}
		return true;
	}

	/**
	 * Convert Munsell (HVC) to CIE 1931 XYZ destructively.
	 * @param obj Munsell color
	 * @return XYZ color (Converted obj)
	 */
	static public double[] toXYZ$(final double[] obj) {
		return toXYZ(obj, obj);
	}

	/**
	 * Returns the string representation of Munsell numerical representation.
	 * @param hvc Munsell color
	 * @return String representation
	 */
	static public String toString(final double[] hvc) {
		if(hvc[2] < MONO_LIMIT_C) {
			return String.format("N %.1f", hvc[1]);
		} else {
			return String.format("%s %.1f/%.1f", hueValueToHueName(hvc[0], hvc[2]), hvc[1], hvc[2]);
		}
	}

}
