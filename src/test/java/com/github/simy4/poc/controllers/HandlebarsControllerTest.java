package com.github.simy4.poc.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class HandlebarsControllerTest {

  @Autowired private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void testGetTemplateNoTemplate() throws Exception {
    mockMvc
        .perform(get("/handlebars"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testUpdateTemplateDollarVariable() throws Exception {
    mockMvc
        .perform(
            post("/handlebars")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "template": "template {{foo}}, {{&bar}}" }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", is("template , ")))
        .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")));
  }

  @Test
  void testUpdateTemplateDollarVariableWithParameterValues() throws Exception {
    mockMvc
        .perform(
            post("/handlebars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    { "template": "template {{foo}}, {{&bar}}"
                    , "parameters": {"foo": "foo", "bar": "bar" }
                    }"""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.template", is("template foo, bar")))
        .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")));
  }

  @Test
  void testUpdateTemplateBadTemplate() throws Exception {
    mockMvc
        .perform(
            post("/handlebars")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "template": "template {{foo" }"""))
        .andExpect(status().isBadRequest());
  }
}
