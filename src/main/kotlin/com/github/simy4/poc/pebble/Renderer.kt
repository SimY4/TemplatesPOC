package com.github.simy4.poc.pebble

import com.github.simy4.poc.TemplateRenderer
import io.pebbletemplates.pebble.PebbleEngine
import java.io.StringWriter
import org.springframework.stereotype.Component

@Component("pebble")
open class Renderer(
    private val memoryLoader: MemoryLoader,
    private val pebbleEngine: PebbleEngine,
    private val templateTool: TemplateTool,
) : TemplateRenderer {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  open override fun render(
      template: String,
      parameters: Map<String, String>,
  ): TemplateRenderer.Result {
    memoryLoader.set(template)
    val template = pebbleEngine.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template).associateWith { parameters[it] ?: "" }
    return StringWriter().use {
      template.evaluate(it, parameters)
      TemplateRenderer.Result(it.toString(), parameters)
    }
  }
}
