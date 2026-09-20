package com.tumbuhsehat.app.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GrowthRecordDao_Impl(
  __db: RoomDatabase,
) : GrowthRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGrowthRecordEntity: EntityInsertAdapter<GrowthRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGrowthRecordEntity = object : EntityInsertAdapter<GrowthRecordEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `growth_records` (`id`,`profileId`,`tanggalMillis`,`beratKg`,`panjangCm`,`lingkarKepalaCm`,`catatan`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GrowthRecordEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.profileId)
        statement.bindLong(3, entity.tanggalMillis)
        val _tmpBeratKg: Double? = entity.beratKg
        if (_tmpBeratKg == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpBeratKg)
        }
        val _tmpPanjangCm: Double? = entity.panjangCm
        if (_tmpPanjangCm == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpPanjangCm)
        }
        val _tmpLingkarKepalaCm: Double? = entity.lingkarKepalaCm
        if (_tmpLingkarKepalaCm == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpLingkarKepalaCm)
        }
        val _tmpCatatan: String? = entity.catatan
        if (_tmpCatatan == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpCatatan)
        }
      }
    }
  }

  public override suspend fun insert(record: GrowthRecordEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfGrowthRecordEntity.insertAndReturnId(_connection, record)
    _result
  }

  public override fun observeForProfile(profileId: Long): Flow<List<GrowthRecordEntity>> {
    val _sql: String = "SELECT * FROM growth_records WHERE profileId = ? ORDER BY tanggalMillis ASC"
    return createFlow(__db, false, arrayOf("growth_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProfileId: Int = getColumnIndexOrThrow(_stmt, "profileId")
        val _columnIndexOfTanggalMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalMillis")
        val _columnIndexOfBeratKg: Int = getColumnIndexOrThrow(_stmt, "beratKg")
        val _columnIndexOfPanjangCm: Int = getColumnIndexOrThrow(_stmt, "panjangCm")
        val _columnIndexOfLingkarKepalaCm: Int = getColumnIndexOrThrow(_stmt, "lingkarKepalaCm")
        val _columnIndexOfCatatan: Int = getColumnIndexOrThrow(_stmt, "catatan")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProfileId: Long
          _tmpProfileId = _stmt.getLong(_columnIndexOfProfileId)
          val _tmpTanggalMillis: Long
          _tmpTanggalMillis = _stmt.getLong(_columnIndexOfTanggalMillis)
          val _tmpBeratKg: Double?
          if (_stmt.isNull(_columnIndexOfBeratKg)) {
            _tmpBeratKg = null
          } else {
            _tmpBeratKg = _stmt.getDouble(_columnIndexOfBeratKg)
          }
          val _tmpPanjangCm: Double?
          if (_stmt.isNull(_columnIndexOfPanjangCm)) {
            _tmpPanjangCm = null
          } else {
            _tmpPanjangCm = _stmt.getDouble(_columnIndexOfPanjangCm)
          }
          val _tmpLingkarKepalaCm: Double?
          if (_stmt.isNull(_columnIndexOfLingkarKepalaCm)) {
            _tmpLingkarKepalaCm = null
          } else {
            _tmpLingkarKepalaCm = _stmt.getDouble(_columnIndexOfLingkarKepalaCm)
          }
          val _tmpCatatan: String?
          if (_stmt.isNull(_columnIndexOfCatatan)) {
            _tmpCatatan = null
          } else {
            _tmpCatatan = _stmt.getText(_columnIndexOfCatatan)
          }
          _item = GrowthRecordEntity(_tmpId,_tmpProfileId,_tmpTanggalMillis,_tmpBeratKg,_tmpPanjangCm,_tmpLingkarKepalaCm,_tmpCatatan)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatest(profileId: Long): GrowthRecordEntity? {
    val _sql: String = "SELECT * FROM growth_records WHERE profileId = ? ORDER BY tanggalMillis DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProfileId: Int = getColumnIndexOrThrow(_stmt, "profileId")
        val _columnIndexOfTanggalMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalMillis")
        val _columnIndexOfBeratKg: Int = getColumnIndexOrThrow(_stmt, "beratKg")
        val _columnIndexOfPanjangCm: Int = getColumnIndexOrThrow(_stmt, "panjangCm")
        val _columnIndexOfLingkarKepalaCm: Int = getColumnIndexOrThrow(_stmt, "lingkarKepalaCm")
        val _columnIndexOfCatatan: Int = getColumnIndexOrThrow(_stmt, "catatan")
        val _result: GrowthRecordEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProfileId: Long
          _tmpProfileId = _stmt.getLong(_columnIndexOfProfileId)
          val _tmpTanggalMillis: Long
          _tmpTanggalMillis = _stmt.getLong(_columnIndexOfTanggalMillis)
          val _tmpBeratKg: Double?
          if (_stmt.isNull(_columnIndexOfBeratKg)) {
            _tmpBeratKg = null
          } else {
            _tmpBeratKg = _stmt.getDouble(_columnIndexOfBeratKg)
          }
          val _tmpPanjangCm: Double?
          if (_stmt.isNull(_columnIndexOfPanjangCm)) {
            _tmpPanjangCm = null
          } else {
            _tmpPanjangCm = _stmt.getDouble(_columnIndexOfPanjangCm)
          }
          val _tmpLingkarKepalaCm: Double?
          if (_stmt.isNull(_columnIndexOfLingkarKepalaCm)) {
            _tmpLingkarKepalaCm = null
          } else {
            _tmpLingkarKepalaCm = _stmt.getDouble(_columnIndexOfLingkarKepalaCm)
          }
          val _tmpCatatan: String?
          if (_stmt.isNull(_columnIndexOfCatatan)) {
            _tmpCatatan = null
          } else {
            _tmpCatatan = _stmt.getText(_columnIndexOfCatatan)
          }
          _result = GrowthRecordEntity(_tmpId,_tmpProfileId,_tmpTanggalMillis,_tmpBeratKg,_tmpPanjangCm,_tmpLingkarKepalaCm,_tmpCatatan)
        } else {
          _result = null
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
