package com.tumbuhsehat.app.ui.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumbuhsehat.app.data.local.ProfileEntity
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.data.repository.StatusGizi
import com.tumbuhsehat.app.data.repository.UserRepository
import com.tumbuhsehat.app.ui.components.StatusBadgeLevel
import com.tumbuhsehat.app.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BerandaUiState(
    val profile: ProfileEntity? = null,
    val usiaLabel: String = "",
    val statusLabel: String = "Normal",
    val statusLevel: StatusBadgeLevel = StatusBadgeLevel.NORMAL,
    val beratTerakhir: String = "-",
    val panjangTerakhir: String = "-",
    val reminderTitle: String? = null,
    val reminderSubtitle: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BerandaViewModel(private val userRepository: UserRepository) : ViewModel() {

    val uiState: StateFlow<BerandaUiState> = userRepository.observeActiveProfile()
        .flatMapLatest { profile ->
            if (profile == null) {
                flowOf(BerandaUiState())
            } else {
                combine(
                    userRepository.observeGrowthRecords(profile.id),
                    userRepository.observeNextReminder(profile.id),
                ) { growthRecords, nextReminder ->
                    val latest = growthRecords.maxByOrNull { it.tanggalMillis }
                    var statusLabel = "Belum ada data"
                    var statusLevel = StatusBadgeLevel.NORMAL
                    var usiaLabel = ""

                    if (profile.tipe == TipeProfil.ANAK) {
                        val usiaBulan = profile.tanggalLahirMillis?.let { DateUtils.usiaBulanDari(it) } ?: 0
                        usiaLabel = "$usiaBulan bulan"
                        if (latest?.beratKg != null) {
                            val status = userRepository.hitungStatusBerat(profile, usiaBulan, latest.beratKg)
                            statusLabel = status.label
                            statusLevel = when (status) {
                                StatusGizi.NORMAL -> StatusBadgeLevel.NORMAL
                                StatusGizi.KURANG, StatusGizi.LEBIH -> StatusBadgeLevel.WASPADA
                                StatusGizi.SANGAT_KURANG -> StatusBadgeLevel.PERHATIAN
                            }
                        }
                    } else {
                        val minggu = profile.hphtMillis?.let { DateUtils.usiaKehamilanMingguDari(it) } ?: 0
                        usiaLabel = "$minggu minggu (trimester ${DateUtils.trimesterDariMinggu(minggu)})"
                        statusLabel = "Trimester ${DateUtils.trimesterDariMinggu(minggu)}"
                        statusLevel = StatusBadgeLevel.NORMAL
                    }

                    BerandaUiState(
                        profile = profile,
                        usiaLabel = usiaLabel,
                        statusLabel = statusLabel,
                        statusLevel = statusLevel,
                        beratTerakhir = latest?.beratKg?.let { "$it kg" } ?: "-",
                        panjangTerakhir = latest?.panjangCm?.let { "$it cm" } ?: "-",
                        reminderTitle = nextReminder?.judul,
                        reminderSubtitle = nextReminder?.let { DateUtils.formatTanggal(it.tanggalPengingatMillis) },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BerandaUiState())
}
