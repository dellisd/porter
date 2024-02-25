package ca.derekellis.porter.manifest

import kotlinx.serialization.Serializable

@Serializable
data class Asset(val name: String, val url: String)
