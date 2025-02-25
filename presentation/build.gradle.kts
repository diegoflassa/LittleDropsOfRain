@file:Suppress("UnstableApiUsage")

import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions
import com.android.build.gradle.AppExtension
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "2.1.10-1.0.30"
    // Google Services plugin
    id("com.google.gms.google-services")
    // Apply the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.firebase-perf")
    // Apply the App Distribution Gradle plugin
    id("com.google.firebase.appdistribution")
    //id("dagger.hilt.android.plugin")
}
apply {
    plugin("kotlin-android")
}

// Creates a variable called keystorePropertiesFile, and initializes it to the
// keystore.properties file.
val keystorePropertiesFile = rootProject.file("keystore.properties")

// Initializes a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()

// Loads the keystore.properties file into the keystoreProperties object.
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    lint.abortOnError = false
    namespace = "app.web.diegoflassa_site.littledropsofrain"

    compileSdk = Config.COMPILE_SDK_VERSION
    //compileSdkPreview = Config.compileSdkPreviewVersion
    buildToolsVersion = Config.BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MINIMUM_SDK_VERSION
        targetSdk = Config.TARGET_SDK_VERSION
        //targetSdkPreview = Config.targetSdkPreviewVersion
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // DFL - Configuração para assinar o APK. Nao se preocupe agora
        create("release") {
            storeFile = file(keystoreProperties.getProperty("KEYSTORE_FILE"))
            storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = keystoreProperties.getProperty("KEYSTORE_ALIAS")
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        debug {
            //applicationIdSuffix '.debug'
            //ext.ennableCrashlytics = false
            //ext.alwaysUpdateBuildId = false
            firebaseAppDistribution {
                releaseNotesFile = "${project.rootDir}/releaseNotes/releaseNotes.txt"
                testers = "andrea.setecinco@gmail.com, diegoflassa@gmail.com"
            }
            resValue("string", "version_name", "${defaultConfig.versionName}")
            //versionNameSuffix = "-debug"
            // firebaseCrashlytics {
            //   mappingFileUploadEnabled = false
            //}
        }
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            firebaseAppDistribution {
                releaseNotesFile = "${project.rootDir}/releaseNotes/releaseNotes.txt"
                testers = "andrea.setecinco@gmail.com, diegoflassa@gmail.com"
            }
            resValue("string", "version_name", "${defaultConfig.versionName}")
            //versionNameSuffix = "-release"
            // firebaseCrashlytics {
            //     mappingFileUploadEnabled = true
            // }
        }
        /*
        val extension = project.extensions.getByType(BaseAppModuleExtension::class.java)
        extension.all { variant ->
            variant.outputs.all {
                val bvoi = this as BaseVariantOutputImpl
                val appName = "littledropsofrain"
                if (variant.name == "release") {
                    bvoi.outputFileName = "${appName}-${this.outputFile.name}-release"
                    println("release")
                } else if (variant.name == "debug") {
                    bvoi.outputFileName = "${appName}-${this.outputFile.name}-debug"
                    println("debug")
                }
            }
            return@buildTypes
        }
         */
    }
    compileOptions {
        // Sets Java compatibility to Java 11
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }
    testOptions {
        emulatorSnapshots {
            // Generates snapshots that are compressed into a single TAR file.
            compressSnapshots = true
        }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
        // freeCompilerArgs = freeCompilerArgs + "-Xallow-jvm-ir-dependencies"
    }
    buildFeatures {
        viewBinding = true
        // Enables Jetpack Compose for this module
        //compose = true
    }
    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/NOTICE.md")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.ANDROIDX_JETPACK_COMPOSE
    }
}

// Avoid build error
gradle.taskGraph.whenReady {
    tasks.forEach { task ->
        if (task.name.contains("compileDebugUnitTestKotlin") || task.name.contains("compileReleaseUnitTestKotlin")
        // ||
        ) {
            // task.name.contains("uploadCrashlyticsMappingFile")) {
            // Uncomment if error occurs
            task.enabled = false
        }
    }
}

