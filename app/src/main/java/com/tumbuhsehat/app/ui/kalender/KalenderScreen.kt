package com.tumbuhsehat.app.ui.kalender

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.data.local.ImmunizationRecordEntity
import com.tumbuhsehat.app.data.local.ReminderEntity
import com.tumbuhsehat.app.di.viewModelFactory
import com.tumbuhsehat.app.ui.components.DatePickerField
import com.tumbuhsehat.app.ui.components.EmptyState
import com.tumbuhsehat.app.ui.components.IconBadge
import com.tumbuhsehat.app.ui.components.PageHeader
import com.tumbuhsehat.app.ui.components.SectionHeader
import com.tumbuhsehat.app.ui.theme.PinkPrimary
import com.tumbuhsehat.app.ui.theme.PinkSoft
import com.tumbuhsehat.app.ui.theme.PinkSoftStrong
import com.tumbuhsehat.app.ui.theme.ShapeCard
import com.tumbuhsehat.app.ui.theme.StatusNormalBg
import com.tumbuhsehat.app.ui.theme.StatusNormalText
import com.tumbuhsehat.app.ui.theme.SurfaceNeutral
import com.tumbuhsehat.app.ui.theme.TextMuted
import com.tumbuhsehat.app.ui.theme.TextPrimary
import com.tumbuhsehat.app.ui.theme.TextSecondary
import com.tumbuhsehat.app.util.DateUtils

@Composable
fun KalenderScreen() {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    val viewModel: KalenderViewModel = viewModel(
        factory = viewModelFactory { KalenderViewModel(app.container.userRepository) }
    )
    val state by viewModel.uiState.collectAsState()
    var itemUntukDijadwalkan by remember { mutableStateOf<ImmunizationRecordEntity?>(null) }

    Column(Modifier.fillMaxSize()) {
        PageHeader(Icons.Filled.CalendarMonth, "Kalender")

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)) {
            if (state.reminders.isEmpty() && !state.isAnak) {
                item {
                    EmptyState(
                        icon = Icons.Filled.NotificationsActive,
                        title = "Belum ada pengingat",
                        message = "Nanti kalau kamu jadwalkan checkup atau imunisasi, pengingatnya bakal muncul di sini.",
                    )
                }
            } else {
                item { SectionHeader("Pengingat") }
                if (state.reminders.isEmpty()) {
                    item { EmptyHint("Belum ada pengingat yang dijadwalkan") }
                } else {
                    items(state.reminders) { reminder ->
                        ReminderRow(reminder, onToggleDone = { viewModel.tandaiPengingatSelesai(reminder) })
                    }
                }
            }

            if (state.isAnak) {
                item { SectionHeader("Checklist imunisasi") }
                if (state.belumDiberikan.isEmpty()) {
                    item { EmptyHint("Semua imunisasi wajib sudah tercatat selesai \uD83C\uDF89") }
                } else {
                    items(state.belumDiberikan) { record ->
                        ImmunizationRow(
                            record = record,
                            onJadwalkan = { itemUntukDijadwalkan = record },
                            onTandaiSelesai = { viewModel.tandaiImunisasiSelesai(record) },
                        )
                    }
                }

                item { SectionHeader("Sudah diberikan") }
                if (state.sudahDiberikan.isEmpty()) {
                    item { EmptyHint("Belum ada riwayat imunisasi") }
                } else {
                    items(state.sudahDiberikan) { record -> ImmunizationDoneRow(record) }
                }
            }
            item { androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp)) }
        }
    }

    val selected = itemUntukDijadwalkan
    if (selected != null) {
        var tanggal by remember { mutableStateOf<Long?>(null) }
        AlertDialog(
            onDismissRequest = { itemUntukDijadwalkan = null },
            shape = ShapeCard,
            title = { Text("Jadwalkan pengingat") },
            text = {
                Column {
                    Text(
                        "${selected.namaVaksin} - dosis ${selected.dosisLabel}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Text(
                        "Target: ${selected.usiaTarget}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 12.dp, top = 5.dp),
                    )
                    DatePickerField(
                        label = "Pilih tanggal pengingat",
                        selectedMillis = tanggal,
                        onDateSelected = { tanggal = it },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = tanggal != null,
                    onClick = {
                        viewModel.jadwalkanPengingatImunisasi(selected, tanggal!!)
                        itemUntukDijadwalkan = null
                    },
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { itemUntukDijadwalkan = null }) { Text("Batal") }
            },
        )
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextMuted,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun ReminderRow(reminder: ReminderEntity, onToggleDone: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceNeutral),
        shape = ShapeCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, PinkSoftStrong),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onToggleDone) {
                Icon(
                    if (reminder.sudahSelesai) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (reminder.sudahSelesai) StatusNormalText else TextMuted,
                )
            }
            Column(Modifier.padding(start = 4.dp)) {
                Text(reminder.judul, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(
                    DateUtils.formatTanggal(reminder.tanggalPengingatMillis),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }
        }
    }
}

@Composable
private fun ImmunizationRow(
    record: ImmunizationRecordEntity,
    onJadwalkan: () -> Unit,
    onTandaiSelesai: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        shape = ShapeCard,
        color = SurfaceNeutral,
        border = androidx.compose.foundation.BorderStroke(1.dp, PinkSoftStrong),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            IconBadge(Icons.Filled.Vaccines, size = 38.dp)
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(record.namaVaksin, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(
                    "Dosis ${record.dosisLabel} · target ${record.usiaTarget}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onJadwalkan, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Jadwalkan", style = MaterialTheme.typography.labelLarge)
                    }
                    TextButton(onClick = onTandaiSelesai, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Tandai selesai", color = PinkPrimary, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ImmunizationDoneRow(record: ImmunizationRecordEntity) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StatusNormalText)
        Column(Modifier.padding(start = 10.dp)) {
            Text(record.namaVaksin, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(
                "Dosis ${record.dosisLabel}" + (record.tanggalDiberikanMillis?.let { " · ${DateUtils.formatTanggal(it)}" } ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
        }
    }
}
