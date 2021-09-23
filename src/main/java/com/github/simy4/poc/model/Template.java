package com.github.simy4.poc.model;

import java.util.Collections;
import java.util.Set;

/** Template JSON mapper. */
public record Template(String template, Set<String> parameters) {
  public static final Template EMPTY = new Template("", Collections.emptySet());
}
