import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
		gradlePluginPortal()
		maven {
			url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
			credentials {
				username = "braintree_team_sdk"
				password = "AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp"
			}
		}
    }
    dependencies {
        //classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("com.android.tools.build:gradle:7.0.4")
        //classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_compiler_version}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        //classpath("com.google.gms:google-services:${Versions.google_services}")
        classpath("com.google.gms:google-services:4.3.10")
        //classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeargs_plugin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.0-beta02")

        // Add the Crashlytics Gradle plugin.
        //classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlytics}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
        // Performance Monitoring plugin
        //classpath("com.google.firebase:perf-plugin:${Versions.perf}")
        classpath("com.google.firebase:perf-plugin:1.4.0")
        // Add the App Distribution Gradle plugin
        //classpath("com.google.firebase:firebase-appdistribution-gradle:${Versions.app_distribution}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:2.2.0")

        //classpath("com.google.android.gms:oss-licenses-plugin:${Versions.oss_plugin}")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.4")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
		classpath("com.github.ben-manes:gradle-versions-plugin:0.41.0")
        //classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.version_hilt}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
    }
}

plugins {         
    id("com.diffplug.spotless") version "6.1.0" apply true
	id("com.github.ben-manes.versions") version "0.41.0" apply true
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
	id("org.jetbrains.kotlinx.kover") version "0.5.0-RC"
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
        maven(url = "https://jitpack.io")
		maven {
			url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
			credentials {
				username = "braintree_team_sdk"
				password = "AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp"
			}
		}
    }
}