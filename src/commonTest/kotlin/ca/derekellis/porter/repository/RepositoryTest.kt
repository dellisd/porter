package ca.derekellis.porter.repository

import app.cash.turbine.Turbine
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import ca.derekellis.porter.manifest.Asset
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.Test

class RepositoryTest {
  private val engine = MockEngine { _ ->
    respondOk(content = "Hello World!")
  }

  private val fileSystem = FakeFileSystem()
  private val client = HttpClient(engine)
  private val downloader = Downloader(client, fileSystem = fileSystem)
  private val listener = TestListener()
  private val repository = Repository(
    root = "/".toPath(),
    downloader = downloader,
    fileSystem = fileSystem,
    listener = listener,
  )

  @Test
  fun `basic sync downloads file`() = runTest {
    val asset = Asset("test.txt", "http://example.com")

    repository.sync(asset)

    assertThat(fileSystem.exists("/test.txt".toPath())).isTrue()

    assertThat(listener.events.awaitItem()).isEqualTo(TestListener.DownloadStart(asset))
    assertThat(listener.events.awaitItem()).isEqualTo(TestListener.DownloadProgress(asset, progress = 0))
    assertThat(listener.events.awaitItem()).isEqualTo(TestListener.DownloadSuccess(asset))
  }

  @Test
  fun `sync skips file already present`() = runTest {
    val asset = Asset("test.txt", "http://example.com")
    val path = "/test.txt".toPath()

    fileSystem.sink(path).buffer().use { buffer ->
      buffer.writeUtf8("Hello World!")
    }

    repository.sync(asset)
    assertThat(repository.isSynced(asset)).isTrue()
    listener.events.expectNoEvents()
  }

  @Test
  fun `asset removal completes`() = runTest {
    val asset = Asset("test.txt", "http://example.com")
    val path = "/test.txt".toPath()

    fileSystem.sink(path).buffer().use { buffer ->
      buffer.writeUtf8("Hello World!")
    }

    repository.remove(asset)
    assertThat(fileSystem.exists(path)).isFalse()
  }

  class TestListener : RepositoryListener() {
    val events = Turbine<Event>()

    data class DownloadStart(val asset: Asset) : Event

    override fun downloadStart(asset: Asset) {
      events.add(DownloadStart(asset))
    }

    data class DownloadProgress(val asset: Asset, val progress: Int) : Event

    override fun downloadProgress(asset: Asset, progress: Int) {
      events.add(DownloadProgress(asset, progress))
    }

    data class DownloadFail(val asset: Asset, val exception: Exception) : Event

    override fun downloadFail(asset: Asset, exception: Exception) {
      events.add(DownloadFail(asset, exception))
    }

    data class DownloadSuccess(val asset: Asset) : Event

    override fun downloadSuccess(asset: Asset) {
      events.add(DownloadSuccess(asset))
    }

    data class DownloadSkipped(val asset: Asset) : Event

    override fun downloadSkipped(asset: Asset) {
      events.add(DownloadSkipped(asset))
    }

    sealed interface Event
  }
}