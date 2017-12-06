package com.github.simy4.poc.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Velocity template utils.
 */
@Service("velocityTemplateTool")
public class TemplateTool {

    private final Map<String, Class> toolClassMap;

    @Autowired
    public TemplateTool(ToolManager toolManager) {
        this.toolClassMap = toolManager.createContext().getToolClassMap();
    }

    /**
     * Retrieves a list of arguments referenced in given template.
     *
     * @param template template to introspect
     * @return set of arguments
     */
    public Set<String> referenceSet(Template template) {
        Node node = (Node) template.getData();
        ReferenceNodeVisitor referenceNodeVisitor = new ReferenceNodeVisitor(toolClassMap);
        node.jjtAccept(referenceNodeVisitor, null);
        return referenceNodeVisitor.getReferenceSet();
    }

    private static class ReferenceNodeVisitor extends BaseVisitor {

        private final Map<String, Class> toolClassMap;
        private final Set<String> referenceSet = new HashSet<>();
        private final Set<String> localReferences = new HashSet<>();

        private ReferenceNodeVisitor(Map<String, Class> toolClassMap) {
            this.toolClassMap = toolClassMap;
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
            if (!toolClassMap.containsKey(literal) && !localReferences.contains(literal)) {
                referenceSet.add(literal);
            }
            return super.visit(node, data);
        }

    }

}
