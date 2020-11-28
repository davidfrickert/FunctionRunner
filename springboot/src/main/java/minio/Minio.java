package minio;

import io.minio.MinioClient;

public class Minio {
	public static final String LOCATION = "http://localhost:9000";
	public static final String MINIO_ACCESS_KEY = "minio";
	public static final String MINIO_SECRET_KEY = "minio123";
	public static final String BUCKET = "msc-faas-graalvm";

	private static MinioClient INSTANCE;

	public static MinioClient get() {
		if (INSTANCE == null) {
			INSTANCE = MinioClient.builder()
					.endpoint(LOCATION)
					.credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
					.build();
		}
		return INSTANCE;
	}
}