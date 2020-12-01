package minio;

import io.minio.Digest;
import io.minio.MinioClient;
import io.minio.Time;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

public class Minio {
	public static final String LOCATION = "http://localhost:9000";
	public static final String MINIO_ACCESS_KEY = "minio";
	public static final String MINIO_SECRET_KEY = "minio123";
	public static final String BUCKET = "msc-faas-graalvm";

	private static MinioClient INSTANCE;
	private Credentials credentials;

	public static MinioClient get() {
		if (INSTANCE == null) {
			INSTANCE = MinioClient.builder()
					.endpoint(LOCATION)
					.credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
					.build();
		}
		return INSTANCE;
	}

	public Minio() {
		credentials = new Credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY);
	}
	public Minio(String accessKey, String secretKey) {
		credentials = new Credentials(accessKey, secretKey);
	}

	public InputStream getObj(String bucket, String objectName) throws NoSuchAlgorithmException, InsufficientDataException, InternalException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		final URI uri = URI.create(LOCATION + "/" + bucket + "/" + objectName);
		final HttpHeaders headers = headers(new byte[0], 0);
		var httpEntity = new HttpEntity<>(headers);
		final ResponseEntity<Resource> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Resource.class);

		InputStream responseInputStream;
		try {
			responseInputStream = response.getBody().getInputStream();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		return responseInputStream;
	}

	public InputStream post(String bucket, String objectName, InputStream fileStream) throws NoSuchAlgorithmException, InsufficientDataException, InternalException, IOException {
		RestTemplate restTemplate = new RestTemplate();
		final URI uri = URI.create(LOCATION + "/" + bucket + "/" + objectName);
		byte[] info = fileStream.readAllBytes();
		final HttpHeaders headers = headers(info, info.length);
		var httpEntity = new HttpEntity<>(headers);
		final ResponseEntity<Resource> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, Resource.class);

		InputStream responseInputStream;
		try {
			responseInputStream = response.getBody().getInputStream();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		return responseInputStream;
	}

	public HttpHeaders headers (byte[] body, int length) throws InsufficientDataException, NoSuchAlgorithmException, InternalException, IOException {
		HttpHeaders headers = new HttpHeaders();

		String sha256Hash = null;
		String md5Hash = null;
		if (credentials != null) {
			Object data = body;
			int len = length;
			if (body == null) {
				data = new byte[0];
				len = 0;
			}

			String[] hashes = Digest.sha256Md5Hashes(data, len);
			sha256Hash = hashes[0];
			md5Hash = hashes[1];

		} else if (body != null) {
			md5Hash = Digest.md5Hash(body, length);
		}

		if (md5Hash != null) {
			headers.set("Content-MD5", md5Hash);
		}

		if (sha256Hash != null) {
			headers.set("x-amz-content-sha256", sha256Hash);
		}

		if (credentials != null && credentials.getSessionToken() != null) {
			headers.set("X-Amz-Security-Token", credentials.getSessionToken());
		}

		ZonedDateTime date = ZonedDateTime.now();
		headers.set("x-amz-date", date.format(Time.AMZ_DATE_FORMAT));
		headers.set("User-Agent", "MinIO (Linux; amd64) minio-java/dev");
		headers.set("Connection", "close");

		/*
		if (length > 0) {
			var requestBody = BodyInserters.fromPublisher(Mono.just(body), byte[].class);
			result = requestBuilder.body(requestBody);
		} else {
			result = requestBuilder;
		}

		 */
		return headers;
	}
}