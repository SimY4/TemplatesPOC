package com.github.simy4.poc.freemarker

import com.github.simy4.poc.TemplateRenderer
import freemarker.cache.StringTemplateLoader
import freemarker.template.Configuration
import java.io.StringWriter
import org.springframework.stereotype.Component

@Component("freemarker")
open class Renderer(
    private val stringTemplateLoader: StringTemplateLoader,
    private val freemarkerConfiguration: Configuration,
    private val templateTool: TemplateTool,
) : TemplateRenderer {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  open override fun render(
      template: String,
      parameters: Map<String, String>,
  ): TemplateRenderer.Result {
    stringTemplateLoader.putTemplate(TEMPLATE_NAME, template)
    val template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template).associateWith { parameters[it] ?: "" }
    return StringWriter().use {
      template.process(parameters, it)
      TemplateRenderer.Result(it.toString(), parameters)
    }
  }
}
