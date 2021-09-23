package com.github.simy4.poc.model;

import java.util.Set;

/** Rendered template. */
public record RenderedTemplate(String template, Set<String> parameters, long conversionTime) {}
