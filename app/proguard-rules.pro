# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep ZXing classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# Keep PDF417 encoder classes
-keep class com.google.zxing.pdf417.** { *; }
-keep class com.google.zxing.pdf417.encoder.** { *; }