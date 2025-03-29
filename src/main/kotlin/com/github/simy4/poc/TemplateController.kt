package com.github.simy4.poc

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class TemplateController {
  @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
  fun index(): String {
    return "redirect:/freemarker"
  }

  @GetMapping("/{template}", produces = [MediaType.TEXT_HTML_VALUE])
  fun template(@PathVariable("template") template: String, model: Model): String {
    model.addAttribute("template", template)
    model.addAttribute("parameters", ModelMap())
    model.addAttribute("result", "")
    return "index"
  }
}
