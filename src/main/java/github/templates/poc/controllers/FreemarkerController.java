package github.templates.poc.controllers;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import github.templates.poc.model.TemplateTO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.*;

@RestController
@RequestMapping("/freemarker")
public class FreemarkerController {

    private static final String TEMPLATE_NAME = "template";

    private final StringTemplateLoader stringTemplateLoader;
    private final Configuration freemarkerConfiguration;

    @Autowired
    public FreemarkerController(StringTemplateLoader stringTemplateLoader, Configuration freemarkerConfiguration) {
        this.stringTemplateLoader = stringTemplateLoader;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateTO getTemplate() throws IOException {
        Template template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = new HashSet<>(Arrays.asList(template.getCustomAttributeNames()));
        Object templateSource = stringTemplateLoader.findTemplateSource(TEMPLATE_NAME);
        Reader reader = stringTemplateLoader.getReader(templateSource, "UTF-8");
        String templateString = IOUtils.toString(reader);
        return new TemplateTO(templateString, parameters, -1L);
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateTO updateTemplate(@RequestBody Map<String, String> requestBody) throws IOException, TemplateException {
        String templateText = requestBody.remove("__template__");
        if (templateText != null) {
            setTemplate(templateText);
        }
        Template template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = new HashSet<>(Arrays.asList(template.getCustomAttributeNames()));
        long conversionTime = System.currentTimeMillis();
        StringWriter writer = new StringWriter();
        template.process(requestBody, writer);
        return new TemplateTO(writer.toString(), parameters, System.currentTimeMillis() - conversionTime);
    }

    private void setTemplate(String template) {
        stringTemplateLoader.putTemplate(TEMPLATE_NAME, template);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleTemplateLoadingFailure() { }

    @ExceptionHandler(TemplateNotFoundException.class)
    public TemplateTO handleAbsentTemplate() {
        return new TemplateTO("", Collections.<String>emptySet(), -1L);
    }

    @ExceptionHandler(ParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleTemplateParseFailure() { }

    @ExceptionHandler(TemplateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleTemplateConversionFailure() { }

}
