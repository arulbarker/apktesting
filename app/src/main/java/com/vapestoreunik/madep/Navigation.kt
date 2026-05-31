package com.vapestoreunik.madep

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.vapestoreunik.madep.ui.auth.ChangePinScreen
import com.vapestoreunik.madep.ui.auth.PinLoginScreen
import com.vapestoreunik.madep.ui.setup.SetupWizardScreen

@Composable
fun MainNavigation(rootViewModel: RootViewModel = hiltViewModel()) {
    val rootState by rootViewModel.state.collectAsStateWithLifecycle()
    val initial: NavKey = if (!rootState.setupCompleted) SetupWizard else PinLogin
    val backStack = rememberNavBackStack(initial)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SetupWizard> {
                SetupWizardScreen(onComplete = { backStack.clearAndPush(PinLogin) })
            }
            entry<PinLogin> {
                PinLoginScreen(onUnlock = { backStack.clearAndPush(MainScaffold) })
            }
            entry<MainScaffold> {
                // Placeholder — diisi di Phase 5 (MainScaffoldScreen)
                Text(
                    "Phase 4 OK — Main Scaffold akan diisi di Phase 5",
                    modifier = Modifier.safeDrawingPadding().padding(16.dp),
                )
            }
            entry<ChangePin> {
                ChangePinScreen(
                    onDone = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}

internal fun NavBackStack.clearAndPush(key: NavKey) {
    while (isNotEmpty()) removeLastOrNull()
    add(key)
}
