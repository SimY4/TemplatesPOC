package com.github.simy4.poc.velocity

import io.micrometer.core.annotation.Timed
import java.io.StringWriter
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.exception.MethodInvocationException
import org.apache.velocity.exception.ParseErrorException
import org.apache.velocity.runtime.resource.loader.StringResourceLoader
import org.apache.velocity.tools.ToolManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Controller("velocityController")
@RequestMapping("/velocity")
open class Controller(
    private val velocityEngine: VelocityEngine,
    private val toolManager: ToolManager,
    private val templateTool: TemplateTool,
) {

  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @Timed("velocity.template.update")
  @PostMapping(
      produces = [MediaType.TEXT_HTML_VALUE],
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
  )
  open fun updateTemplate(@RequestParam parametersMap: Map<String, String>, model: Model): String {
    StringResourceLoader.getRepository()
        .putStringResource(TEMPLATE_NAME, parametersMap["template"] ?: "", "UTF-8")
    val template = velocityEngine.getTemplate(TEMPLATE_NAME, "UTF-8")
    val parameters = templateTool.referenceSet(template)
    val toolContext = toolManager.createContext()
    parameters.forEach { parameter ->
      parametersMap[parameter]?.let { toolContext.put(parameter, it) }
    }
    return StringWriter().use {
      template.merge(toolContext, it)
      model.addAttribute("template", "velocity")
      val modelMap = ModelMap()
      parameters.forEach { modelMap.addAttribute(it, parametersMap[it] ?: "") }
      model.addAttribute("parameters", modelMap)
      model.addAttribute("result", it.toString())
      "result"
    }
  }

  @ExceptionHandler(ParseErrorException::class, MethodInvocationException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
