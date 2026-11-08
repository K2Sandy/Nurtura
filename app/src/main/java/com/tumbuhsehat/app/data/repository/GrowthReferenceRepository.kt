package com.tumbuhsehat.app.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow

enum class JenisKelamin { LAKI_LAKI, PEREMPUAN }

enum class JenisUkuran(val labelSingkat: String) {
    BERAT_USIA("Berat/usia"),
    PANJANG_USIA("Panjang/usia"),
    BERAT_PANJANG("Berat/panjang"),
    LINGKAR_KEPALA_USIA("Lingkar kepala/usia"),
}

/** One row of the WHO LMS reference table: L, M, S at a given age (months) or length (cm). */
data class GrowthReferenceRow(
    val x: Double,
    val l: Double,
    val m: Double,
    val s: Double,
    /** ~2.3rd percentile (WHO's "-2SD" line, what the app shows as the lower edge of the normal band). */
    val pLower: Double,
    val pMedian: Double,
    /** ~97.7th percentile (WHO's "+2SD" line, upper edge of the normal band). */
    val pUpper: Double,
)

enum class StatusGizi(val label: String) {
    SANGAT_KURANG("Sangat kurang"),
    KURANG("Kurang"),
    NORMAL("Normal"),
    LEBIH("Lebih"),
}

/**
 * Reads the WHO Child Growth Standards CSVs (bundled in assets/who_growth)
 * and provides z-score / status calculations.
 *
 * Coverage: 0-60 months. Rows 0-24 are the CDC mirror of the WHO 0-2 y
 * tables (recumbent length); rows 25-60 come from the official WHO 2-5 y
 * LMS tables (weight-for-age, head circumference, and standing
 * height-for-age for the length/height file). Percentile columns for the
 * added rows are derived from L, M, S the same way as the original rows.
 *
 * NOTE on classification thresholds: this uses a simplified, unified
 * <-3 / -3..-2 / -2..2 / >2 SD banding across all four indicators for the
 * MVP. Official Indonesian practice (Buku KIA / Kemenkes) has some
 * indicator-specific nuance - review against the primary sources in
 * README_sumber_data.md before this is used to give real guidance to users.
 */
class GrowthReferenceRepository(private val context: Context) {

    private val cache = mutableMapOf<String, List<GrowthReferenceRow>>()

    private fun assetPath(jenisKelamin: JenisKelamin, jenisUkuran: JenisUkuran): String {
        val gender = if (jenisKelamin == JenisKelamin.LAKI_LAKI) "boys" else "girls"
        val metric = when (jenisUkuran) {
            JenisUkuran.BERAT_USIA -> "weight_for_age"
            JenisUkuran.PANJANG_USIA -> "length_for_age"
            JenisUkuran.BERAT_PANJANG -> "weight_for_length"
            JenisUkuran.LINGKAR_KEPALA_USIA -> "head_circumference"
        }
        return "who_growth/${gender}_$metric.csv"
    }

    suspend fun getReferenceRows(
        jenisKelamin: JenisKelamin,
        jenisUkuran: JenisUkuran,
    ): List<GrowthReferenceRow> = withContext(Dispatchers.IO) {
        val path = assetPath(jenisKelamin, jenisUkuran)
        cache[path] ?: parseCsv(path).also { cache[path] = it }
    }

    private fun parseCsv(path: String): List<GrowthReferenceRow> {
        val text = context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val lines = text.lines().map { it.trim('\uFEFF', '\r', '\n', ' ') }.filter { it.isNotBlank() }
        // Header: Month/Length, L, M, S, 2nd(2.3rd), 5th, 10th, 25th, 50th, 75th, 90th, 95th, 98th(97.7th)
        return lines.drop(1).map { line ->
            val cols = line.split(",").map { it.trim() }
            GrowthReferenceRow(
                x = cols[0].toDouble(),
                l = cols[1].toDouble(),
                m = cols[2].toDouble(),
                s = cols[3].toDouble(),
                pLower = cols[4].toDouble(),
                pMedian = cols[8].toDouble(),
                pUpper = cols[12].toDouble(),
            )
        }
    }

    /** Linearly interpolates the reference row for an arbitrary x (age in months, or length in cm). */
    fun interpolate(rows: List<GrowthReferenceRow>, x: Double): GrowthReferenceRow? {
        if (rows.isEmpty()) return null
        val sorted = rows.sortedBy { it.x }
        if (x <= sorted.first().x) return sorted.first()
        if (x >= sorted.last().x) return sorted.last()
        val upperIdx = sorted.indexOfFirst { it.x >= x }
        val lower = sorted[upperIdx - 1]
        val upper = sorted[upperIdx]
        val span = upper.x - lower.x
        val t = if (span == 0.0) 0.0 else (x - lower.x) / span
        fun lerp(a: Double, b: Double) = a + (b - a) * t
        return GrowthReferenceRow(
            x = x,
            l = lerp(lower.l, upper.l),
            m = lerp(lower.m, upper.m),
            s = lerp(lower.s, upper.s),
            pLower = lerp(lower.pLower, upper.pLower),
            pMedian = lerp(lower.pMedian, upper.pMedian),
            pUpper = lerp(lower.pUpper, upper.pUpper),
        )
    }

    /** WHO LMS formula: converts a measured value into a z-score given L, M, S at that age/length. */
    fun hitungZScore(nilaiTerukur: Double, l: Double, m: Double, s: Double): Double {
        return if (l != 0.0) {
            (( (nilaiTerukur / m).pow(l) ) - 1.0) / (l * s)
        } else {
            ln(nilaiTerukur / m) / s
        }
    }

    /** Converts a z-score back into the equivalent measurement value - useful for drawing curves. */
    fun zScoreKeNilai(z: Double, l: Double, m: Double, s: Double): Double {
        return if (l != 0.0) {
            m * (1 + l * s * z).pow(1.0 / l)
        } else {
            m * exp(s * z)
        }
    }

    fun statusDariZScore(z: Double): StatusGizi = when {
        z < -3.0 -> StatusGizi.SANGAT_KURANG
        z < -2.0 -> StatusGizi.KURANG
        z <= 2.0 -> StatusGizi.NORMAL
        else -> StatusGizi.LEBIH
    }
}
