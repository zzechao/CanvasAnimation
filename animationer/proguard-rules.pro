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

-keep public class com.base.animation.item.**{*;}
-keep public class com.base.animation.xml.AnimEncoder{*;}
-keep public class com.base.animation.node.**{*;}
-keep public class com.base.animation.AnimationEx{*;}
-keep public class com.base.animation.AnimCache{*;}
-keep class com.base.animation.xml.node.AnimNodeName
-keep class com.base.animation.xml.node.AnimAttributeName
-keep class com.base.animation.cache.AteDisplayItem
-keep public class * extends android.view.View
-keep @com.base.animation.xml.node.AnimNodeName class * {*;}
-keep @com.base.animation.xml.node.AnimAttributeName class * {*;}
-keep @com.base.animation.cache.AteDisplayItem class * {*;}