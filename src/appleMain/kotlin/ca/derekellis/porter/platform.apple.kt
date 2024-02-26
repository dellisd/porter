package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import okio.Path

actual inline fun platformEngine(): HttpClientEngineFactory<*> = CIO

actual fun platformTempDir(): Path {
  TODO("Not yet implemented")
}
