# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#使用webview的
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#去除编译时警告
-ignorewarnings
#不进行优化
-dontoptimize
#不进行预校验
-dontpreverify

#保留
-keep public class com.example.rungame10.biyue.Application.BiYueApplication
-keep public class com.example.rungame10.biyue.Service.FloatService
-keep public class com.example.rungame10.buyue.View.NotifyDialog
-keep public class com.example.rungame10.biyue.SDK.** {*;}

#保留native方法的类名和方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

# Gson
-keepattributes *Annotation*
-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.stream.** {*;}
-keep class com.example.rungame10.biyue.Model.** {*;}
-keep class com.example.rungame10.biyue.Intf.** {*;}

#不要混淆xUtils中的注解类型，添加混淆配置：
-keep class * extends java.lang.annotation.Annotation { *; }

#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**