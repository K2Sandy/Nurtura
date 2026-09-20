package com.tumbuhsehat.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _profileDao: Lazy<ProfileDao> = lazy {
    ProfileDao_Impl(this)
  }

  private val _growthRecordDao: Lazy<GrowthRecordDao> = lazy {
    GrowthRecordDao_Impl(this)
  }

  private val _immunizationRecordDao: Lazy<ImmunizationRecordDao> = lazy {
    ImmunizationRecordDao_Impl(this)
  }

  private val _reminderDao: Lazy<ReminderDao> = lazy {
    ReminderDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "b7f47d252453046151238c5fd1bff96b", "d18a1e39c8a23ccaf355677cbe60c954") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nama` TEXT NOT NULL, `tipe` TEXT NOT NULL, `jenisKelamin` TEXT, `tanggalLahirMillis` INTEGER, `hphtMillis` INTEGER, `budgetPerPorsiRp` INTEGER, `aktif` INTEGER NOT NULL, `dibuatPadaMillis` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `growth_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `profileId` INTEGER NOT NULL, `tanggalMillis` INTEGER NOT NULL, `beratKg` REAL, `panjangCm` REAL, `lingkarKepalaCm` REAL, `catatan` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `immunization_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `profileId` INTEGER NOT NULL, `namaVaksin` TEXT NOT NULL, `dosisLabel` TEXT NOT NULL, `usiaTarget` TEXT NOT NULL, `sudahDiberikan` INTEGER NOT NULL, `tanggalDiberikanMillis` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `profileId` INTEGER NOT NULL, `judul` TEXT NOT NULL, `deskripsi` TEXT, `tanggalPengingatMillis` INTEGER NOT NULL, `sudahSelesai` INTEGER NOT NULL, `tipe` TEXT NOT NULL, `workRequestId` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b7f47d252453046151238c5fd1bff96b')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `growth_records`")
        connection.execSQL("DROP TABLE IF EXISTS `immunization_records`")
        connection.execSQL("DROP TABLE IF EXISTS `reminders`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProfiles.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("nama", TableInfo.Column("nama", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("tipe", TableInfo.Column("tipe", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("jenisKelamin", TableInfo.Column("jenisKelamin", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("tanggalLahirMillis", TableInfo.Column("tanggalLahirMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("hphtMillis", TableInfo.Column("hphtMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("budgetPerPorsiRp", TableInfo.Column("budgetPerPorsiRp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("aktif", TableInfo.Column("aktif", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfiles.put("dibuatPadaMillis", TableInfo.Column("dibuatPadaMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoProfiles: TableInfo = TableInfo("profiles", _columnsProfiles, _foreignKeysProfiles, _indicesProfiles)
        val _existingProfiles: TableInfo = read(connection, "profiles")
        if (!_infoProfiles.equals(_existingProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |profiles(com.tumbuhsehat.app.data.local.ProfileEntity).
              | Expected:
              |""".trimMargin() + _infoProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingProfiles)
        }
        val _columnsGrowthRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGrowthRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("profileId", TableInfo.Column("profileId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("tanggalMillis", TableInfo.Column("tanggalMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("beratKg", TableInfo.Column("beratKg", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("panjangCm", TableInfo.Column("panjangCm", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("lingkarKepalaCm", TableInfo.Column("lingkarKepalaCm", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("catatan", TableInfo.Column("catatan", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGrowthRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGrowthRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoGrowthRecords: TableInfo = TableInfo("growth_records", _columnsGrowthRecords, _foreignKeysGrowthRecords, _indicesGrowthRecords)
        val _existingGrowthRecords: TableInfo = read(connection, "growth_records")
        if (!_infoGrowthRecords.equals(_existingGrowthRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |growth_records(com.tumbuhsehat.app.data.local.GrowthRecordEntity).
              | Expected:
              |""".trimMargin() + _infoGrowthRecords + """
              |
              | Found:
              |""".trimMargin() + _existingGrowthRecords)
        }
        val _columnsImmunizationRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsImmunizationRecords.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("profileId", TableInfo.Column("profileId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("namaVaksin", TableInfo.Column("namaVaksin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("dosisLabel", TableInfo.Column("dosisLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("usiaTarget", TableInfo.Column("usiaTarget", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("sudahDiberikan", TableInfo.Column("sudahDiberikan", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsImmunizationRecords.put("tanggalDiberikanMillis", TableInfo.Column("tanggalDiberikanMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysImmunizationRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesImmunizationRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoImmunizationRecords: TableInfo = TableInfo("immunization_records", _columnsImmunizationRecords, _foreignKeysImmunizationRecords, _indicesImmunizationRecords)
        val _existingImmunizationRecords: TableInfo = read(connection, "immunization_records")
        if (!_infoImmunizationRecords.equals(_existingImmunizationRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |immunization_records(com.tumbuhsehat.app.data.local.ImmunizationRecordEntity).
              | Expected:
              |""".trimMargin() + _infoImmunizationRecords + """
              |
              | Found:
              |""".trimMargin() + _existingImmunizationRecords)
        }
        val _columnsReminders: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReminders.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("profileId", TableInfo.Column("profileId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("judul", TableInfo.Column("judul", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("deskripsi", TableInfo.Column("deskripsi", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("tanggalPengingatMillis", TableInfo.Column("tanggalPengingatMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("sudahSelesai", TableInfo.Column("sudahSelesai", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("tipe", TableInfo.Column("tipe", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReminders.put("workRequestId", TableInfo.Column("workRequestId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReminders: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReminders: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoReminders: TableInfo = TableInfo("reminders", _columnsReminders, _foreignKeysReminders, _indicesReminders)
        val _existingReminders: TableInfo = read(connection, "reminders")
        if (!_infoReminders.equals(_existingReminders)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |reminders(com.tumbuhsehat.app.data.local.ReminderEntity).
              | Expected:
              |""".trimMargin() + _infoReminders + """
              |
              | Found:
              |""".trimMargin() + _existingReminders)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "profiles", "growth_records", "immunization_records", "reminders")
  }

  public override fun clearAllTables() {
    super.performClear(false, "profiles", "growth_records", "immunization_records", "reminders")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ProfileDao::class, ProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GrowthRecordDao::class, GrowthRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ImmunizationRecordDao::class, ImmunizationRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReminderDao::class, ReminderDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun profileDao(): ProfileDao = _profileDao.value

  public override fun growthRecordDao(): GrowthRecordDao = _growthRecordDao.value

  public override fun immunizationRecordDao(): ImmunizationRecordDao = _immunizationRecordDao.value

  public override fun reminderDao(): ReminderDao = _reminderDao.value
}
