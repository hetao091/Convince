# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/gadejn/Documents/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#指定代码的压缩级别
-optimizationpasses 5

## 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解 内部类
-keepattributes *Annotation*,InnerClasses

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump proguard/class_files.txt
#未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######

## --------------- Start Project specifics --------------- ##

# 四大组件及基本类
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class android.app.Notification
-keep public class android.webkit.**
#保护WebView对HTML页面的API不被混淆
-keep class **.Webview2JsInterface {*; }
-keep public class * extends android.app.Dialog
-keep public class * extends android.view

-keep class android.support.** {*;}


# support-v4
#https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }


# support-v7

########## support.v7 ##########
-dontwarn android.support.v7.**

-keep class android.support.v7.** { *; }
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep public class android.support.v7.widget.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}


########## support.design ##########
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


########## cardView ##########
# http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
-keep class android.support.v7.widget.RoundRectDrawable { *; }



####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

#保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保护泛型 反射
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable


#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#保持R文件不被混淆
-keep class **.R*{*;}
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

## Keep the BuildConfig
#-keep class com.agile.jxoffice.BuildConfig { *; }
#
##保护实体类
#-keep class com.agile.jxoffice.data.** { *;  }
#-keep class com.agile.jxoffice.net.** { *;  }
##保护自定义加载
#-keep class com.agile.jxoffice.view.progress.** { *;  }


## ---------------- End Project specifics ---------------- ##



## SVG Android ##
#https://github.com/pents90/svg-android
-keep class com.larvalabs.svgandroid.** {*;}
-keep class com.larvalabs.svgandroid.*$* {*;}


########### muPDF ##########
#-keep public class com.artifex.mupdfdemo.MuPDFActivity
#-keep public class com.artifex.mupdfdemo.MuPDFCore
#-keep class com.artifex.mupdfdemo.** {*;}

########### butterknife ##########
#-keep class butterknife.** { *; }
#-dontwarn butterknife.internal.**
#-keep class **$$ViewBinder { *; }
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <fields>;
#}
#
#-keepclasseswithmembernames class * {
#    @butterknife.* <methods>;
#}


-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**
-dontwarn org.bouncycastle.**




