package ca.derekellis.porter.repository

import ca.derekellis.porter.manifest.Asset
import okio.FileSystem
import okio.Path

class Repository(
  private val root: Path,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {

  /**
   * Sync a single asset.
   */
  suspend fun sync(asset: Asset) {

  }

  fun isSynced(asset: Asset): Boolean {
    return fileSystem.exists(root / asset.name)
  }
}
