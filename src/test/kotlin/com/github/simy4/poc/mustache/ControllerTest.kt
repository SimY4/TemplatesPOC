package com.github.simy4.poc.mustache

import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ControllerTest {

  @Autowired lateinit var webApplicationContext: WebApplicationContext

  private lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).build()
  }

  @Test
  fun testGetTemplateNoTemplate() {
    mockMvc
        .perform(get("/mustache"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
  }

  @Test
  fun testUpdateTemplateVariable() {
    mockMvc
        .perform(
            post("/mustache")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "template": "template {{foo}}, {{&bar}}" }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", `is`("template , ")))
        .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")))
  }

  @Test
  fun testUpdateTemplateVariableWithParameterValues() {
    mockMvc
        .perform(
            post("/mustache")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    { "template": "template {{foo}}, {{&bar}}"
                    , "parameters": {"foo": "foo", "bar": "bar" }
                    }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", `is`("template foo, bar")))
        .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")))
  }

  @Test
  fun testUpdateTemplateBadTemplate() {
    mockMvc
        .perform(
            post("/mustache")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{ "template": "template {{foo" }"""))
        .andExpect(status().isBadRequest())
  }
}
