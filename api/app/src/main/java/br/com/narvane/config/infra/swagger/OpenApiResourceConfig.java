package br.com.narvane.config.infra.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Expõe o openapi.yaml em META-INF em /openapi.yaml para o Swagger UI
 * (mesma convenção do projeto todo: spec em META-INF).
 */
@Configuration
public class OpenApiResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/openapi.yaml")
                .addResourceLocations("classpath:META-INF/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource spec = location.createRelative("openapi.yaml");
                        return spec.exists() && spec.isReadable() ? spec : null;
                    }
                });
    }
}
