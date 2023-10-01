package com.github.simy4.poc.handlebars

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.HandlebarsException
import com.github.jknack.handlebars.TagType
import com.github.jknack.handlebars.Template as HandlebarsTemplate
import com.github.simy4.poc.RenderTemplate
import com.github.simy4.poc.Template
import io.micrometer.core.annotation.Timed
import jakarta.validation.Valid
import java.io.IOException
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** Handlebars template engine REST controller. */
@RestController
@RequestMapping("/handlebars")
open class HandlebarsController(private val handlebars: Handlebars) {
  private val templateStore: AtomicReference<HandlebarsTemplate?> = AtomicReference(null)

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  open fun getTemplate(): Template =
      templateStore.get()?.let { template ->
        Template(template.text(), template.collectReferenceParameters().toSet())
      }
          ?: Template.EMPTY

  @Timed("handlebars.template.update")
  @PostMapping(
      consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Throws(IOException::class)
  open fun updateTemplate(@RequestBody @Valid request: RenderTemplate): Template {
    request.template?.let(this::setTemplate)
    val template = templateStore.get() ?: return Template.EMPTY
    val context = Context.newContext(request.parameters)
    val parameters =
        template.collect(TagType.VAR, TagType.STAR_VAR, TagType.AMP_VAR, TagType.TRIPLE_VAR).toSet()
    return StringWriter().use {
      template.apply(context, it)
      Template(it.toString(), parameters)
    }
  }

  private fun setTemplate(template: String) {
    templateStore.set(handlebars.compileInline(template))
  }

  @ExceptionHandler(HandlebarsException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
