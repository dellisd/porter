package ca.derekellis.porter.manifest

import kotlinx.serialization.Serializable

@Serializable
data class Manifest(val assets: List<Asset>)
