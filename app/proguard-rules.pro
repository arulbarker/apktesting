# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# Kotlin serialization
-keepattributes Signature, *Annotation*

# bcrypt
-keep class at.favre.lib.crypto.bcrypt.** { *; }
