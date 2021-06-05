package pt.ist.photon_graal.runner.isolateutils.base;

import org.graalvm.nativeimage.ImageInfo;

public class Enviroment {
	public static boolean isNative() {
		return ImageInfo.inImageCode();
	}
}
