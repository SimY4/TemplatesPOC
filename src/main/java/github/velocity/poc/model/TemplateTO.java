package github.velocity.poc.model;

import java.util.Collections;
import java.util.Set;

public class TemplateTO {

    public static final TemplateTO EMPTY_TEMPLATE = new TemplateTO("", Collections.<String>emptySet());

    private final String template;
    private final Set<String> parameters;

    public TemplateTO(String template, Set<String> parameters) {
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
