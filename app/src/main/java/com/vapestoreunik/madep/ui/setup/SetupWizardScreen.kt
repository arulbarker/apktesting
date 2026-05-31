package com.vapestoreunik.madep.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.BrandHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWizardScreen(
    onComplete: () -> Unit,
    vm: SetupWizardViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.completed) { if (state.completed) onComplete() }

    Scaffold(topBar = { TopAppBar(title = { Text("Selamat Datang") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrandHeader(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp))
            LinearProgressIndicator(
                progress = { (state.step + 1) / 3f },
                modifier = Modifier.fillMaxWidth(),
            )
            when (state.step) {
                0 -> StepProfile(state, vm)
                1 -> StepPin(state, vm)
                2 -> StepSummary(state)
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (state.step > 0) {
                    OutlinedButton(onClick = vm::prevStep, modifier = Modifier.weight(1f)) {
                        Text("Kembali")
                    }
                }
                Button(onClick = vm::nextStep, modifier = Modifier.weight(1f)) {
                    Text(if (state.step == 2) "Mulai Pakai" else "Lanjut")
                }
            }
        }
    }
}

@Composable
private fun StepProfile(s: SetupWizardState, vm: SetupWizardViewModel) {
    Text("Profil Toko", style = MaterialTheme.typography.titleLarge)
    OutlinedTextField(
        value = s.name,
        onValueChange = vm::setName,
        label = { Text("Nama Toko *") },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = s.address,
        onValueChange = vm::setAddress,
        label = { Text("Alamat") },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = s.phone,
        onValueChange = vm::setPhone,
        label = { Text("Telepon") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepPin(s: SetupWizardState, vm: SetupWizardViewModel) {
    Text("Set PIN", style = MaterialTheme.typography.titleLarge)
    Text("4-6 digit angka untuk masuk aplikasi", style = MaterialTheme.typography.bodyMedium)
    OutlinedTextField(
        value = s.pin,
        onValueChange = vm::setPin,
        label = { Text("PIN") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = s.pinConfirm,
        onValueChange = vm::setPinConfirm,
        label = { Text("Konfirmasi PIN") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepSummary(s: SetupWizardState) {
    Text("Konfirmasi", style = MaterialTheme.typography.titleLarge)
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Nama Toko: ${s.name}")
            if (s.address.isNotBlank()) Text("Alamat: ${s.address}")
            if (s.phone.isNotBlank()) Text("Telepon: ${s.phone}")
            Text("PIN: ${"•".repeat(s.pin.length)}")
        }
    }
    Text(
        "Tap 'Mulai Pakai' untuk membuat database & kategori default.",
        style = MaterialTheme.typography.bodySmall,
    )
}
