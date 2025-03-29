package com.github.simy4.poc.pebble

import io.micrometer.core.annotation.Timed
import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.error.PebbleException
import java.io.IOException
import java.io.StringWriter
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

@Controller("pebbleController")
@RequestMapping("/pebble")
open class Controller(
    private val memoryLoader: MemoryLoader,
    private val pebbleEngine: PebbleEngine,
    private val templateTool: TemplateTool
) {

  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @Timed("pebble.template.update")
  @PostMapping(
      produces = [MediaType.TEXT_HTML_VALUE],
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
  @Throws(IOException::class)
  open fun updateTemplate(@RequestParam parametersMap: Map<String, String>, model: Model): String {
    memoryLoader.set(parametersMap["template"] ?: "")
    val template = pebbleEngine.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template)
    return StringWriter().use {
      template.evaluate(it, parametersMap - setOf("template", "result"))
      model.addAttribute("template", "pebble")
      val modelMap = ModelMap()
      parameters.forEach { modelMap.addAttribute(it, "") }
      modelMap.addAllAttributes(parametersMap.filterKeys(parameters::contains))
      model.addAttribute("parameters", modelMap)
      model.addAttribute("result", it.toString())
      "result"
    }
  }

  @ExceptionHandler(PebbleException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
