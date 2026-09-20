package com.tumbuhsehat.app.ui.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tumbuhsehat.app.TumbuhSehatApplication
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.di.viewModelFactory
import com.tumbuhsehat.app.ui.components.GreetingHeroCard
import com.tumbuhsehat.app.ui.components.QuickAccessCard
import com.tumbuhsehat.app.ui.components.ReminderCard
import com.tumbuhsehat.app.ui.components.StatusCard
import com.tumbuhsehat.app.ui.theme.PinkPrimaryDark

@Composable
fun BerandaScreen(
    onNavigateToGizi: () -> Unit,
    onNavigateToKalender: () -> Unit,
    onNavigateToProfil: () -> Unit,
) {
    val app = LocalContext.current.applicationContext as TumbuhSehatApplication
    val viewModel: BerandaViewModel = viewModel(
        factory = viewModelFactory { BerandaViewModel(app.container.userRepository) }
    )
    val state by viewModel.uiState.collectAsState()
    val isHamil = state.profile?.tipe == TipeProfil.HAMIL

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        GreetingHeroCard(
            eyebrow = "Halo, Bunda \uD83D\uDC4B",
            title = state.profile?.let { "${it.nama} · ${state.usiaLabel}" } ?: "Selamat datang",
            trailing = {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                        .clickable(onClick = onNavigateToProfil),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "Ganti profil",
                        tint = PinkPrimaryDark,
                    )
                }
            },
        )

        StatusCard(
            title = if (isHamil) "Status kehamilan" else "Status pertumbuhan",
            badgeLabel = state.statusLabel,
            badgeLevel = state.statusLevel,
            icon = if (isHamil) Icons.Filled.Favorite else Icons.Filled.MonitorWeight,
            metrics = if (isHamil) {
                listOf("Usia kandungan" to state.usiaLabel)
            } else {
                listOf("Berat" to state.beratTerakhir, "Tinggi" to state.panjangTerakhir)
            },
        )

        if (state.reminderTitle != null) {
            ReminderCard(
                title = state.reminderTitle.orEmpty(),
                subtitle = state.reminderSubtitle.orEmpty(),
                onClick = onNavigateToKalender,
            )
        } else {
            ReminderCard(
                title = "Belum ada pengingat",
                subtitle = "Atur jadwal di tab Kalender",
                onClick = onNavigateToKalender,
            )
        }

        Text(
            "Akses cepat",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 5.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAccessCard(
                icon = if (isHamil) Icons.Filled.Restaurant else Icons.Filled.ChildCare,
                label = if (isHamil) "Gizi kehamilan" else "Menu MPASI hari ini",
                onClick = onNavigateToGizi,
            )
            QuickAccessCard(
                icon = Icons.Filled.CalendarMonth,
                label = "Lihat kalender",
                onClick = onNavigateToKalender,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}
