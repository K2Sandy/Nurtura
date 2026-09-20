package com.tumbuhsehat.app.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    /** Whole months between [tanggalLahirMillis] and [sekarang]. Simple day-based approximation (30.44 days/month). */
    fun usiaBulanDari(tanggalLahirMillis: Long, sekarang: Long = System.currentTimeMillis()): Int {
        val hari = TimeUnit.MILLISECONDS.toDays(sekarang - tanggalLahirMillis)
        return (hari / 30.44).toInt().coerceAtLeast(0)
    }

    /** Weeks pregnant, counted from HPHT (hari pertama haid terakhir). */
    fun usiaKehamilanMingguDari(hphtMillis: Long, sekarang: Long = System.currentTimeMillis()): Int {
        val hari = TimeUnit.MILLISECONDS.toDays(sekarang - hphtMillis)
        return (hari / 7).toInt().coerceAtLeast(0)
    }

    fun trimesterDariMinggu(mingguKehamilan: Int): Int = when {
        mingguKehamilan <= 13 -> 1
        mingguKehamilan <= 27 -> 2
        else -> 3
    }

    private val displayFormat = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))

    fun formatTanggal(millis: Long): String = displayFormat.format(millis)
}
