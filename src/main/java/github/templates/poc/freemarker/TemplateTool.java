package github.templates.poc.freemarker;

import freemarker.core.TemplateElement;
import freemarker.ext.beans.StringModel;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Freemarker template utils.
 */
@Service("freemarkerTemplateTool")
public class TemplateTool {

    /**
     * Retrieves a list of arguments referenced in given template.
     *
     * @param template template to introspect
     * @return set of arguments
     */
    public Set<String> referenceSet(Template template) throws TemplateModelException {
        Set<String> result = new HashSet<>();
        TemplateElement rootTreeNode = template.getRootTreeNode();
        for (int i = 0; i < rootTreeNode.getChildCount(); i++) {
            TemplateModel templateModel = rootTreeNode.getChildNodes().get(i);
            if (templateModel instanceof StringModel) {
                processStringModel((StringModel) templateModel).ifPresent(result::add);
            }
        }
        return result;
    }

    private Optional<String> processStringModel(StringModel stringModel) {
        Object wrappedObject = stringModel.getWrappedObject();
        switch (wrappedObject.getClass().getSimpleName()) {
            case "DollarVariable":
                return processDollarVariable(wrappedObject);
            default:
                return Optional.empty();
        }
    }

    private Optional<String> processDollarVariable(Object dollarVariable) {
        Object expression = getInternalState(dollarVariable, "expression");
        switch (expression.getClass().getSimpleName()) {
            case "Identifier":
                return Optional.of(getInternalState(expression, "name").toString());
            case "DefaultToExpression":
                return Optional.of(getInternalState(expression, "lho").toString());
            case "MethodCall":
            case "BuiltinVariable":
                return Optional.empty();
            default:
                throw new IllegalStateException("Unable to introspect variable " + expression
                        + " of type " + expression.getClass());
        }
    }

    private Object getInternalState(Object o, String fieldName) {
        Field field = ReflectionUtils.findField(o.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, o);
    }

}
