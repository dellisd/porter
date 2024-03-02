package ca.derekellis.porter.commands

import ca.derekellis.porter.manifest.ManifestReader
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.ui.Text
import okio.FileSystem
import okio.Path.Companion.toPath

class Clean : StandardPorterCommand(FileSystem.SYSTEM) {
  private val manifestReader = ManifestReader()

  override suspend fun MosaicScope.mosaicRun() {
    val manifest = manifestReader.read(manifestPath.toPath())

    for (asset in manifest.assets) {
      repository.remove(asset)
    }

    setContent {
      Text(value = "Removed synced files in $destination")
    }
  }
}
