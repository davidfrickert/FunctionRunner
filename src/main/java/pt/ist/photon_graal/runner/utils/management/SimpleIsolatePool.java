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
		if (pool.isEmpty()) {
			LOG.debug("Pool Empty - Creating new isolate.");
			return Isolates.createIsolate(Isolates.CreateIsolateParameters.getDefault());
		} else {
			LOG.debug("Fetching isolate from pool");
			return Isolates.attachCurrentThread(WordFactory.signed(pool.poll()));
		}
	}

	@Override
	public void release(final IsolateThread isolateThread) {
		final Isolate isolate = Isolates.getIsolate(isolateThread);
		Isolates.detachThread(isolateThread);
		if (pool.size() < maxCachedIsolates) {
			LOG.debug("Adding isolate to pool");
			pool.add(isolate.rawValue());
		} else {
			LOG.debug("Pool full. Discarding isolate");
			new Thread(() ->
					Isolates.tearDownIsolate(Isolates.attachCurrentThread(isolate))
			).start();
		}
	}
}
