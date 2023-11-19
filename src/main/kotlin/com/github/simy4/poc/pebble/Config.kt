package com.github.simy4.poc.pebble

import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.Loader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("pebbleConfig")
open class Config {
  @Bean open fun memoryLoader(): MemoryLoader = MemoryLoader()

  @Bean
  open fun pebbleEngine(loader: Loader<String>): PebbleEngine =
      PebbleEngine.Builder().cacheActive(false).loader(loader).build()
}
