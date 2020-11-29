package app.web.diegoflassa_site.littledropsofrain.buildsrc

import java.io.File
import java.io.FileInputStream
import java.util.*

@Suppress("Unused")
object Config {
    const val ktlint = "0.39.0"
    const val applicationId = "app.web.diegoflassa_site.littledropsofrain"
    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 7
    private const val versionClassifier = "debug"
    private const val minimumSdkVersion = 23
    const val compileSdkVersion = 30
    const val targetSdkVersion = 30
    const val buildToolsVersion = "30.0.2"
    val versionCode = buildVersionCode()
    val versionName = buildVersionName()

    private fun buildVersionCode(): Int {
        return minimumSdkVersion * 10000000 + versionMajor * 10000 + versionMinor * 100 + versionPatch
    }

    private fun buildVersionNameWithoutClassifier(): String {
        return "littledropsofrain"
    }

    private fun buildVersionName(): String {
        val versionProps = Properties()
        val versionPropsFile = File("version.properties")
        if (versionPropsFile.exists()) {
            versionProps.load(FileInputStream(versionPropsFile))
        }
        val code = (versionProps["VERSION_CODE"] ?: "0").toString().toInt() + 1
        versionProps["VERSION_CODE"] = code.toString()
        versionProps.store(versionPropsFile.writer(), null)

        var versionName = buildVersionNameWithoutClassifier()
        if (versionClassifier.isNotEmpty()) {
            versionName = versionName + "-" + versionClassifier + "-Build:${code}"
        }else{
            versionName += "-Build:${code}"
        }
        return versionName
    }
}
