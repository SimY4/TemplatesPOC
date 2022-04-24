package com.github.simy4.poc.controllers;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.TagType;
import com.github.simy4.poc.model.RenderTemplate;
import com.github.simy4.poc.model.RenderedTemplate;
import com.github.simy4.poc.model.Template;
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
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/** Handlebars template engine REST controller. */
@RestController
@RequestMapping("/handlebars")
public class HandlebarsController {

  private final Handlebars handlebars;
  private final AtomicReference<com.github.jknack.handlebars.Template> templateStore;

  /**
   * Constructor.
   *
   * @param handlebars handlebars template engine
   * @param templateTool velocity utilities
   */
  @Autowired
  public HandlebarsController(Handlebars handlebars) {
    this.handlebars = handlebars;
    this.templateStore = new AtomicReference<>(null);
  }

  /**
   * Retrieves latest template with a list of arguments.
   *
   * @return latest template or new empty template
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Template getTemplate() {
    return Optional.ofNullable(templateStore.get())
        .map(
            template -> {
              return new Template(
                  template.text(), Set.copyOf(template.collectReferenceParameters()));
            })
        .orElse(Template.EMPTY);
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
  public RenderedTemplate updateTemplate(@RequestBody @Valid RenderTemplate request)
      throws IOException {
    Optional.ofNullable(request.template()).ifPresent(this::setTemplate);
    var template = templateStore.get();
    var context = Context.newContext(request.parameters());
    var parameters =
        Set.copyOf(
            template.collect(TagType.VAR, TagType.STAR_VAR, TagType.AMP_VAR, TagType.TRIPLE_VAR));
    var writer = new StringWriter();
    var conversionTime = System.nanoTime();
    template.apply(context, writer);
    conversionTime = System.nanoTime() - conversionTime;
    return new RenderedTemplate(writer.toString(), parameters, conversionTime);
  }

  private void setTemplate(String template) {
    try {
      templateStore.set(handlebars.compileInline(template));
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
  }

  @ExceptionHandler({HandlebarsException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTemplateParseFailure() {}
}
