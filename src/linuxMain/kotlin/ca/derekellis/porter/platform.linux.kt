package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

actual inline fun platformEngine(): HttpClientEngineFactory<*> = CIO

@OptIn(ExperimentalForeignApi::class)
actual fun platformTempDir(): Path {
  val envPath = getenv("TMPDIR")?.toKString()
  return envPath?.toPath() ?: "/tmp".toPath()
}
