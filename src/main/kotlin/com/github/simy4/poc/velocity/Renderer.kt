package com.github.simy4.poc.velocity

import com.github.simy4.poc.TemplateRenderer
import java.io.StringWriter
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.resource.loader.StringResourceLoader
import org.apache.velocity.tools.ToolManager
import org.springframework.stereotype.Component

@Component("velocity")
open class Renderer(
    private val velocityEngine: VelocityEngine,
    private val toolManager: ToolManager,
    private val templateTool: TemplateTool,
) : TemplateRenderer {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  open override fun render(
      template: String,
      parameters: Map<String, String>,
  ): TemplateRenderer.Result {
    StringResourceLoader.getRepository().putStringResource(TEMPLATE_NAME, template, "UTF-8")
    val template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8")
    val toolContext = toolManager.createContext()
    val parameters =
        templateTool.referenceSet(template).associateWith { parameter ->
          parameters[parameter]?.also { toolContext.put(parameter, it) } ?: ""
        }
    return StringWriter().use {
      template.merge(toolContext, it)
      TemplateRenderer.Result(it.toString(), parameters)
    }
  }
}
