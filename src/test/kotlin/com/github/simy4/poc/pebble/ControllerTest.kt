package com.github.simy4.poc.pebble

import com.github.simy4.poc.IntegrationTest
import org.hamcrest.Matchers.hasEntry
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.MultiValueMap

class ControllerTest : IntegrationTest() {
  @Test
  fun testUpdateTemplateVariable() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template {{foo}}")
        )
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "pebble"),
            model().attribute("parameters", hasEntry("foo", "")),
            model().attribute("result", "template "),
        )
  }

  @Test
  fun testUpdateTemplateVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formFields(
                    MultiValueMap.fromSingleValue(
                        mapOf("template" to "template {{foo}}", "foo" to "foo")
                    )
                )
        )
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "pebble"),
            model().attribute("parameters", hasEntry("foo", "foo")),
            model().attribute("result", "template foo"),
        )
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template {{foo")
        )
        .andExpect(status().isBadRequest())
  }
}
