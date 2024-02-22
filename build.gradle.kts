plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.mosaic)
}

group = "ca.derekellis"
version = "0.1.0-SNAPSHOT"

kotlin {
  linuxX64()
  mingwX64()
  macosX64()
  macosArm64()

  sourceSets {
    commonMain.dependencies {
      implementation(libs.clikt)
      implementation(libs.coroutines)
      implementation(libs.mosaic)
      implementation(libs.serialization)
      implementation(libs.yamlkt)
    }
  }
}

kotlin {
  jvmToolchain(17)
}
