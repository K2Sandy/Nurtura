package com.tumbuhsehat.app.ui.gizi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.data.model.KalkulatorGiziHasil
import com.tumbuhsehat.app.data.model.MpasiStage
import com.tumbuhsehat.app.data.model.PortionGuide
import com.tumbuhsehat.app.data.model.Recipe
import com.tumbuhsehat.app.data.repository.JenisKelamin
import com.tumbuhsehat.app.data.repository.NutritionCalculator
import com.tumbuhsehat.app.data.repository.ReferenceRepository
import com.tumbuhsehat.app.data.repository.UserRepository
import com.tumbuhsehat.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface KalkulatorUiState {
    data object Idle : KalkulatorUiState
    data object Loading : KalkulatorUiState
    data class Success(val hasil: KalkulatorGiziHasil) : KalkulatorUiState
    data class Error(val pesan: String) : KalkulatorUiState
}

enum class SortMode { NONE, ASCENDING, DESCENDING }

data class GiziUiState(
    val profileId: Long? = null,
    val profileNama: String = "",
    val isPregnancy: Boolean = false,
    val loading: Boolean = true,

    // Kehamilan
    val trimester: Int = 1,
    val portionGuide: PortionGuide? = null,
    val pregnancyRecipesSemua: List<Recipe> = emptyList(),

    // Anak - form kalkulator (prefill dari profil, tetap bisa diedit)
    val usiaBulanInput: String = "",
    val beratInput: String = "",
    val tinggiInput: String = "",
    val jenisKelaminInput: JenisKelamin? = null,
    val kelompokLabel: String = "",
    val mpasiStage: MpasiStage? = null,
    val kalkulator: KalkulatorUiState = KalkulatorUiState.Idle,

    // Filter anggaran - dipakai bersama untuk hasil kalkulator maupun resep ibu hamil
    val filterPanelTerbuka: Boolean = false,
    val sortMode: SortMode = SortMode.NONE,
    val budgetFilterRp: Int? = null,
) {
    val pregnancyRecipes: List<Recipe>
        get() = terapkanFilter(pregnancyRecipesSemua)

    val menuKalkulator: List<Recipe>
        get() = (kalkulator as? KalkulatorUiState.Success)?.hasil?.rekomendasiMenu?.let { terapkanFilter(it) } ?: emptyList()

    private fun terapkanFilter(list: List<Recipe>): List<Recipe> {
        val setelahBudget = budgetFilterRp?.let { b -> list.filter { it.estimasi_biaya_rp <= b } } ?: list
        return when (sortMode) {
            SortMode.ASCENDING -> setelahBudget.sortedBy { it.estimasi_biaya_rp }
            SortMode.DESCENDING -> setelahBudget.sortedByDescending { it.estimasi_biaya_rp }
            SortMode.NONE -> setelahBudget
        }
    }
}

class GiziViewModel(
    private val userRepository: UserRepository,
    private val referenceRepository: ReferenceRepository,
    private val nutritionCalculator: NutritionCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GiziUiState())
    val uiState: StateFlow<GiziUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.observeActiveProfile().collectLatest { profile ->
                if (profile == null) {
                    _uiState.value = GiziUiState(loading = false)
                    return@collectLatest
                }

                if (profile.tipe == TipeProfil.HAMIL) {
                    val minggu = profile.hphtMillis?.let { DateUtils.usiaKehamilanMingguDari(it) } ?: 0
                    val trimester = DateUtils.trimesterDariMinggu(minggu)
                    val db = referenceRepository.getRecipes()
                    _uiState.value = _uiState.value.copy(
                        profileId = profile.id,
                        profileNama = profile.nama,
                        isPregnancy = true,
                        loading = false,
                        trimester = trimester,
                        portionGuide = db.panduan_porsi_ibu_hamil,
                        pregnancyRecipesSemua = db.resep_ibu_hamil.resep,
                    )
                } else {
                    val usiaBulan = profile.tanggalLahirMillis?.let { DateUtils.usiaBulanDari(it) } ?: 0
                    val stages = referenceRepository.getMpasiStages()
                    val kelompok = referenceRepository.kelompokUntukUsia(usiaBulan)
                    val stage = stages.tahapan.firstOrNull { stageMatchesAge(it.usia_bulan, usiaBulan) }
                    val latestUkuran = userRepository.latestGrowthRecord(profile.id)
                    val jk = profile.jenisKelamin?.let { if (it == "LAKI_LAKI") JenisKelamin.LAKI_LAKI else JenisKelamin.PEREMPUAN }

                    _uiState.value = _uiState.value.copy(
                        profileId = profile.id,
                        profileNama = profile.nama,
                        isPregnancy = false,
                        loading = false,
                        kelompokLabel = kelompok.label,
                        mpasiStage = stage,
                        // prefill form dari profil + data pengukuran terakhir - tetap bisa diedit user
                        usiaBulanInput = usiaBulan.toString(),
                        beratInput = latestUkuran?.beratKg?.toString() ?: "",
                        tinggiInput = latestUkuran?.panjangCm?.toString() ?: "",
                        jenisKelaminInput = jk,
                    )
                }
            }
        }
    }

    fun updateUsia(v: String) { _uiState.value = _uiState.value.copy(usiaBulanInput = v.filter { it.isDigit() }) }
    fun updateBerat(v: String) { _uiState.value = _uiState.value.copy(beratInput = v) }
    fun updateTinggi(v: String) { _uiState.value = _uiState.value.copy(tinggiInput = v) }
    fun updateJenisKelamin(jk: JenisKelamin) { _uiState.value = _uiState.value.copy(jenisKelaminInput = jk) }

    fun hitungGizi() {
        val s = _uiState.value
        val usiaBulan = s.usiaBulanInput.toIntOrNull() ?: return
        val berat = s.beratInput.toDoubleOrNull() ?: return
        val tinggi = s.tinggiInput.toDoubleOrNull() ?: return
        val jk = s.jenisKelaminInput ?: return

        _uiState.value = s.copy(kalkulator = KalkulatorUiState.Loading)
        viewModelScope.launch {
            try {
                val hasil = nutritionCalculator.hitung(usiaBulan, berat, tinggi, jk)
                _uiState.value = _uiState.value.copy(kalkulator = KalkulatorUiState.Success(hasil))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    kalkulator = KalkulatorUiState.Error(e.message ?: "Terjadi kesalahan saat menghitung"),
                )
            }
        }
    }

    fun toggleFilterPanel() {
        _uiState.value = _uiState.value.copy(filterPanelTerbuka = !_uiState.value.filterPanelTerbuka)
    }

    fun setSortMode(mode: SortMode) {
        _uiState.value = _uiState.value.copy(sortMode = mode)
    }

    fun setBudgetFilter(rp: Int?) {
        _uiState.value = _uiState.value.copy(budgetFilterRp = rp)
    }

    private fun stageMatchesAge(usiaBulanRange: String, usiaBulan: Int): Boolean {
        val parts = usiaBulanRange.split("-").mapNotNull { it.trim().toIntOrNull() }
        if (parts.size != 2) return false
        return usiaBulan in parts[0]..parts[1]
    }
}
