package com.vapestoreunik.madep.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val KasirShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Pill / capsule shape — used by PrimaryButton for the "yellow capsule"
// retail-CTA look. Hardcoded 50% so it always reads as full pill regardless of height.
val PillShape = RoundedCornerShape(50)
