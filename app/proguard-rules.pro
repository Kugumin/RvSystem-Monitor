# Project-specific ProGuard rules for RvSystem-Monitor

# 1. JNI: Keep classes and their native methods for linkage
-keep class com.rve.systemmonitor.utils.** {
    native <methods>;
}

# 2. Battery: Keep reflection targets for internal PowerProfile
-keep class com.android.internal.os.PowerProfile {
    public <init>(android.content.Context);
    public double getBatteryCapacity();
}

# 3. Backdrop: Consumer rules are bundled with the library.
# We keep only the dontwarn for optional dependencies.
-dontwarn io.github.kyant0.backdrop.**

# 4. JNA & AWT: Suppress warnings for missing desktop-only classes
-dontwarn java.awt.**
-dontwarn com.sun.jna.**

# 5. Standard Android/Compose attributes
-keepattributes Signature,InnerClasses,EnclosingMethod,AnnotationDefault,*Annotation*
