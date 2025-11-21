package com.github.simy4.poc

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTest {
  protected lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setUp(@Autowired webApplicationContext: WebApplicationContext) {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
  }
}
