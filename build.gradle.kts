import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
		//maven {
		//	url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
		//	credentials {
		//		username = "braintree_team_sdk"
		//		password = "AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp"
		//	}
		//}
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.google.gms:google-services:4.3.14")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0-alpha01")

        // Add the Crashlytics Gradle plugin.
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
        // Performance Monitoring plugin
        classpath("com.google.firebase:perf-plugin:1.4.1")
        // Add the App Distribution Gradle plugin
        classpath("com.google.firebase:firebase-appdistribution-gradle:3.0.3")

        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
		classpath("com.github.ben-manes:gradle-versions-plugin:0.42.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
    }
}

plugins {         
    id("com.diffplug.spotless") version "6.11.0" apply true
	id("com.github.ben-manes.versions") version "0.42.0" apply true
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
	id("org.jetbrains.kotlinx.kover") version "0.6.0"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
           target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint(Versions.ktlint).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
            licenseHeaderFile("${project.rootProject.projectDir}/spotless/copyright.kt")
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
		//maven {
		//	url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
		//	credentials {
		//		username = "braintree_team_sdk"
		//		password = "AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp"
		//	}
		//}
    }
}