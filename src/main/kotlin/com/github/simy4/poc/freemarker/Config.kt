package com.github.simy4.poc.freemarker

import freemarker.cache.StringTemplateLoader
import freemarker.template.TemplateExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("freemarkerConfig")
open class Config {
  @Bean open fun templateLoader(): StringTemplateLoader = StringTemplateLoader()

  @Bean
  open fun freeMarkerConfiguration(
      templateLoader: StringTemplateLoader
  ): freemarker.template.Configuration =
      freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32).also {
        it.defaultEncoding = "UTF-8"
        it.templateUpdateDelayMilliseconds = 0L
        it.templateLoader = templateLoader
        it.templateExceptionHandler = TemplateExceptionHandler.IGNORE_HANDLER
      }
}
