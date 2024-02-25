package ca.derekellis.porter.manifest

import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

class ManifestReader(
  private val yaml: Yaml = Yaml,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
) {
  fun read(path: Path): Manifest {
    val content = fileSystem.source(path).buffer().use {  it.readUtf8() }
    return yaml.decodeFromString<Manifest>(content)
  }
}
