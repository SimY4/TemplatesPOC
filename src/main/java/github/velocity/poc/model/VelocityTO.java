package github.velocity.poc.model;

import java.util.Collections;
import java.util.Set;

public class VelocityTO {

    public static final VelocityTO EMPTY_TEMPLATE = new VelocityTO("", Collections.<String>emptySet());

    private final String template;
    private final Set<String> parameters;

    public VelocityTO(String template, Set<String> parameters) {
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
