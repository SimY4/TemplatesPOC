package github.templates.poc.controllers;

import github.templates.poc.model.Template;
import github.templates.poc.velocity.TemplateTool;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Velocity template engine REST controller.
 */
@RestController
@RequestMapping("/velocity")
public class VelocityController {

    private static final String TEMPLATE_NAME = "template";

    private final VelocityEngine velocityEngine;
    private final ToolManager toolManager;
    private final TemplateTool templateTool;

    /**
     * Constructor.
     *
     * @param velocityEngine velocity template store
     * @param toolManager    built in velocity tools
     * @param templateTool   velocity utilities
     */
    @Autowired
    public VelocityController(VelocityEngine velocityEngine, ToolManager toolManager, TemplateTool templateTool) {
        this.velocityEngine = velocityEngine;
        this.toolManager = toolManager;
        this.templateTool = templateTool;
    }

    /**
     * Retrieves latest template with a list of arguments.
     *
     * @return latest template or new empty template
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Template getTemplate() {
        StringResourceRepository templatesRepository = StringResourceLoader.getRepository();
        StringResource templateResource = templatesRepository.getStringResource(TEMPLATE_NAME);
        org.apache.velocity.Template template = velocityEngine.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = templateTool.referenceSet(template);
        return new Template(templateResource.getBody(), parameters);
    }


    /**
     * Sets the new template as well as new list of argument values.
     *
     * @param requestBody map containing new template with it's values
     * @return template engine application result
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Template updateTemplate(@RequestBody Map<String, String> requestBody) {
        Optional<String> maybeTemplateText = Optional.ofNullable(requestBody.remove("__template__"));
        maybeTemplateText.ifPresent(this::setTemplate);
        org.apache.velocity.Template template = velocityEngine.getTemplate(TEMPLATE_NAME);
        Set<String> parameters = templateTool.referenceSet(template);
        ToolContext toolContext = toolManager.createContext();
        toolContext.putAll(requestBody);
        StringWriter writer = new StringWriter();
        long conversionTime = System.nanoTime();
        velocityEngine.mergeTemplate(TEMPLATE_NAME, "UTF-8", toolContext, writer);
        conversionTime = System.nanoTime() - conversionTime;
        return new Template(writer.toString(), parameters, conversionTime);
    }

    private void setTemplate(String template) {
        StringResourceRepository templatesRepository = StringResourceLoader.getRepository();
        templatesRepository.putStringResource(TEMPLATE_NAME, template, "UTF-8");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Template handleAbsentTemplate() {
        return Template.EMPTY;
    }

    @ExceptionHandler({
            ParseErrorException.class,
            MethodInvocationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleTemplateParseFailure() { }

}
