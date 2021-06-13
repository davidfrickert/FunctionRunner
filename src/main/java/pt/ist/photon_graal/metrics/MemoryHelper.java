package pt.ist.photon_graal.metrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryHelper {
	private static final Logger log = LoggerFactory.getLogger(MemoryHelper.class);

	private static final long pid = ProcessHandle.current().pid();
	private static final String getRssOfPid = "ps -q " + pid + " -o rss=";

	public static Long heapMemory(Runtime runtime) {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public static Long rssMemory(Runtime runtime) {

		StringBuilder output = new StringBuilder();

		try {
			final Process proc = runtime.exec(getRssOfPid);
			proc.waitFor();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}
		} catch (Exception e) {
			log.error("Couldn't fetch RSS memory!", e);
		}

		return Long.parseLong(output.toString().trim()) * 1000; // ps returns value in kb
	}
}
