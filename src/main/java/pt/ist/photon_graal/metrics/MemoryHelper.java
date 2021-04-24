package pt.ist.photon_graal.metrics;

public class MemoryHelper {
	public static Long currentMemoryUsage(Runtime runtime) {
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
