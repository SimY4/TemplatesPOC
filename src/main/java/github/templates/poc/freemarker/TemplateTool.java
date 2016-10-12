package github.templates.poc.freemarker;

import freemarker.core.TemplateElement;
import freemarker.ext.beans.StringModel;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service("freeMarkerTemplateTool")
public class TemplateTool {

    public Set<String> referenceSet(Template template) throws TemplateModelException {
        Set<String> result = new HashSet<>();
        TemplateElement rootTreeNode = template.getRootTreeNode();
        for (int i = 0; i < rootTreeNode.getChildCount(); i++) {
            TemplateModel templateModel = rootTreeNode.getChildNodes().get(i);
            if (!(templateModel instanceof StringModel)) {
                continue;
            }
            Object wrappedObject = ((StringModel) templateModel).getWrappedObject();
            switch (wrappedObject.getClass().getSimpleName()) {
                case "DollarVariable":
                    result.addAll(processDollarVariable(wrappedObject));
                    break;
            }
        }
        return result;
    }

    private Collection<String> processDollarVariable(Object dollarVariable) throws TemplateModelException {
        try {
            Object expression = getInternalState(dollarVariable, "expression");
            switch (expression.getClass().getSimpleName()) {
                case "Identifier":
                    return Collections.singleton(getInternalState(expression, "name").toString());
                case "DefaultToExpression":
                    return Collections.singleton(getInternalState(expression, "lho").toString());
                case "MethodCall":
                case "BuiltinVariable":
                    return Collections.emptySet();
                default:
                    throw new TemplateModelException("Unable to introspect variable " + expression +
                            " of type " + expression.getClass());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new TemplateModelException("Unable to reflect template model", e);
        }
    }

    private Object getInternalState(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        boolean wasAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(o);
        } finally {
            field.setAccessible(wasAccessible);
        }
    }

}
