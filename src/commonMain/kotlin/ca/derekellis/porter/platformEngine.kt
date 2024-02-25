package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory

expect inline fun platformEngine(): HttpClientEngineFactory<*>
