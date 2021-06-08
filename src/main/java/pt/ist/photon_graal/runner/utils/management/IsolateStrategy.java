package pt.ist.photon_graal.runner.utils.management;

import org.graalvm.nativeimage.IsolateThread;

public interface IsolateStrategy {
	IsolateThread get();
	void release(IsolateThread isolateThread);

	IsolateStrategy DEFAULT = new SingleUseSync();
}
