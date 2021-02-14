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
        /*
        final Runtime runtime = Runtime.getRuntime();
        System.out.println("Spring Boot free memory: " + runtime.freeMemory() / (1024 * 1024) + "MB");
        System.out.println("Spring Boot total memory: " + runtime.totalMemory() / (1024 * 1024) + "MB");
        System.out.println("Spring Boot used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
        */
        return GZIPCompressionRequest.execInIsolate(file);
    }

    @PostMapping(value = "/compressNoIsolate")
    public @ResponseBody byte[] compressNoIsolate (@RequestParam("file") MultipartFile file) throws IOException {
        return GZIPCompressionRequest.exec(file);
    }
}
