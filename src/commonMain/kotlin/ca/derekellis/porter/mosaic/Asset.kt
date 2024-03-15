package ca.derekellis.porter.mosaic

import androidx.compose.runtime.Composable
import ca.derekellis.porter.manifest.Asset
import ca.derekellis.porter.repository.AssetState
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

@Composable
fun Asset(asset: Asset, state: AssetState) {
  Column {
    when (state) {
      AssetState.Success -> Text(value = "✓ ${asset.name}", color = Color.Green)
      AssetState.NotSynced -> Text(value = "• ${asset.name} (Not Synced)", color = Color.Yellow)
      is AssetState.Failed -> {
        Text(value = "✗ ${asset.name} (Failed)", color = Color.Red)
        Text(value = "  ${state.exception.message}", color = Color.Red)
      }
    }
  }
}
