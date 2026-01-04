package com.github.simy4.poc.freemarker

import freemarker.cache.StringTemplateLoader
import freemarker.core.ParseException
import freemarker.template.Configuration
import freemarker.template.TemplateException
import io.micrometer.core.annotation.Timed
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

@Controller("freemarkerController")
@RequestMapping("/freemarker")
open class Controller(
    private val stringTemplateLoader: StringTemplateLoader,
    private val freemarkerConfiguration: Configuration,
    private val templateTool: TemplateTool,
) {

  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @Timed("freemarker.template.update")
  @PostMapping(
      produces = [MediaType.TEXT_HTML_VALUE],
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
  )
  @Throws(IOException::class, TemplateException::class)
  open fun updateTemplate(@RequestParam parametersMap: Map<String, String>, model: Model): String {
    stringTemplateLoader.putTemplate(TEMPLATE_NAME, parametersMap["template"] ?: "")
    val template = freemarkerConfiguration.getTemplate(TEMPLATE_NAME)
    val parameters = templateTool.referenceSet(template).associateWith { parametersMap[it] ?: "" }
    return StringWriter().use {
      template.process(parameters, it)
      model.addAttribute("template", "freemarker")
      val modelMap = ModelMap()
      modelMap.addAllAttributes(parameters)
      model.addAttribute("parameters", modelMap)
      model.addAttribute("result", it.toString())
      "index :: result"
    }
  }

  @ExceptionHandler(ParseException::class, TemplateException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
