package com.vapestoreunik.madep

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

/**
 * Placeholder Navigation — akan diisi di Phase 4 setelah ViewModels siap.
 */
@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(SetupWizard)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
      entry<SetupWizard> {
        Text(
          "Phase 0 OK — Navigation akan diisi di Phase 4",
          modifier = Modifier.safeDrawingPadding().padding(16.dp),
        )
      }
    },
  )
}
