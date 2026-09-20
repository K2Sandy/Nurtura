package com.tumbuhsehat.app.data.repository

import com.tumbuhsehat.app.data.model.KalkulatorGiziHasil
import com.tumbuhsehat.app.data.model.Recipe
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

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
 *
 * Status gizi ikut mempengaruhi hasil (bukan cuma usia):
 *  - Berat/usia Kurang / Sangat kurang  -> target kalori dinaikkan (catch-up growth):
 *    +10% / +20%.
 *  - Tinggi/usia Kurang / Sangat kurang (risiko stunting) -> porsi protein dinaikkan
 *    dari ~12.5% ke ~17% energi.
 *  - Urutan resep: resep yang paling memenuhi target yang dinaikkan tadi naik peringkat.
 *    Kalau kedua status Normal/Lebih, urutan sama persis seperti sebelumnya.
 *
 * Faktor-faktor di atas adalah konsep umum catch-up growth, BUKAN tabel resmi -
 * sama seperti target energi, perlu diverifikasi sebelum dipakai untuk panduan medis.
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

        // 1) Kalori: naik kalau berat/usia di bawah normal (catch-up growth)
        val faktorKalori = faktorKalori(statusBerat)
        val targetKkal = (targetEnergiHarian(usiaBulan) * faktorKalori).roundToInt()

        // 2) Protein: naik kalau tinggi/usia di bawah normal (risiko stunting)
        val proteinPersen = proteinPersenEnergi(statusTinggi)

        // Makro: lemak tetap ~30% energi, protein sesuai di atas, sisanya karbo.
        // (Untuk status normal hasilnya sama dengan sebelumnya: 12.5% / 30% / 57.5%.)
        val proteinGr = (targetKkal * proteinPersen) / 4.0
        val lemakGr = (targetKkal * LEMAK_PERSEN) / 9.0
        val karboGr = (targetKkal * (1.0 - proteinPersen - LEMAK_PERSEN)) / 4.0

        // 3) Rekomendasi menu: urutan mengikuti kebutuhan (kalori/protein) yang dinaikkan
        val kelompok = referenceRepository.kelompokUntukUsia(usiaBulan)
        val semuaResep = referenceRepository.getAllRecipesFlattened()
        val kandidat = semuaResep
            .filter { it.kelompok == kelompok }
            .map { it.recipe }
        val rekomendasi = urutkanRekomendasi(
            resep = kandidat,
            targetKkal = targetKkal,
            proteinPersenTarget = proteinPersen,
            prioritasKalori = faktorKalori > 1.0,
            prioritasProtein = proteinPersen > PROTEIN_PERSEN_DASAR,
        )

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
            faktorKalori = faktorKalori,
            proteinPersenEnergi = proteinPersen,
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

    /** Pengali target kalori berdasarkan status berat/usia (catch-up growth). */
    private fun faktorKalori(statusBerat: StatusGizi): Double = when (statusBerat) {
        StatusGizi.SANGAT_KURANG -> FAKTOR_KALORI_SANGAT_KURANG
        StatusGizi.KURANG -> FAKTOR_KALORI_KURANG
        else -> 1.0
    }

    /** Porsi energi dari protein berdasarkan status tinggi/usia (risiko stunting). */
    private fun proteinPersenEnergi(statusTinggi: StatusGizi): Double = when (statusTinggi) {
        StatusGizi.SANGAT_KURANG, StatusGizi.KURANG -> PROTEIN_PERSEN_KEJAR_TINGGI
        else -> PROTEIN_PERSEN_DASAR
    }

    /**
     * Mengurutkan resep untuk rekomendasi.
     *
     * - Kalau tidak ada yang perlu dikejar (kedua flag false): urutan lama, yaitu resep yang
     *   kalorinya paling dekat dengan kebutuhan per kali makan.
     * - Kalau ada: tiap resep diberi skor "seberapa memenuhi target yang dinaikkan", lalu
     *   diurutkan dari skor tertinggi. Skor sengaja jenuh di angka target (maks 1 per dimensi),
     *   jadi resep dengan nilai jauh di atas target (mis. protein sangat tinggi karena porsi ikan
     *   besar) tidak otomatis selalu jadi nomor 1. Seri diurutkan dengan aturan lama.
     */
    private fun urutkanRekomendasi(
        resep: List<Recipe>,
        targetKkal: Int,
        proteinPersenTarget: Double,
        prioritasKalori: Boolean,
        prioritasProtein: Boolean,
    ): List<Recipe> {
        val kkalPerMakan = targetKkal / MAKAN_PER_HARI // ~4x makan/hari (utama + selingan)

        fun skorKesesuaian(r: Recipe): Double {
            val kkal = r.gizi_per_porsi.energi_kkal
            var skor = 0.0
            if (prioritasKalori && kkalPerMakan > 0) {
                skor += min(kkal.toDouble() / kkalPerMakan, 1.0)
            }
            if (prioritasProtein && kkal > 0) {
                val persenProtein = (r.gizi_per_porsi.protein_gr * 4.0) / kkal
                skor += min(persenProtein / proteinPersenTarget, 1.0)
            }
            return skor
        }

        return resep.sortedWith(
            compareByDescending<Recipe> { skorKesesuaian(it) }
                .thenBy { abs(it.gizi_per_porsi.energi_kkal - kkalPerMakan) },
        )
    }

    private companion object {
        const val MAKAN_PER_HARI = 4
        const val LEMAK_PERSEN = 0.30
        const val PROTEIN_PERSEN_DASAR = 0.125
        const val PROTEIN_PERSEN_KEJAR_TINGGI = 0.17
        const val FAKTOR_KALORI_KURANG = 1.10
        const val FAKTOR_KALORI_SANGAT_KURANG = 1.20
    }
}
