package com.vapestoreunik.madep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.vapestoreunik.madep.ui.categories.CategoryManageScreen
import com.vapestoreunik.madep.ui.main.MainScaffoldScreen
import com.vapestoreunik.madep.ui.products.ProductFormScreen
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
                MainScaffoldScreen(
                    onOpenSettings = { backStack.add(Settings) },
                    onOpenCheckout = { backStack.add(Checkout) },
                    onOpenProductForm = { id -> backStack.add(ProductForm(id)) },
                    onOpenReceipt = { id -> backStack.add(Receipt(id)) },
                    onOpenReports = { backStack.add(Reports) },
                )
            }
            entry<Checkout> { PhaseStub("Checkout — Phase 7", backStack) }
            entry<Receipt> { PhaseStub("Receipt — Phase 8", backStack) }
            entry<ProductForm> { key ->
                ProductFormScreen(
                    productId = key.productId,
                    onSaved = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<CategoryManage> {
                CategoryManageScreen(onBack = { backStack.removeLastOrNull() })
            }
            entry<Reports> { PhaseStub("Reports — Phase 9", backStack) }
            entry<Settings> { PhaseStub("Settings — Phase 10", backStack) }
            entry<StoreProfile> { PhaseStub("StoreProfile — Phase 10", backStack) }
            entry<BackupRestore> { PhaseStub("BackupRestore — Phase 10", backStack) }
            entry<ChangePin> {
                ChangePinScreen(
                    onDone = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}

@Composable
private fun PhaseStub(text: String, backStack: NavBackStack) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        Text(
            "Tap back untuk kembali",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

internal fun NavBackStack.clearAndPush(key: NavKey) {
    while (isNotEmpty()) removeLastOrNull()
    add(key)
}
