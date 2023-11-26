package com.github.simy4.poc.mustache

import com.github.mustachejava.Code
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheException
import com.github.mustachejava.MustacheFactory
import com.github.simy4.poc.RenderTemplate
import com.github.simy4.poc.Template
import io.micrometer.core.annotation.Timed
import jakarta.validation.Valid
import java.io.IOException
import java.io.StringReader
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

@RestController("mustacheController")
@RequestMapping("/mustache")
open class Controller(private val mustache: MustacheFactory) {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  private val templateStore: AtomicReference<Mustache?> = AtomicReference(null)

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  open fun getTemplate(): Template =
      templateStore.get()?.let { template ->
        StringWriter().use { writer ->
          template.identity(writer)
          Template(writer.toString(), template.codes.parameters())
        }
      }
          ?: Template.EMPTY

  @Timed("mustache.template.update")
  @PostMapping(
      consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Throws(IOException::class)
  open fun updateTemplate(@RequestBody @Valid request: RenderTemplate): Template {
    request.template?.let(this::setTemplate)
    val template = templateStore.get() ?: return Template.EMPTY
    return StringWriter().use { writer ->
      template.execute(writer, request.parameters)
      Template(writer.toString(), template.codes.parameters())
    }
  }

  private fun setTemplate(template: String) {
    StringReader(template).use { templateStore.set(mustache.compile(it, TEMPLATE_NAME)) }
  }

  private fun Array<Code>?.parameters(): Set<String> =
      this?.let { arr -> arr.map { it.name }.toSet() + arr.flatMap { it.codes.parameters() } }
          ?: emptySet()

  @ExceptionHandler(MustacheException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
