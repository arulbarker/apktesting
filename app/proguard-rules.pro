# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# Kotlin Serialization — keep generated $serializer companion + @Serializable classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both serializable classes and objects).
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Our @Serializable NavKey types — keep entire classes
-keep,includedescriptorclasses class com.vapestoreunik.madep.**$$serializer { *; }
-keepclassmembers class com.vapestoreunik.madep.** {
    *** Companion;
}
-keepclasseswithmembers class com.vapestoreunik.madep.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# bcrypt
-keep class at.favre.lib.crypto.bcrypt.** { *; }

# Compose Navigation 3 — reflective NavKey serialization
-keep class androidx.navigation3.** { *; }
-dontwarn androidx.navigation3.**

# DataStore — keep PreferencesKey
-keep class androidx.datastore.** { *; }

# Keep entity & DAO classes so Room generated code can reach them
-keep class com.vapestoreunik.madep.data.local.entity.** { *; }
-keep interface com.vapestoreunik.madep.data.local.dao.** { *; }
