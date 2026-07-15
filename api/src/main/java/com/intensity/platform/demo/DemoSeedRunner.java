package com.intensity.platform.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Configuration
@Profile("demo")
@EnableConfigurationProperties(DemoProperties.class)
class DemoConfiguration {
}

@Component
@Profile("demo")
@Order(100)
@ConditionalOnProperty(prefix = "intensity.demo", name = "enabled", havingValue = "true", matchIfMissing = true)
class DemoSeedRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DemoSeedRunner.class);

	private final DemoSeedService demoSeedService;

	DemoSeedRunner(DemoSeedService demoSeedService) {
		this.demoSeedService = demoSeedService;
	}

	@Override
	public void run(ApplicationArguments args) {
		boolean applied = demoSeedService.seedIfEmpty();
		if (applied) {
			log.info("Demo world ready (leo/maya/nico@demo.intensity.app / {})", DemoSeedService.DEMO_PASSWORD);
		}
	}
}
