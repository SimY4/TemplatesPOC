package com.github.simy4.poc.mustache

import com.github.simy4.poc.IntegrationTest
import org.hamcrest.Matchers.both
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
            post("/mustache")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template {{foo}}, {{&bar}}"))
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "mustache"),
            model().attribute("parameters", both(hasEntry("foo", "")).and(hasEntry("bar", ""))),
            model().attribute("result", "template , "))
  }

  @Test
  fun testUpdateTemplateVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/mustache")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formFields(
                    MultiValueMap.fromSingleValue(
                        mapOf(
                            "template" to "template {{foo}}, {{&bar}}",
                            "foo" to "foo",
                            "bar" to "bar"))))
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "mustache"),
            model()
                .attribute("parameters", both(hasEntry("foo", "foo")).and(hasEntry("bar", "bar"))),
            model().attribute("result", "template foo, bar"))
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/mustache")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template {{foo"))
        .andExpect(status().isBadRequest())
  }
}
