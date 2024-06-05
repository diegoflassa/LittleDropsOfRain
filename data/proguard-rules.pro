# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnProductInsertedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnTaskFinishedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.parser.ProductParser$OnParseProgress
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.repository.IluriaProductsRepository
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.dao.FilesDao
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.dao.MessageDao
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.dao.ProductDao
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.dao.UserDao
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.entities.TopicMessage$Topic$Companion
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataChangeListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnDataFailureListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedFailureListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnFileUploadedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnProductInsertedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnTaskFinishedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUserFoundListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.interfaces.OnUsersLoadedListener
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.parser.ProductParser$OnParseProgress
-dontwarn app.web.diegoflassa_site.littledropsofrain.data.repository.IluriaProductsRepository