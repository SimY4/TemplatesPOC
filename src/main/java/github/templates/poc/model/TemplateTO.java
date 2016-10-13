package github.templates.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

public class TemplateTO {

    public static final TemplateTO EMPTY = new TemplateTO("", Collections.emptySet());

    private final String template;
    private final Set<String> parameters;
    private final long conversionTime;

    public TemplateTO(String template, Set<String> parameters) {
        this(template, parameters, -1L);
    }

    @JsonCreator
    public TemplateTO(@JsonProperty("template") String template,
                      @JsonProperty("parameters") Set<String> parameters,
                      @JsonProperty("conversionTime") long conversionTime) {
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
