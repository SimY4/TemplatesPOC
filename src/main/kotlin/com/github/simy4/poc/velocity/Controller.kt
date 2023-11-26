package com.github.simy4.poc.velocity

import com.github.simy4.poc.RenderTemplate
import com.github.simy4.poc.Template
import io.micrometer.core.annotation.Timed
import jakarta.validation.Valid
import java.io.StringWriter
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.exception.MethodInvocationException
import org.apache.velocity.exception.ParseErrorException
import org.apache.velocity.exception.ResourceNotFoundException
import org.apache.velocity.runtime.resource.loader.StringResourceLoader
import org.apache.velocity.tools.ToolManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController("velocityController")
@RequestMapping("/velocity")
open class Controller(
    private val velocityEngine: VelocityEngine,
    private val toolManager: ToolManager,
    private val templateTool: TemplateTool
) {
  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
  open fun getTemplate(): Template {
    val templatesRepository = StringResourceLoader.getRepository()
    val templateResource = templatesRepository.getStringResource(TEMPLATE_NAME)
    val template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8")
    val parameters = templateTool.referenceSet(template)
    return Template(templateResource.body, parameters)
  }

  @Timed("velocity.template.update")
  @PostMapping(
      consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
  open fun updateTemplate(@RequestBody @Valid request: RenderTemplate): Template {
    request.template?.let(this::setTemplate)
    val template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8")
    val toolContext = toolManager.createContext()
    toolContext.putAll(request.parameters)
    val parameters = templateTool.referenceSet(template)
    return StringWriter().use {
      template.merge(toolContext, it)
      Template(it.toString(), parameters)
    }
  }

  private fun setTemplate(template: String) {
    val templatesRepository = StringResourceLoader.getRepository()
    templatesRepository.putStringResource(TEMPLATE_NAME, template, "UTF-8")
  }

  @ExceptionHandler(ResourceNotFoundException::class)
  open fun handleAbsentTemplate(): Template = Template.EMPTY

  @ExceptionHandler(ParseErrorException::class, MethodInvocationException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
