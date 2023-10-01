package com.github.simy4.poc.freemarker

import freemarker.ext.beans.StringModel
import freemarker.template.Template
import freemarker.template.TemplateModelException
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils

/** Freemarker template utils. */
@Service("freemarkerTemplateTool")
class TemplateTool {
  @Suppress("DEPRECATION")
  @Throws(TemplateModelException::class)
  fun referenceSet(template: Template): Set<String> =
      template.rootTreeNode.let { rootTreeNode ->
        (0..rootTreeNode.childCount)
            .mapNotNull { i ->
              rootTreeNode.childNodes[i].let { templateModel ->
                when (templateModel) {
                  is StringModel -> processStringModel(templateModel)
                  else -> null
                }
              }
            }
            .toSet()
      }

  private fun processStringModel(stringModel: StringModel): String? =
      stringModel.wrappedObject.let { wrappedObject ->
        when (wrappedObject.javaClass.simpleName) {
          "DollarVariable" -> processDollarVariable(wrappedObject)
          else -> null
        }
      }

  private fun processDollarVariable(dollarVariable: Any): String? =
      dollarVariable.internalState("expression").let { expression ->
        when (expression!!.javaClass.simpleName) {
          "Identifier" -> expression.internalState("name").toString()
          "DefaultToExpression" -> expression.internalState("lho").toString()
          "MethodCall",
          "BuiltinVariable" -> null
          else ->
              throw IllegalStateException(
                  "Unable to introspect variable " +
                      expression +
                      " of type " +
                      expression.javaClass)
        }
      }

  private fun Any.internalState(fieldName: String): Any? {
    val field = ReflectionUtils.findField(javaClass, fieldName)!!
    ReflectionUtils.makeAccessible(field)
    return ReflectionUtils.getField(field, this)
  }
}
