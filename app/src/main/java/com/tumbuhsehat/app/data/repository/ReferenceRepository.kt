package com.tumbuhsehat.app.data.repository

import android.content.Context
import com.tumbuhsehat.app.data.model.ImmunizationDatabase
import com.tumbuhsehat.app.data.model.KelompokUsiaResep
import com.tumbuhsehat.app.data.model.MpasiStagesDatabase
import com.tumbuhsehat.app.data.model.RecipeWithGroup
import com.tumbuhsehat.app.data.model.ResepDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads the static reference data bundled in `assets/data/.json`.
 * These files are read once and cached in memory for the lifetime of the
 * process - they're small (a few KB each) and never change at runtime.
 */
class ReferenceRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private var immunizationCache: ImmunizationDatabase? = null
    private var mpasiStagesCache: MpasiStagesDatabase? = null
    private var recipeCache: ResepDatabase? = null

    suspend fun getImmunizationSchedule(): ImmunizationDatabase = withContext(Dispatchers.IO) {
        immunizationCache ?: json.decodeFromString<ImmunizationDatabase>(
            readAsset("data/jadwal_imunisasi.json")
        ).also { immunizationCache = it }
    }

    suspend fun getMpasiStages(): MpasiStagesDatabase = withContext(Dispatchers.IO) {
        mpasiStagesCache ?: json.decodeFromString<MpasiStagesDatabase>(
            readAsset("data/tahapan_mpasi.json")
        ).also { mpasiStagesCache = it }
    }

    suspend fun getRecipes(): ResepDatabase = withContext(Dispatchers.IO) {
        recipeCache ?: json.decodeFromString<ResepDatabase>(
            readAsset("data/resep_makanan.json")
        ).also { recipeCache = it }
    }

    /** Flattens every recipe group into one list, tagged with which age group it belongs to. */
    suspend fun getAllRecipesFlattened(): List<RecipeWithGroup> {
        val db = getRecipes()
        return buildList {
            db.resep_mpasi_6_8_bulan.resep.forEach { add(RecipeWithGroup(it, KelompokUsiaResep.MPASI_6_8)) }
            db.resep_mpasi_9_11_bulan.resep.forEach { add(RecipeWithGroup(it, KelompokUsiaResep.MPASI_9_11)) }
            db.resep_mpasi_12_23_bulan.resep.forEach { add(RecipeWithGroup(it, KelompokUsiaResep.MPASI_12_23)) }
            db.resep_2_5_tahun.resep.forEach { add(RecipeWithGroup(it, KelompokUsiaResep.TODDLER_2_5)) }
            db.resep_ibu_hamil.resep.forEach { add(RecipeWithGroup(it, KelompokUsiaResep.IBU_HAMIL)) }
        }
    }

    /** Returns the recipe age-group that matches a child's current age in months. */
    fun kelompokUntukUsia(usiaBulan: Int): KelompokUsiaResep = when {
        usiaBulan < 6 -> KelompokUsiaResep.MPASI_6_8 // belum mulai MPASI - tampilkan sebagai pratinjau tahap berikutnya
        usiaBulan in 6..8 -> KelompokUsiaResep.MPASI_6_8
        usiaBulan in 9..11 -> KelompokUsiaResep.MPASI_9_11
        usiaBulan in 12..23 -> KelompokUsiaResep.MPASI_12_23
        else -> KelompokUsiaResep.TODDLER_2_5
    }

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
}
