package github.templates.poc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Template JSON mapper.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public final class Template {

    public static final Template EMPTY = new Template("", Collections.emptySet());

    private final String template;
    private final Set<String> parameters;
    private final Long conversionTime;

    public Template(String template, Set<String> parameters) {
        this(template, parameters, null);
    }

    /**
     * Constructor.
     *
     * @param template       template string
     * @param parameters     template arguments
     * @param conversionTime template processing time
     */
    @JsonCreator
    public Template(@JsonProperty("template") String template,
                    @JsonProperty("parameters") Set<String> parameters,
                    @JsonProperty("conversionTime") Long conversionTime) {
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

    public Long getConversionTime() {
        return conversionTime;
    }

}
