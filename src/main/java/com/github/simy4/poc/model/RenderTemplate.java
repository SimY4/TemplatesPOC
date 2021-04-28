package com.github.simy4.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

import java.util.Map;

/** Render template. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RenderTemplate {

  private final String template;
  private final @NotNull Map<String, String> parameters;

  @JsonCreator
  public RenderTemplate(
      @JsonProperty("template") String template,
      @JsonProperty("parameters") Map<String, String> parameters) {
    this.template = template;
    this.parameters = null == parameters ? Map.of() : parameters;
  }

  public String getTemplate() {
    return template;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }
}
