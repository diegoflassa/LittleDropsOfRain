import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.AppExtension
import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    // Google Services plugin
    id("com.google.gms.google-services")
    // Apply the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.firebase-perf")
    // Apply the App Distribution Gradle plugin
    id("com.google.firebase.appdistribution")
    id("dagger.hilt.android.plugin")
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
	lint.checkDependencies = true

    compileSdk = Config.compileSdkVersion
    //compileSdkPreview = Config.compileSdkPreviewVersion
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        applicationId = Config.applicationId
        minSdk = Config.minimumSdkVersion
        targetSdk = Config.targetSdkVersion
        //targetSdkPreview = Config.targetSdkPreviewVersion
        versionCode = Config.versionCode
        versionName = Config.versionName
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
            //ext.enableCrashlytics = false
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
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        emulatorSnapshots {
            // Generates snapshots that are compressed into a single TAR file.
            compressSnapshots = true
        }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        // freeCompilerArgs = freeCompilerArgs + "-Xallow-jvm-ir-dependencies"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        // Enables Jetpack Compose for this module
        compose = true
    }
    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/NOTICE.md")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.androidx_jetpack_compose
    }
}

kapt {
	correctErrorTypes = true
    javacOptions {
        // These options are normally set automatically via the Hilt Gradle plugin, but we
        // set them manually to workaround a bug in the Kotlin 1.5.20
        option("-Adagger.fastInit=ENABLED")
        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
    }
}
//hilt {
//    enableAggregatingTask = true
//}

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
                tasks.findByName("uploadCrashlyticsMappingFile${variant.name.capitalize()}")
            val packageTask = tasks.findByName("package${variant.name.capitalize()}")
            val packageBundleTask = tasks.findByName("package${variant.name.capitalize()}Bundle")
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

    // Avoid build error
    //tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    tasks.withType<KotlinCompile>().configureEach {
        // Sets Java compatibility to Java 11
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = JavaVersion.VERSION_11.toString()

        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true
            // Set the target vm version
            // DO NOT UPDATE or will get Class Not Found Exception
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    dependencies {
        implementation(project(mapOf("path" to ":data")))
        implementation(project(mapOf("path" to ":domain")))

        // Kotlin + coroutines
        implementation("androidx.work:work-runtime-ktx:${Versions.workVersion}")
        // optional - RxJava2 support
        implementation("androidx.work:work-rxjava2:${Versions.workVersion}")
        // optional - GCMNetworkManager support
        implementation("androidx.work:work-gcm:${Versions.workVersion}")

        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

        //SafetyNet
        implementation("com.google.firebase:firebase-appcheck-safetynet:${Versions.safetynet}")

        // Volley
        implementation("com.android.volley:volley:${Versions.volley}")

        // Licenses
        implementation("com.google.android.gms:play-services-oss-licenses:${Versions.oss}")

        // UCrop
        implementation("com.github.yalantis:ucrop:${Versions.ucrop}")

        // Coil COroutines Image Loader
        implementation("io.coil-kt:coil:${Versions.coil}")
        implementation("io.coil-kt:coil-compose:${Versions.coil_compose}")

        // Preferences DataStore
        implementation("androidx.datastore:datastore-preferences:${Versions.data_store}")

        // Proto DataStore
        implementation("androidx.datastore:datastore-core:${Versions.data_store}")

		// Compose Navigation
        implementation("androidx.navigation:navigation-compose:${Versions.navigation_compose}")

		// Gerencianet
		// implementation("br.com.gerencianet.gnsdk:gn-api-sdk-java:${Versions.gerencianet}")


        //Retrofix 2
        implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
        implementation("com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit_adapter}")
        implementation("com.squareup.retrofit2:converter-jackson:${Versions.converter_jackson}")
        implementation("com.squareup.retrofit2:converter-gson:${Versions.converter}")

        // Jackson
        implementation("com.fasterxml.jackson.core:jackson-core:${Versions.jackson}")
        implementation("com.fasterxml.jackson.core:jackson-annotations:${Versions.jackson}")
        implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.jackson}")
        // Fix bug of XMLInputFactory not found
        implementation("javax.xml.stream:stax-api:${Versions.stax_api}")

        //GSON
        implementation("com.google.code.gson:gson:${Versions.gson}")

        // Dagger & Hilt
        implementation("com.google.dagger:hilt-android:${Versions.hilt}")
        kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
        implementation("androidx.hilt:hilt-common:${Versions.hilt_other}")
        kapt("androidx.hilt:hilt-compiler:${Versions.hilt_other}")
        implementation("androidx.hilt:hilt-navigation-fragment:${Versions.hilt_other}")
        implementation("androidx.hilt:hilt-work:${Versions.hilt_other}")

        // RX Java 3
        implementation("io.reactivex.rxjava3:rxjava:${Versions.rxjava}")
        implementation("io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}")

        // Jetpack Compose toolkit dependencies
        implementation("androidx.compose.ui:ui:${Versions.androidx_jetpack_compose}")
        // Tooling support (Previews, etc.)
        implementation("androidx.compose.ui:ui-tooling:${Versions.androidx_jetpack_compose}")
        // Foundation(Border, Background, Box, Image, Scroll, shapes, animations, etc.)
        implementation("androidx.compose.foundation:foundation:${Versions.foundation}")
        // Material Design
        implementation("androidx.compose.material:material:${Versions.androidx_jetpack_compose}")
        // Material design icons
        implementation("androidx.compose.material:material-icons-core:${Versions.androidx_jetpack_compose}")
        //implementation("androidx.compose.material:accompanist:${Versions.androidx_jetpack_compose_accompanist}")
        // Integration with observables
        implementation("androidx.compose.runtime:runtime-livedata:${Versions.androidx_jetpack_compose}")
        implementation("androidx.compose.runtime:runtime-rxjava2:${Versions.androidx_jetpack_compose}")
        // View Binding
        implementation("androidx.compose.ui:ui-viewbinding:${Versions.androidx_jetpack_compose}")
        // UI Tests
        androidTestImplementation("androidx.compose.ui:ui-test:${Versions.androidx_jetpack_compose}")
        debugImplementation("androidx.compose.ui:ui-tooling:${Versions.androidx_jetpack_compose}")
        debugImplementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_compiler_version}")


        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:${Versions.firebaseBOM}"))
        // com.google.firebase
        implementation("com.google.firebase:firebase-iid")
        // Add the Firebase SDK for Google Analytics
        // For an optimal experience using FCM, add the Firebase SDK
        // for Google Analytics. This is recommended, but not required.
        // Recommended: Add the Firebase SDK for Google Analytics.
        // Add the Firebase Crashlytics SDK.
        // Declare the dependencies for the Remote Config and Analytics libraries
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-config-ktx")
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-appindexing")
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
        implementation("com.firebaseui:firebase-ui-auth:${Versions.auth}")
        implementation("com.firebaseui:firebase-ui-database:${Versions.database}")
        implementation("com.firebaseui:firebase-ui-storage:${Versions.storage}")
        // OAuth Library
        implementation("com.google.auth:google-auth-library-oauth2-http:${Versions.oauth2}")

        // Leak Canary
        debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}")

        // Google Sign In SDK (only required for Google Sign In)
        implementation("com.google.android.gms:play-services-auth:${Versions.play_services_auth}")
        implementation("com.google.android.material:material:${Versions.material}")
        implementation("com.google.android.gms:play-services-location:${Versions.play_services_location}")
		implementation("com.google.android.play:core-ktx:${Versions.play_core}")
        implementation("com.google.gms:google-services:${Versions.google_services}")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin_compiler_version}")
        implementation("androidx.activity:activity-ktx:${Versions.activity_ktx}")
        implementation("androidx.annotation:annotation:${Versions.annotation}")
        implementation("androidx.appcompat:appcompat:${Versions.appcompat}")
        implementation("androidx.browser:browser:${Versions.browser}")
        implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}")
        // Compose ConstrainLayout
        implementation("androidx.constraintlayout:constraintlayout-compose:${Versions.constraintlayout_compose}")
        implementation("androidx.core:core-ktx:${Versions.core_ktx}")
        implementation("androidx.fragment:fragment-ktx:${Versions.fragment_ktx}")
        implementation("androidx.legacy:legacy-support-v4:${Versions.legacy_support_v4}")
        implementation("androidx.legacy:legacy-support-core-utils:${Versions.legacy_support_core_utils}")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
        implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")
        implementation("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
		implementation("androidx.lifecycle:lifecycle-common:${Versions.lifecycle}")
        implementation("androidx.navigation:navigation-runtime-ktx:${Versions.navigation}")
        implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
        implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
        implementation("androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}")
        implementation("androidx.preference:preference-ktx:${Versions.preference_ktx}")
        implementation("androidx.recyclerview:recyclerview:${Versions.recyclerview}")
        implementation("androidx.recyclerview:recyclerview-selection:${Versions.recyclerview_selection}")
        implementation("androidx.room:room-runtime:${Versions.room}")
        implementation("androidx.room:room-ktx:${Versions.room}")
        implementation("androidx.vectordrawable:vectordrawable-animated:${Versions.vectordrawable_animated}")
		
		// Icons
		//implementation("com.joanzapata.iconify:android-iconify-fontawesome:${Versions.iconify}") // (v4.5)
		//implementation("com.joanzapata.iconify:android-iconify-entypo:${Versions.iconify}") // (v3,2015)
		implementation("com.joanzapata.iconify:android-iconify-typicons:${Versions.iconify}") // (v2.0.7)
		//implementation("com.joanzapata.iconify:android-iconify-material:${Versions.iconify}") // (v2.0.0)
		//implementation("com.joanzapata.iconify:android-iconify-material-community:${Versions.iconify}") // (v1.4.57)
		//implementation("com.joanzapata.iconify:android-iconify-meteocons:${Versions.iconify}") // (latest)
		//implementation("com.joanzapata.iconify:android-iconify-weathericons:${Versions.iconify}") // (v2.0)
		implementation("com.joanzapata.iconify:android-iconify-simplelineicons:${Versions.iconify}") // (v1.0.0)
		//implementation("com.joanzapata.iconify:android-iconify-ionicons:${Versions.iconify}") // (v2.0.1)
		
		// Braintree
		// to offer card payments
		implementation("com.braintreepayments.api:card:${Versions.braintree}")
		// to collect device data
		implementation("com.braintreepayments.api:data-collector:${Versions.braintree}")
		// to offer PayPal 
		implementation("com.braintreepayments.api:paypal:${Versions.braintree}")
		// to offer local payments
		implementation("com.braintreepayments.api:local-payment:${Versions.braintree}")
		// to offer Google Pay
		implementation("com.braintreepayments.api:google-pay:${Versions.braintree}")
		// to offer Union Pay
		implementation("com.braintreepayments.api:union-pay:${Versions.braintree}")
		// to perform 3DS verification 
		implementation("com.braintreepayments.api:three-d-secure:${Versions.braintree}")
		// to offer Venmo
		implementation("com.braintreepayments.api:venmo:${Versions.braintree}")

        // Annotation processor
        kapt("androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}")
        kapt("androidx.databinding:databinding-compiler-common:${Versions.databinding}")
        kapt("androidx.room:room-compiler:${Versions.room}")

        // Testing dependencies
        androidTestImplementation("androidx.annotation:annotation:${Versions.annotation}")
        androidTestImplementation("androidx.test:core:${Versions.core}")
        androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
        testImplementation("androidx.test.espresso:espresso-intents:${Versions.espresso}")
        testImplementation("androidx.test.ext:truth:${Versions.truth}")
        androidTestImplementation("androidx.test.ext:junit-ktx:${Versions.junit_ktx}")
        androidTestImplementation("androidx.test:rules:${Versions.test}")
        androidTestImplementation("androidx.test:runner:${Versions.test}")

        androidTestImplementation("junit:junit:${Versions.junit}")
    }
}