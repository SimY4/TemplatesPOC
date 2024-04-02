package com.github.simy4.poc.freemarker

import com.github.simy4.poc.IntegrationTest
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ControllerTest : IntegrationTest() {
  @Test
  fun testGetTemplateNoTemplate() {
    mockMvc
        .perform(get("/freemarker"))
        .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
  }

  @Test
  fun testUpdateTemplateDollarVariable() {
    mockMvc
        .perform(
            post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "template": "template ${"$"}{foo}, ${"$"}{bar!''}" }"""))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.template", `is`("template , ")),
            jsonPath("$.parameters", hasItems("foo", "bar")))
  }

  @Test
  fun testUpdateTemplateDollarVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    { "template": "template ${"$"}{foo}, ${"$"}{bar!''}"
                    , "parameters": { "foo": "foo", "bar": "bar"}
                    }"""))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.template", `is`("template foo, bar")),
            jsonPath("$.parameters", hasItems("foo", "bar")))
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"template": "template ${"$"}{olo" }"""))
        .andExpect(status().isBadRequest())
  }
}
