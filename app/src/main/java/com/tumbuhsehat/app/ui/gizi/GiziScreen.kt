package com.tumbuhsehat.app.ui.gizi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.data.model.KalkulatorGiziHasil
import com.tumbuhsehat.app.data.model.Recipe
import com.tumbuhsehat.app.data.repository.JenisKelamin
import com.tumbuhsehat.app.data.repository.StatusGizi
import com.tumbuhsehat.app.di.viewModelFactory
import com.tumbuhsehat.app.ui.components.IconBadge
import com.tumbuhsehat.app.ui.components.PageHeader
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.PinkPrimaryDark
import com.tumbuhsehat.app.ui.theme.PinkSoft
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.ShapeCard
import com.tumbuhsehat.app.ui.theme.StatusAlertBg
import com.tumbuhsehat.app.ui.theme.StatusAlertText
import com.tumbuhsehat.app.ui.theme.StatusNormalBg
import com.tumbuhsehat.app.ui.theme.StatusNormalText
import com.tumbuhsehat.app.ui.theme.StatusWarnBg
import com.tumbuhsehat.app.ui.theme.StatusWarnText
import com.tumbuhsehat.app.ui.theme.SurfaceNeutral
import com.tumbuhsehat.app.ui.theme.TextMuted
import com.tumbuhsehat.app.ui.theme.TextPrimary
import com.tumbuhsehat.app.ui.theme.TextSecondary

@Composable
fun GiziScreen() {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    val viewModel: GiziViewModel = viewModel(
        factory = viewModelFactory {
            GiziViewModel(app.container.userRepository, app.container.reference, app.container.nutritionCalculator)
        }
    )
    val state by viewModel.uiState.collectAsState()

    val headerIcon = if (state.isPregnancy) Icons.Filled.Favorite else Icons.Filled.Calculate
    val headerTitle = if (state.isPregnancy) "Gizi" else "Kalkulator Gizi Anak"

    Column(Modifier.fillMaxSize()) {
        if (state.loading) {
            PageHeader(icon = headerIcon, title = headerTitle)
            Text(
                "Memuat...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            return@Column
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header ikut scroll (collapse on scroll) - bar KawalTumbuh di atas tetap diam
            item { PageHeader(icon = headerIcon, title = headerTitle, bottomPadding = 8.dp) }

            if (state.isPregnancy) {
                state.portionGuide?.let { guide ->
                    item { PortionGuideCard(trimester = state.trimester, guide = guide) }
                }
                item {
                    FilterAnggaranSection(
                        terbuka = state.filterPanelTerbuka,
                        sortMode = state.sortMode,
                        budget = state.budgetFilterRp,
                        jumlahHasil = state.pregnancyRecipes.size,
                        jumlahTotal = state.pregnancyRecipesSemua.size,
                        onToggle = { viewModel.toggleFilterPanel() },
                        onSortChange = { viewModel.setSortMode(it) },
                        onBudgetApply = { viewModel.setBudgetFilter(it) },
                    )
                }
                items(state.pregnancyRecipes) { recipe -> RecipeCard(recipe) }
            } else {
                item {
                    InputKalkulatorCard(
                        usia = state.usiaBulanInput, onUsiaChange = viewModel::updateUsia,
                        berat = state.beratInput, onBeratChange = viewModel::updateBerat,
                        tinggi = state.tinggiInput, onTinggiChange = viewModel::updateTinggi,
                        jenisKelamin = state.jenisKelaminInput, onJenisKelaminChange = viewModel::updateJenisKelamin,
                        onHitung = { viewModel.hitungGizi() },
                    )
                }

                when (val k = state.kalkulator) {
                    is KalkulatorUiState.Idle -> item {
                        Text(
                            "Isi data di atas lalu tekan \"Hitung\" untuk lihat status gizi & rekomendasi menu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }
                    is KalkulatorUiState.Loading -> item {
                        Row(Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(color = PinkPrimary)
                        }
                    }
                    is KalkulatorUiState.Error -> item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = StatusAlertBg),
                            shape = ShapeCard,
                        ) {
                            Text(k.pesan, style = MaterialTheme.typography.bodyMedium, color = StatusAlertText, modifier = Modifier.padding(16.dp))
                        }
                    }
                    is KalkulatorUiState.Success -> {
                        item { HasilStatusCard(k.hasil) }
                        item { TargetGiziCard(k.hasil) }
                        item {
                            FilterAnggaranSection(
                                terbuka = state.filterPanelTerbuka,
                                sortMode = state.sortMode,
                                budget = state.budgetFilterRp,
                                jumlahHasil = state.menuKalkulator.size,
                                jumlahTotal = k.hasil.rekomendasiMenu.size,
                                onToggle = { viewModel.toggleFilterPanel() },
                                onSortChange = { viewModel.setSortMode(it) },
                                onBudgetApply = { viewModel.setBudgetFilter(it) },
                            )
                        }
                        if (state.menuKalkulator.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada menu yang cocok dengan filter ini.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted,
                                )
                            }
                        } else {
                            items(state.menuKalkulator) { recipe -> RecipeCard(recipe) }
                        }
                    }
                }
            }
            item { Spacer(Modifier.padding(bottom = 12.dp)) }
        }
    }
}

