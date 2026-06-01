package com.vapestoreunik.madep.ui.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vapestoreunik.madep.core.ui.components.BrandHeader
import com.vapestoreunik.madep.core.ui.components.PrimaryButton
import com.vapestoreunik.madep.core.ui.components.SecondaryButton
import com.vapestoreunik.madep.theme.BrandAsh
import com.vapestoreunik.madep.theme.BrandCarbon
import com.vapestoreunik.madep.theme.BrandIron
import com.vapestoreunik.madep.theme.BrandJet
import com.vapestoreunik.madep.theme.BrandRed
import com.vapestoreunik.madep.theme.BrandSnow
import com.vapestoreunik.madep.theme.BrandYellow

@Composable
fun SetupWizardScreen(
    onComplete: () -> Unit,
    vm: SetupWizardViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.completed) { if (state.completed) onComplete() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandJet)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        BrandHeader(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
        Spacer(Modifier.height(20.dp))

        // ── Custom 3-dot step indicator ─────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "LANGKAH ${state.step + 1} / 3",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = BrandAsh,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    val active = i <= state.step
                    Box(
                        Modifier
                            .width(if (i == state.step) 24.dp else 8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (active) BrandYellow else BrandIron),
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
            when (state.step) {
                0 -> StepProfile(state, vm)
                1 -> StepPin(state, vm)
                2 -> StepSummary(state)
            }
            state.error?.let {
                Text(
                    it,
                    color = BrandRed,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) {
            if (state.step > 0) {
                SecondaryButton(
                    text = "Kembali",
                    onClick = vm::prevStep,
                    modifier = Modifier.weight(1f),
                )
            }
            PrimaryButton(
                text = if (state.step == 2) "Mulai Pakai" else "Lanjut",
                onClick = vm::nextStep,
                modifier = Modifier.weight(if (state.step == 0) 1f else 1.4f),
            )
        }
    }
}

@Composable
private fun StepProfile(s: SetupWizardState, vm: SetupWizardViewModel) {
    StepTitle("Profil Toko", "Identitas yang akan tampil di struk.")
    BrandTextField(
        value = s.name,
        onValueChange = vm::setName,
        label = "Nama Toko *",
    )
    BrandTextField(
        value = s.address,
        onValueChange = vm::setAddress,
        label = "Alamat",
    )
    BrandTextField(
        value = s.phone,
        onValueChange = vm::setPhone,
        label = "Telepon",
        keyboardType = KeyboardType.Phone,
    )
}

@Composable
private fun StepPin(s: SetupWizardState, vm: SetupWizardViewModel) {
    StepTitle("Set PIN Akses", "4-6 digit angka untuk membuka aplikasi.")
    BrandTextField(
        value = s.pin,
        onValueChange = vm::setPin,
        label = "PIN",
        keyboardType = KeyboardType.NumberPassword,
    )
    BrandTextField(
        value = s.pinConfirm,
        onValueChange = vm::setPinConfirm,
        label = "Konfirmasi PIN",
        keyboardType = KeyboardType.NumberPassword,
    )
}

@Composable
private fun StepSummary(s: SetupWizardState) {
    StepTitle("Konfirmasi", "Cek sebelum membuat database.")
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandCarbon),
        border = BorderStroke(1.dp, BrandIron),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryRow("Nama", s.name)
            if (s.address.isNotBlank()) SummaryRow("Alamat", s.address)
            if (s.phone.isNotBlank()) SummaryRow("Telepon", s.phone)
            SummaryRow("PIN", "•".repeat(s.pin.length))
        }
    }
    Text(
        "Tap 'MULAI PAKAI' untuk membuat database & kategori default.",
        style = MaterialTheme.typography.bodySmall,
        color = BrandAsh,
    )
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            color = BrandAsh,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = BrandSnow,
        )
    }
}

@Composable
private fun StepTitle(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = BrandSnow,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = BrandAsh,
        )
    }
}

@Composable
private fun BrandTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedBorderColor = BrandIron,
            focusedLabelColor = BrandYellow,
            unfocusedLabelColor = BrandAsh,
            cursorColor = BrandYellow,
            focusedTextColor = BrandSnow,
            unfocusedTextColor = BrandSnow,
        ),
    )
}

