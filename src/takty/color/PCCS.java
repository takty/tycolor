package takty.color;

/**
 * This class converts the PCCS color system.
 * Colors where h is -1.0 are handled especially as an achromatic color (n).
 * Reference: KOBAYASHI Mituo and YOSIKI Kayoko,
 * Mathematical Relation among PCCS Tones, PCCS Color Attributes and Munsell Color Attributes,
 * Journal of the Color Science Association of Japan 25(4), 249-261, 2001.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public class PCCS {

	// Hue [0.0, 24.0), 24.0 is also acceptable
	static public final double MIN_HUE = 0.0;
	static public final double MAX_HUE = 24.0;  // same as MIN_HUE

	static public final double MONO_LIMIT_S = 0.01;

	static private final String[] HUE_NAMES = {"", "pR", "R", "yR", "rO", "O", "yO", "rY", "Y", "gY", "YG", "yG", "G", "bG", "GB", "GB", "gB", "B", "B", "pB", "V", "bP", "P", "rP", "RP"};

	static private final String[] TONE_NAMES = {"p", "p+", "ltg", "g", "dkg", "lt", "lt+", "sf", "d", "dk", "b", "s", "dp", "v", "none"};

	static private final int[] MUNSELL_H = {
		96,  // Dummy
		 0,  4,  7, 10, 14, 18, 22, 25, 28, 33, 38, 43,
		49, 55, 60, 65, 70, 73, 76, 79, 83, 87, 91, 96, 100
	};

	static private final double[][] COEFFICIENTS = {
		{0.853642,  0.084379, -0.002798},  // 0 == 24
		{1.042805,  0.046437,  0.001607},  // 2
		{1.079160,  0.025470,  0.003052},  // 4
		{1.039472,  0.054749, -0.000511},  // 6
		{0.925185,  0.050245,  0.000953},  // 8
		{0.968557,  0.012537,  0.003375},  // 10
		{1.070433, -0.047359,  0.007385},  // 12
		{1.087030, -0.051075,  0.006526},  // 14
		{1.089652, -0.050206,  0.006056},  // 16
		{0.880861,  0.060300, -0.001280},  // 18
		{0.897326,  0.053912, -0.000860},  // 20
		{0.887834,  0.055086, -0.000847},  // 22
		{0.853642,  0.084379, -0.002798},  // 24
	};

	/**
	 * Enum type for conversion methods.
	 */
	static public enum ConversionMethod {
		/**
		 * Concise conversion
		 */
		CONCISE,

		/**
		 * Accurate conversion
		 */
		ACCURATE
	}

	/**
	 * Indicates the currently selected color vision characteristic conversion method.
	 */
	static public ConversionMethod conversionMethod = ConversionMethod.ACCURATE;

	/**
	 * Enum type for Tone.
	 */
	static public enum Tone {p, p_p, ltg, g, dkg, lt, lt_p, sf, d, dk, b, s, dp, v, none}

	/*
	 * Calculation of PCCS value (accurate)
	 */

	static private double calcPccsH(double H) {
		int h1 = -1, h2 = -1;
		for(int i = 1; i < MUNSELL_H.length; ++i) {
			if(MUNSELL_H[i] <= H) h1 = i;
			if(H < MUNSELL_H[i]) {
				h2 = i;
				break;
			}
		}
		if(h1 == -1) System.out.println("h1 is -1, H = " + H);
		if(h2 == -1) System.out.println("h2 is -1, H = " + H);
		return h1 + (h2 - h1) * (H - MUNSELL_H[h1]) / (MUNSELL_H[h2] - MUNSELL_H[h1]);
	}

	static private double calcPccsS(final double V, final double C, final double h) {
		final double[] a = calcInterpolatedCoefficientes(h);
		final double g = 0.81 - 0.24 * Math.sin((h - 2.6) / 12.0 * Math.PI);
		final double a0 = -C / (1.0 - Math.exp(-g * V));

		return solveEquation(simplyCalcPccsS(V, C, h), a[3], a[2], a[1], a0);
	}

	private static double[] calcInterpolatedCoefficientes(double h) {
		if(MAX_HUE < h) h -= MAX_HUE;
		int hf = (int)Math.floor(h);
		if(hf % 2 != 0) --hf;
		int hc = hf + 2;
		if(MAX_HUE < hc) hc -= MAX_HUE;

		final double[] af = COEFFICIENTS[hf / 2], ac = COEFFICIENTS[hc / 2], a = new double[4];
		for(int i = 0; i < 3; ++i) {
			a[i + 1] = (h - hf) / (hc - hf) * (ac[i]- af[i]) + af[i];
		}
		return a;
	}

	static private double solveEquation(final double x0, final double a3, final double a2, final double a1, final double a0) {
		double x = x0;
		while(true) {
			double y = a3 * x * x * x + a2 * x * x + a1 * x + a0;
			double yp = 3.0 * a3 * x * x + 2.0 * a2 * x + a1;
			double x1 = -y / yp + x;
			if(Math.abs(x1 - x) < 0.001) break;
			x = x1;
		}
		return x;
	}

	/*
	 * Calculation of Munsell value (accurate)
	 */

	static private double calcMunsellH(final double h) {
		int h1 = (int)Math.floor(h), h2 = h1 + 1;
		double H1 = MUNSELL_H[h1], H2 = MUNSELL_H[h2];
		if(H1 > H2) H2 = 100.0;
		return H1 + (H2 - H1) * (h - h1) / (h2 - h1);
	}

	static private double calcMunsellS(final double h, final double l, final double s) {
		final double[] a = calcInterpolatedCoefficientes(h);
		final double g = 0.81 - 0.24 * Math.sin((h - 2.6) / 12.0 * Math.PI);
		return (a[3] * s * s * s + a[2] * s * s + a[1] * s) * (1.0 - Math.exp(-g * l));
	}

	/*
	 * Calculation of PCCS value (concise)
	 */

	static private double simplyCalcPccsH(final double H) {
		final double y = H * Math.PI / 50.0;
		return 24.0 * y / (2.0 * Math.PI) + 1.24
				+ 0.02 * Math.cos(y) - 0.10 * Math.cos(2.0 * y) - 0.11 * Math.cos(3.0 * y)
				+ 0.68 * Math.sin(y) - 0.30 * Math.sin(2.0 * y) + 0.013 * Math.sin(3.0 * y);
	}

	static private double simplyCalcPccsS(final double V, final double C, final double h) {
		final double Ct = 12.0 + 1.7 * Math.sin((h + 2.2) * Math.PI / 12.0);
		final double gt = 0.81 - 0.24 * Math.sin((h - 2.6) * Math.PI / 12.0);
		final double e2 = 0.0040, e1 = 0.077, e0 = -C / (Ct * (1.0 - Math.exp(-gt * V)));
		return (-e1 + Math.sqrt(e1 * e1 - 4.0 * e2 * e0)) / (2.0 * e2);
	}

	/*
	 * Calculation of Munsell value (concise)
	 */

	static private double simplyCalcMunsellH(final double h) {
		final double x = (h - 1.0) * Math.PI / 12.0;
		return 100.0 * x / (2.0 * Math.PI) - 1.0
				+ 0.12 * Math.cos(x) + 0.34 * Math.cos(2.0 * x) + 0.40 * Math.cos(3.0 * x)
				- 2.7 * Math.sin(x) + 1.5 * Math.sin(2.0 * x) - 0.4 * Math.sin(3.0 * x);
	}

	static private double simplyCalcMunsellS(final double h, final double l, final double s) {
		final double Ct = 12.0 + 1.7 * Math.sin((h + 2.2) * Math.PI / 12.0);
		final double gt = 0.81 - 0.24 * Math.sin((h - 2.6) * Math.PI / 12.0);
		return Ct * (0.077 * s + 0.0040 * s * s) * (1.0 - Math.exp(-gt * l));
	}

	/**
	 * Convert Munsell (HVC) to PCCS (hls).
	 * This method works even if src and dest are the same object.
	 * @param src Munsell color
	 * @param dest PCCS color
	 * @return PCCS color (dest)
	 */
	static public double[] fromMunsell(double[] src, double[] dest) {
		double H = src[0], V = src[1], C = src[2];
		if(Munsell.MAX_HUE <= H) H -= Munsell.MAX_HUE;
		double h = 0.0, l = V, s = 0.0;

		if(C < Munsell.MONO_LIMIT_C) {
			switch(conversionMethod) {
			case CONCISE:  h = simplyCalcPccsH(H); break;
			case ACCURATE: h = calcPccsH(H); break;
			}
		} else {
			switch(conversionMethod) {
			case CONCISE:
				h = simplyCalcPccsH(H);        // Hue
				s = simplyCalcPccsS(V, C, h);  // Saturation
				break;
			case ACCURATE:
				h = calcPccsH(H);        // Hue
				s = calcPccsS(V, C, h);  // Saturation
				break;
			}
		}
		if(MAX_HUE <= h) h -= MAX_HUE;
		dest[0] = h; dest[1] = l; dest[2] = s;
		return dest;
	}

	/**
	 * Convert Munsell (HVC) to PCCS (hls) destructively.
	 * @param obj Munsell color
	 * @return PCCS color (Converted obj)
	 */
	static public double[] fromMunsell$(double[] obj) {
		return fromMunsell(obj, obj);
	}

	/**
	 * Convert PCCS (hls) to Munsell (HVC).
	 * This method works even if src and dest are the same object.
	 * @param src PCCS color
	 * @param dest Munsell color
	 * @return Munsell color (dest)
	 */
	static public double[] toMunsell(final double[] src, final double[] dest) {
		final double h = src[0], l = src[1], s = src[2];
		double H = 0.0, V = l, C = 0.0;

		if(s < MONO_LIMIT_S) {
			switch(conversionMethod) {
			case CONCISE:  H = simplyCalcMunsellH(h); break;
			case ACCURATE: H = calcMunsellH(h); break;
			}
		} else {
			switch(conversionMethod) {
			case CONCISE:
				H = simplyCalcMunsellH(h);
				C = simplyCalcMunsellS(h, l, s);
				break;
			case ACCURATE:
				H = calcMunsellH(h);
				C = calcMunsellS(h, l, s);
				break;
			}
		}
		if(H < 0.0) H += Munsell.MAX_HUE;
		if(Munsell.MAX_HUE <= H) H -= Munsell.MAX_HUE;
		dest[0] = H; dest[1] = V; dest[2] = C;
		return dest;
	}

	/**
	 * Convert PCCS (hls) to Munsell (HVC) destructively.
	 * @param obj PCCS color
	 * @return Munsell color (Converted obj)
	 */
	static public double[] toMunsell$(double[] obj) {
		return toMunsell(obj, obj);
	}

	/**
	 * Calculate tone.
	 * @param hls PCCS color
	 * @return Tone
	 */
	static public Tone tone(final double[] hls) {
		final double t = relativeLightness(hls), s = hls[2];
		final double tu = s * -3.0 / 10.0 + 8.5, td = s * 3.0 / 10.0 + 2.5;

		if(s < 1.0) {
			return Tone.none;
		} else if(1.0 <= s && s < 4.0) {
			if(t < td) return Tone.dkg;
			if(t < 5.5) return Tone.g;
			if(t < tu) return Tone.ltg;
			if(s < 2.5) return Tone.p;
			return Tone.p_p;
		} else if(4.0 <= s && s < 7.0) {
			if(t < td) return Tone.dk;
			if(t < 5.5) return Tone.d;
			if(t < tu) return Tone.sf;
			if(s < 5.5) return Tone.lt;
			return Tone.lt_p;
		} else if(7.0 <= s && s < 8.5) {
			if(t < td) return Tone.dp;
			if(t < tu) return Tone.s;
			return Tone.b;
		} else {
			return Tone.v;
		}
	}

	/**
	 * Return relative lightness (lightness in tone coordinate system).
	 * @param hls PCCS color
	 * @return Relative lightness l
	 */
	static public double relativeLightness(final double[] hls) {
		return hls[1] - (0.25 - 0.34 * Math.sqrt(1.0 - Math.sin((hls[0] - 2.0) * Math.PI / 12.0))) * hls[2];
	}

	/**
	 * Return absolute lightness (lightness in PCCS).
	 * @param hLs Tone coordinate color
	 * @return Absolute lightnes l
	 */
	static public double absoluteLightness(final double[] hLs) {
		return hLs[1] + (0.25 - 0.34 * Math.sqrt(1.0 - Math.sin((hLs[0] - 2.0) * Math.PI / 12.0))) * hLs[2];
	}

	/**
	 * Convert PCCS color to tone coordinate color.
	 * This method works even if src and dest are the same object.
	 * @param src PCCS color
	 * @param dest Tone coordinate color
	 * @return Tone coordinate color (dest)
	 */
	static public double[] toToneCoordinate(final double[] src, final double[] dest) {
		dest[0] = src[0];  // h
		dest[1] = relativeLightness(src);
		dest[2] = src[2];  // s
		return dest;
	}

	/**
	 * Convert tone coordinate color to PCCS color.
	 * This method works even if src and dest are the same object.
	 * @param src Tone coordinate color
	 * @param dest PCCS color
	 * @return PCCS color (dest)
	 */
	static public double[] toNormalCoordinate(final double[] src, final double[] dest) {
		dest[0] = src[0];  // h
		dest[1] = absoluteLightness(src);
		dest[2] = src[2];  // s
		return dest;
	}

	/**
	 * Returns the string representation of PCCS numerical representation.
	 * @param hlc PCCS color
	 * @return String representation
	 */
	static public String toString(final double[] hlc) {
		if(hlc[2] < MONO_LIMIT_S) {
			if(9.5 <= hlc[1]) return String.format("W N-%.1f", hlc[1]);
			if(hlc[1] <= 1.5) return String.format("Bk N-%.1f", hlc[1]);
			return String.format("Gy-%.1f N-%.1f", hlc[1], hlc[1]);
		} else {
			final Tone t = tone(hlc);
			int tn = (int)Math.round(hlc[0]);
			if(tn <= 0) tn = (int)MAX_HUE;
			if(MAX_HUE < tn) tn -= (int)MAX_HUE;
			if(t == Tone.none) {
				return String.format("%.1f:%s-%.1f-%.1fs", hlc[0], HUE_NAMES[tn], hlc[1], hlc[2]);
			} else {
				return String.format("%s%.1f %.1f:%s-%.1f-%.1fs", TONE_NAMES[t.ordinal()], hlc[0], hlc[0], HUE_NAMES[tn], hlc[1], hlc[2]);
			}
		}
	}

	static public String toHueString(final double[] hlc) {
		if(hlc[2] < MONO_LIMIT_S) {
			return "N";
		} else {
			int tn = (int)Math.round(hlc[0]);
			if(tn <= 0) tn = (int)MAX_HUE;
			if(MAX_HUE < tn) tn -= (int)MAX_HUE;
			return HUE_NAMES[tn];
		}
	}

	static public String toToneString(final double[] hlc) {
		if(hlc[2] < MONO_LIMIT_S) {
			if(9.5 <= hlc[1]) return "W";
			if(hlc[1] <= 1.5) return "Bk";
			return "Gy";
		} else {
			final Tone t = tone(hlc);
			return TONE_NAMES[t.ordinal()];
		}
	}

}
