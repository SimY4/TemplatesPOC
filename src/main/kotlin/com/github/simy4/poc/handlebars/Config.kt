package com.github.simy4.poc.handlebars

import com.github.jknack.handlebars.Handlebars
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("handlebarsConfig")
open class Config {
  @Bean open fun handlebars(): Handlebars = Handlebars()
}
