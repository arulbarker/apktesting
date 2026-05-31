package com.vapestoreunik.madep.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.vapestoreunik.madep.ui.dashboard.DashboardScreen
import com.vapestoreunik.madep.ui.pos.PosScreen
import com.vapestoreunik.madep.ui.products.ProductListScreen

internal enum class Tab(val label: String, val icon: ImageVector) {
    Beranda("Beranda", Icons.Default.Home),
    Kasir("Kasir", Icons.Default.ShoppingCart),
    Produk("Produk", Icons.Default.Inventory),
    Riwayat("Riwayat", Icons.AutoMirrored.Filled.ReceiptLong),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldScreen(
    onOpenSettings: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenProductForm: (Long?) -> Unit,
    onOpenReceipt: (Long) -> Unit,
    onOpenReports: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.Beranda) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kasir Vapestore") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = selectedTab == t,
                        onClick = { selectedTab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) },
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            Tab.Beranda -> DashboardScreen(
                onOpenReports = onOpenReports,
                modifier = Modifier.padding(padding),
            )
            Tab.Kasir -> PosScreen(
                onCheckout = onOpenCheckout,
                modifier = Modifier.padding(padding),
            )
            Tab.Produk -> ProductListScreen(
                onOpenForm = onOpenProductForm,
                modifier = Modifier.padding(padding),
            )
            Tab.Riwayat -> PlaceholderTab("Tab Riwayat akan diisi di Phase 8", Modifier.padding(padding))
        }
    }
}

@Composable
private fun PlaceholderTab(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
