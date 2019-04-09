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

#app
-keep public class com.tzpt.cloudlibrary.widget.** { *;}
-dontwarn com.tzpt.cloudlibrary.widget.**
-keep public class com.tzpt.cloudlibrary.utils.badger.** { *;}
-dontwarn com.tzpt.cloudlibrary.utils.badger.**
-keep public class com.tzpt.cloudlibrary.bean.** { *;}
#bean
-dontwarn com.tzpt.cloudlibrary.bean.**
#adapter
-keep public class * extends com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter
#pojo
-keep public class com.tzpt.cloudlibrary.modle.remote.pojo.** { *;}
-dontwarn com.tzpt.cloudlibrary.modle.remote.pojo.**
#pojo
-keep public class com.tzpt.cloudlibrary.base.** { *;}
-dontwarn com.tzpt.cloudlibrary.base.**
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
#gaode map
-keep public class com.amap.api.** { *;}
-dontwarn com.amap.api.**
-keep public class com.autonavi.** { *;}
-dontwarn com.autonavi.**
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

# greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class com.cn.daqi.otv.db.*{ *; }
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
#glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }
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
#WeCart share and qq share
-keep class com.tencent.** { *; }
-dontwarn com.tencent.**

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.tzpt.cloudlibrary.R$*{
public static final int *;
}
#ebook
-keep class com.amse.ys.zip.**{*;}
-keep class com.tzpt.cloudlibrary.cbreader.**{ *;}
-keep class com.tzpt.cloudlibrary.reader.**{ *; }
-keep class com.tzpt.cloudlibrary.zlibrary.**{ *; }
-keep class com.vimgadgets.linebreak.**{ *; }
#weibo
-keep class com.sina.weibo.sdk.** { *;}
-dontwarn com.sina.weibo.sdk.**
#nineoldandroids
-keep class com.nineoldandroids.** { *;}
-dontwarn com.nineoldandroids.**
#umeng
-keep class com.umeng.commonsdk.** {*;}

#支付宝
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


-keepclassmembers class com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity$JsInterface {
  public *;
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

#zbar
-keep class com.tzpt.cloudlibrary.zbar.** { *;}
-dontwarn com.tzpt.cloudlibrary.zbar.**