//Avoid the uploadCrashlyticsMappingFile error
afterEvaluate {
    project.extensions.configure<AppExtension>("android") {
        applicationVariants.all { variant ->
            val uploadCrashlyticsTask =
                tasks.findByName(
                    "uploadCrashlyticsMappingFile${
                        variant.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }"
                )
            val packageTask = tasks.findByName(
                "package${
                    variant.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }"
            )
            val packageBundleTask = tasks.findByName(
                "package${
                    variant.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }Bundle"
            )
            if (uploadCrashlyticsTask != null && packageTask != null) {
                println(packageTask.name)
                uploadCrashlyticsTask.mustRunAfter(packageTask)
            }
            if (uploadCrashlyticsTask != null && packageBundleTask != null) {
                println(packageBundleTask.name)
                uploadCrashlyticsTask.mustRunAfter(packageBundleTask)
            }
            return@configure
        }
    }

    dependencies {
        implementation(project(mapOf("path" to ":data")))
        implementation(project(mapOf("path" to ":domain")))

        // Kotlin + coroutines
        implementation("androidx.work:work-runtime-ktx:${Versions.WORK_VERSION}")
        // optional - RxJava2 support
        implementation("androidx.work:work-rxjava2:${Versions.WORK_VERSION}")
        // optional - GCMNetworkManager support
        implementation("androidx.work:work-gcm:${Versions.WORK_VERSION}")

        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

        //SafetyNet
        implementation("com.google.android.play:integrity:${Versions.INTEGRITY}")

        // Volley
        implementation("com.android.volley:volley:${Versions.VOLLEY}")

        // Licenses
        implementation("com.google.android.gms:play-services-oss-licenses:${Versions.OSS}")

        // UCrop
        implementation("com.github.yalantis:ucrop:${Versions.UCROP}")

        // Coil COroutines Image Loader
        implementation("io.coil-kt:coil:${Versions.COIL}")

        // Preferences DataStore
        implementation("androidx.datastore:datastore-preferences:${Versions.DATA_STORE}")

        // Proto DataStore
        implementation("androidx.datastore:datastore-core:${Versions.DATA_STORE}")

        //Retrofix 2
        implementation("com.squareup.retrofit2:retrofit:${Versions.RETROFIT}")
        implementation("com.squareup.retrofit2:adapter-rxjava3:${Versions.RETROFIT_ADAPTER}")
        implementation("com.squareup.retrofit2:converter-jackson:${Versions.CONVERTER_JACKSON}")
        implementation("com.squareup.retrofit2:converter-gson:${Versions.CONVERTER}")

        // Jackson
        implementation("com.fasterxml.jackson.core:jackson-core:${Versions.JACKSON}")
        implementation("com.fasterxml.jackson.core:jackson-annotations:${Versions.JACKSON}")
        implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}")
        // Fix bug of XMLInputFactory not found
        implementation("javax.xml.stream:stax-api:${Versions.STAX_API}")

        //GSON
        implementation("com.google.code.gson:gson:${Versions.GSON}")

        // Dagger Core
        implementation("com.google.dagger:dagger:${Versions.DAGGER}")
        ksp("com.google.dagger:dagger-compiler:${Versions.DAGGER}")

        // Dagger Android
        api("com.google.dagger:dagger-android:${Versions.DAGGER}")
        //api("com.google.dagger:dagger-android-support:${Versions.DAGGER}")
        ksp("com.google.dagger:dagger-android-processor:${Versions.DAGGER}")

        //Hilt
        implementation("com.google.dagger:hilt-android:${Versions.HILT}")
        ksp("com.google.dagger:hilt-android-compiler:${Versions.HILT}")

        // RX Java 3
        implementation("io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA}")
        implementation("io.reactivex.rxjava3:rxandroid:${Versions.RX_ANDROID}")

        // Koin main features for Android (Scope,ViewModel ... )
        implementation("io.insert-koin:koin-core:${Versions.KOIN}")
        // Koin main features for Android (Scope,ViewModel ...)
        implementation("io.insert-koin:koin-android:${Versions.KOIN}")
        // Koin Android - experimental builder extensions
        implementation("io.insert-koin:koin-android-ext:${Versions.KOIN_EXT}")
        // Koin for Jetpack WorkManager
        implementation("io.insert-koin:koin-androidx-workmanager:${Versions.KOIN}")
        // Koin for Jetpack Compose (unstable version)
        implementation("io.insert-koin:koin-androidx-compose:${Versions.KOIN_COMPOSE}")


        // Jetpack Compose toolkit dependencies
        implementation("androidx.compose.ui:ui:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        // Tooling support (Previews, etc.)
        implementation("androidx.compose.ui:ui-tooling:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        // Foundation(Border, Background, Box, Image, Scroll, shapes, animations, etc.)
        //implementation("androidx.compose.foundation:foundation:${Versions.foundation}")
        // Material Design
        implementation("androidx.compose.material:material:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        // Material design icons
        implementation("androidx.compose.material:material-icons-core:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        implementation("androidx.compose.material:material-icons-extended:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        // Integration with observables
        implementation("androidx.compose.runtime:runtime-livedata:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        implementation("androidx.compose.runtime:runtime-rxjava2:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        // UI Tests
        androidTestImplementation("androidx.compose.ui:ui-test:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        debugImplementation("androidx.compose.ui:ui-tooling:${Versions.ANDROIDX_JETPACK_COMPOSE}")
        debugImplementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN_COMPILER_VERSION}")


        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"))
        // com.google.firebase
        implementation("com.google.firebase:firebase-iid:21.1.0")
        // Add the Firebase SDK for Google Analytics
        // For an optimal experience using FCM, add the Firebase SDK
        // for Google Analytics. This is recommended, but not required.
        // Recommended: Add the Firebase SDK for Google Analytics.
        // Add the Firebase Crashlytics SDK.
        // Declare the dependencies for the Remote Config and Analytics libraries
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-config-ktx")
        implementation("com.google.firebase:firebase-analytics-ktx")
        //implementation("com.google.firebase:firebase-appindexing")
        // Firebase Authentication(Kotlin)
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-crashlytics-ktx")
        implementation("com.google.firebase:firebase-database-ktx")
        // Add the SDK for Firebase Cloud Messaging
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-inappmessaging-ktx")
        implementation("com.google.firebase:firebase-inappmessaging-display-ktx")
        implementation("com.google.firebase:firebase-messaging-ktx")
        implementation("com.google.firebase:firebase-storage-ktx")
        implementation("com.google.firebase:firebase-dynamic-links-ktx")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-perf-ktx")
        // Firebase UI
        implementation("com.firebaseui:firebase-ui-auth:${Versions.AUTH}")
        implementation("com.firebaseui:firebase-ui-database:${Versions.DATABASE}")
        implementation("com.firebaseui:firebase-ui-storage:${Versions.STORAGE}")
        // OAuth Library
        implementation("com.google.auth:google-auth-library-oauth2-http:${Versions.OAUTH2}")

        // Leak Canary
        debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.LEAKCANARY}")

        // Google Sign In SDK (only required for Google Sign In)
        implementation("com.google.android.gms:play-services-auth:${Versions.PLAY_SERVICES_AUTH}")
        implementation("com.google.android.material:material:${Versions.MATERIAL}")
        implementation("com.google.android.gms:play-services-location:${Versions.PLAY_SERVICES_LOCATION}")
        implementation("com.google.gms:google-services:${Versions.GOOGLE_SERVICES}")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN_COMPILER_VERSION}")
        implementation("androidx.activity:activity-ktx:${Versions.ACTIVITY_KTX}")
        implementation("androidx.annotation:annotation:${Versions.ANNOTATION}")
        implementation("androidx.appcompat:appcompat:${Versions.APPCOMPAT}")
        implementation("androidx.browser:browser:${Versions.BROWSER}")
        implementation("androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINTLAYOUT}")
        implementation("androidx.core:core-ktx:${Versions.CORE_KTX}")
        implementation("androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}")
        implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.LIFECYCLE}")
        implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.LIFECYCLE}")
        implementation("androidx.lifecycle:lifecycle-common:${Versions.LIFECYCLE}")
        implementation("androidx.navigation:navigation-runtime-ktx:${Versions.NAVIGATION}")
        implementation("androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}")
        implementation("androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}")
        implementation("androidx.preference:preference-ktx:${Versions.PREFERENCE_KTX}")
        implementation("androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}")
        implementation("androidx.recyclerview:recyclerview-selection:${Versions.RECYCLERVIEW_SELECTION}")
        implementation("androidx.room:room-runtime:${Versions.ROOM}")
        implementation("androidx.room:room-ktx:${Versions.ROOM}")
        implementation("androidx.vectordrawable:vectordrawable-animated:${Versions.VECTORDRAWABLE_ANIMATED}")

        // Icons
        //implementation("com.joanzapata.iconify:android-iconify-fontawesome:${Versions.iconify}") // (v4.5)
        //implementation("com.joanzapata.iconify:android-iconify-entypo:${Versions.iconify}") // (v3,2015)
        implementation("com.joanzapata.iconify:android-iconify-typicons:${Versions.ICONIFY}")// (v2.0.7)
        {
            exclude(group = "com.android.support", module = "support-v4")
        }
        //implementation("com.joanzapata.iconify:android-iconify-material:${Versions.iconify}") // (v2.0.0)
        //implementation("com.joanzapata.iconify:android-iconify-material-community:${Versions.iconify}") // (v1.4.57)
        //implementation("com.joanzapata.iconify:android-iconify-meteocons:${Versions.iconify}") // (latest)
        //implementation("com.joanzapata.iconify:android-iconify-weathericons:${Versions.iconify}") // (v2.0)
        implementation("com.joanzapata.iconify:android-iconify-simplelineicons:${Versions.ICONIFY}") // (v1.0.0)
        {
            exclude(group = "com.android.support", module = "support-v4")
        }
        //implementation("com.joanzapata.iconify:android-iconify-ionicons:${Versions.iconify}") // (v2.0.1)

        // Annotation processor
        ksp("androidx.lifecycle:lifecycle-common-java8:${Versions.LIFECYCLE}")
        ksp("androidx.databinding:databinding-compiler-common:${Versions.DATABINDING}")
        ksp("androidx.room:room-compiler:${Versions.ROOM}")

        // Testing dependencies
        androidTestImplementation("androidx.annotation:annotation:${Versions.ANNOTATION}")
        androidTestImplementation("androidx.test:core:${Versions.CORE}")
        androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")
        testImplementation("androidx.test.espresso:espresso-intents:${Versions.ESPRESSO}")
        testImplementation("androidx.test.ext:truth:${Versions.TEST}")
        androidTestImplementation("androidx.test.ext:junit-ktx:${Versions.JUNIT_KTX}")
        androidTestImplementation("androidx.test:rules:${Versions.RULES}")
        androidTestImplementation("androidx.test:runner:${Versions.TEST_RUNNER}")

        androidTestImplementation("junit:junit:${Versions.JUNIT}")
    }
}