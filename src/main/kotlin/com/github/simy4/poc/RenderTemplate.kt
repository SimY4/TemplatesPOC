package com.github.simy4.poc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Render template. */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RenderTemplate(val template: String?, val parameters: Map<String, String> = emptyMap())
