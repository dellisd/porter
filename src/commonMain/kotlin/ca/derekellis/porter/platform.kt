package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import okio.Path

expect inline fun platformEngine(): HttpClientEngineFactory<*>

expect fun platformTempDir(): Path
