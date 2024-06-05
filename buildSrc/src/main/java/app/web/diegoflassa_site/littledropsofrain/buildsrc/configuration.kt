package app.web.diegoflassa_site.littledropsofrain.buildsrc

import java.io.File
import java.io.FileInputStream
import java.util.*

@Suppress("Unused", "MemberVisibilityCanBePrivate")
object Config {
    const val APPLICATION_ID = "app.web.diegoflassa_site.littledropsofrain"
    private const val VERSION_MAJOR = 1
    private const val VERSION_MINOR = 0
    private const val VERSION_PATCH = 12
    private var versionClassifier = "debug"
    const val MINIMUM_SDK_VERSION = 24
    const val COMPILE_SDK_VERSION = 34
    const val TARGET_SDK_VERSION = 34
    const val BUILD_TOOLS_VERSION = "34.0.0"
    const val VERSION_CODE = 240010014
    const val VERSION_NAME = "240010014"

    private fun buildVersionCode(): Int {
        return MINIMUM_SDK_VERSION * 10000000 + VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    }

    private fun buildVersionNameWithoutClassifier(): String {
        return "littledropsofrain"
    }

    @Suppress("Unused")
    fun buildVersionName(): String {
        val versionProps = Properties()
            val versionPropsFile = File("version.properties")
        val fileInputStream = FileInputStream(versionPropsFile)
        fileInputStream.use { fis ->
            if (versionPropsFile.exists()) {
                versionProps.load(fis)
            }
            val code = (versionProps["VERSION_CODE"] ?: "0").toString().toInt() + 1
            versionProps["VERSION_CODE"] = code.toString()
            versionProps.store(versionPropsFile.writer(), null)

            var versionName = buildVersionNameWithoutClassifier()
            if (versionClassifier.isNotEmpty()) {
                //versionName = versionName + "-" + versionClassifier + "-Build:${code}"
            } else {
                versionName += "-Build:${code}"
            }
        }
        return VERSION_NAME
    }
}
