# Add project specific ProGuard rules here.

# Preserve generic signatures for Retrofit/Moshi (required for type resolution)
-keepattributes Signature

# Retrofit API interface
-keep interface com.aurox.gotham.data.remote.NycOpenDataApi { *; }

# Retrofit suspend function support
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Moshi - keep generated adapters and DTOs
-keep class com.aurox.gotham.data.remote.dto.** { *; }
-keep class **JsonAdapter { *; }

# Moshi enums
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
    **[] values();
}
