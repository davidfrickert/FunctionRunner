package rest.request.compression;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import isolateutils.conversion.registry.TypeConversionRegistry;
import isolateutils.handles.HandleUnwrapUtils;
import lombok.SneakyThrows;
import minio.Minio;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CEntryPoint.IsolateThreadContext;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.management.ManagementFactory;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

import static minio.Minio.BUCKET;

public class GZIPMinIORequestIsolate {
	private static TypeConversionRegistry getRegistry () {
		return TypeConversionRegistry.getInstance();
	}

	@SneakyThrows
	@CEntryPoint
	static boolean execute (@IsolateThreadContext IsolateThread requestIsolate,
							IsolateThread parentIsolate,
							ObjectHandle byteArrayHandlerFile) {
		final Runtime runtime = Runtime.getRuntime();

		System.out.println("(Runtime)Isolate total memory: " + runtime.totalMemory() / (1024 * 1024) + "MB");
		System.out.println("(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		System.out.println("(MXBeans)Isolate committed memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / (1024 * 1024) + "MB");
		System.out.println("(MXBeans)Isolate used memory: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");

		// Unwrap handles
		String fileName = HandleUnwrapUtils.get(byteArrayHandlerFile);
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		final MinioClient minioClient = Minio.get();
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		System.out.println("Getting file from MinIO");
		GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder().bucket(BUCKET).object(fileName).build());
		System.out.println("Got file from MinIO successfully");
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try (GZIPOutputStream gzipOut = new GZIPOutputStream(bos)) {
			gzipOut.write(response.read());
		}
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		final PipedInputStream pipedInputStream = new PipedInputStream();
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
		final PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		new Thread(new Runnable() {
			@SneakyThrows
			@Override
			public void run () {
				bos.writeTo(pipedOutputStream);
				System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
				bos.close();
				System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
				pipedOutputStream.close();
				System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
			}
		}).start();

		boolean success;
		try {
			minioClient.putObject(PutObjectArgs.builder().bucket(BUCKET).object("GZIPPED").stream(pipedInputStream, -1, 10485760).build());
			System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
			pipedInputStream.close();
			System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");
			success = true;
		} catch (Exception e) {
			System.out.println("Put gzip in MinIO error");
			success = false;
		}

		System.out.println("returning result " + success);
		// return result as handle

		System.out.println("(END)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + "MB");

		return success;

	}
}