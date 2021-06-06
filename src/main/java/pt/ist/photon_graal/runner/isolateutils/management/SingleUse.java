package pt.ist.photon_graal.runner.isolateutils.management;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;

public class SingleUse implements IsolateStrategy {

	@Override
	public IsolateThread get() {
		return Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
	}

	@Override
	public void release(final IsolateThread isolateThread) {
		Isolates.tearDownIsolate(isolateThread);
	}
}
