package com.tumbuhsehat.app.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumbuhsehat.app.data.local.GrowthRecordEntity
import com.tumbuhsehat.app.data.local.ImmunizationRecordEntity
import com.tumbuhsehat.app.data.local.ProfileEntity
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.data.repository.GrowthReferenceRepository
import com.tumbuhsehat.app.data.repository.GrowthReferenceRow
import com.tumbuhsehat.app.data.repository.JenisKelamin
import com.tumbuhsehat.app.data.repository.JenisUkuran
import com.tumbuhsehat.app.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfilUiState(
    val allProfiles: List<ProfileEntity> = emptyList(),
    val activeProfile: ProfileEntity? = null,
    val growthRecords: List<GrowthRecordEntity> = emptyList(),
    val referenceRows: List<GrowthReferenceRow> = emptyList(),
    val completedImmunizations: List<ImmunizationRecordEntity> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilViewModel(
    private val userRepository: UserRepository,
    private val growthReferenceRepository: GrowthReferenceRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfilUiState> = combine(
        userRepository.observeAllProfiles(),
        userRepository.observeActiveProfile().flatMapLatest { profile ->
            if (profile == null) {
                flowOf(Triple<ProfileEntity?, List<GrowthRecordEntity>, List<ImmunizationRecordEntity>>(null, emptyList(), emptyList()))
            } else {
                combine(
                    userRepository.observeGrowthRecords(profile.id),
                    userRepository.observeImmunizationRecords(profile.id),
                ) { growth, immunizations -> Triple(profile, growth, immunizations) }
            }
        },
    ) { allProfiles, (activeProfile, growthRecords, immunizations) ->
        val referenceRows = if (activeProfile?.tipe == TipeProfil.ANAK && activeProfile.jenisKelamin != null) {
            val gender = if (activeProfile.jenisKelamin == "LAKI_LAKI") JenisKelamin.LAKI_LAKI else JenisKelamin.PEREMPUAN
            growthReferenceRepository.getReferenceRows(gender, JenisUkuran.BERAT_USIA)
        } else {
            emptyList()
        }
        ProfilUiState(
            allProfiles = allProfiles,
            activeProfile = activeProfile,
            growthRecords = growthRecords,
            referenceRows = referenceRows,
            completedImmunizations = immunizations.filter { it.sudahDiberikan },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfilUiState())

    fun pindahProfil(id: Long) {
        viewModelScope.launch { userRepository.switchActiveProfile(id) }
    }

    fun tambahUkuran(profileId: Long, tanggalMillis: Long, beratKg: Double?, panjangCm: Double?, lingkarKepalaCm: Double?) {
        viewModelScope.launch {
            userRepository.addGrowthRecord(profileId, tanggalMillis, beratKg, panjangCm, lingkarKepalaCm, null)
        }
    }

    fun tandaiSudahLahir(profileId: Long, jenisKelamin: String, tanggalLahirMillis: Long) {
        viewModelScope.launch { userRepository.tandaiSudahLahir(profileId, jenisKelamin, tanggalLahirMillis) }
    }
}
