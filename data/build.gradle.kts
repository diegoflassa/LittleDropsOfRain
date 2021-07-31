import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
}

android {
    lint.abortOnError = false

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
        jvmTarget = "1.8"
    }
}

dependencies {

	// Misc
    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:${Versions.workVersion}")
    implementation("androidx.core:core-ktx:${Versions.core_ktx}")
    implementation("androidx.appcompat:appcompat:${Versions.appcompat}")
    implementation("com.google.android.material:material:${Versions.material}")
    testImplementation("junit:junit:${Versions.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.junit_ktx}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
	
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

	//GSON
	implementation("com.google.code.gson:gson:${Versions.gson}")
	
	// RX Java 3
	implementation("io.reactivex.rxjava3:rxjava:${Versions.rxjava}")
	implementation("io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:${Versions.firebaseBOM}"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
}