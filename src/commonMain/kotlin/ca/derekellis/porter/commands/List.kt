package ca.derekellis.porter.commands

import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.ui.Column
import okio.FileSystem

class List : StandardPorterCommand(FileSystem.SYSTEM) {
  private val manifestReader = ManifestReader()

  override suspend fun MosaicScope.mosaicRun() {
    val manifest = manifestReader.read(manifestPath)

    val dataPath = destination
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