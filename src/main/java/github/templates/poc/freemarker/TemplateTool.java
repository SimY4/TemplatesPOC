package github.templates.poc.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service("freemarkerTemplateTool")
public class TemplateTool {

    public Set<String> referenceSet(Template template) throws TemplateModelException {
        return Collections.emptySet();
    }

}
