package com.github.simy4.poc.pebble

import io.pebbletemplates.pebble.node.Node
import io.pebbletemplates.pebble.node.PrintNode
import io.pebbletemplates.pebble.node.RootNode
import io.pebbletemplates.pebble.node.expression.BinaryExpression
import io.pebbletemplates.pebble.node.expression.ContextVariableExpression
import io.pebbletemplates.pebble.node.expression.TernaryExpression
import io.pebbletemplates.pebble.node.expression.UnaryExpression
import io.pebbletemplates.pebble.template.PebbleTemplate
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils

@Service("pebbleTemplateTool")
class TemplateTool {
  fun referenceSet(template: PebbleTemplate): Set<String> =
      template.internalState<RootNode>("rootNode").let { rootNode ->
        fun go(node: Node): Iterable<String> =
            when (node) {
              is ContextVariableExpression -> listOf(node.name)
              is UnaryExpression -> go(node.childExpression)
              is PrintNode -> go(node.expression)
              is BinaryExpression<*> -> go(node.leftExpression) + go(node.rightExpression)
              is TernaryExpression ->
                  listOf(node.expression1, node.expression2, node.expression3).flatMap { go(it) }
              else -> emptyList()
            }

        rootNode.body.children.flatMap { go(it) }.toSet()
      }

  @Suppress("UNCHECKED_CAST")
  private fun <T> Any.internalState(fieldName: String): T {
    val field = ReflectionUtils.findField(javaClass, fieldName)!!
    ReflectionUtils.makeAccessible(field)
    return ReflectionUtils.getField(field, this) as T
  }
}
