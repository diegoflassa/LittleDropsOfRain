import app.web.diegoflassa_site.littledropsofrain.buildsrc.Config
import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
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

    implementation("androidx.core:core-ktx:${Versions.core_ktx}")
    implementation("androidx.appcompat:appcompat:${Versions.appcompat}")
    implementation("com.google.android.material:material:${Versions.material}")
    testImplementation("junit:junit:${Versions.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.junit_ktx}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
	
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
}