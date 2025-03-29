package com.github.simy4.poc.velocity

import com.github.simy4.poc.IntegrationTest
import org.hamcrest.Matchers.anEmptyMap
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
  fun testUpdateTemplateDollarVariable() {
    mockMvc
        .perform(
            post("/velocity")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template ${"$"}foo, $!bar, ${"$"}{foo}, $!{bar}"))
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "velocity"),
            model().attribute("parameters", both(hasEntry("foo", "")).and(hasEntry("bar", ""))),
            model().attribute("result", "template \$foo, , \${foo}, "))
  }

  @Test
  fun testUpdateTemplateDollarVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/velocity")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formFields(
                    MultiValueMap.fromSingleValue(
                        mapOf(
                            "template" to "template ${"$"}foo, \$!bar, ${"$"}{foo}, \$!{bar}",
                            "foo" to "foo",
                            "bar" to "bar"))))
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "velocity"),
            model()
                .attribute("parameters", both(hasEntry("foo", "foo")).and(hasEntry("bar", "bar"))),
            model().attribute("result", "template foo, bar, foo, bar"))
  }

  @Test
  fun testUpdateTemplateSet() {
    mockMvc
        .perform(
            post("/velocity")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template #set(${"$"}foo = 'test')"))
        .andExpectAll(
            status().isOk(),
            model().attribute("template", "velocity"),
            model().attribute("parameters", anEmptyMap<Any, Any>()),
            model().attribute("result", "template "))
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/velocity")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formField("template", "template ${"$"}{foo"))
        .andExpect(status().isBadRequest())
  }
}
