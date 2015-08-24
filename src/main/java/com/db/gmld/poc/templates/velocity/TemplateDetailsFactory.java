package com.db.gmld.poc.templates.velocity;

import com.db.gmld.poc.templates.model.Template;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateDetailsFactory {

    private static final Pattern VELOCITY_VARIABLE_PATTERN = Pattern.compile(
            "(?<assignment>#set\\(\\p{Blank}*)?\\$!?\\{?(?<variable>[\\p{Alnum}-_]+)\\p{all}*?\\}?"
    );

    @Resource
    private Map<String, Object> commonTemplateContext;

    public Template createTemplateDetails(String templateBody) {
        Matcher matcher = VELOCITY_VARIABLE_PATTERN.matcher(templateBody);
        int start = 0;
        Set<String> variables = new HashSet<>();
        Set<String> assignments = new HashSet<>();
        while (matcher.find(start)) {
            String variable = matcher.group("variable");
            if (commonTemplateContext.containsKey(variable)) {
                start = matcher.end();
                continue;
            }
            String assignment = matcher.group("assignment");
            if (Objects.isNull(assignment) && !assignments.contains(variable)) {
                variables.add(variable);
            } else {
                assignments.add(variable);
            }
            start = matcher.end();
        }
        return new Template(templateBody, Collections.unmodifiableSet(variables));
    }

}
