package ca.derekellis.porter.repository

import ca.derekellis.porter.platformTempDir
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.utils.DEFAULT_HTTP_BUFFER_SIZE
import io.ktor.http.Url
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

class Downloader(
  private val httpClient: HttpClient,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
  private val tempPath = platformTempDir() / "porter-temp"

  init {
    if (!fileSystem.exists(tempPath)) {
      fileSystem.createDirectories(tempPath)
    }
  }

  /**
   * Download a file at [url] to the [destination].
   * @param progress Callback that reports download progress in the range of `0..100`.
   */
  suspend fun get(url: Url, destination: Path, progress: (Int) -> Unit) {
    val request = httpClient.prepareGet(url) {
      onDownload { bytesSentTotal, contentLength ->
        if (contentLength < 0) {
          progress(0)
        } else {
          val percent = ((bytesSentTotal.toDouble() / contentLength) * 100).toInt()
          progress(percent)
        }
      }
    }


    val tempDestination = tempPath / destination.nameBytes.sha256().hex()
    request.execute { httpResponse ->
      val channel = httpResponse.bodyAsChannel()
      fileSystem.copy(channel, tempDestination)
    }

    ensureDirectoriesCreated(destination)
    fileSystem.copy(tempDestination, destination)
  }

  private fun ensureDirectoriesCreated(target: Path) {
    target.parent?.let { parent ->
      if (!fileSystem.exists(parent)) {
        fileSystem.createDirectories(parent)
      }
    }
  }

  fun cleanupTempFiles() {
    fileSystem.deleteRecursively(tempPath)
  }

  private suspend fun FileSystem.copy(source: ByteReadChannel, target: Path) {
    sink(target).buffer().use { sink ->
      while (!source.isClosedForRead) {
        val packet = source.readRemaining(DEFAULT_HTTP_BUFFER_SIZE.toLong())
        while (!packet.isEmpty) {
          val bytes = packet.readBytes()
          sink.write(bytes)
        }
      }
    }
  }
}