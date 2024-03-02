package ca.derekellis.porter.repository

import ca.derekellis.porter.manifest.Asset
import io.ktor.http.Url
import okio.FileSystem
import okio.Path


public class Repository(
  private val root: Path,
  private val downloader: Downloader,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
  private val listener: RepositoryListener = RepositoryListener.NONE,
) {

  /**
   * Sync a single asset.
   */
  public suspend fun sync(asset: Asset) {
    if (isSynced(asset)) return
    listener.downloadStart(asset)

    val destination = root / asset.name
    try {
      downloader.get(Url(asset.url), destination) {
        listener.downloadProgress(asset, it)
      }
    } catch (e: Exception) {
      listener.downloadFail(asset, e)
      return
    }
    listener.downloadSuccess(asset)
  }

  public fun isSynced(asset: Asset): Boolean {
    return fileSystem.exists(root / asset.name)
  }

  public fun remove(asset: Asset) {
    fileSystem.delete(root / asset.name)
  }
}
