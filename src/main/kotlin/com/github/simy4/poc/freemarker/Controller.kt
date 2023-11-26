package com.github.simy4.poc.freemarker

import com.github.simy4.poc.RenderTemplate
import com.github.simy4.poc.Template
import freemarker.cache.StringTemplateLoader
import freemarker.core.ParseException
import freemarker.template.Configuration
import freemarker.template.TemplateException
import freemarker.template.TemplateModelException
import freemarker.template.TemplateNotFoundException
import io.micrometer.core.annotation.Timed
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

@RestController("freemarkerController")
@RequestMapping("/freemarker")
open class Controller(
    private val stringTemplateLoader: StringTemplateLoader,
    private val freemarkerConfiguration: Configuration,
    private val templateTool: TemplateTool
) {

  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  @Throws(IOException::class, TemplateModelException::class)
  open fun getTemplate(): Template {
    val template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template)
    return Template(template.toString(), parameters)
  }

  @Timed("freemarker.template.update")
  @PostMapping(
      consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  @Throws(IOException::class, TemplateException::class)
  open fun updateTemplate(@RequestBody @Valid request: RenderTemplate): Template {
    request.template?.let(this::setTemplate)
    val template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template)
    return StringWriter().use {
      template.process(request.parameters, it)
      Template(it.toString(), parameters)
    }
  }

  private fun setTemplate(template: String) {
    stringTemplateLoader.putTemplate(TEMPLATE_NAME, template)
  }

  @ExceptionHandler(TemplateNotFoundException::class)
  open fun handleAbsentTemplate(): Template = Template.EMPTY

  @ExceptionHandler(ParseException::class, TemplateException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
