package com.tumbuhsehat.app.`data`.local

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ImmunizationRecordDao_Impl(
  __db: RoomDatabase,
) : ImmunizationRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfImmunizationRecordEntity:
      EntityInsertAdapter<ImmunizationRecordEntity>

  private val __updateAdapterOfImmunizationRecordEntity:
      EntityDeleteOrUpdateAdapter<ImmunizationRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfImmunizationRecordEntity = object : EntityInsertAdapter<ImmunizationRecordEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `immunization_records` (`id`,`profileId`,`namaVaksin`,`dosisLabel`,`usiaTarget`,`sudahDiberikan`,`tanggalDiberikanMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ImmunizationRecordEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.profileId)
        statement.bindText(3, entity.namaVaksin)
        statement.bindText(4, entity.dosisLabel)
        statement.bindText(5, entity.usiaTarget)
        val _tmp: Int = if (entity.sudahDiberikan) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpTanggalDiberikanMillis: Long? = entity.tanggalDiberikanMillis
        if (_tmpTanggalDiberikanMillis == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpTanggalDiberikanMillis)
        }
      }
    }
    this.__updateAdapterOfImmunizationRecordEntity = object : EntityDeleteOrUpdateAdapter<ImmunizationRecordEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `immunization_records` SET `id` = ?,`profileId` = ?,`namaVaksin` = ?,`dosisLabel` = ?,`usiaTarget` = ?,`sudahDiberikan` = ?,`tanggalDiberikanMillis` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ImmunizationRecordEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.profileId)
        statement.bindText(3, entity.namaVaksin)
        statement.bindText(4, entity.dosisLabel)
        statement.bindText(5, entity.usiaTarget)
        val _tmp: Int = if (entity.sudahDiberikan) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpTanggalDiberikanMillis: Long? = entity.tanggalDiberikanMillis
        if (_tmpTanggalDiberikanMillis == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpTanggalDiberikanMillis)
        }
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insertAll(records: List<ImmunizationRecordEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfImmunizationRecordEntity.insert(_connection, records)
  }

  public override suspend fun update(record: ImmunizationRecordEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfImmunizationRecordEntity.handle(_connection, record)
  }

  public override fun observeForProfile(profileId: Long): Flow<List<ImmunizationRecordEntity>> {
    val _sql: String = "SELECT * FROM immunization_records WHERE profileId = ?"
    return createFlow(__db, false, arrayOf("immunization_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProfileId: Int = getColumnIndexOrThrow(_stmt, "profileId")
        val _columnIndexOfNamaVaksin: Int = getColumnIndexOrThrow(_stmt, "namaVaksin")
        val _columnIndexOfDosisLabel: Int = getColumnIndexOrThrow(_stmt, "dosisLabel")
        val _columnIndexOfUsiaTarget: Int = getColumnIndexOrThrow(_stmt, "usiaTarget")
        val _columnIndexOfSudahDiberikan: Int = getColumnIndexOrThrow(_stmt, "sudahDiberikan")
        val _columnIndexOfTanggalDiberikanMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalDiberikanMillis")
        val _result: MutableList<ImmunizationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ImmunizationRecordEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProfileId: Long
          _tmpProfileId = _stmt.getLong(_columnIndexOfProfileId)
          val _tmpNamaVaksin: String
          _tmpNamaVaksin = _stmt.getText(_columnIndexOfNamaVaksin)
          val _tmpDosisLabel: String
          _tmpDosisLabel = _stmt.getText(_columnIndexOfDosisLabel)
          val _tmpUsiaTarget: String
          _tmpUsiaTarget = _stmt.getText(_columnIndexOfUsiaTarget)
          val _tmpSudahDiberikan: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSudahDiberikan).toInt()
          _tmpSudahDiberikan = _tmp != 0
          val _tmpTanggalDiberikanMillis: Long?
          if (_stmt.isNull(_columnIndexOfTanggalDiberikanMillis)) {
            _tmpTanggalDiberikanMillis = null
          } else {
            _tmpTanggalDiberikanMillis = _stmt.getLong(_columnIndexOfTanggalDiberikanMillis)
          }
          _item = ImmunizationRecordEntity(_tmpId,_tmpProfileId,_tmpNamaVaksin,_tmpDosisLabel,_tmpUsiaTarget,_tmpSudahDiberikan,_tmpTanggalDiberikanMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countForProfile(profileId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM immunization_records WHERE profileId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
