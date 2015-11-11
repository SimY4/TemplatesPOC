package github.templates.poc.freemarker;

import freemarker.core.TemplateElement;
import freemarker.ext.beans.StringModel;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Service("freeMarkerTemplateTool")
public class TemplateTool {

    @SuppressWarnings("deprecation")
    public Set<String> referenceSet(Template template) throws TemplateModelException {
        Set<String> result = new HashSet<>();
        TemplateElement rootTreeNode = template.getRootTreeNode();
        for (int i = 0; i < rootTreeNode.getChildCount(); i++) {
            TemplateModel templateModel = rootTreeNode.getChildNodes().get(i);
            if (!(templateModel instanceof StringModel)) {
                continue;
            }
            Object wrappedObject = ((StringModel) templateModel).getWrappedObject();
            if (!"DollarVariable".equals(wrappedObject.getClass().getSimpleName())) {
                continue;
            }

            try {
                Object expression = getInternalState(wrappedObject, "expression");
                switch (expression.getClass().getSimpleName()) {
                    case "Identifier":
                        result.add(getInternalState(expression, "name").toString());
                        break;
                    case "DefaultToExpression":
                        result.add(getInternalState(expression, "lho").toString());
                        break;
                    case "BuiltinVariable":
                        break;
                    default:
                        throw new IllegalStateException("Unable to introspect variable");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new TemplateModelException("Unable to reflect template model");
            }
        }
        return result;
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
