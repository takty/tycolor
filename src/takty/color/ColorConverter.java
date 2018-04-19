package takty.color;

/**
 * This class mutually converts the color system.
 * @author Takuto Yanagida
 * @version 2018-04-19
 */
public abstract class ColorConverter {

	static public ColorConverter create(Class<? extends ColorConverter> ...cs) {
		ColorConverter cc = null, next = null;
		for(int i = cs.length - 1; i >= 0; --i) {
			Class<? extends ColorConverter> c = cs[i];
			try {
				cc = (ColorConverter)c.newInstance();
				cc.next_ = next;
				next = cc;
			} catch (InstantiationException | IllegalAccessException ex) {
			}
		}
		return cc;
	}

	// sRGB <-> LRGB ###########################################################

	static public class RGB_LRGB extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {RGB.toLRGB(src, dest); return false;}
	}

	static public class LRGB_RGB extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {
			LRGB.toRGB(src, dest);
			return RGB.isSaturated;
		}
	}

	// LRGB <-> YIQ ############################################################

	static public class LRGB_YIQ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {LRGB.toYIQ(src, dest); return false;}
	}

	static public class YIQ_LRGB extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {YIQ.toLRGB(src, dest); return false;}
	}

	// LRGB <-> XYZ ############################################################

	static public class LRGB_XYZ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {LRGB.toXYZ(src, dest); return false;}
	}

	static public class XYZ_LRGB extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {XYZ.toLRGB(src, dest); return false;}
	}

	// XYZ <-> CIELAB ##########################################################

	static public class XYZ_Lab extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {XYZ.toLab(src, dest); return false;}
	}

	static public class XYZ_LabL extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {
			dest[0] = Lab.lightnessFromXYZ(src); dest[1] = 0.0; dest[2] = 0.0;
			return false;
		}
	}

	static public class Lab_XYZ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {Lab.toXYZ(src, dest); return false;}
	}

	// XYZ <-> LMS #############################################################

	static public class XYZ_LMS extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {XYZ.toLMS(src, dest); return false;}
	}

	static public class LMS_XYZ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {LMS.toXYZ(src, dest); return false;}
	}

	// LMS -> LMSp, d ##########################################################

	static public class LMS_LMSp extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {ColorVisionSimulation.lmsToProtanopia(src, dest); return false;}
	}

	static public class LMS_LMSd extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {ColorVisionSimulation.lmsToDeuteranopia(src, dest); return false;}
	}

	// LRGB -> LRGBp, d ##########################################################

	static public class LRGB_LRGBp extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {ColorVisionSimulation.lrgbToProtanopia(src, dest); return false;}
	}

	static public class LRGB_LRGBd extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {ColorVisionSimulation.lrgbToDeuteranopia(src, dest); return false;}
	}

	// XYZ <-> Yxy #############################################################

	static public class XYZ_Yxy extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {XYZ.toYxy(src, dest); return false;}
	}

	static public class Yxy_XYZ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {
			Yxy.toXYZ(src, dest);
			return Yxy.isSaturated;
		}
	}

	// Lab -> Lab eAb, yAb ########################################################

	static public class Lab_LabeAb extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {AgeSimulation.labToElderlyAB(src, dest); return false;}
	}

	static public class Lab_LabyAb extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {AgeSimulation.labToYoungAB(src, dest); return false;}
	}

	// Munsell <-> XYZ #########################################################

	static public class Munsell_XYZ extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {Munsell.toXYZ(src, dest); return false;}
	}

	static public class XYZ_Munsell extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {Munsell.fromXYZ(src, dest); return false;}
	}

	// PCCS <-> Munsell ########################################################

	static public class PCCS_Munsell extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {PCCS.toMunsell(src, dest); return false;}
	}

	static public class Munsell_PCCS extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {PCCS.fromMunsell(src, dest); return false;}
	}

	// PCCS <-> PCCS Tone ########################################################

	static public class PCCS_Tone extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {PCCS.toToneCoordinate(src, dest); return false;}
	}

	static public class Tone_PCCS extends ColorConverter {
		@Override
		protected boolean function(double[] src, double[] dest) {PCCS.toNormalCoordinate(src, dest); return false;}
	}

	// Utilities ###############################################################

	static public ColorConverter createRgbToLab() {
		return ColorConverter.create(RGB_LRGB.class, LRGB_XYZ.class, XYZ_Lab.class);
	}

	static public ColorConverter createLabToRgb() {
		return ColorConverter.create(Lab_XYZ.class, XYZ_LRGB.class, LRGB_RGB.class);
	}

	static public ColorConverter createRgbToRgbP() {
		return ColorConverter.create(RGB_LRGB.class, LRGB_XYZ.class, XYZ_LMS.class, LMS_LMSp.class, LMS_XYZ.class, XYZ_LRGB.class, LRGB_RGB.class);
	}

	static public ColorConverter createRgbToRgbD() {
		return ColorConverter.create(RGB_LRGB.class, LRGB_XYZ.class, XYZ_LMS.class, LMS_LMSd.class, LMS_XYZ.class, XYZ_LRGB.class, LRGB_RGB.class);
	}

	static public ColorConverter createRgbToRgbL() {
		return ColorConverter.create(RGB_LRGB.class, LRGB_XYZ.class, XYZ_LabL.class, Lab_XYZ.class, XYZ_LRGB.class, LRGB_RGB.class);
	}

	static public ColorConverter createRgbToYxy() {
		return ColorConverter.create(RGB_LRGB.class, LRGB_XYZ.class, XYZ_Yxy.class);
	}

	// Operators ###############################################################

	private ColorConverter next_;
	private double[] temp_;
	private boolean isSaturated_ = false;

	protected abstract boolean function(double[] src, double[] dest);

	public double[] convert(double[] src) {
		return convert(src, new double[3]);
	}

	public double[] convert(double[] src, double[] dest) {
		isSaturated_ = false;
		if(temp_ == null) {
			temp_ = (double[])src.clone();
		} else {
			temp_[0] = src[0]; temp_[1] = src[1]; temp_[2] = src[2];
		}
		ColorConverter cc = this;
		while(true) {
			final boolean ret = cc.function(temp_, dest);
			if(ret == true) isSaturated_ = true;
			if(cc.next_ == null) break;
			temp_[0] = dest[0]; temp_[1] = dest[1]; temp_[2] = dest[2];
			cc = cc.next_;
		}
		return dest;
	}

	public boolean isSaturated() {
		return isSaturated_;
	}

}
