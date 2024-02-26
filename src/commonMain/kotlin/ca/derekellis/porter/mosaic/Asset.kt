package ca.derekellis.porter.mosaic

import androidx.compose.runtime.Composable
import ca.derekellis.porter.manifest.Asset
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Text

@Composable
fun Asset(asset: Asset, synced: Boolean = true) {
  val displayName = if (synced) asset.name else "${asset.name} (Not Synced)"
  Text(
    value = displayName,
    color = if (synced) Color.Green else Color.Yellow,
  )
}
