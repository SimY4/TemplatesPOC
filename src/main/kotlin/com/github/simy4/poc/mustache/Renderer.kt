package com.github.simy4.poc.mustache

import com.github.mustachejava.Code
import com.github.mustachejava.MustacheFactory
import com.github.simy4.poc.TemplateRenderer
import java.io.StringReader
import java.io.StringWriter
import org.springframework.stereotype.Component

@Component("mustache")
open class Renderer(private val mustache: MustacheFactory) : TemplateRenderer {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  open override fun render(
      template: String,
      parameters: Map<String, String>,
  ): TemplateRenderer.Result {
    val template = StringReader(template).use { mustache.compile(it, TEMPLATE_NAME) }
    val parameters =
        template.codes?.parameters()?.associateWith { parameters[it] ?: "" } ?: emptyMap()
    return StringWriter().use {
      template.execute(it, parameters)
      TemplateRenderer.Result(it.toString(), parameters)
    }
  }

  private fun Array<Code>.parameters(): Set<String> =
      mapNotNull { it.name }.toSet() + flatMap { it.codes?.parameters() ?: emptySet() }
}
