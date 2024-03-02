package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.curl.Curl
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv

actual inline fun platformEngine(): HttpClientEngineFactory<*> = Curl

@OptIn(ExperimentalForeignApi::class)
actual fun platformTempDir(): Path {
  val envPath = getenv("TMPDIR")?.toKString()
  return envPath?.toPath() ?: "/tmp".toPath()
}
