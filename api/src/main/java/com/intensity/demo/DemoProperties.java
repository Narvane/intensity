package com.intensity.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "intensity.demo")
public class DemoProperties {

	/**
	 * When true (default under profile {@code demo}), {@link DemoSeedRunner} applies the sample world.
	 */
	private boolean enabled = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
