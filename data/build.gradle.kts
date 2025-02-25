import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "2.1.10-1.0.30"
}

android {
    lint.abortOnError = false
    namespace = "app.web.diegoflassa_site.littledropsofrain.data"

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
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
}

dependencies {

	// Misc
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:${Versions.WORK_VERSION}")
    implementation("androidx.core:core-ktx:${Versions.CORE_KTX}")
    implementation("androidx.appcompat:appcompat:${Versions.APPCOMPAT}")
    implementation("com.google.android.material:material:${Versions.MATERIAL}")
    testImplementation("junit:junit:${Versions.JUNIT}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.JUNIT_KTX}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.ESPRESSO}")
	
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

	//GSON
	implementation("com.google.code.gson:gson:${Versions.GSON}")
	
	// RX Java 3
	implementation("io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA}")
	implementation("io.reactivex.rxjava3:rxandroid:${Versions.RX_ANDROID}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}