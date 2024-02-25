package ca.derekellis.porter

import ca.derekellis.porter.commands.Sync
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class Porter : CliktCommand() {
  init {
    subcommands(Sync())
  }

  override fun run() {
  }
}
