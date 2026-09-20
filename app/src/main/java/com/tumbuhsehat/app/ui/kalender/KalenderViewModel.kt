package com.tumbuhsehat.app.ui.kalender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumbuhsehat.app.data.local.ImmunizationRecordEntity
import com.tumbuhsehat.app.data.local.ReminderEntity
import com.tumbuhsehat.app.data.local.TipePengingat
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class KalenderUiState(
    val profileId: Long? = null,
    val isAnak: Boolean = false,
    val reminders: List<ReminderEntity> = emptyList(),
    val belumDiberikan: List<ImmunizationRecordEntity> = emptyList(),
    val sudahDiberikan: List<ImmunizationRecordEntity> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class KalenderViewModel(private val userRepository: UserRepository) : ViewModel() {

    val uiState: StateFlow<KalenderUiState> = userRepository.observeActiveProfile()
        .flatMapLatest { profile ->
            if (profile == null) {
                flowOf(KalenderUiState())
            } else {
                combine(
                    userRepository.observeReminders(profile.id),
                    userRepository.observeImmunizationRecords(profile.id),
                ) { reminders, immunizations ->
                    KalenderUiState(
                        profileId = profile.id,
                        isAnak = profile.tipe == TipeProfil.ANAK,
                        reminders = reminders.sortedBy { it.tanggalPengingatMillis },
                        belumDiberikan = immunizations.filter { !it.sudahDiberikan },
                        sudahDiberikan = immunizations.filter { it.sudahDiberikan },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KalenderUiState())

    fun jadwalkanPengingatImunisasi(record: ImmunizationRecordEntity, tanggalMillis: Long) {
        viewModelScope.launch {
            userRepository.buatPengingat(
                profileId = record.profileId,
                judul = "Imunisasi ${record.namaVaksin}",
                deskripsi = "Dosis: ${record.dosisLabel} - target usia ${record.usiaTarget}",
                tanggalMillis = tanggalMillis,
                tipe = TipePengingat.IMUNISASI,
            )
        }
    }

    fun buatPengingat(profileId: Long, judul: String, deskripsi: String?, tanggalMillis: Long) {
        viewModelScope.launch {
            userRepository.buatPengingat(profileId, judul, deskripsi, tanggalMillis, TipePengingat.CHECKUP)
        }
    }

    fun tandaiImunisasiSelesai(record: ImmunizationRecordEntity) {
        viewModelScope.launch { userRepository.tandaiImunisasiSelesai(record, System.currentTimeMillis()) }
    }

    fun tandaiPengingatSelesai(reminder: ReminderEntity) {
        viewModelScope.launch { userRepository.tandaiPengingatSelesai(reminder) }
    }
}
