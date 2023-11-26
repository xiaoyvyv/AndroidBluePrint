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

# 禁止混淆泛型，项目会读取信息初始化
-keepattributes Signature

# 项目
-keep class * implements androidx.viewbinding.ViewBinding {*;}
-keep class * extends androidx.lifecycle.ViewModel{*;}

-keep class * extends com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel{*;}
-keep class * extends com.xiaoyv.widget.binder.BaseQuickBindingAdapter
-keep class * extends com.xiaoyv.widget.binder.BaseQuickBindingHolder
-keep class * extends com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

-keep class com.xiaoyv.blueprint.base.**
-keep class com.xiaoyv.widget.binder.**