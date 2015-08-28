package github.templates.poc.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service("velocityTemplateTool")
public class TemplateTool {

    @Resource
    private Map<String, Object> commonTemplateContext;

    public Set<String> referenceSet(Template template) {
        SimpleNode simpleNode = (SimpleNode) template.getData();
        ReferenceNodeVisitor referenceNodeVisitor = new ReferenceNodeVisitor(commonTemplateContext);
        simpleNode.jjtAccept(referenceNodeVisitor, null);
        return referenceNodeVisitor.getReferenceSet();
    }

    private static class ReferenceNodeVisitor extends BaseVisitor {

        private final Map<String, Object> commonTemplateContext;
        private final Set<String> referenceSet = new HashSet<>();
        private final Set<String> localReferences = new HashSet<>();

        private ReferenceNodeVisitor(Map<String, Object> commonTemplateContext) {
            this.commonTemplateContext = commonTemplateContext;
        }

        private Set<String> getReferenceSet() {
            return referenceSet;
        }

        @Override
        public Object visit(ASTSetDirective node, Object data) {
            localReferences.add(((ASTReference) node.jjtGetChild(0)).getRootString());
            return super.visit(node, data);
        }

        @Override
        public Object visit(ASTReference node, Object data) {
            String literal = node.getRootString();
            if (!commonTemplateContext.containsKey(literal) && !localReferences.contains(literal)) {
                referenceSet.add(literal);
            }
            return super.visit(node, data);
        }

    }

}
