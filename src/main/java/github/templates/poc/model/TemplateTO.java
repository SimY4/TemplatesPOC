package github.templates.poc.model;

import java.util.Set;

public class TemplateTO {

    private final String template;
    private final Set<String> parameters;
    private final long conversionTime;

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
