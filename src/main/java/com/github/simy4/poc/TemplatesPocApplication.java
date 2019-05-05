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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

/**
 * Starting point of this application.
 */
@SpringBootApplication
@EnableSwagger2
public class TemplatesPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplatesPocApplication.class, args);
    }

    /**
     * Swagger API configuration.
     *
     * @return Swagger API docket. Never returns null.
     */
    @Bean
    public Docket api() {
        var defaultResponseMessages = Arrays.asList(
                new ResponseMessageBuilder().code(HttpStatus.BAD_REQUEST.value())
                        .message("Template Is Malformed").build(),
                new ResponseMessageBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal Server Error").build()
        );
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.github.simy4.poc.controllers"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, defaultResponseMessages)
                .globalResponseMessage(RequestMethod.POST, defaultResponseMessages);
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
        velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.Log4JLogChute");
        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger",
                "github.templates.poc.velocity");
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "string");
        velocityEngine.setProperty("string.resource.loader.description",
                "Velocity StringResource loader");
        velocityEngine.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        velocityEngine.setProperty("string.resource.loader.repository.class",
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
        var configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateUpdateDelayMilliseconds(0L);
        configuration.setTemplateLoader(templateLoader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return configuration;
    }

}
