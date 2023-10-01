package com.github.simy4.poc

/** Template JSON mapper. */
data class Template(val template: String, val parameters: Set<String>) {
  companion object {
    val EMPTY = Template("", emptySet())
  }
}
