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

@Service("freemarkerTemplateTool")
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
            if ("DollarVariable".equals(wrappedObject.getClass().getSimpleName())) {
                try {
                    Object expression = getInternalState(wrappedObject, "expression");
                    Object lho = getInternalState(expression, "lho");
                    result.add(lho.toString());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new TemplateModelException("Unable to reflect template model");
                }
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
