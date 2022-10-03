import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "app.web.diegoflassa_site.littledropsofrain.data"
    compileSdk = Config.compileSdkVersion
    //compileSdkPreview = Config.compileSdkPreviewVersion
    buildToolsVersion = Config.buildToolsVersion


    defaultConfig {
        minSdk = Config.minimumSdkVersion
        targetSdk = Config.targetSdkVersion
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
    lint {
        abortOnError = false
    }
}

dependencies {

    // Misc
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:${Versions.workVersion}")
    implementation("androidx.core:core-ktx:${Versions.core_ktx}")
    implementation("androidx.appcompat:appcompat:${Versions.appcompat}")
    implementation("com.google.android.material:material:${Versions.material}")
    implementation("androidx.room:room-ktx:2.3.0")
    testImplementation("junit:junit:${Versions.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.junit_ktx}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")

    // Room
    implementation("androidx.room:room-runtime:${Versions.room}")
    implementation("androidx.room:room-ktx:${Versions.room}")

    // Retrofix 2
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit_adapter}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit_adapter}")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:${Versions.okhttp_bom}"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")

    // GSON
    implementation("com.google.code.gson:gson:${Versions.gson}")

    // RX Java 3
    implementation("io.reactivex.rxjava3:rxjava:${Versions.rxjava}")
    implementation("io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}")

    // Dagger & Hilt
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    implementation("androidx.hilt:hilt-common:${Versions.hilt_other}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hilt_other}")
    implementation("androidx.hilt:hilt-work:${Versions.hilt_other}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${Versions.firebase_bom}"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}