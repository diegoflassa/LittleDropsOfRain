# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\Diego\Programming\SDKs\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
## Flutter wrapper
-keepattributes Signature
-keepattributes *Annotation*

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# For supressing the warnings
#Presentation
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.workers.UpdateProductsWork
-dontwarn io.grpc.InternalGlobalInterceptors

#Data
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

#Domain
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.auth.FirebaseAuthLiveData
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.auth.UserLiveData
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.ExtensionsKt
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.FIleUtils
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper$Companion
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.Helper
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.LoggedUser
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.MainActivityHolder$Companion
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.helpers.MainActivityHolder
-dontwarn app.web.diegoflassa_site.littledropsofrain.domain.preferences.MyOnSharedPreferenceChangeListener

#Other
-dontwarn com.sun.activation.registries.LogSupport
-dontwarn com.sun.activation.registries.MailcapFile
-dontwarn java.awt.datatransfer.DataFlavor
-dontwarn java.awt.datatransfer.Transferable
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn javax.xml.stream.Location
-dontwarn javax.xml.stream.XMLEventFactory
-dontwarn javax.xml.stream.XMLInputFactory
-dontwarn javax.xml.stream.XMLOutputFactory
-dontwarn javax.xml.stream.XMLResolver
-dontwarn javax.xml.stream.XMLStreamConstants
-dontwarn javax.xml.stream.XMLStreamException
-dontwarn javax.xml.stream.XMLStreamReader
-dontwarn javax.xml.stream.XMLStreamWriter
-dontwarn javax.xml.stream.util.StreamReaderDelegate
-dontwarn javax.xml.stream.util.XMLEventAllocator
-dontwarn com.google.android.gms.auth.api.credentials.Credential$Builder
-dontwarn com.google.android.gms.auth.api.credentials.Credential
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequest$Builder
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequest
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequestResponse
-dontwarn com.google.android.gms.auth.api.credentials.Credentials
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsClient
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsOptions$Builder
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsOptions
-dontwarn com.google.android.gms.auth.api.credentials.HintRequest$Builder
-dontwarn com.google.android.gms.auth.api.credentials.HintRequest