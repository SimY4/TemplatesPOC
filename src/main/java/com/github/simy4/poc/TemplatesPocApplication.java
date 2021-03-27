package com.github.simy4.poc;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Starting point of this application.
 */
@SpringBootApplication
public class TemplatesPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplatesPocApplication.class, args);
    }

    // Velocity template POC dependencies

    /**
     * Velocity engine bean factory.
     *
     * @return new velocity engine. Never returns null.
     */
    @Bean(initMethod = "init")
    public VelocityEngine velocityEngine() {
        var velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(Velocity.RUNTIME_LOG_NAME,
                "com.github.simy4.poc.velocity");
        velocityEngine.setProperty(Velocity.RESOURCE_LOADERS, "string");
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER + "string.description",
                "Velocity StringResource loader");
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER + ".string.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER + "string.repository.class",
                "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
        return velocityEngine;
    }

    @Bean
    public ToolManager toolManager() {
        return new ToolManager(true);
    }

    // Freemarker template POC dependencies

    @Bean
    public StringTemplateLoader templateLoader() {
        return new StringTemplateLoader();
    }

    /**
     * Freemarker engine bean factory.
     *
     * @return new freemarker engine. Never returns null.
     */
    @Bean
    public Configuration freeMarkerConfiguration(StringTemplateLoader templateLoader) {
        var configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateUpdateDelayMilliseconds(0L);
        configuration.setTemplateLoader(templateLoader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return configuration;
    }

}
