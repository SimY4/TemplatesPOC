package com.github.simy4.poc.model;

import java.util.Collections;
import java.util.Set;

/** Template JSON mapper. */
public final class Template {

  public static final Template EMPTY = new Template("", Collections.emptySet());

  private final String template;
  private final Set<String> parameters;

  public Template(String template, Set<String> parameters) {
    this.template = template;
    this.parameters = parameters;
  }

  public String getTemplate() {
    return template;
  }

  public Set<String> getParameters() {
    return parameters;
  }
}
