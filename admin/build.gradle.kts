import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    //id("com.android.library")
    id("com.android.dynamic-feature")
    id("kotlin-android")
}

android {
    compileSdk = Config.COMPILE_SDK_VERSION
    //compileSdkPreview = Config.compileSdkPreviewVersion
    buildToolsVersion = Config.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdk = Config.MINIMUM_SDK_VERSION
        //targetSdk = Config.targetSdkVersion
        //targetSdkPreview = Config.targetSdkPreviewVersion
        //versionCode = Config.versionCode
        //versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(project(mapOf("path" to ":presentation")))

    implementation("androidx.core:core-ktx:${Versions.CORE_KTX}")
    implementation("androidx.appcompat:appcompat:${Versions.APPCOMPAT}")
    implementation("com.google.android.material:material:${Versions.MATERIAL}")
    androidTestImplementation("junit:junit:${Versions.JUNIT}")
    androidTestImplementation("androidx.test.ext:junit-ktx:${Versions.JUNIT_KTX}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")
}