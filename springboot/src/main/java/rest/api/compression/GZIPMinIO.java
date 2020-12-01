package rest.api.compression;

import org.springframework.web.bind.annotation.*;
import rest.request.compression.GZIPMinIORequest;

import java.io.IOException;

@RestController
@RequestMapping(value = "/files")
public class GZIPMinIO {
	@GetMapping(value = "/isolated/compress/{file_name}")
	public boolean compress(@PathVariable(value = "file_name") String fileName) throws IOException {
		return GZIPMinIORequest.execInIsolate(fileName);
	}

	@GetMapping(value = "/isolated/compress/latency")
	public Double getAvgLatency() {
		return GZIPMinIORequest.getLatencyValues().stream().mapToDouble(a -> a).average().getAsDouble();
	}
	/*
	@PostMapping(value = "/compress/{file_name}")
	public @ResponseBody byte[] compressNoIsolate (@PathVariable(value = "file_name") String fileName) throws IOException {
		return GZIPMinIORequest.exec(fileName);
	}

	 */
}
