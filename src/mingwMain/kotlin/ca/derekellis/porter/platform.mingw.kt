package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.winhttp.WinHttp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.windows.GetTempPathW
import platform.windows.WCHARVar

actual inline fun platformEngine(): HttpClientEngineFactory<*> = WinHttp

@OptIn(ExperimentalForeignApi::class)
actual fun platformTempDir(): Path {
  val bufferLength = PATH_MAX + 1
  val kString = memScoped {
    val buffer = allocArray<WCHARVar>(bufferLength)

    GetTempPathW(bufferLength.toUInt(), buffer)
    buffer.toKString()
  }

  return kString.toPath()
}
