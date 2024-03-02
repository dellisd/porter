package ca.derekellis.porter.mosaic

import androidx.compose.runtime.Composable
import ca.derekellis.porter.manifest.Asset
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Text

@Composable
fun Asset(asset: Asset, synced: Boolean = true) {
  if (synced) {
    Text(value = "✓ ${asset.name}", color = Color.Green)
  } else {
    Text(value = "• ${asset.name} (Not Synced)", color = Color.Yellow)
  }
}
