package minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
