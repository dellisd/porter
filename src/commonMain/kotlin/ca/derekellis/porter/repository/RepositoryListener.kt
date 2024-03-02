package ca.derekellis.porter.repository

import ca.derekellis.porter.manifest.Asset

open class RepositoryListener {
  open fun downloadStart(asset: Asset) {
  }

  open fun downloadProgress(asset: Asset, progress: Int) {
  }

  open fun downloadFail(asset: Asset, exception: Exception) {
  }

  open fun downloadSuccess(asset: Asset) {
  }

  open fun downloadSkipped(asset: Asset) {
  }

  companion object {
    val NONE = RepositoryListener()
  }
}
