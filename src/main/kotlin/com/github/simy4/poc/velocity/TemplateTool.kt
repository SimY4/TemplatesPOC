package com.github.simy4.poc.velocity

import org.apache.velocity.Template
import org.apache.velocity.runtime.parser.node.ASTNegateNode
import org.apache.velocity.runtime.parser.node.ASTReference
import org.apache.velocity.runtime.parser.node.ASTSetDirective
import org.apache.velocity.runtime.parser.node.Node
import org.apache.velocity.runtime.visitor.BaseVisitor
import org.apache.velocity.tools.ToolManager
import org.springframework.stereotype.Service

/** Velocity template utils. */
@Service("velocityTemplateTool")
class TemplateTool(toolManager: ToolManager) {
  private val toolClassMap: Map<String, Class<*>> = toolManager.createContext().toolClassMap

  @Suppress("UNCHECKED_CAST")
  fun referenceSet(template: Template): Set<String> =
      (template.data as Node).jjtAccept(ReferenceNodeVisitor(toolClassMap), mutableSetOf<String>())
          as Set<String>

  private class ReferenceNodeVisitor(private val toolClassMap: Map<String, Class<*>>) :
      BaseVisitor() {
    private val localReferences: MutableSet<String> = hashSetOf()

    override fun visit(node: ASTSetDirective, referenceSet: Any): Any {
      localReferences.add((node.jjtGetChild(0) as ASTReference).rootString)
      return super.visit(node, referenceSet)
    }

    @Suppress("UNCHECKED_CAST")
    override fun visit(node: ASTReference, referenceSet: Any): Any {
      val literal = node.rootString
      if (!toolClassMap.containsKey(literal) && !localReferences.contains(literal)) {
        (referenceSet as MutableSet<String>) += literal
      }
      return super.visit(node, referenceSet)
    }

    override fun visit(node: ASTNegateNode, data: Any): Any = node.childrenAccept(this, data)
  }
}
