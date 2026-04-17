package com.github.simy4.poc.handlebars

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.TagType
import com.github.simy4.poc.TemplateRenderer
import java.io.StringWriter
import org.springframework.stereotype.Component

@Component("handlebars")
open class Renderer(private val handlebars: Handlebars) : TemplateRenderer {
  open override fun render(
      template: String,
      parameters: Map<String, String>,
  ): TemplateRenderer.Result {
    val template = handlebars.compileInline(template)
    val variables =
        template.collect(TagType.VAR, TagType.STAR_VAR, TagType.AMP_VAR, TagType.TRIPLE_VAR).toSet()
    val parameters = variables.associateWith { parameters[it] ?: "" }
    return StringWriter().use {
      template.apply(Context.newContext(parameters), it)
      TemplateRenderer.Result(it.toString(), parameters)
    }
  }
}
