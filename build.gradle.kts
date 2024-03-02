import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
      implementation(libs.ktor.client)
      implementation(libs.mosaic)
      implementation(libs.okio)
      implementation(libs.serialization)
      implementation(libs.yamlkt)
    }

    commonTest.dependencies {
      implementation(libs.assertk)
      implementation(libs.coroutines.test)
      implementation(libs.ktor.client.mock)
      implementation(libs.okio.fakeFilesystem)
      implementation(libs.turbine)
      implementation(kotlin("test"))
    }

    appleMain.dependencies {
      implementation(libs.ktor.client.cio)
    }

    linuxMain.dependencies {
      implementation(libs.ktor.client.cio)
    }

    mingwMain.dependencies {
      implementation(libs.ktor.client.winhttp)
    }
  }

  targets.withType<KotlinNativeTarget>().configureEach {
    binaries.executable {
      entryPoint("ca.derekellis.porter.main")
    }
  }
}

kotlin {
  jvmToolchain(17)
}
