package com.github.simy4.poc

interface TemplateRenderer {
  fun render(template: String, parameters: Map<String, String>): Result

  data class Result(val template: String, val parameters: Map<String, Any>)
}
