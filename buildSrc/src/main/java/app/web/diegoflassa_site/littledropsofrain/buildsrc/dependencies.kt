package app.web.diegoflassa_site.littledropsofrain.buildsrc

@Suppress("Unused")
object Versions {
    const val ktlint = "0.39.0"
	// DO NOT UPDATE or will get build errors
    const val kotlinCompilerVersion = "1.4.10"
    const val spotless = "5.8.2"
    const val workVersion = "2.4.0"
    const val firebaseBOM = "26.1.0"
    const val iconify = "2.2.2"
    const val androidxJetpackCompose = "1.0.0-alpha07"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-alpha15"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.0.9"

    const val junit = "junit:junit:4.13"

    const val material = "com.google.android.material:material:1.1.0"

    object Accompanist {
        private const val version = "0.3.1"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
    }

    object Kotlin {
        private const val version = "1.4.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.3.9"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object OkHttp {
        private const val version = "4.7.2"
        const val okhttp = "com.squareup.okhttp3:okhttp:$version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0-rc01"
        const val palette = "androidx.palette:palette:1.0.0"

        const val core = "androidx.core:core:1.5.0-alpha02"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha02"

        object Compose {
            private const val snapshot = ""//"6973398"
            const val version = "1.0.0-alpha07"

            @get:JvmStatic
            val snapshotUrl: String
                get() = "https://androidx.dev/snapshots/builds/$snapshot/artifacts/ui/repository/"

            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val foundation = "androidx.compose.foundation:foundation:${version}"
            const val layout = "androidx.compose.foundation:foundation-layout:${version}"

            const val ui = "androidx.compose.ui:ui:${version}"
            const val material = "androidx.compose.material:material:${version}"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:${version}"

            const val tooling = "androidx.ui:ui-tooling:${version}"
            const val test = "androidx.ui:ui-test:${version}"
        }

        object Test {
            private const val version = "1.2.0"
            const val core = "androidx.test:core:$version"
            const val rules = "androidx.test:rules:$version"

            object Ext {
                private const val version = "1.1.2-rc01"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        object Room {
            private const val version = "2.2.5"
            const val runtime = "androidx.room:room-runtime:${version}"
            const val ktx = "androidx.room:room-ktx:${version}"
            const val compiler = "androidx.room:room-compiler:${version}"
        }

        object Lifecycle {
            private const val version = "2.2.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }

    object Rome {
        private const val version = "1.14.1"
        val rome = "com.rometools:rome:$version"
        val modules = "com.rometools:rome-modules:$version"
    }
}
