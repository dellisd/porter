package ca.derekellis.porter.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.ByteReadChannel
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

class Downloader(
  private val httpClient: HttpClient,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
  /**
   * Download a file at [url] to the [destination].
   * @param progress Callback that reports download progress in the range of `0..100`.
   */
  suspend fun get(url: Url, destination: Path, progress: (Int) -> Unit) {
    val response = httpClient.get(url) {
      onDownload { bytesSentTotal, contentLength ->
        val percent = ((bytesSentTotal.toDouble() / contentLength) * 100).toInt()
        progress(percent)
      }
    }
    val incomingBytes = response.bodyAsChannel()
    fileSystem.copy(incomingBytes, destination)
  }

  private suspend fun FileSystem.copy(source: ByteReadChannel, target: Path) {
    val bufferSize = 1024
    val byteBuffer = ByteArray(bufferSize)

    sink(target).buffer().use { sink ->
      var offset = 0
      do {
        val read = source.readAvailable(byteBuffer, offset, bufferSize)
        sink.write(byteBuffer)
        offset += read
      } while (read > 0)
    }
  }
}