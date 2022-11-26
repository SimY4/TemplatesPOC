package com.github.simy4.poc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/** Render template. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RenderTemplate(String template, @NotNull Map<String, String> parameters) {
  public RenderTemplate {
    if (null == parameters) {
      parameters = Map.of();
    }
  }
}
