package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.curl.Curl
import okio.Path

actual inline fun platformEngine(): HttpClientEngineFactory<*> = Curl

actual fun platformTempDir(): Path {
  TODO("Not yet implemented")
}
