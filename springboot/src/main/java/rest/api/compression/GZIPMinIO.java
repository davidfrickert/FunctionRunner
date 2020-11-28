package rest.api.compression;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rest.request.compression.GZIPCompressionRequest;
import rest.request.compression.GZIPMinIORequest;

import java.io.IOException;

@RestController
@RequestMapping(value = "/files")
public class GZIPMinIO {
	@PostMapping(value = "/isolated/compress/{file_name}")
	public @ResponseBody
	boolean compress(@PathVariable(value = "file_name") String fileName) throws IOException {
		return GZIPMinIORequest.execInIsolate(fileName);
	}

	/*
	@PostMapping(value = "/compress/{file_name}")
	public @ResponseBody byte[] compressNoIsolate (@PathVariable(value = "file_name") String fileName) throws IOException {
		return GZIPMinIORequest.exec(fileName);
	}

	 */
}
