package com.github.simy4.poc.pebble

import io.pebbletemplates.pebble.loader.Loader
import java.io.Reader
import java.io.StringReader
import java.util.concurrent.atomic.AtomicReference

class MemoryLoader : AtomicReference<String>(""), Loader<String> {
  override fun getReader(cacheKey: String?): Reader = StringReader(get())

  override fun setCharset(charset: String?) {}

  override fun setPrefix(prefix: String?) {}

  override fun setSuffix(suffix: String?) {}

  override fun resolveRelativePath(relativePath: String, anchorPath: String?) = relativePath

  override fun createCacheKey(templateName: String) = templateName

  override fun resourceExists(templateName: String?) = true
}
