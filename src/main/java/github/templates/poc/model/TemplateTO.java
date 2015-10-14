package github.templates.poc.model;

import java.util.Collections;
import java.util.Set;

public class TemplateTO {

    public static final TemplateTO EMPTY = new TemplateTO("", Collections.<String>emptySet());

    private final String template;
    private final Set<String> parameters;
    private final long conversionTime;

    public TemplateTO(String template, Set<String> parameters) {
        this(template, parameters, -1L);
    }

    public TemplateTO(String template, Set<String> parameters, long conversionTime) {
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
