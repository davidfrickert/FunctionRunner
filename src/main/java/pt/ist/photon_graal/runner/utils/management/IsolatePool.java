package pt.ist.photon_graal.runner.utils.management;

import org.graalvm.nativeimage.IsolateThread;

public class IsolatePool implements IsolateStrategy {

	@Override
	public IsolateThread get() {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public void release(final IsolateThread isolateThread) {
		throw new IllegalStateException("not implemented");
	}
}
