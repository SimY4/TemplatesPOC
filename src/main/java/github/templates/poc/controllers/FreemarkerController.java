package github.templates.poc.controllers;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import github.templates.poc.freemarker.TemplateTool;
import github.templates.poc.model.TemplateTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/freemarker")
public class FreemarkerController {

    private static final String TEMPLATE_NAME = "template";

    private final StringTemplateLoader stringTemplateLoader;
    private final Configuration freemarkerConfiguration;
    private final TemplateTool templateTool;

    @Autowired
    public FreemarkerController(StringTemplateLoader stringTemplateLoader, Configuration freemarkerConfiguration,
                                TemplateTool templateTool) {
        this.stringTemplateLoader = stringTemplateLoader;
        this.freemarkerConfiguration = freemarkerConfiguration;
        this.templateTool = templateTool;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateTO getTemplate() throws IOException, TemplateModelException {
        Template template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = templateTool.referenceSet(template);
        return new TemplateTO(template.toString(), parameters, -1L);
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TemplateTO updateTemplate(@RequestBody Map<String, String> requestBody) throws IOException, TemplateException {
        String templateText = requestBody.remove("__template__");
        if (templateText != null) {
            setTemplate(templateText);
        }
        Template template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = templateTool.referenceSet(template);
        long conversionTime = System.nanoTime();
        StringWriter writer = new StringWriter();
        template.process(requestBody, writer);
        return new TemplateTO(writer.toString(), parameters, System.nanoTime() - conversionTime);
    }

    private void setTemplate(String template) {
        stringTemplateLoader.putTemplate(TEMPLATE_NAME, template);
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    public TemplateTO handleAbsentTemplate() {
        return new TemplateTO("", Collections.<String>emptySet(), -1L);
    }

    @ExceptionHandler({ParseException.class, MalformedTemplateNameException.class, TemplateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleTemplateParseFailure() { }

}
