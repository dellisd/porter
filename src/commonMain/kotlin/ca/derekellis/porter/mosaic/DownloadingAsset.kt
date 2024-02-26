package ca.derekellis.porter.mosaic

import androidx.compose.runtime.Composable
import ca.derekellis.porter.manifest.Asset
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

@Composable
fun DownloadingAsset(asset: Asset, progress: Int) {
  Column {
    Text(value = "${asset.name} ($progress/100)%")
    Text(value = "  ${asset.url}")
  }
}
