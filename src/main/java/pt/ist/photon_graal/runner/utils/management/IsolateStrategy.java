package pt.ist.photon_graal.runner.utils.management;

import org.graalvm.nativeimage.IsolateThread;

public interface IsolateStrategy {
	IsolateThread get();
	void release(IsolateThread isolateThread);

	IsolateStrategy DEFAULT = new SimpleIsolatePool(4);

	enum Strategy {
		SIMPLE_POOL,
		SINGLE_USE_ASYNC_DETACH,
		SINGLE_USE_SYNC_DETACH
	}

	static IsolateStrategy fromEnum(Strategy name, String... extraArgs) {
		switch (name) {
			case SIMPLE_POOL:
				return new SimpleIsolatePool(Integer.parseInt(extraArgs[0]));
			case SINGLE_USE_ASYNC_DETACH:
				return new SingleUseAsync();
			case SINGLE_USE_SYNC_DETACH:
				return new SingleUseSync();
			default:
				throw new IllegalArgumentException("No strategy found for: " + name);
		}
	}
}
