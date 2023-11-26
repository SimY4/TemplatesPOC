package com.github.simy4.poc.mustache

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("mustacheConfig")
open class Config {
  @Bean open fun mustache(): MustacheFactory = DefaultMustacheFactory()
}
