import app.web.diegoflassa_site.littledropsofrain.buildsrc.Versions

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://jitpack.io")
        jcenter() // Warning: this repository is going to shut down soon
    }
    dependencies {
        //classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("com.android.tools.build:gradle:7.1.0-alpha01")
        //classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinCompilerVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        //classpath("com.google.gms:google-services:${Versions.google_services}")
        classpath("com.google.gms:google-services:4.3.8")
        //classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.safeargs_plugin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")

        // Add the Crashlytics Gradle plugin.
        //classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlytics}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.6.1")
        // Performance Monitoring plugin
        //classpath("com.google.firebase:perf-plugin:${Versions.perf}")
        classpath("com.google.firebase:perf-plugin:1.4.0")
        // Add the App Distribution Gradle plugin
        //classpath("com.google.firebase:firebase-appdistribution-gradle:${Versions.app_distribution}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:2.1.2")

        //classpath("com.google.android.gms:oss-licenses-plugin:${Versions.oss_plugin}")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.4")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.diffplug.spotless") version "5.12.5" apply true
    id("org.sonarqube") version "3.2.0" apply true
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
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://jitpack.io")
        jcenter() // Warning: this repository is going to shut down soon
    }
}