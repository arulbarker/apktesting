package com.vapestoreunik.madep

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.vapestoreunik.madep.ui.auth.ChangePinScreen
import com.vapestoreunik.madep.ui.auth.PinLoginScreen
import com.vapestoreunik.madep.ui.categories.CategoryManageScreen
import com.vapestoreunik.madep.ui.checkout.CheckoutScreen
import com.vapestoreunik.madep.ui.main.MainScaffoldScreen
import com.vapestoreunik.madep.ui.products.ProductFormScreen
import com.vapestoreunik.madep.ui.reports.ReportsScreen
import com.vapestoreunik.madep.ui.settings.BackupRestoreScreen
import com.vapestoreunik.madep.ui.settings.SettingsScreen
import com.vapestoreunik.madep.ui.settings.StoreProfileScreen
import com.vapestoreunik.madep.ui.setup.SetupWizardScreen
import com.vapestoreunik.madep.ui.transactions.ReceiptScreen

@Composable
fun MainNavigation(rootViewModel: RootViewModel = hiltViewModel()) {
    val rootState by rootViewModel.state.collectAsStateWithLifecycle()
    val initial: NavKey = if (!rootState.setupCompleted) SetupWizard else PinLogin
    val backStack = rememberNavBackStack(initial)

    fun clearAndPush(key: NavKey) {
        while (backStack.isNotEmpty()) backStack.removeLastOrNull()
        backStack.add(key)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SetupWizard> {
                SetupWizardScreen(onComplete = { clearAndPush(PinLogin) })
            }
            entry<PinLogin> {
                PinLoginScreen(onUnlock = { clearAndPush(MainScaffold) })
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
            entry<Checkout> {
                CheckoutScreen(
                    onSuccess = { id ->
                        backStack.removeLastOrNull()
                        backStack.add(Receipt(id))
                    },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Receipt> { key ->
                ReceiptScreen(
                    transactionId = key.transactionId,
                    onNewTransaction = { clearAndPush(MainScaffold) },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
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
            entry<Reports> { ReportsScreen(onBack = { backStack.removeLastOrNull() }) }
            entry<Settings> {
                SettingsScreen(
                    onOpenStoreProfile = { backStack.add(StoreProfile) },
                    onOpenChangePin = { backStack.add(ChangePin) },
                    onOpenCategoryManage = { backStack.add(CategoryManage) },
                    onOpenReports = { backStack.add(Reports) },
                    onOpenBackup = { backStack.add(BackupRestore) },
                    onLock = { clearAndPush(PinLogin) },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<StoreProfile> { StoreProfileScreen(onBack = { backStack.removeLastOrNull() }) }
            entry<BackupRestore> { BackupRestoreScreen(onBack = { backStack.removeLastOrNull() }) }
            entry<ChangePin> {
                ChangePinScreen(
                    onDone = { backStack.removeLastOrNull() },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
