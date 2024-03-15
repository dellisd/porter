package ca.derekellis.porter.repository

sealed interface AssetState {
  data object Success : AssetState
  data object NotSynced : AssetState
  data class Failed(val exception: Exception) : AssetState
}
