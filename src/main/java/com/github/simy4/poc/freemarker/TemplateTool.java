package com.github.simy4.poc.freemarker;

import freemarker.ext.beans.StringModel;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

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
    @SuppressWarnings("deprecation")
    public Set<String> referenceSet(Template template) throws TemplateModelException {
        var result = new HashSet<String>();
        var rootTreeNode = template.getRootTreeNode();
        for (var i = 0; i < rootTreeNode.getChildCount(); i++) {
            var templateModel = rootTreeNode.getChildNodes().get(i);
            if (templateModel instanceof StringModel) {
                processStringModel((StringModel) templateModel).ifPresent(result::add);
            }
        }
        return result;
    }

    private Optional<String> processStringModel(StringModel stringModel) {
        var wrappedObject = stringModel.getWrappedObject();
        switch (wrappedObject.getClass().getSimpleName()) {
            case "DollarVariable":
                return processDollarVariable(wrappedObject);
            default:
                return Optional.empty();
        }
    }

    private Optional<String> processDollarVariable(Object dollarVariable) {
        var expression = getInternalState(dollarVariable, "expression");
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
        var field = ReflectionUtils.findField(o.getClass(), fieldName);
        ReflectionUtils.makeAccessible(requireNonNull(field, "Field is null"));
        return ReflectionUtils.getField(field, o);
    }

}
