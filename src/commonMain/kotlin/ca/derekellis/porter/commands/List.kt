package ca.derekellis.porter.commands

import ca.derekellis.porter.manifest.ManifestReader
import ca.derekellis.porter.mosaic.Asset
import ca.derekellis.porter.repository.Repository
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Column
import okio.FileSystem
import okio.Path.Companion.toPath

class List : CliktCommand() {
  private val manifestPath by argument(name = "manifest").default("porter.yaml")
  private val destination by argument(name = "dest").default("data/")

  private val manifestReader = ManifestReader()
  private val fileSystem = FileSystem.SYSTEM
  private val repository by lazy { Repository(destination.toPath(), fileSystem) }

  override fun run() {
    val manifest = manifestReader.read(manifestPath.toPath())

    val dataPath = destination.toPath()
    if (!fileSystem.exists(dataPath)) {
      fileSystem.createDirectories(dataPath)
    }

    runMosaicBlocking {
      setContent {
        Column {
          for (asset in manifest.assets) {
            Asset(asset, synced = repository.isSynced(asset))
          }
        }
      }
    }
  }
}