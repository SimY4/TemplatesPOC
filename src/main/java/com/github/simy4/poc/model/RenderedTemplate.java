package com.github.simy4.poc.model;

import java.util.Set;

public class RenderedTemplate {

    private final String template;
    private final Set<String> parameters;
    private final long conversionTime;

    /**
     * Constructor.
     *
     * @param template       template string
     * @param parameters     template arguments
     * @param conversionTime template processing time
     */
    public RenderedTemplate(String template, Set<String> parameters, long conversionTime) {
        this.template = template;
        this.parameters = parameters;
        this.conversionTime = conversionTime;
    }

    public String getTemplate() {
        return template;
    }

    public Set<String> getParameters() {
        return parameters;
    }

    public long getConversionTime() {
        return conversionTime;
    }

}
