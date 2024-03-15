package ca.derekellis.porter.commands

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import ca.derekellis.porter.repository.RepositoryListener
import ca.derekellis.porter.manifest.Asset
import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import ca.derekellis.porter.mosaic.DownloadingAsset
import ca.derekellis.porter.platformEngine
import ca.derekellis.porter.repository.AssetState
import ca.derekellis.porter.repository.Downloader
import ca.derekellis.porter.repository.Repository
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.ui.Column
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okio.FileSystem

@Stable
class Sync : StandardPorterCommand(FileSystem.SYSTEM) {
  private val manifestReader = ManifestReader()
  private val downloader = Downloader(HttpClient(platformEngine()))

  override val repository: Repository by lazy {
    Repository(
      root = destination,
      downloader = downloader,
      fileSystem = fileSystem,
      listener = Listener()
    )
  }

  private val completed = mutableStateListOf<CompletedAsset>()
  private val inProgress = mutableStateMapOf<Asset, Int>()

  override suspend fun MosaicScope.mosaicRun() {
    val manifest = manifestReader.read(manifestPath)

    setContent {
      Column {
        for ((asset, state) in completed) {
          Asset(asset, state)
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
          repository.sync(asset)
        }
        downloader.cleanupTempFiles()
      }.join()
    }
  }

  private inner class Listener : RepositoryListener() {
    override fun downloadStart(asset: Asset) {
      inProgress[asset] = 0
    }

    override fun downloadProgress(asset: Asset, progress: Int) {
      inProgress[asset] = progress
    }

    override fun downloadSuccess(asset: Asset) {
      inProgress.remove(asset)
      completed.add(CompletedAsset(asset, AssetState.Success))
    }

    override fun downloadSkipped(asset: Asset) {
      completed.add(CompletedAsset(asset, AssetState.Success))
    }

    override fun downloadFail(asset: Asset, exception: Exception) {
      inProgress.remove(asset)
      completed.add(CompletedAsset(asset, AssetState.Failed(exception)))
    }
  }

  private data class CompletedAsset(val asset: Asset, val state: AssetState)
}
