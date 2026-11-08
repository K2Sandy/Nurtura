package com.tumbuhsehat.app.ui.onboarding

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.di.viewModelFactory
import com.tumbuhsehat.app.ui.components.DatePickerField
import com.tumbuhsehat.app.ui.components.IconBadge
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.PinkPrimaryDark
import com.tumbuhsehat.app.ui.theme.PinkSoft
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.ShapeCard
import com.tumbuhsehat.app.ui.theme.TextPrimary
import com.tumbuhsehat.app.ui.theme.TextSecondary

private enum class StatusAwal { HAMIL, ANAK }

@Composable
fun OnboardingScreen(onSelesai: () -> Unit) {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    val viewModel: OnboardingViewModel = viewModel(
        factory = viewModelFactory { OnboardingViewModel(app.container.userRepository) }
    )

    var status by remember { mutableStateOf<StatusAwal?>(null) }
    var nama by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf<String?>(null) }
    var tanggalMillis by remember { mutableStateOf<Long?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))

        IconBadge(Icons.Filled.Favorite, size = 72.dp, background = PinkSoft, tint = PinkPrimary)

        Text(
            "Selamat datang di KawalTumbuh",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 18.dp),
        )
        Text(
            "Yuk mulai dengan pilih status kamu sekarang",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp),
        )

        StatusPilihanCard(
            icon = Icons.Filled.PregnantWoman,
            title = "Sedang hamil",
            description = "Pantau gizi kehamilan & jadwal periksa",
            selected = status == StatusAwal.HAMIL,
            onClick = { status = StatusAwal.HAMIL },
        )
        Spacer(Modifier.height(14.dp))
        StatusPilihanCard(
            icon = Icons.Filled.ChildCare,
            title = "Sudah punya anak",
            description = "Pantau tumbuh kembang, MPASI & imunisasi",
            selected = status == StatusAwal.ANAK,
            onClick = { status = StatusAwal.ANAK },
        )

        if (status != null) {
            Column(Modifier.padding(top = 28.dp).fillMaxWidth()) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text(if (status == StatusAwal.HAMIL) "Nama panggilan" else "Nama anak") },
                    shape = ShapeCard,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    if (status == StatusAwal.HAMIL) "Hari pertama haid terakhir (HPHT)" else "Tanggal lahir",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
                )
                DatePickerField(
                    label = "Pilih tanggal",
                    selectedMillis = tanggalMillis,
                    onDateSelected = { tanggalMillis = it },
                )

                if (status == StatusAwal.ANAK) {
                    Text(
                        "Jenis kelamin",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = jenisKelamin == "LAKI_LAKI",
                            onClick = { jenisKelamin = "LAKI_LAKI" },
                            label = { Text("Laki-laki") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                        )
                        FilterChip(
                            selected = jenisKelamin == "PEREMPUAN",
                            onClick = { jenisKelamin = "PEREMPUAN" },
                            label = { Text("Perempuan") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                        )
                    }
                }

                val siapDisimpan = nama.isNotBlank() && tanggalMillis != null &&
                    (status == StatusAwal.HAMIL || jenisKelamin != null)

                Spacer(Modifier.height(28.dp))
                Button(
                    onClick = {
                        when (status) {
                            StatusAwal.HAMIL -> viewModel.buatProfilHamil(nama, tanggalMillis!!, onSelesai)
                            StatusAwal.ANAK -> viewModel.buatProfilAnak(nama, jenisKelamin!!, tanggalMillis!!, onSelesai)
                            null -> Unit
                        }
                    },
                    enabled = siapDisimpan,
                    shape = ShapeCard,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                ) {
                    Text("Mulai", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StatusPilihanCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (selected) PinkSoft else Color.White),
        shape = ShapeCard,
        border = BorderStroke(1.5.dp, if (selected) PinkPrimary else PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconBadge(
                icon,
                size = 44.dp,
                background = if (selected) PinkPrimary else PinkSoftStrong,
                tint = if (selected) Color.White else PinkPrimaryDark,
            )
            Column(Modifier.padding(start = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}
