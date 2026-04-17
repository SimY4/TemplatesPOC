package com.github.simy4.poc

import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
open class TemplateController(
    @param:Autowired private val renderers: Map<String, TemplateRenderer>
) {
  companion object {
    private const val PATH_PATTERN = "freemarker|handlebars|mustache|pebble|velocity"
    private const val TEMPLATE = "template"
  }

  @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
  open fun index(): String {
    return "redirect:/freemarker"
  }

  @GetMapping("/{${TEMPLATE}:${PATH_PATTERN}}", produces = [MediaType.TEXT_HTML_VALUE])
  open fun template(@PathVariable(TEMPLATE) template: String, model: Model): String {
    model.addAttribute(TEMPLATE, template)
    model.addAttribute("parameters", ModelMap())
    model.addAttribute("result", "")
    return "index"
  }

  @Timed("template.update")
  @PostMapping(
      "/{${TEMPLATE}:${PATH_PATTERN}}",
      consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
      produces = [MediaType.TEXT_HTML_VALUE],
  )
  open fun renderTemplate(
      @PathVariable(TEMPLATE) template: String,
      @RequestParam parametersMap: Map<String, String>,
      model: Model,
  ): String =
      runCatching {
            val templateInput = parametersMap[TEMPLATE] ?: ""
            renderers[template]!!.render(templateInput, parametersMap - TEMPLATE)
          }
          .fold(
              onSuccess = { result ->
                model.addAttribute(TEMPLATE, template)
                val modelMap = ModelMap()
                modelMap.addAllAttributes(result.parameters)
                model.addAttribute("parameters", modelMap)
                model.addAttribute("result", result.template)
                return "index :: result"
              },
              onFailure = { t ->
                model.addAttribute("error", t.message)
                return "index :: error"
              },
          )
}
