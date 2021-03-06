package com.github.simy4.poc.controllers;

import com.github.simy4.poc.freemarker.TemplateTool;
import com.github.simy4.poc.model.RenderTemplate;
import com.github.simy4.poc.model.RenderedTemplate;
import com.github.simy4.poc.model.Template;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

/** Freemarker template engine REST controller. */
@RestController
@RequestMapping("/freemarker")
public class FreemarkerController {

  private static final String TEMPLATE_NAME = "template";

  private final StringTemplateLoader stringTemplateLoader;
  private final Configuration freemarkerConfiguration;
  private final TemplateTool templateTool;

  /**
   * Constructor.
   *
   * @param stringTemplateLoader freemarker template store
   * @param freemarkerConfiguration freemarker configuration
   * @param templateTool freemarker utilities
   */
  @Autowired
  public FreemarkerController(
      StringTemplateLoader stringTemplateLoader,
      Configuration freemarkerConfiguration,
      TemplateTool templateTool) {
    this.stringTemplateLoader = stringTemplateLoader;
    this.freemarkerConfiguration = freemarkerConfiguration;
    this.templateTool = templateTool;
  }

  /**
   * Retrieves latest template with a list of arguments.
   *
   * @return latest template or new empty template
   * @throws IOException should never happen.
   * @throws TemplateModelException on failure to parse template arguments.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Template getTemplate() throws IOException, TemplateModelException {
    var template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
    var parameters = templateTool.referenceSet(template);
    return new Template(template.toString(), parameters);
  }

  /**
   * Sets the new template as well as new list of argument values.
   *
   * @param request request containing a new template with parameter values
   * @return template engine application result
   * @throws IOException should never happen.
   * @throws TemplateException on malformed template or inconsistent arguments.
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public RenderedTemplate updateTemplate(@RequestBody @Valid RenderTemplate request)
      throws IOException, TemplateException {
    var maybeTemplateText = Optional.ofNullable(request.getTemplate());
    maybeTemplateText.ifPresent(this::setTemplate);
    var template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME);
    var parameters = templateTool.referenceSet(template);
    var writer = new StringWriter();
    var conversionTime = System.nanoTime();
    template.process(request.getParameters(), writer);
    conversionTime = System.nanoTime() - conversionTime;
    return new RenderedTemplate(writer.toString(), parameters, conversionTime);
  }

  private void setTemplate(String template) {
    stringTemplateLoader.putTemplate(TEMPLATE_NAME, template);
  }

  @ExceptionHandler(TemplateNotFoundException.class)
  public Template handleAbsentTemplate() {
    return Template.EMPTY;
  }

  @ExceptionHandler({ParseException.class, TemplateException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTemplateParseFailure() {}
}
