package com.intensity;

import com.intensity.platform.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class IntensityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntensityApiApplication.class, args);
	}

}
