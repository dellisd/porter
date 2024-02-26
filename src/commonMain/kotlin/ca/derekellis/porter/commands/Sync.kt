package ca.derekellis.porter.commands

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import ca.derekellis.porter.manifest.Asset
import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import ca.derekellis.porter.mosaic.DownloadingAsset
import ca.derekellis.porter.platformEngine
import ca.derekellis.porter.repository.Downloader
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath

@Stable
class Sync : CliktCommand() {
  private val manifestPath by argument(name = "manifest").default("porter.yaml")
  private val destination by argument(name = "dest").default("data/")

  private val manifestReader = ManifestReader()
  private val downloader = Downloader(HttpClient(platformEngine()))
  private val scope = CoroutineScope(SupervisorJob())
  private val fileSystem = FileSystem.SYSTEM

  override fun run() {
    val manifest = manifestReader.read(manifestPath.toPath())

    val dataPath = destination.toPath()
    if (!fileSystem.exists(dataPath)) {
      fileSystem.createDirectories(dataPath)
    }

    runMosaicBlocking {
      val completed = mutableStateListOf<Asset>()
      val inProgress = mutableStateMapOf<Asset, Int>()

      setContent {
        Column {
          for (asset in completed) {
            Asset(asset)
          }
          for ((asset, progress) in inProgress) {
            DownloadingAsset(asset, progress)
          }
        }
      }

      // TODO: Download files in parallel, and check if download is needed
      launch(Dispatchers.IO) {
        for (asset in manifest.assets) {
          inProgress[asset] = 0
          downloader.get(Url(asset.url), dataPath.resolve(asset.name)) {
            inProgress[asset] = it
          }
          inProgress.remove(asset)
          completed.add(asset)
        }
      }.join()
    }
  }


}
