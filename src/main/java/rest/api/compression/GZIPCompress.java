package rest.api.compression;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rest.request.compression.GZIPCompressionRequest;

import java.io.IOException;

@RestController
public class GZIPCompress {

    @PostMapping(value = "/compress")
    public @ResponseBody byte[] compress(@RequestParam("file") MultipartFile file) throws IOException {
        return GZIPCompressionRequest.compressFile(file);
    }
}
