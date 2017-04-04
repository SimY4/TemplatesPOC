package github.templates.poc.controllers;

import github.templates.poc.TemplatesPocApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TemplatesPocApplication.class, webEnvironment = RANDOM_PORT)
public class FreemarkerControllerTest {

    @Autowired private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetTemplateNoTemplate() throws Exception {
        mockMvc.perform(get("/freemarker"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testUpdateTemplateDollarVariable() throws Exception {
        mockMvc.perform(post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"__template__\":\"template ${foo}, ${bar!''}\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.template", is("template , ")))
                .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")));
    }

    @Test
    public void testUpdateTemplateDollarVariableWithParameterValues() throws Exception {
        mockMvc.perform(post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"__template__\":\"template ${foo}, ${bar!''}\", \"foo\":\"foo\", \"bar\":\"bar\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.template", is("template foo, bar")))
                .andExpect(jsonPath("$.parameters", hasItems("foo", "bar")));
    }

    @Test
    public void testUpdateTemplateBadTemplate() throws Exception {
        mockMvc.perform(post("/freemarker")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"__template__\":\"template ${olo\"}"))
                .andExpect(status().isBadRequest());
    }

}