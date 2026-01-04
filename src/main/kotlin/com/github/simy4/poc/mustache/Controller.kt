package com.github.simy4.poc.mustache

import com.github.mustachejava.Code
import com.github.mustachejava.MustacheException
import com.github.mustachejava.MustacheFactory
import io.micrometer.core.annotation.Timed
import java.io.IOException
import java.io.StringReader
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

@Controller("mustacheController")
@RequestMapping("/mustache")
open class Controller(private val mustache: MustacheFactory) {

  companion object {
    private const val TEMPLATE_NAME = "template"
  }

  @Timed("mustache.template.update")
  @PostMapping(
      produces = [MediaType.TEXT_HTML_VALUE],
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
  )
  @Throws(IOException::class)
  open fun updateTemplate(@RequestParam parametersMap: Map<String, String>, model: Model): String {
    val template =
        StringReader(parametersMap["template"] ?: "").use { mustache.compile(it, TEMPLATE_NAME) }
    val parameters =
        template.codes?.parameters()?.associateWith { parametersMap[it] ?: "" } ?: emptyMap()
    return StringWriter().use {
      template.execute(it, parameters)
      model.addAttribute("template", "mustache")
      val modelMap = ModelMap()
      modelMap.addAllAttributes(parameters)
      model.addAttribute("parameters", modelMap)
      model.addAttribute("result", it.toString())
      "index :: result"
    }
  }

  private fun Array<Code>.parameters(): Set<String> =
      mapNotNull { it.name }.toSet() + flatMap { it.codes?.parameters() ?: emptySet() }

  @ExceptionHandler(MustacheException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
