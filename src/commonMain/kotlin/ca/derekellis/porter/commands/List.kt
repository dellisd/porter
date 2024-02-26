package ca.derekellis.porter.commands

import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import ca.derekellis.porter.repository.Repository
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.ui.Column
import okio.FileSystem
import okio.Path.Companion.toPath

class List : StandardPorterCommand() {
  private val manifestReader = ManifestReader()
  private val fileSystem = FileSystem.SYSTEM
  private val repository by lazy { Repository(destination.toPath(), fileSystem) }

  override suspend fun MosaicScope.mosaicRun() {
    val manifest = manifestReader.read(manifestPath.toPath())

    val dataPath = destination.toPath()
    if (!fileSystem.exists(dataPath)) {
      fileSystem.createDirectories(dataPath)
    }

    setContent {
      Column {
        for (asset in manifest.assets) {
          Asset(asset, synced = repository.isSynced(asset))
        }
      }
    }
  }
}