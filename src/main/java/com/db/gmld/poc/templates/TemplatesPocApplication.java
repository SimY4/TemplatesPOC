package com.db.gmld.poc.templates;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration(exclude = VelocityAutoConfiguration.class)
public class TemplatesPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplatesPocApplication.class, args);
    }

    @Bean(initMethod = "init")
    public VelocityEngine getVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "string");
        velocityEngine.setProperty("string.resource.loader.description", "Velocity StringResource loader");
        velocityEngine.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        velocityEngine.setProperty("string.resource.loader.repository.class", "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
        return velocityEngine;
    }

    //TODO use ToolManager for this purpose
    @Bean(name = "commonTemplateContext")
    public Map<String, Object> getCommonTemplateContext() {
        Map<String, Object> commonTemplateContext = new HashMap<>();
        commonTemplateContext.put("dateTool", new DateTool());
        return Collections.unmodifiableMap(commonTemplateContext);
    }

}
