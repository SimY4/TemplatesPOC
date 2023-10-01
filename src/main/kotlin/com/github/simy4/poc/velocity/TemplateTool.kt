package com.github.simy4.poc.velocity

import org.apache.velocity.Template
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

  fun referenceSet(template: Template): Set<String> =
      (template.data as Node).let { node ->
        val referenceNodeVisitor = ReferenceNodeVisitor(toolClassMap)
        node.jjtAccept(referenceNodeVisitor, null)
        return referenceNodeVisitor.referenceSet
      }

  private class ReferenceNodeVisitor(private val toolClassMap: Map<String, Class<*>>) :
      BaseVisitor() {
    val referenceSet: MutableSet<String> = hashSetOf()
    val localReferences: MutableSet<String> = hashSetOf()

    override fun visit(node: ASTSetDirective, data: Any?): Any? {
      localReferences.add((node.jjtGetChild(0) as ASTReference).rootString)
      return super.visit(node, data)
    }

    override fun visit(node: ASTReference, data: Any?): Any? {
      val literal = node.rootString
      if (!toolClassMap.containsKey(literal) && !localReferences.contains(literal)) {
        referenceSet += literal
      }
      return super.visit(node, data)
    }
  }
}
