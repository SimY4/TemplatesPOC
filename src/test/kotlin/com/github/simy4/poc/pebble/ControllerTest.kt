package com.github.simy4.poc.pebble

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

class ControllerTest: IntegrationTest() {
  @Test
  fun testGetTemplateNoTemplate() {
    mockMvc
        .perform(get("/pebble"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
  }

  @Test
  fun testUpdateTemplateVariable() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "template": "template {{foo}}" }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", `is`("template ")))
        .andExpect(jsonPath("$.parameters", hasItems("foo")))
  }

  @Test
  fun testUpdateTemplateVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    { "template": "template {{foo}}"
                    , "parameters": {"foo": "foo" }
                    }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", `is`("template foo")))
        .andExpect(jsonPath("$.parameters", hasItems("foo")))
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/pebble")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "template": "template {{foo" }"""))
        .andExpect(status().isBadRequest())
  }
}
