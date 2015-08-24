package github.velocity.poc.model;

import java.util.Collections;
import java.util.Set;

public class Template {

    public static final Template EMPTY_TEMPLATE = new Template("", Collections.<String>emptySet());

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
