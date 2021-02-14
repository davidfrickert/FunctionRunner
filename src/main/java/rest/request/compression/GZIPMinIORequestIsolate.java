package rest.request.compression;

import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import lombok.SneakyThrows;
import minio.Minio;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.GZIPOutputStream;

import static minio.Minio.BUCKET;

public class GZIPMinIORequestIsolate {
	private static TypeConversionRegistry getRegistry () {
		return TypeConversionRegistry.getInstance();
	}

	@SneakyThrows
	@CEntryPoint
	static ObjectHandle execute (@IsolateThreadContext IsolateThread requestIsolate,
							IsolateThread parentIsolate,
							ObjectHandle fileNameHandle) {
		final Runtime runtime = Runtime.getRuntime();

		System.out.println("(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");

		// Unwrap handles
		String fileName = HandleUnwrapUtils.get(fileNameHandle);
		System.out.println("(Unwrap filename handle)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");
		Minio m = new Minio();
		final InputStream obj = m.getObj(BUCKET, fileName);

		//final MinioClient minioClient = Minio.get();
		//minioClient.traceOn(System.out);
		System.out.println("(Minio.get())(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");

		//GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET).object(fileName).build());
		System.out.println("(Get Object from MinIO)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");


		final PipedInputStream pipedInputStream = new PipedInputStream();
		final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);

		final Thread thread = new Thread(() -> {
			try {
				try (GZIPOutputStream gzipOut = new GZIPOutputStream(pipedOutputStream)) {
					IOUtils.copy(obj, gzipOut);
					obj.close();
				}
				System.out.println("(Written gzip to pipe)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");
				pipedOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.start();

		boolean success;
		try {
			m.putObject(BUCKET, "file.gzip", pipedInputStream);
			//minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET).object("file.gzip").stream(pipedInputStream, -1, 10485760).build());
			pipedInputStream.close();
			success = true;
		} catch (Exception e) {
			System.out.println("Put gzip in MinIO error");
			success = false;
		}

		thread.join();

		for (Thread t : Thread.getAllStackTraces().keySet()) {
			System.out.printf("Thread %s is %s.%s%n", t.getName(), t.isAlive() ? "alive" : "dead", t.isDaemon() ? " Daemon.": "");
		}
		return getRegistry().createHandle(parentIsolate, "success");
	}
}