package com.tumbuhsehat.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ---------------------------------------------------------------------
// Entities
// ---------------------------------------------------------------------

/** "HAMIL" while pregnant, becomes "ANAK" once the child is born (same row - see ProfileRepository). */
object TipeProfil {
    const val HAMIL = "HAMIL"
    const val ANAK = "ANAK"
}

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val tipe: String,
    val jenisKelamin: String? = null, // "LAKI_LAKI" | "PEREMPUAN", set once the child is born
    val tanggalLahirMillis: Long? = null,
    val hphtMillis: Long? = null, // hari pertama haid terakhir - only relevant while tipe == HAMIL
    val budgetPerPorsiRp: Int? = null, // budget makan per porsi, dipakai buat saring rekomendasi resep
    val aktif: Boolean = true,
    val dibuatPadaMillis: Long = System.currentTimeMillis(),
)

@Entity(tableName = "growth_records")
data class GrowthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val tanggalMillis: Long,
    val beratKg: Double? = null,
    val panjangCm: Double? = null,
    val lingkarKepalaCm: Double? = null,
    val catatan: String? = null,
)

@Entity(tableName = "immunization_records")
data class ImmunizationRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val namaVaksin: String,
    val dosisLabel: String,
    val usiaTarget: String,
    val sudahDiberikan: Boolean = false,
    val tanggalDiberikanMillis: Long? = null,
)

object TipePengingat {
    const val IMUNISASI = "IMUNISASI"
    const val CHECKUP = "CHECKUP"
    const val LAINNYA = "LAINNYA"
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val judul: String,
    val deskripsi: String? = null,
    val tanggalPengingatMillis: Long,
    val sudahSelesai: Boolean = false,
    val tipe: String = TipePengingat.LAINNYA,
    val workRequestId: String? = null, // links back to the scheduled WorkManager request, for cancellation
)

// ---------------------------------------------------------------------
// DAOs
// ---------------------------------------------------------------------

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY dibuatPadaMillis ASC")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE aktif = 1 LIMIT 1")
    fun observeActive(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getById(id: Long): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: ProfileEntity): Long

    @Query("UPDATE profiles SET aktif = 0")
    suspend fun deactivateAll()

    @Query("UPDATE profiles SET aktif = 1 WHERE id = :id")
    suspend fun setActive(id: Long)

    @Query("UPDATE profiles SET budgetPerPorsiRp = :budget WHERE id = :id")
    suspend fun setBudget(id: Long, budget: Int?)
}

@Dao
interface GrowthRecordDao {
    @Query("SELECT * FROM growth_records WHERE profileId = :profileId ORDER BY tanggalMillis ASC")
    fun observeForProfile(profileId: Long): Flow<List<GrowthRecordEntity>>

    @Query("SELECT * FROM growth_records WHERE profileId = :profileId ORDER BY tanggalMillis DESC LIMIT 1")
    suspend fun getLatest(profileId: Long): GrowthRecordEntity?

    @Insert
    suspend fun insert(record: GrowthRecordEntity): Long
}

@Dao
interface ImmunizationRecordDao {
    @Query("SELECT * FROM immunization_records WHERE profileId = :profileId")
    fun observeForProfile(profileId: Long): Flow<List<ImmunizationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<ImmunizationRecordEntity>)

    @Update
    suspend fun update(record: ImmunizationRecordEntity)

    @Query("SELECT COUNT(*) FROM immunization_records WHERE profileId = :profileId")
    suspend fun countForProfile(profileId: Long): Int
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE profileId = :profileId ORDER BY tanggalPengingatMillis ASC")
    fun observeForProfile(profileId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE profileId = :profileId AND sudahSelesai = 0 ORDER BY tanggalPengingatMillis ASC LIMIT 1")
    fun observeNextUpcoming(profileId: Long): Flow<ReminderEntity?>

    @Insert
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)
}

// ---------------------------------------------------------------------
// Database
// ---------------------------------------------------------------------

@Database(
    entities = [
        ProfileEntity::class,
        GrowthRecordEntity::class,
        ImmunizationRecordEntity::class,
        ReminderEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun immunizationRecordDao(): ImmunizationRecordDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tumbuhsehat.db",
                )
                    // Skema baru nambah kolom budget - di tahap dev ini belum ada data
                    // produksi yang perlu dijaga, jadi migrasi destruktif paling simpel.
                    // Data uji coba yang sudah ada di HP/emulator bakal ke-reset sekali.
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
