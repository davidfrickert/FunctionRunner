package pt.ist.photon_graal.runner.utils.base;

import org.graalvm.nativeimage.ImageInfo;

public class Enviroment {
	public static boolean isNative() {
		return ImageInfo.inImageCode();
	}
}
