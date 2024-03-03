package ca.derekellis.porter

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.ProcessedArgument
import com.github.ajalt.clikt.parameters.arguments.RawArgument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.RawOption
import com.github.ajalt.clikt.parameters.options.convert
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

private fun pathType(context: Context, fileOkay: Boolean, folderOkay: Boolean): String = when {
  fileOkay && !folderOkay -> context.localization.pathTypeFile()
  !fileOkay && folderOkay -> context.localization.pathTypeDirectory()
  else -> context.localization.pathTypeOther()
}

private fun convertToPath(
  path: String,
  mustExist: Boolean,
  canBeFile: Boolean,
  canBeFolder: Boolean,
  canBeSymlink: Boolean,
  fileSystem: FileSystem,
  context: Context,
  fail: (String) -> Unit,
): Path {
  val name = pathType(context, canBeFile, canBeFolder)
  return with(context.localization) {
    path.toPath().also {
      if (mustExist && !fileSystem.exists(it)) fail(pathDoesNotExist(name, it.toString()))

      val metadata = fileSystem.metadataOrNull(it)
      if (!canBeFile && metadata?.isRegularFile == true) fail(pathIsFile(name, it.toString()))
      if (!canBeFolder && metadata?.isDirectory == true) fail(pathIsDirectory(name, it.toString()))
      if (!canBeSymlink && metadata?.symlinkTarget != null) fail(pathIsSymlink(name, it.toString()))
    }
  }
}

fun RawArgument.path(
  mustExist: Boolean = false,
  canBeFile: Boolean = true,
  canBeDir: Boolean = true,
  canBeSymlink: Boolean = true,
  fileSystem: FileSystem = FileSystem.SYSTEM,
): ProcessedArgument<Path, Path> {
  return convert(completionCandidates = CompletionCandidates.Path) { str ->
    convertToPath(
      path = str,
      mustExist = mustExist,
      canBeFile = canBeFile,
      canBeFolder = canBeDir,
      canBeSymlink = canBeSymlink,
      fileSystem = fileSystem,
      context = context
    ) { fail(it) }
  }
}

fun RawOption.path(
  mustExist: Boolean = false,
  canBeFile: Boolean = true,
  canBeDir: Boolean = true,
  canBeSymlink: Boolean = true,
  fileSystem: FileSystem = FileSystem.SYSTEM,
): NullableOption<Path, Path> {
  return convert({ localization.pathMetavar() }, CompletionCandidates.Path) { str ->
    convertToPath(
      path = str,
      mustExist = mustExist,
      canBeFile = canBeFile,
      canBeFolder = canBeDir,
      canBeSymlink = canBeSymlink,
      fileSystem = fileSystem,
      context = context
    ) { fail(it) }
  }
}
