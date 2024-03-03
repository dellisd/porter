package ca.derekellis.porter.commands

import ca.derekellis.porter.path
import ca.derekellis.porter.platformEngine
import ca.derekellis.porter.repository.Downloader
import ca.derekellis.porter.repository.Repository
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.runMosaicBlocking
import io.ktor.client.HttpClient
import okio.FileSystem
import okio.Path.Companion.toPath

abstract class StandardPorterCommand(protected val fileSystem: FileSystem) : CliktCommand() {
  protected val manifestPath by argument(name = "manifest")
    .path(mustExist = true, canBeFile = false, fileSystem = fileSystem)
    .default("porter.yaml".toPath())

  protected val destination by argument(name = "dest")
    .path(canBeFile = false, fileSystem = fileSystem)
    .default("data/".toPath())

  protected open val repository: Repository by lazy {
    Repository(
      root = destination,
      downloader = Downloader(HttpClient(platformEngine())),
      fileSystem = fileSystem,
    )
  }

  final override fun run() {
    if (!fileSystem.exists(destination)) {
      fileSystem.createDirectories(destination)
    }

    runMosaicBlocking {
      mosaicRun()
    }
  }

  protected abstract suspend fun MosaicScope.mosaicRun()
}