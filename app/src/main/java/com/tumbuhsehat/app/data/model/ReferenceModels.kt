package com.tumbuhsehat.app.data.model

import kotlinx.serialization.Serializable

/*
 * These data classes intentionally mirror the snake_case keys used in the
 * bundled JSON files (assets/data/.json) 1:1, so no @SerialName mapping is
 * needed. The JSON files themselves are the ones produced during the
 * research/data-gathering phase of this project - see README_sumber_data.md
 * for provenance and caveats (some values still need verification against
 * primary sources before this ships to real users).
 */

// ---------------------------------------------------------------------
// jadwal_imunisasi.json
// ---------------------------------------------------------------------

@Serializable
data class ImmunizationDatabase(
    val sumber: List<String>,
    val disclaimer: String,
    val vaksin: List<VaccineSchedule>,
)

@Serializable
data class VaccineSchedule(
    val nama: String,
    val kategori: String, // "wajib_nasional" | "opsional_endemis" | "opsional"
    val jadwal: List<VaccineDose>,
    val catatan: String? = null,
)

@Serializable
data class VaccineDose(
    val dosis: String,
    val usia: String,
)

// ---------------------------------------------------------------------
// tahapan_mpasi.json
// ---------------------------------------------------------------------

@Serializable
data class MpasiStagesDatabase(
    val sumber: List<String>,
    val empat_syarat_mpasi_kemenkes_2024: List<String>,
    val tahapan: List<MpasiStage>,
    val catatan_pengembangan: String,
)

@Serializable
data class MpasiStage(
    val usia_bulan: String,
    val tekstur: String,
    val frekuensi_menu_utama: String,
    val frekuensi_selingan: String,
    val takaran: String,
)

// ---------------------------------------------------------------------
// resep_makanan.json
// ---------------------------------------------------------------------

@Serializable
data class ResepDatabase(
    val sumber: String,
    val catatan: String,
    val panduan_porsi_ibu_hamil: PortionGuide,
    val resep_mpasi_6_8_bulan: MpasiRecipeGroup,
    val resep_mpasi_9_11_bulan: MpasiRecipeGroup,
    val resep_mpasi_12_23_bulan: MpasiRecipeGroup,
    val resep_2_5_tahun: ToddlerRecipeGroup,
    val resep_ibu_hamil: PregnancyRecipeGroup,
)

@Serializable
data class PortionGuide(
    val sumber_tambahan: String,
    val makanan_pokok: FoodPortion,
    val sayur: FoodPortion,
    val buah: FoodPortion,
    val protein_hewani: FoodPortion,
    val protein_nabati: FoodPortion,
    val tablet_tambah_darah: String,
    val air_putih: String,
    val catatan_lain: String,
)

@Serializable
data class FoodPortion(
    val trimester_1: String? = null,
    val trimester_2_3: String? = null,
    val semua_trimester: String? = null,
    val satuan_porsi: String,
)

@Serializable
data class MpasiRecipeGroup(
    val kebutuhan_energi_tambahan: String,
    val resep: List<Recipe>,
)

@Serializable
data class ToddlerRecipeGroup(
    val catatan: String,
    val prinsip: List<String>,
    val resep: List<Recipe>,
)

@Serializable
data class PregnancyRecipeGroup(
    val catatan: String,
    val resep: List<Recipe>,
)

@Serializable
data class Recipe(
    val nama: String,
    val porsi_resep: Int,
    val bahan: List<String> = emptyList(),
    val cara_buat: List<String> = emptyList(),
    val gizi_per_porsi: GiziPerPorsi,
    val estimasi_biaya_rp: Int = 0,
)

@Serializable
data class GiziPerPorsi(
    val energi_kkal: Int,
    val protein_gr: Double,
    val lemak_gr: Double,
    val catatan: String? = null,
)

/** Which age group a recipe collection belongs to - used to filter/display in the UI. */
enum class KelompokUsiaResep(val label: String) {
    MPASI_6_8("6-8 bulan"),
    MPASI_9_11("9-11 bulan"),
    MPASI_12_23("12-23 bulan"),
    TODDLER_2_5("2-5 tahun"),
    IBU_HAMIL("Ibu hamil"),
}

data class RecipeWithGroup(val recipe: Recipe, val kelompok: KelompokUsiaResep)

// ---------------------------------------------------------------------
// Kalkulator Gizi Anak - input & hasil (dihitung lokal di HP, lihat
// NutritionCalculator.kt - tidak ada server/backend di project ini)
// ---------------------------------------------------------------------

data class KalkulatorGiziInput(
    val usiaBulan: Int,
    val beratKg: Double,
    val tinggiCm: Double,
    val jenisKelamin: com.tumbuhsehat.app.data.repository.JenisKelamin,
)

data class KalkulatorGiziHasil(
    val statusBerat: com.tumbuhsehat.app.data.repository.StatusGizi,
    val zScoreBerat: Double,
    val statusTinggi: com.tumbuhsehat.app.data.repository.StatusGizi,
    val zScoreTinggi: Double,
    val targetKkal: Int,
    val targetProteinGr: Double,
    val targetLemakGr: Double,
    val targetKarboGr: Double,
    val rekomendasiMenu: List<Recipe>,
    /** Pengali target kalori dari status berat/usia: 1.0 = normal, 1.1 / 1.2 = dinaikkan (catch-up growth). */
    val faktorKalori: Double = 1.0,
    /** Porsi energi dari protein (0.125 = standar, ~0.17 = dinaikkan karena tinggi/usia di bawah normal). */
    val proteinPersenEnergi: Double = 0.125,
)
