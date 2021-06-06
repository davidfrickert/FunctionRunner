package pt.ist.photon_graal.runner.isolateutils.management;

import org.graalvm.nativeimage.IsolateThread;

public interface IsolateStrategy {
	IsolateThread get();
	void release(IsolateThread isolateThread);

	IsolateStrategy DEFAULT = new SingleUseAsync();
}
