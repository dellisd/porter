package ca.derekellis.porter.commands

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import ca.derekellis.porter.manifest.Asset
import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import ca.derekellis.porter.mosaic.DownloadingAsset
import ca.derekellis.porter.platformEngine
import ca.derekellis.porter.repository.Downloader
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.ui.Column
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path.Companion.toPath

@Stable
class Sync : StandardPorterCommand() {
  private val manifestReader = ManifestReader()
  private val downloader = Downloader(HttpClient(platformEngine()))
  private val scope = CoroutineScope(SupervisorJob())
  private val fileSystem = FileSystem.SYSTEM

  override suspend fun MosaicScope.mosaicRun() {
    val manifest = manifestReader.read(manifestPath.toPath())

    val dataPath = destination.toPath()
    if (!fileSystem.exists(dataPath)) {
      fileSystem.createDirectories(dataPath)
    }

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
    coroutineScope {
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
