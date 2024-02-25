package ca.derekellis.porter

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.winhttp.WinHttp

actual inline fun platformEngine(): HttpClientEngineFactory<*> = WinHttp
