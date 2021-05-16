package pt.ist.photon_graal.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsPusher extends Thread {

	private static final Logger log = LoggerFactory.getLogger(MetricsPusher.class);

	private final MetricsSupport metrics;

	public MetricsPusher(MetricsSupport metrics) {
		this.metrics = metrics;
	}

	@Override
	public void run() {
		try {
			while (true) {
				sleep(50);
				metrics.push();
			}
		} catch (InterruptedException e) {
			log.error("Interrupted!", e);
		}
	}
}
