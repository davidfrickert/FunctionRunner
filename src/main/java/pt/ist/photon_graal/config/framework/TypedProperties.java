package pt.ist.photon_graal.config.framework;

import java.util.Optional;
import java.util.Properties;

public class TypedProperties extends Properties {
	public TypedProperties() {
		super();
	}

	public Integer getInt(String property) {
		return Integer.parseInt(getProperty(property));
	}

	public Integer getIntOrDefault(String property, int defaultValue) {
		return Optional.ofNullable(getInt(property)).orElse(defaultValue);
	}

	public Boolean getBoolean(String property) {
		return Boolean.parseBoolean(getProperty(property));
	}

	public Boolean getBooleanOrDefault(String property, boolean defaultValue) {
		return Optional.ofNullable(getBoolean(property)).orElse(defaultValue);
	}
}
