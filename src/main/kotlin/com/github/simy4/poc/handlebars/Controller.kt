package com.github.simy4.poc.handlebars

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.HandlebarsException
import com.github.jknack.handlebars.TagType
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

@Controller("handlebarsController")
@RequestMapping("/handlebars")
open class Controller(private val handlebars: Handlebars) {

  @Timed("handlebars.template.update")
  @PostMapping(
      produces = [MediaType.TEXT_HTML_VALUE],
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
  @Throws(IOException::class)
  open fun updateTemplate(@RequestParam parametersMap: Map<String, String>, model: Model): String {
    val template = handlebars.compileInline(parametersMap["template"] ?: "")
    val variables =
        template.collect(TagType.VAR, TagType.STAR_VAR, TagType.AMP_VAR, TagType.TRIPLE_VAR).toSet()
    val parameters = variables.associateWith { parametersMap[it] ?: "" }
    return StringWriter().use {
      template.apply(Context.newContext(parameters), it)
      model.addAttribute("template", "handlebars")
      val modelMap = ModelMap()
      modelMap.addAllAttributes(parameters)
      model.addAttribute("parameters", modelMap)
      model.addAttribute("result", it.toString())
      "result"
    }
  }

  @ExceptionHandler(HandlebarsException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun handleTemplateParseFailure() {}
}
