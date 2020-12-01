package minio;

import lombok.Data;

@Data
public class Credentials {
	private final String accessKey;
	private final String secretKey;
	private final String sessionToken;

	public Credentials(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.sessionToken = null;
	}
}
