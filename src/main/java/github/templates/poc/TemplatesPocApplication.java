package github.templates.poc;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {VelocityAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class TemplatesPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplatesPocApplication.class, args);
    }

    @Bean
    public Parser getAutoDetectParser() {
        return new AutoDetectParser();
    }

    // Velocity template POC dependencies

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

    // Freemarker template POC dependencies

    @Bean
    public StringTemplateLoader getTemplateLoader() {
        return new StringTemplateLoader();
    }

    @Bean
    public Configuration getFreemarkerConfiguration(StringTemplateLoader templateLoader) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateLoader(templateLoader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return configuration;
    }

}
