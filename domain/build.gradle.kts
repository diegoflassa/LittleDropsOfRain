import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

android {
    lint.abortOnError = false
    namespace = "app.web.diegoflassa_site.littledropsofrain.domain"

    compileSdk = Config.COMPILE_SDK_VERSION
    //compileSdkPreview = Config.compileSdkPreviewVersion
    buildToolsVersion = Config.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdk = Config.MINIMUM_SDK_VERSION
        //targetSdkPreview = Config.targetSdkPreviewVersion
        //versionCode = Config.versionCode
        //versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(project(mapOf("path" to ":data")))

    // Misc
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:${Versions.WORK_VERSION}")
    implementation("androidx.core:core-ktx:${Versions.CORE_KTX}")
    implementation("androidx.appcompat:appcompat:${Versions.APPCOMPAT}")
    implementation("com.google.android.material:material:${Versions.MATERIAL}")
    testImplementation("junit:junit:${Versions.JUNIT}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.JUNIT_KTX}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")

    // RX Java 3
    implementation("io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA}")

    // Android X
    implementation("androidx.navigation:navigation-runtime-ktx:${Versions.NAVIGATION}")
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Firebase Authentication(Kotlin)
    implementation("com.google.firebase:firebase-auth-ktx")

    // Add the SDK for Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-inappmessaging-ktx")

    //Retrofix 2
    implementation("com.squareup.retrofit2:retrofit:${Versions.RETROFIT}")
    implementation("com.squareup.retrofit2:adapter-rxjava3:${Versions.RETROFIT_ADAPTER}")
    implementation("com.squareup.retrofit2:converter-jackson:${Versions.CONVERTER_JACKSON}")
    implementation("com.squareup.retrofit2:converter-gson:${Versions.CONVERTER}")
}