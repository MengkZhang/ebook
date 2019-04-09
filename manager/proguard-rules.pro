# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
native <methods>;
}
-keepclasseswithmembernames class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
-keep public class com.tzpt.cloundlibrary.manager.widget.** { *;}
-dontwarn com.tzpt.cloundlibrary.manager.widget.**
-keep public class com.tzpt.cloundlibrary.manager.bean.** { *;}
-dontwarn com.tzpt.cloundlibrary.manager.bean.**
-keep public class com.tzpt.cloundlibrary.manager.modle.remote.pojo.** { *;}
-dontwarn com.tzpt.cloundlibrary.manager.modle.remote.pojo.**

-keep public class com.tzpt.cloundlibrary.manager.ui.adapter.** { *;}
-dontwarn com.tzpt.cloundlibrary.manager.ui.adapter.**


#ocr
-keep public class com.idcard.** { *;}
-dontwarn com.idcard.**
#zbar
-keep public class net.sourceforge.zbar.** { *;}
-dontwarn net.sourceforge.zbar.**
-keep public class com.android.support.** { *;}
-dontwarn com.android.support.**
#Gson
-keep public class com.google.** { *;}
-dontwarn com.google.**
-keep public class okio.** { *;}
-dontwarn okio.**
-keep public class okhttp3.** { *;}
-dontwarn okhttp3.**
#time picker
-keep public class com.wdullaer.materialdatetimepicker.** { *;}
-dontwarn com.wdullaer.materialdatetimepicker.**
#gaode map
-keep public class com.amap.api.** { *;}
-dontwarn com.amap.api.**
-keep public class com.autonavi.aps.amapapi.model.** { *;}
-dontwarn com.autonavi.aps.amapapi.model.**
-keep public class com.loc.** { *;}
-dontwarn com.loc.**

#greeddao
### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**

### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class com.cn.daqi.otv.db.*{ *; }
# If you do not use RxJava:
-dontwarn rx.**
# If you do not use RxJava:
-keep public class rx.** { *;}
-dontwarn rx.**
-dontwarn sun.misc.*
-keepclassmembers class rx.internal.util.unsafe.ArrayQueueField {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#squareup
-keep public class com.squareup.** { *;}
-dontwarn com.squareup.**
-keep public class com.squareup.** { *;}
-dontwarn com.squareup.**
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#event bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

#JPush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

#zbar
-keep class com.tzpt.cloudlibrary.zbar.** { *;}
-dontwarn com.tzpt.cloudlibrary.zbar.**