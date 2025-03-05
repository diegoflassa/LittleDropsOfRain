import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
		gradlePluginPortal()
    }
    dependencies {
        //classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("com.android.tools.build:gradle:8.9.0")
        //classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_compiler_version}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
        //classpath("com.google.gms:google-services:${Versions.google_services}")
        classpath("com.google.gms:google-services:4.4.2")
        //classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeargs_plugin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.8")

        // Add the Crashlytics Gradle plugin.
        //classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlytics}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
        // Performance Monitoring plugin
        //classpath("com.google.firebase:perf-plugin:${Versions.perf}")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        // Add the App Distribution Gradle plugin
        //classpath("com.google.firebase:firebase-appdistribution-gradle:${Versions.app_distribution}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:5.1.1")

        //classpath("com.google.android.gms:oss-licenses-plugin:${Versions.oss_plugin}")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
		classpath("com.github.ben-manes:gradle-versions-plugin:0.51.0")
        //classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.version_hilt}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}

plugins {         
    id("com.diffplug.spotless") version "6.22.0" apply true
	id("com.github.ben-manes.versions") version "0.51.0" apply true
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
           target("**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint(Versions.KT_LINT).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
            licenseHeaderFile("${project.rootProject.projectDir}/spotless/copyright.kt")
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}