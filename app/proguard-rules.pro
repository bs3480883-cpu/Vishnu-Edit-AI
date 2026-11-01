# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Firestore data models and annotations for release builds
-keep class com.example.data.model.** { *; }
-keepnames class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** {
    <init>(...);
    public *;
}

# Preserve line number information for stack traces
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod
