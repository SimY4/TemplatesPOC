package com.github.simy4.poc.pebble

import com.github.simy4.poc.RenderTemplate
import com.github.simy4.poc.Template
import io.micrometer.core.annotation.Timed
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.error.LoaderException
import io.pebbletemplates.pebble.error.PebbleException
import jakarta.validation.Valid
import java.io.IOException
import java.io.StringWriter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pebble")
open class PebbleController(
    private val memoryLoader: MemoryLoader,
    private val pebbleEngine: PebbleEngine,
    private val templateTool: TemplateTool
) {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  open fun getTemplate(): Template {
    val template = pebbleEngine.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template)
    return Template(template.toString(), parameters)
  }

  @Timed("pebble.template.update")
  @PostMapping(
      consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Throws(IOException::class)
  open fun updateTemplate(@RequestBody @Valid request: RenderTemplate): Template {
    request.template?.let(this::setTemplate)
    val template = pebbleEngine.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template)
    return StringWriter().use {
      template.evaluate(it, request.parameters)
      Template(it.toString(), parameters)
    }
  }

  private fun setTemplate(template: String) {
    memoryLoader.set(template)
  }

  @ExceptionHandler(LoaderException::class)
  open fun handleAbsentTemplate(): Template = Template.EMPTY

  @ExceptionHandler(PebbleException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
