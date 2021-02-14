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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
		final Runtime runtime = Runtime.getRuntime();

		RestTemplate restTemplate = new RestTemplate();

		final URI uri = URI.create(LOCATION + "/" + bucket + "/" + objectName);
		final HttpHeaders headers = headers(new byte[0], 0);

		var httpEntity = new HttpEntity<>(headers);
		final ResponseEntity<Resource> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Resource.class);
		System.out.println("(Exchange request)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");
		InputStream responseInputStream;
		try {
			responseInputStream = response.getBody().getInputStream();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return responseInputStream;
	}

	public void putObject(String bucket, String objectName, InputStream fileStream) throws NoSuchAlgorithmException, InsufficientDataException, InternalException, IOException {
		final Runtime runtime = Runtime.getRuntime();

		RestTemplate restTemplate = new RestTemplate();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);
		restTemplate.setRequestFactory(requestFactory);

		final URI uri = URI.create(LOCATION + "/" + bucket + "/" + objectName);
		System.out.println("(URI + setup RestTemplate)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");

		byte[] info = fileStream.readAllBytes();
		System.out.println("(readAllBytes)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");

		final HttpHeaders headers = headers(info, info.length);
		headers.set("Content-Type", "application/octet-stream");
		var httpEntity = new HttpEntity<>(info, headers);

		final ResponseEntity<?> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
		System.out.println("(Response)(Runtime)Isolate used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024. * 1024) + "MB");
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