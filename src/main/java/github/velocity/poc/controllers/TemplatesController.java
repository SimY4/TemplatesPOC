package github.velocity.poc.controllers;

import github.velocity.poc.model.Template;
import github.velocity.poc.velocity.TemplateDetailsFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@RestController
@RequestMapping("/templates")
public class TemplatesController {

    private static final String TEMPLATE_NAME = "template";

    private final VelocityEngine velocityEngine;
    private final TemplateDetailsFactory templateDetailsFactory;

    @Resource
    private Map<String, Object> commonTemplateContext;

    @Autowired
    public TemplatesController(VelocityEngine velocityEngine, TemplateDetailsFactory templateDetailsFactory) {
        this.velocityEngine = velocityEngine;
        this.templateDetailsFactory = templateDetailsFactory;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Template getTemplate() {
        StringResourceRepository templatesRepository = StringResourceLoader.getRepository();
        StringResource templateResource = templatesRepository.getStringResource(TEMPLATE_NAME);
        return templateResource == null ? Template.EMPTY_TEMPLATE :
                templateDetailsFactory.createTemplateDetails(templateResource.getBody());
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadTemplate(@RequestParam("template") MultipartFile file) throws IOException {
        StringResourceRepository templatesRepository = StringResourceLoader.getRepository();
        templatesRepository.putStringResource(TEMPLATE_NAME, new String(file.getBytes()), "UTF-8");
    }

    @RequestMapping(value = "convert", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String convert(@RequestBody Map<String, String> requestBody) {
        VelocityContext context = new VelocityContext(requestBody);
        for (Map.Entry<String, Object> commonContextItem : commonTemplateContext.entrySet()) {
            context.put(commonContextItem.getKey(), commonContextItem.getValue());
        }
        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate(TEMPLATE_NAME, "UTF-8", context, writer);
        return writer.toString();
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleFileUploadFailure() {
    }

}
