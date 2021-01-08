package app.web.diegoflassa_site.littledropsofrain.buildsrc

@Suppress("Unused")
object Versions {
    const val ktlint = "0.39.0"

    // DO NOT UPDATE or will get build errors
    const val kotlinCompilerVersion = "1.4.21"
    const val coil = "1.0.0"
    const val volley = "1.1.1"
    const val ucrop = "2.2.6"
    const val oss = "17.0.0"
    const val oauth2 = "0.22.1"
    const val gson = "2.8.6"
    const val location = "17.1.0"
    const val leakcanary = "2.4"
    const val play_services_auth = "19.0.0"
    const val material = "1.2.1"
    const val play_services_location = "17.1.0"
    const val google_services = "4.3.4"
    const val activity_ktx = "1.2.0-rc01"
    const val annotation = "1.1.0"
    const val appcompat = "1.3.0-alpha2"
    const val browser = "1.3.0"
    const val constraintlayout = "2.0.4"
    const val core_ktx = "1.3.2"
    const val fragment_ktx = "1.3.0-rc01"
    const val legacy_support_v4 = "1.0.0"
    const val legacy_support_core_utils = "1.0.0"
    const val lifecycle = "2.3.0-rc01"
    const val navigation = "2.3.2"
    const val preference_ktx = "1.1.1"
    const val recyclerview = "1.1.0"
    const val recyclerview_selection = "1.1.0-rc03"
    const val room = "2.2.6"
    const val vectordrawable_animated = "1.1.0"
    const val databinding = "4.1.1"
    const val core = "1.3.1-alpha02"
    const val expresso = "3.3.0"
    const val junit_ktx = "1.1.3-alpha02"
    const val rules = "1.3.1-alpha02"
    const val runner = "1.3.1-alpha02"
    const val gradle="7.0.0-alpha03"
    const val gradle_plugin="1.4.21"
    const val safeargs_plugin="2.3.1"
    const val crashlytics = "2.4.1"
    const val perf = "1.3.4"
	const val appdistribution = "2.0.1"
    const val oss_plugin="0.10.2"
    const val spotless="5.8.2"
    const val junit = "4.13.1"
    const val auth = "6.3.0"
    const val database = "4.0.0"
    const val storage = "6.2.1"
    const val workVersion = "2.4.0"
    const val firebaseBOM = "26.1.0"
    const val iconify = "2.2.2"
    const val androidxJetpackCompose = "1.0.0-alpha09"
}

@Suppress("Unused")
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
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinCompilerVersion}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinCompilerVersion}"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlinCompilerVersion}"
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
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val palette = "androidx.palette:palette:1.0.0"

        const val core = "androidx.core:core:1.5.0-alpha02"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha02"

        object Compose {
            private const val snapshot = ""//"6973398"

            @get:JvmStatic
            val snapshotUrl: String
                get() = "https://androidx.dev/snapshots/builds/$snapshot/artifacts/ui/repository/"

            const val runtime = "androidx.compose.runtime:runtime:${Versions.androidxJetpackCompose}"
            const val foundation = "androidx.compose.foundation:foundation:${Versions.androidxJetpackCompose}"
            const val layout = "androidx.compose.foundation:foundation-layout:${Versions.androidxJetpackCompose}"

            const val ui = "androidx.compose.ui:ui:${Versions.androidxJetpackCompose}"
            const val material = "androidx.compose.material:material:${Versions.androidxJetpackCompose}"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:${Versions.androidxJetpackCompose}"

            const val tooling = "androidx.compose.ui:ui-tooling:${Versions.androidxJetpackCompose}"
            const val test = "androidx.compose.ui:ui-test:${Versions.androidxJetpackCompose}"
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
        const val rome = "com.rometools:rome:$version"
        const val modules = "com.rometools:rome-modules:$version"
    }
}
