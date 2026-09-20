package com.tumbuhsehat.app.data.repository

import com.tumbuhsehat.app.data.local.AppDatabase
import com.tumbuhsehat.app.data.local.GrowthRecordEntity
import com.tumbuhsehat.app.data.local.ImmunizationRecordEntity
import com.tumbuhsehat.app.data.local.ProfileEntity
import com.tumbuhsehat.app.data.local.ReminderEntity
import com.tumbuhsehat.app.data.local.TipePengingat
import com.tumbuhsehat.app.data.local.TipeProfil
import com.tumbuhsehat.app.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow

/**
 * Business-logic layer sitting on top of Room + the static reference
 * repositories. Screens/ViewModels talk to this class rather than the DAOs
 * directly.
 */
class UserRepository(
    private val db: AppDatabase,
    private val referenceRepository: ReferenceRepository,
    private val growthReferenceRepository: GrowthReferenceRepository,
    private val reminderScheduler: ReminderScheduler,
) {

    // ---- Profiles ----

    fun observeActiveProfile(): Flow<ProfileEntity?> = db.profileDao().observeActive()
    fun observeAllProfiles(): Flow<List<ProfileEntity>> = db.profileDao().observeAll()

    suspend fun createPregnancyProfile(nama: String, hphtMillis: Long): Long {
        db.profileDao().deactivateAll()
        return db.profileDao().upsert(
            ProfileEntity(nama = nama, tipe = TipeProfil.HAMIL, hphtMillis = hphtMillis, aktif = true)
        )
    }

    /** Direct entry point for parents who already have a baby - no pregnancy step required. */
    suspend fun createChildProfile(nama: String, jenisKelamin: String, tanggalLahirMillis: Long): Long {
        db.profileDao().deactivateAll()
        val id = db.profileDao().upsert(
            ProfileEntity(
                nama = nama,
                tipe = TipeProfil.ANAK,
                jenisKelamin = jenisKelamin,
                tanggalLahirMillis = tanggalLahirMillis,
                aktif = true,
            )
        )
        seedImmunizationChecklist(id)
        return id
    }

    suspend fun switchActiveProfile(id: Long) {
        db.profileDao().deactivateAll()
        db.profileDao().setActive(id)
    }

    suspend fun setBudget(profileId: Long, budgetPerPorsiRp: Int?) {
        db.profileDao().setBudget(profileId, budgetPerPorsiRp)
    }

    /** The "transisi otomatis" moment: a pregnancy profile becomes a child profile once the baby is born. */
    suspend fun tandaiSudahLahir(profileId: Long, jenisKelamin: String, tanggalLahirMillis: Long) {
        val profile = db.profileDao().getById(profileId) ?: return
        db.profileDao().upsert(
            profile.copy(tipe = TipeProfil.ANAK, jenisKelamin = jenisKelamin, tanggalLahirMillis = tanggalLahirMillis)
        )
        seedImmunizationChecklist(profileId)
    }

    private suspend fun seedImmunizationChecklist(profileId: Long) {
        if (db.immunizationRecordDao().countForProfile(profileId) > 0) return
        val schedule = referenceRepository.getImmunizationSchedule()
        val rows = schedule.vaksin
            .filter { it.kategori == "wajib_nasional" }
            .flatMap { vaksin ->
                vaksin.jadwal.map { dose ->
                    ImmunizationRecordEntity(
                        profileId = profileId,
                        namaVaksin = vaksin.nama,
                        dosisLabel = dose.dosis,
                        usiaTarget = dose.usia,
                    )
                }
            }
        db.immunizationRecordDao().insertAll(rows)
    }

    // ---- Growth ----

    fun observeGrowthRecords(profileId: Long): Flow<List<GrowthRecordEntity>> =
        db.growthRecordDao().observeForProfile(profileId)

    suspend fun latestGrowthRecord(profileId: Long): GrowthRecordEntity? =
        db.growthRecordDao().getLatest(profileId)

    suspend fun addGrowthRecord(
        profileId: Long,
        tanggalMillis: Long,
        beratKg: Double?,
        panjangCm: Double?,
        lingkarKepalaCm: Double?,
        catatan: String?,
    ): Long = db.growthRecordDao().insert(
        GrowthRecordEntity(
            profileId = profileId,
            tanggalMillis = tanggalMillis,
            beratKg = beratKg,
            panjangCm = panjangCm,
            lingkarKepalaCm = lingkarKepalaCm,
            catatan = catatan,
        )
    )

    /** Weight-for-age status for a given measurement - used for the Beranda status badge. */
    suspend fun hitungStatusBerat(profile: ProfileEntity, usiaBulan: Int, beratKg: Double): StatusGizi {
        val gender = if (profile.jenisKelamin == "LAKI_LAKI") JenisKelamin.LAKI_LAKI else JenisKelamin.PEREMPUAN
        val rows = growthReferenceRepository.getReferenceRows(gender, JenisUkuran.BERAT_USIA)
        val ref = growthReferenceRepository.interpolate(rows, usiaBulan.toDouble()) ?: return StatusGizi.NORMAL
        val z = growthReferenceRepository.hitungZScore(beratKg, ref.l, ref.m, ref.s)
        return growthReferenceRepository.statusDariZScore(z)
    }

    // ---- Immunization ----

    fun observeImmunizationRecords(profileId: Long): Flow<List<ImmunizationRecordEntity>> =
        db.immunizationRecordDao().observeForProfile(profileId)

    suspend fun tandaiImunisasiSelesai(record: ImmunizationRecordEntity, tanggalMillis: Long) {
        db.immunizationRecordDao().update(record.copy(sudahDiberikan = true, tanggalDiberikanMillis = tanggalMillis))
    }

    // ---- Reminders (in-app / local notification only - WhatsApp integration comes later) ----

    fun observeReminders(profileId: Long): Flow<List<ReminderEntity>> = db.reminderDao().observeForProfile(profileId)
    fun observeNextReminder(profileId: Long): Flow<ReminderEntity?> = db.reminderDao().observeNextUpcoming(profileId)

    suspend fun buatPengingat(
        profileId: Long,
        judul: String,
        deskripsi: String?,
        tanggalMillis: Long,
        tipe: String = TipePengingat.LAINNYA,
    ): Long {
        val notifId = ((profileId * 100_003) + (tanggalMillis % 100_003)).toInt()
        val workId = reminderScheduler.schedule(judul, deskripsi.orEmpty(), tanggalMillis, notifId)
        return db.reminderDao().insert(
            ReminderEntity(
                profileId = profileId,
                judul = judul,
                deskripsi = deskripsi,
                tanggalPengingatMillis = tanggalMillis,
                tipe = tipe,
                workRequestId = workId,
            )
        )
    }

    suspend fun tandaiPengingatSelesai(reminder: ReminderEntity) {
        db.reminderDao().update(reminder.copy(sudahSelesai = true))
    }
}
