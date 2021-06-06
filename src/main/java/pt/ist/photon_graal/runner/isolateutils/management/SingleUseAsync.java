package pt.ist.photon_graal.runner.isolateutils.management;

import java.time.Duration;
import java.time.Instant;
import org.graalvm.nativeimage.Isolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleUseAsync implements IsolateStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(SingleUseAsync.class);

	@Override
	public IsolateThread get() {
		return Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
	}

	@Override
	public void release(final IsolateThread isolateThread) {
		final Instant start = Instant.now();
		LOG.debug("Starting Isolate thread detach");
		final Isolate isolate = Isolates.getIsolate(isolateThread);
		Isolates.detachThread(isolateThread);
		LOG.debug("Fishing Isolate thread detach. Took {} ms", Duration.between(start, Instant.now()).toMillis());
		new Thread(() ->
			Isolates.tearDownIsolate(Isolates.attachCurrentThread(isolate))
		).start();
	}
}
