package com.tumbuhsehat.app.ui.profil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.data.local.ProfileEntity
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.di.viewModelFactory
import com.tumbuhsehat.app.ui.components.DatePickerField
import com.tumbuhsehat.app.ui.components.GreetingHeroCard
import com.tumbuhsehat.app.ui.components.IconBadge
import com.tumbuhsehat.app.ui.components.PageHeader
import com.tumbuhsehat.app.ui.components.SectionHeader
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.ShapeCard
import com.tumbuhsehat.app.ui.theme.StatusNormalText
import com.tumbuhsehat.app.ui.theme.SurfaceNeutral
import com.tumbuhsehat.app.ui.theme.TextMuted
import com.tumbuhsehat.app.ui.theme.TextPrimary
import com.tumbuhsehat.app.util.DateUtils

@Composable
fun ProfilScreen(onTambahProfil: () -> Unit) {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    val viewModel: ProfilViewModel = viewModel(
        factory = viewModelFactory {
            ProfilViewModel(app.container.userRepository, app.container.growthReference)
        }
    )
    val state by viewModel.uiState.collectAsState()
    var showTambahUkuran by remember { mutableStateOf(false) }
    var showTandaiLahir by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            // Header ikut scroll (collapse on scroll), sama seperti Kalender & Gizi
            PageHeader(Icons.Filled.Person, "Profil")

            ProfileSwitcherRow(
                profiles = state.allProfiles,
                activeId = state.activeProfile?.id,
                onSelect = { viewModel.pindahProfil(it) },
                onTambah = onTambahProfil,
            )

            val profile = state.activeProfile
            if (profile == null) {
                Text(
                    "Belum ada profil aktif",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    if (profile.tipe == TipeProfil.HAMIL) {
                        GreetingHeroCard(
                            eyebrow = "Masih dalam kandungan",
                            title = "Sudah lahir?",
                            modifier = Modifier.padding(top = 16.dp),
                            trailing = {
                                Button(
                                    onClick = { showTandaiLahir = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                ) { Text("Tandai lahir") }
                            },
                        )

                        profile.hphtMillis?.let { hpht ->
                            val hplMillis = hpht + (280L * 24 * 60 * 60 * 1000)
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
                                shape = ShapeCard,
                                border = BorderStroke(1.dp, PinkSoftStrong),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            ) {
                                Row(Modifier.padding(18.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    IconBadge(Icons.Filled.CalendarMonth, size = 40.dp)
                                    Column(Modifier.padding(start = 12.dp)) {
                                        Text("Perkiraan tanggal lahir (HPL)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                        Text(
                                            DateUtils.formatTanggal(hplMillis),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(top = 3.dp),
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        if (state.referenceRows.isNotEmpty()) {
                            SectionHeader("Grafik pertumbuhan")
                            var grafikTinggi by remember { mutableStateOf(false) }
                            val actualPoints = state.growthRecords.mapNotNull { record ->
                                val lahir = profile.tanggalLahirMillis ?: return@mapNotNull null
                                val nilai = (if (grafikTinggi) record.panjangCm else record.beratKg) ?: return@mapNotNull null
                                val usiaBulan = DateUtils.usiaBulanDari(lahir, record.tanggalMillis).toDouble()
                                usiaBulan to nilai
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
                                shape = ShapeCard,
                                border = BorderStroke(1.dp, PinkSoftStrong),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        PilihanGrafikChip("Berat", selected = !grafikTinggi) { grafikTinggi = false }
                                        PilihanGrafikChip("Tinggi", selected = grafikTinggi) { grafikTinggi = true }
                                    }
                                    GrowthChart(
                                        referenceRows = if (grafikTinggi) state.referenceRowsTinggi else state.referenceRows,
                                        actualPoints = actualPoints,
                                        satuan = if (grafikTinggi) "cm" else "kg",
                                        modifier = Modifier.padding(top = 12.dp),
                                    )
                                    GrowthChartLegend(Modifier.padding(top = 10.dp))
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showTambahUkuran = true },
                            shape = ShapeCard,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                            border = BorderStroke(1.dp, PinkPrimary),
                            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Tambah data pengukuran", modifier = Modifier.padding(start = 6.dp))
                        }

                        SectionHeader("Riwayat imunisasi")
                        if (state.completedImmunizations.isEmpty()) {
                            Text(
                                "Belum ada riwayat imunisasi yang tercatat",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                            )
                        } else {
                            state.completedImmunizations.forEach { record ->
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StatusNormalText)
                                    Column(Modifier.padding(start = 10.dp)) {
                                        Text("${record.namaVaksin} · dosis ${record.dosisLabel}", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                        record.tanggalDiberikanMillis?.let {
                                            Text(DateUtils.formatTanggal(it), style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTambahUkuran && state.activeProfile != null) {
        TambahUkuranDialog(
            onDismiss = { showTambahUkuran = false },
            onSimpan = { tanggal, berat, panjang, lingkarKepala ->
                viewModel.tambahUkuran(state.activeProfile!!.id, tanggal, berat, panjang, lingkarKepala)
                showTambahUkuran = false
            },
        )
    }

    if (showTandaiLahir && state.activeProfile != null) {
        TandaiLahirDialog(
            onDismiss = { showTandaiLahir = false },
            onSimpan = { jenisKelamin, tanggalLahir ->
                viewModel.tandaiSudahLahir(state.activeProfile!!.id, jenisKelamin, tanggalLahir)
                showTandaiLahir = false
            },
        )
    }
}

@Composable
private fun PilihanGrafikChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PinkPrimary,
            selectedLabelColor = androidx.compose.ui.graphics.Color.White,
        ),
    )
}

@Composable
private fun ProfileSwitcherRow(
    profiles: List<ProfileEntity>,
    activeId: Long?,
    onSelect: (Long) -> Unit,
    onTambah: () -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(profiles) { profile ->
            FilterChip(
                selected = profile.id == activeId,
                onClick = { onSelect(profile.id) },
                label = { Text(profile.nama) },
                leadingIcon = { Icon(Icons.Filled.ChildCare, contentDescription = null, modifier = Modifier.size(16.dp)) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PinkPrimary,
                    selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                    selectedLeadingIconColor = androidx.compose.ui.graphics.Color.White,
                ),
            )
        }
        item {
            OutlinedButton(
                onClick = onTambah,
                contentPadding = PaddingValues(horizontal = 14.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                border = BorderStroke(1.dp, PinkPrimary),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah profil", modifier = Modifier.size(16.dp))
                Text("Profil baru", modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
private fun TambahUkuranDialog(
    onDismiss: () -> Unit,
    onSimpan: (tanggal: Long, berat: Double?, panjang: Double?, lingkarKepala: Double?) -> Unit,
) {
    var tanggal by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var berat by remember { mutableStateOf("") }
    var panjang by remember { mutableStateOf("") }
    var lingkarKepala by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ShapeCard,
        title = { Text("Tambah data pengukuran") },
        text = {
            Column {
                DatePickerField(label = "Tanggal", selectedMillis = tanggal, onDateSelected = { tanggal = it })
                OutlinedTextField(
                    value = berat, onValueChange = { berat = it },
                    label = { Text("Berat (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                )
                OutlinedTextField(
                    value = panjang, onValueChange = { panjang = it },
                    label = { Text("Panjang/tinggi (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                )
                OutlinedTextField(
                    value = lingkarKepala, onValueChange = { lingkarKepala = it },
                    label = { Text("Lingkar kepala (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = tanggal != null && (berat.isNotBlank() || panjang.isNotBlank() || lingkarKepala.isNotBlank()),
                onClick = {
                    onSimpan(tanggal!!, berat.toDoubleOrNull(), panjang.toDoubleOrNull(), lingkarKepala.toDoubleOrNull())
                },
            ) { Text("Simpan", color = PinkPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    )
}

@Composable
private fun TandaiLahirDialog(onDismiss: () -> Unit, onSimpan: (jenisKelamin: String, tanggalLahir: Long) -> Unit) {
    var jenisKelamin by remember { mutableStateOf<String?>(null) }
    var tanggal by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ShapeCard,
        title = { Text("Tandai sudah lahir") },
        text = {
            Column {
                DatePickerField(label = "Tanggal lahir", selectedMillis = tanggal, onDateSelected = { tanggal = it })
                Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = jenisKelamin == "LAKI_LAKI",
                        onClick = { jenisKelamin = "LAKI_LAKI" },
                        label = { Text("Laki-laki") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = androidx.compose.ui.graphics.Color.White),
                    )
                    FilterChip(
                        selected = jenisKelamin == "PEREMPUAN",
                        onClick = { jenisKelamin = "PEREMPUAN" },
                        label = { Text("Perempuan") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = androidx.compose.ui.graphics.Color.White),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = jenisKelamin != null && tanggal != null,
                onClick = { onSimpan(jenisKelamin!!, tanggal!!) },
            ) { Text("Simpan", color = PinkPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    )
}
