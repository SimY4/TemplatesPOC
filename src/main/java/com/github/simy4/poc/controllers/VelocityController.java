package com.github.simy4.poc.controllers;

import com.github.simy4.poc.model.RenderTemplate;
import com.github.simy4.poc.model.RenderedTemplate;
import com.github.simy4.poc.model.Template;
import com.github.simy4.poc.velocity.TemplateTool;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
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

import javax.validation.Valid;

import java.io.StringWriter;
import java.util.Optional;

/** Velocity template engine REST controller. */
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
   * @param toolManager built in velocity tools
   * @param templateTool velocity utilities
   */
  @Autowired
  public VelocityController(
      VelocityEngine velocityEngine, ToolManager toolManager, TemplateTool templateTool) {
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
    var templatesRepository = StringResourceLoader.getRepository();
    var templateResource = templatesRepository.getStringResource(TEMPLATE_NAME);
    var template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8");
    var parameters = templateTool.referenceSet(template);
    return new Template(templateResource.getBody(), parameters);
  }

  /**
   * Sets the new template as well as new list of argument values.
   *
   * @param request request containing a new template with parameter values
   * @return template engine application result
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public RenderedTemplate updateTemplate(@RequestBody @Valid RenderTemplate request) {
    Optional.ofNullable(request.template()).ifPresent(this::setTemplate);
    var template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8");
    var toolContext = toolManager.createContext();
    toolContext.putAll(request.parameters());
    var parameters = templateTool.referenceSet(template);
    var writer = new StringWriter();
    var conversionTime = System.nanoTime();
    template.merge(toolContext, writer);
    conversionTime = System.nanoTime() - conversionTime;
    return new RenderedTemplate(writer.toString(), parameters, conversionTime);
  }

  private void setTemplate(String template) {
    StringResourceRepository templatesRepository = StringResourceLoader.getRepository();
    templatesRepository.putStringResource(TEMPLATE_NAME, template, "UTF-8");
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public Template handleAbsentTemplate() {
    return Template.EMPTY;
  }

  @ExceptionHandler({ParseErrorException.class, MethodInvocationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTemplateParseFailure() {}
}
