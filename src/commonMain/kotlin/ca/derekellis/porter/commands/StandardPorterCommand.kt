package ca.derekellis.porter.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.jakewharton.mosaic.MosaicScope
import com.jakewharton.mosaic.runMosaicBlocking

abstract class StandardPorterCommand : CliktCommand() {
  protected val manifestPath by argument(name = "manifest").default("porter.yaml")
  protected val destination by argument(name = "dest").default("data/")

  final override fun run() = runMosaicBlocking {
    mosaicRun()
  }

  protected abstract suspend fun MosaicScope.mosaicRun()
}