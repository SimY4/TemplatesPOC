package com.github.simy4.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RenderTemplate {

    private final String template;
    private final Map<String, String> parameters;

    @JsonCreator
    public RenderTemplate(@JsonProperty("template") String template,
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
