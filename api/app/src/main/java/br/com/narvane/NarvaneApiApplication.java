package br.com.narvane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {"br.com.narvane"},
        exclude = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        })
@EntityScan(basePackages = {"br.com.narvane"})
@EnableJpaRepositories(basePackages = {"br.com.narvane"})
public class NarvaneApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarvaneApiApplication.class, args);
    }
}
