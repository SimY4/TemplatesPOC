package com.github.simy4.poc.velocity

import org.apache.velocity.app.Velocity
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.tools.ToolManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("velocityConfig")
open class Config {
  @Bean(initMethod = "init")
  open fun velocityEngine(): VelocityEngine =
      VelocityEngine().also {
        it.setProperty(Velocity.RUNTIME_LOG_NAME, "com.github.simy4.poc.velocity")
        it.setProperty(Velocity.RESOURCE_LOADERS, "string")
        it.setProperty(
            Velocity.RESOURCE_LOADER + ".string.description", "Velocity StringResource loader")
        it.setProperty(
            Velocity.RESOURCE_LOADER + ".string.class",
            "org.apache.velocity.runtime.resource.loader.StringResourceLoader")
        it.setProperty(
            Velocity.RESOURCE_LOADER + ".string.repository.class",
            "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl")
      }

  @Bean open fun toolManager(): ToolManager = ToolManager(true)
}
