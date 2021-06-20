package pt.ist.photon_graal.runner.utils.management;

import org.graalvm.nativeimage.Isolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.Isolates;
import org.graalvm.word.WordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleIsolatePool implements IsolateStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleIsolatePool.class);

	private final int maxCachedIsolates;

	private final Queue<Long> pool = new ConcurrentLinkedQueue<>();

	public SimpleIsolatePool(final int maxCachedIsolates) {
		this.maxCachedIsolates = maxCachedIsolates;
	}

	@Override
	public IsolateThread get() {
		final Long isolateId = pool.poll();

		if (isolateId == null) {
			LOG.debug("Pool Empty - Creating new isolate.");
			return Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
		} else {
			LOG.debug("Fetching isolate from pool");
			return Isolates.attachCurrentThread(WordFactory.signed(isolateId));
		}
	}

	@Override
	public void release(final IsolateThread isolateThread) {
		final Isolate isolate = Isolates.getIsolate(isolateThread);
		Isolates.detachThread(isolateThread);
		if (pool.size() < maxCachedIsolates) {
			pool.add(isolate.rawValue());
			LOG.debug("Adding isolate to pool");
		} else {
			LOG.debug("Pool full. Discarding isolate");
			new Thread(() ->
					Isolates.tearDownIsolate(Isolates.attachCurrentThread(isolate))
			).start();
		}
	}
}