@Composable
private fun InputKalkulatorCard(
    usia: String, onUsiaChange: (String) -> Unit,
    berat: String, onBeratChange: (String) -> Unit,
    tinggi: String, onTinggiChange: (String) -> Unit,
    jenisKelamin: JenisKelamin?, onJenisKelaminChange: (JenisKelamin) -> Unit,
    onHitung: () -> Unit,
) {
    val siap = usia.toIntOrNull() != null && berat.toDoubleOrNull() != null &&
        tinggi.toDoubleOrNull() != null && jenisKelamin != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Filled.ChildCare, size = 34.dp)
                Column(Modifier.padding(start = 10.dp)) {
                    Text("Data anak", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    Text(
                        "Terisi otomatis dari profil - bisa diubah manual",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }

            OutlinedTextField(
                value = usia, onValueChange = onUsiaChange,
                label = { Text("Usia (bulan)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            )
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = berat, onValueChange = onBeratChange,
                    label = { Text("Berat (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = tinggi, onValueChange = onTinggiChange,
                    label = { Text("Tinggi (cm)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                "Jenis kelamin",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = jenisKelamin == JenisKelamin.LAKI_LAKI,
                    onClick = { onJenisKelaminChange(JenisKelamin.LAKI_LAKI) },
                    label = { Text("Laki-laki") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                )
                FilterChip(
                    selected = jenisKelamin == JenisKelamin.PEREMPUAN,
                    onClick = { onJenisKelaminChange(JenisKelamin.PEREMPUAN) },
                    label = { Text("Perempuan") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                )
            }
            androidx.compose.material3.Button(
                onClick = onHitung,
                enabled = siap,
                shape = ShapeCard,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            ) { Text("Hitung", color = Color.White) }
        }
    }
}

private data class BadgeColors(val bg: Color, val fg: Color, val label: String)

private fun StatusGizi.toBadgeColors(): BadgeColors = when (this) {
    StatusGizi.NORMAL -> BadgeColors(StatusNormalBg, StatusNormalText, label)
    StatusGizi.KURANG, StatusGizi.LEBIH -> BadgeColors(StatusWarnBg, StatusWarnText, label)
    StatusGizi.SANGAT_KURANG -> BadgeColors(StatusAlertBg, StatusAlertText, label)
}

@Composable
private fun HasilStatusCard(hasil: KalkulatorGiziHasil) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Status gizi", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            StatusRow("Berat/usia", hasil.statusBerat)
            StatusRow("Tinggi/usia (risiko stunting)", hasil.statusTinggi)
        }
    }
}

@Composable
private fun StatusRow(label: String, status: StatusGizi) {
    val badge = status.toBadgeColors()
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(end = 10.dp))
        Text(
            badge.label,
            style = MaterialTheme.typography.labelLarge,
            color = badge.fg,
            modifier = Modifier.background(badge.bg, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun TargetGiziCard(hasil: KalkulatorGiziHasil) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Target gizi harian (estimasi)", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                MacroValue("Energi", "${hasil.targetKkal}", "kkal")
                MacroValue("Protein", "%.0f".format(hasil.targetProteinGr), "gr")
                MacroValue("Lemak", "%.0f".format(hasil.targetLemakGr), "gr")
                MacroValue("Karbo", "%.0f".format(hasil.targetKarboGr), "gr")
            }
            val kaloriDinaikkan = hasil.faktorKalori > 1.0
            val proteinDinaikkan = hasil.proteinPersenEnergi > 0.125
            if (kaloriDinaikkan || proteinDinaikkan) {
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (kaloriDinaikkan) {
                        Text(
                            "Energi dinaikkan ${"%.0f".format((hasil.faktorKalori - 1.0) * 100)}% karena berat/usia di bawah normal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    if (proteinDinaikkan) {
                        Text(
                            "Porsi protein dinaikkan (~${"%.0f".format(hasil.proteinPersenEnergi * 100)}% energi) karena tinggi/usia di bawah normal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Text(
                        "Menu di bawah diurutkan sesuai kebutuhan ini. Sebaiknya konsultasikan hasilnya ke tenaga kesehatan atau posyandu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
            Text(
                "Mengacu AKG (Permenkes No. 28 Tahun 2019) per kelompok usia",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun MacroValue(label: String, value: String, satuan: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.padding(top = 2.dp))
        Text(satuan, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

/** Panel filter anggaran: urutkan termurah/termahal dan/atau batasi nominal - dipakai di resep hamil & hasil kalkulator anak. */
@Composable
private fun FilterAnggaranSection(
    terbuka: Boolean,
    sortMode: SortMode,
    budget: Int?,
    jumlahHasil: Int,
    jumlahTotal: Int,
    onToggle: () -> Unit,
    onSortChange: (SortMode) -> Unit,
    onBudgetApply: (Int?) -> Unit,
) {
    var teksBudget by remember(budget) { mutableStateOf(budget?.toString() ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth().clickable(onClick = onToggle),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBadge(Icons.Filled.Wallet, size = 34.dp)
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text("Filter anggaran", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    val ringkasan = buildString {
                        if (sortMode != SortMode.NONE) append(if (sortMode == SortMode.ASCENDING) "Termurah dulu" else "Termahal dulu")
                        if (budget != null) {
                            if (isNotEmpty()) append(" · ")
                            append("maks Rp ${"%,d".format(budget).replace(',', '.')}")
                        }
                        if (isEmpty()) append("Urutkan atau batasi nominal")
                    }
                    Text(ringkasan, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                }
                Icon(
                    if (terbuka) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = PinkPrimaryDark,
                )
            }

            AnimatedVisibility(visible = terbuka) {
                Column(Modifier.padding(top = 14.dp)) {
                    Text("Urutkan", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = sortMode == SortMode.ASCENDING,
                            onClick = { onSortChange(if (sortMode == SortMode.ASCENDING) SortMode.NONE else SortMode.ASCENDING) },
                            label = { Text("Termurah dulu") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                        )
                        FilterChip(
                            selected = sortMode == SortMode.DESCENDING,
                            onClick = { onSortChange(if (sortMode == SortMode.DESCENDING) SortMode.NONE else SortMode.DESCENDING) },
                            label = { Text("Termahal dulu") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, selectedLabelColor = Color.White),
                        )
                    }

                    Text("Batas anggaran per porsi", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(top = 16.dp))
                    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Rp", style = MaterialTheme.typography.bodyLarge, color = TextSecondary, modifier = Modifier.padding(end = 6.dp))
                        OutlinedTextField(
                            value = teksBudget,
                            onValueChange = { teksBudget = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("Contoh: 3000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { onBudgetApply(teksBudget.toIntOrNull()) }, modifier = Modifier.padding(start = 4.dp)) {
                            Text("Terapkan", color = PinkPrimary)
                        }
                    }
                    if (budget != null) {
                        TextButton(
                            onClick = { teksBudget = ""; onBudgetApply(null) },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.padding(top = 4.dp),
                        ) { Text("Hapus batas anggaran", style = MaterialTheme.typography.labelSmall, color = TextMuted) }
                    }

                    Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StatusNormalText, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            "Menampilkan $jumlahHasil dari $jumlahTotal menu",
                            style = MaterialTheme.typography.labelLarge,
                            color = StatusNormalText,
                        )
                    }
                }
            }
        }
    }
}

/** Satu kelompok makanan di panduan porsi: emoji + warna aksen supaya tiap baris mudah dikenali sekilas. */
private data class KelompokPorsi(
    val emoji: String,
    val label: String,
    val porsi: String,
    val satuan: String,
    val bg: Color,
    val accent: Color,
)

/** "6 porsi/hari" -> ("6", "porsi/hari"); "8-12 gelas/hari" -> ("8-12", "gelas/hari"). */
private fun pisahAngka(teks: String): Pair<String, String> {
    val m = Regex("^\\s*(\\d+(?:\\s*[-–]\\s*\\d+)?)\\s*(.*)$").find(teks) ?: return "" to teks
    return m.groupValues[1].replace(" ", "") to m.groupValues[2]
}

@Composable
private fun PortionGuideCard(trimester: Int, guide: com.tumbuhsehat.app.data.model.PortionGuide) {
    fun pilihPorsi(fp: com.tumbuhsehat.app.data.model.FoodPortion): String =
        fp.semua_trimester
            ?: if (trimester == 1) fp.trimester_1 ?: "-" else fp.trimester_2_3 ?: "-"

    val kelompok = listOf(
        KelompokPorsi("🍚", "Makanan pokok", pilihPorsi(guide.makanan_pokok), guide.makanan_pokok.satuan_porsi, Color(0xFFFFF4E0), Color(0xFFD9922A)),
        KelompokPorsi("🥦", "Sayur", pilihPorsi(guide.sayur), guide.sayur.satuan_porsi, Color(0xFFE7F4DE), Color(0xFF4E9E6C)),
        KelompokPorsi("🍊", "Buah", pilihPorsi(guide.buah), guide.buah.satuan_porsi, Color(0xFFFFECDD), Color(0xFFE67A2E)),
        KelompokPorsi("🐟", "Protein hewani", pilihPorsi(guide.protein_hewani), guide.protein_hewani.satuan_porsi, Color(0xFFFDE7EE), PinkPrimary),
        KelompokPorsi("🥜", "Protein nabati", pilihPorsi(guide.protein_nabati), guide.protein_nabati.satuan_porsi, Color(0xFFF1ECE2), Color(0xFF9C7A52)),
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Filled.Restaurant, size = 38.dp)
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text("Panduan porsi harian", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(
                        "Anjuran Kemenkes RI untuk ibu hamil",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Text(
                    "Trimester $trimester",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(PinkPrimary, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                kelompok.forEach { KelompokPorsiRow(it) }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 12.dp).height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SuplemenTile("💊", "Tablet tambah darah", guide.tablet_tambah_darah, Modifier.weight(1f).fillMaxHeight())
                SuplemenTile("💧", "Air putih", guide.air_putih, Modifier.weight(1f).fillMaxHeight())
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(PinkSoft, RoundedCornerShape(16.dp))
                    .padding(14.dp),
            ) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = PinkPrimaryDark, modifier = Modifier.size(18.dp))
                Text(
                    guide.catatan_lain.replaceFirstChar { it.uppercase() }.trimEnd('.') + ".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PinkPrimaryDark,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun KelompokPorsiRow(k: KelompokPorsi) {
    val (angka, unit) = pisahAngka(k.porsi)
    val jumlah = angka.toIntOrNull()

    Row(
        Modifier
            .fillMaxWidth()
            .background(k.bg, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(42.dp).background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(k.emoji, fontSize = 22.sp)
        }
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(k.label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(
                k.satuan,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp),
            )
            if (jumlah != null) {
                // Satu titik per porsi - bantu ibu "menghitung" porsi hariannya sekilas
                Row(Modifier.padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(jumlah.coerceAtMost(8)) {
                        Box(Modifier.size(7.dp).background(k.accent, CircleShape))
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 10.dp),
        ) {
            Text(angka.ifEmpty { k.porsi }, style = MaterialTheme.typography.headlineSmall, color = k.accent)
            if (angka.isNotEmpty()) {
                Text(unit, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }
    }
}

@Composable
private fun SuplemenTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    val (angka, unit) = pisahAngka(value)
    Column(
        modifier
            .border(1.dp, PinkSoftStrong, RoundedCornerShape(16.dp))
            .padding(12.dp),
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(label, style = MaterialTheme.typography.labelLarge, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 2.dp)) {
            Text(angka.ifEmpty { value }, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            if (angka.isNotEmpty()) {
                Text(
                    unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 4.dp, bottom = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Filled.Restaurant, size = 38.dp)
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(recipe.nama, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                    recipe.diadaptasi_dari?.let {
                        Text(
                            "Variasi dari: $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                    Text(
                        "${recipe.gizi_per_porsi.energi_kkal} kkal · ${recipe.gizi_per_porsi.protein_gr}g protein · ${recipe.gizi_per_porsi.lemak_gr}g lemak",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                    if (recipe.estimasi_biaya_rp > 0) {
                        Text(
                            "≈ Rp ${"%,d".format(recipe.estimasi_biaya_rp).replace(',', '.')} / porsi",
                            style = MaterialTheme.typography.labelSmall,
                            color = PinkPrimaryDark,
                            modifier = Modifier.padding(top = 5.dp),
                        )
                    }
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = PinkPrimaryDark,
                )
            }

            if (expanded) {
                if (recipe.bahan.isNotEmpty()) {
                    Text(
                        "Bahan",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                    recipe.bahan.forEach {
                        Text("• $it", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
                    }
                }
                if (recipe.cara_buat.isNotEmpty()) {
                    Text(
                        "Cara buat",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                    recipe.cara_buat.forEachIndexed { i, step ->
                        Text("${i + 1}. $step", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }
                if (recipe.bahan.isEmpty() && recipe.cara_buat.isEmpty()) {
                    Text(
                        "Detail bahan & cara buat belum tersedia di data ringkas ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }
    }
}
