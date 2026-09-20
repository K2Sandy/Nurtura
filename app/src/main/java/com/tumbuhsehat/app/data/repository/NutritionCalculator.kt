package com.tumbuhsehat.app.data.repository

import com.tumbuhsehat.app.data.model.KalkulatorGiziHasil

/**
 * "Kalkulator Gizi Anak" - fully local calculation, no network call.
 *
 * Reuses [GrowthReferenceRepository] (already loads the WHO CSVs bundled in
 * assets/who_growth) for the z-score/status math, and [ReferenceRepository]
 * for matching recipe recommendations from the existing resep database - so
 * nothing here duplicates data that's already in the app.
 *
 * Energy targets below are rough age-band estimates (not the full official
 * AKG table per individual age/weight) - good enough to drive the
 * calculator and recommendations, but flagged here same as other
 * approximated figures in this project: verify against the real AKG
 * (Permenkes No. 28/2019) tables before this is relied on for real medical
 * guidance - see README_sumber_data.md.
 */
class NutritionCalculator(
    private val growthReferenceRepository: GrowthReferenceRepository,
    private val referenceRepository: ReferenceRepository,
) {

    suspend fun hitung(
        usiaBulan: Int,
        beratKg: Double,
        tinggiCm: Double,
        jenisKelamin: JenisKelamin,
    ): KalkulatorGiziHasil {
        val beratRows = growthReferenceRepository.getReferenceRows(jenisKelamin, JenisUkuran.BERAT_USIA)
        val beratRef = growthReferenceRepository.interpolate(beratRows, usiaBulan.toDouble())
        val zBerat = beratRef?.let { growthReferenceRepository.hitungZScore(beratKg, it.l, it.m, it.s) } ?: 0.0
        val statusBerat = growthReferenceRepository.statusDariZScore(zBerat)

        val tinggiRows = growthReferenceRepository.getReferenceRows(jenisKelamin, JenisUkuran.PANJANG_USIA)
        val tinggiRef = growthReferenceRepository.interpolate(tinggiRows, usiaBulan.toDouble())
        val zTinggi = tinggiRef?.let { growthReferenceRepository.hitungZScore(tinggiCm, it.l, it.m, it.s) } ?: 0.0
        val statusTinggi = growthReferenceRepository.statusDariZScore(zTinggi)

        val targetKkal = targetEnergiHarian(usiaBulan)
        // Pembagian makro standar anak: ~12.5% energi dari protein, ~30% dari lemak, sisanya karbo.
        val proteinGr = (targetKkal * 0.125) / 4.0
        val lemakGr = (targetKkal * 0.30) / 9.0
        val karboGr = (targetKkal * 0.575) / 4.0

        val kelompok = referenceRepository.kelompokUntukUsia(usiaBulan)
        val semuaResep = referenceRepository.getAllRecipesFlattened()
        val rekomendasi = semuaResep
            .filter { it.kelompok == kelompok }
            .map { it.recipe }
            .sortedBy { kotlin.math.abs(it.gizi_per_porsi.energi_kkal - (targetKkal / 4)) } // ~4x makan/hari

        return KalkulatorGiziHasil(
            statusBerat = statusBerat,
            zScoreBerat = zBerat,
            statusTinggi = statusTinggi,
            zScoreTinggi = zTinggi,
            targetKkal = targetKkal,
            targetProteinGr = proteinGr,
            targetLemakGr = lemakGr,
            targetKarboGr = karboGr,
            rekomendasiMenu = rekomendasi,
        )
    }

    /** Estimasi energi harian (kkal) per kelompok usia - lihat catatan di kelas ini soal akurasi. */
    private fun targetEnergiHarian(usiaBulan: Int): Int = when {
        usiaBulan < 6 -> 550
        usiaBulan in 6..8 -> 750
        usiaBulan in 9..11 -> 900
        usiaBulan in 12..23 -> 1150
        else -> 1350
    }
}
