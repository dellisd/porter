package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual inline fun platformEngine(): HttpClientEngineFactory<*> = CIO
