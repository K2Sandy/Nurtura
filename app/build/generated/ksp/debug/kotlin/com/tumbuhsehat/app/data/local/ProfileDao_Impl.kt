package com.tumbuhsehat.app.`data`.local

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
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ProfileDao_Impl(
  __db: RoomDatabase,
) : ProfileDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProfileEntity: EntityInsertAdapter<ProfileEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfProfileEntity = object : EntityInsertAdapter<ProfileEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `profiles` (`id`,`nama`,`tipe`,`jenisKelamin`,`tanggalLahirMillis`,`hphtMillis`,`budgetPerPorsiRp`,`aktif`,`dibuatPadaMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProfileEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nama)
        statement.bindText(3, entity.tipe)
        val _tmpJenisKelamin: String? = entity.jenisKelamin
        if (_tmpJenisKelamin == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpJenisKelamin)
        }
        val _tmpTanggalLahirMillis: Long? = entity.tanggalLahirMillis
        if (_tmpTanggalLahirMillis == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpTanggalLahirMillis)
        }
        val _tmpHphtMillis: Long? = entity.hphtMillis
        if (_tmpHphtMillis == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpHphtMillis)
        }
        val _tmpBudgetPerPorsiRp: Int? = entity.budgetPerPorsiRp
        if (_tmpBudgetPerPorsiRp == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpBudgetPerPorsiRp.toLong())
        }
        val _tmp: Int = if (entity.aktif) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.dibuatPadaMillis)
      }
    }
  }

  public override suspend fun upsert(profile: ProfileEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfProfileEntity.insertAndReturnId(_connection, profile)
    _result
  }

  public override fun observeAll(): Flow<List<ProfileEntity>> {
    val _sql: String = "SELECT * FROM profiles ORDER BY dibuatPadaMillis ASC"
    return createFlow(__db, false, arrayOf("profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNama: Int = getColumnIndexOrThrow(_stmt, "nama")
        val _columnIndexOfTipe: Int = getColumnIndexOrThrow(_stmt, "tipe")
        val _columnIndexOfJenisKelamin: Int = getColumnIndexOrThrow(_stmt, "jenisKelamin")
        val _columnIndexOfTanggalLahirMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalLahirMillis")
        val _columnIndexOfHphtMillis: Int = getColumnIndexOrThrow(_stmt, "hphtMillis")
        val _columnIndexOfBudgetPerPorsiRp: Int = getColumnIndexOrThrow(_stmt, "budgetPerPorsiRp")
        val _columnIndexOfAktif: Int = getColumnIndexOrThrow(_stmt, "aktif")
        val _columnIndexOfDibuatPadaMillis: Int = getColumnIndexOrThrow(_stmt, "dibuatPadaMillis")
        val _result: MutableList<ProfileEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProfileEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNama: String
          _tmpNama = _stmt.getText(_columnIndexOfNama)
          val _tmpTipe: String
          _tmpTipe = _stmt.getText(_columnIndexOfTipe)
          val _tmpJenisKelamin: String?
          if (_stmt.isNull(_columnIndexOfJenisKelamin)) {
            _tmpJenisKelamin = null
          } else {
            _tmpJenisKelamin = _stmt.getText(_columnIndexOfJenisKelamin)
          }
          val _tmpTanggalLahirMillis: Long?
          if (_stmt.isNull(_columnIndexOfTanggalLahirMillis)) {
            _tmpTanggalLahirMillis = null
          } else {
            _tmpTanggalLahirMillis = _stmt.getLong(_columnIndexOfTanggalLahirMillis)
          }
          val _tmpHphtMillis: Long?
          if (_stmt.isNull(_columnIndexOfHphtMillis)) {
            _tmpHphtMillis = null
          } else {
            _tmpHphtMillis = _stmt.getLong(_columnIndexOfHphtMillis)
          }
          val _tmpBudgetPerPorsiRp: Int?
          if (_stmt.isNull(_columnIndexOfBudgetPerPorsiRp)) {
            _tmpBudgetPerPorsiRp = null
          } else {
            _tmpBudgetPerPorsiRp = _stmt.getLong(_columnIndexOfBudgetPerPorsiRp).toInt()
          }
          val _tmpAktif: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAktif).toInt()
          _tmpAktif = _tmp != 0
          val _tmpDibuatPadaMillis: Long
          _tmpDibuatPadaMillis = _stmt.getLong(_columnIndexOfDibuatPadaMillis)
          _item = ProfileEntity(_tmpId,_tmpNama,_tmpTipe,_tmpJenisKelamin,_tmpTanggalLahirMillis,_tmpHphtMillis,_tmpBudgetPerPorsiRp,_tmpAktif,_tmpDibuatPadaMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActive(): Flow<ProfileEntity?> {
    val _sql: String = "SELECT * FROM profiles WHERE aktif = 1 LIMIT 1"
    return createFlow(__db, false, arrayOf("profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNama: Int = getColumnIndexOrThrow(_stmt, "nama")
        val _columnIndexOfTipe: Int = getColumnIndexOrThrow(_stmt, "tipe")
        val _columnIndexOfJenisKelamin: Int = getColumnIndexOrThrow(_stmt, "jenisKelamin")
        val _columnIndexOfTanggalLahirMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalLahirMillis")
        val _columnIndexOfHphtMillis: Int = getColumnIndexOrThrow(_stmt, "hphtMillis")
        val _columnIndexOfBudgetPerPorsiRp: Int = getColumnIndexOrThrow(_stmt, "budgetPerPorsiRp")
        val _columnIndexOfAktif: Int = getColumnIndexOrThrow(_stmt, "aktif")
        val _columnIndexOfDibuatPadaMillis: Int = getColumnIndexOrThrow(_stmt, "dibuatPadaMillis")
        val _result: ProfileEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNama: String
          _tmpNama = _stmt.getText(_columnIndexOfNama)
          val _tmpTipe: String
          _tmpTipe = _stmt.getText(_columnIndexOfTipe)
          val _tmpJenisKelamin: String?
          if (_stmt.isNull(_columnIndexOfJenisKelamin)) {
            _tmpJenisKelamin = null
          } else {
            _tmpJenisKelamin = _stmt.getText(_columnIndexOfJenisKelamin)
          }
          val _tmpTanggalLahirMillis: Long?
          if (_stmt.isNull(_columnIndexOfTanggalLahirMillis)) {
            _tmpTanggalLahirMillis = null
          } else {
            _tmpTanggalLahirMillis = _stmt.getLong(_columnIndexOfTanggalLahirMillis)
          }
          val _tmpHphtMillis: Long?
          if (_stmt.isNull(_columnIndexOfHphtMillis)) {
            _tmpHphtMillis = null
          } else {
            _tmpHphtMillis = _stmt.getLong(_columnIndexOfHphtMillis)
          }
          val _tmpBudgetPerPorsiRp: Int?
          if (_stmt.isNull(_columnIndexOfBudgetPerPorsiRp)) {
            _tmpBudgetPerPorsiRp = null
          } else {
            _tmpBudgetPerPorsiRp = _stmt.getLong(_columnIndexOfBudgetPerPorsiRp).toInt()
          }
          val _tmpAktif: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAktif).toInt()
          _tmpAktif = _tmp != 0
          val _tmpDibuatPadaMillis: Long
          _tmpDibuatPadaMillis = _stmt.getLong(_columnIndexOfDibuatPadaMillis)
          _result = ProfileEntity(_tmpId,_tmpNama,_tmpTipe,_tmpJenisKelamin,_tmpTanggalLahirMillis,_tmpHphtMillis,_tmpBudgetPerPorsiRp,_tmpAktif,_tmpDibuatPadaMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): ProfileEntity? {
    val _sql: String = "SELECT * FROM profiles WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNama: Int = getColumnIndexOrThrow(_stmt, "nama")
        val _columnIndexOfTipe: Int = getColumnIndexOrThrow(_stmt, "tipe")
        val _columnIndexOfJenisKelamin: Int = getColumnIndexOrThrow(_stmt, "jenisKelamin")
        val _columnIndexOfTanggalLahirMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalLahirMillis")
        val _columnIndexOfHphtMillis: Int = getColumnIndexOrThrow(_stmt, "hphtMillis")
        val _columnIndexOfBudgetPerPorsiRp: Int = getColumnIndexOrThrow(_stmt, "budgetPerPorsiRp")
        val _columnIndexOfAktif: Int = getColumnIndexOrThrow(_stmt, "aktif")
        val _columnIndexOfDibuatPadaMillis: Int = getColumnIndexOrThrow(_stmt, "dibuatPadaMillis")
        val _result: ProfileEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNama: String
          _tmpNama = _stmt.getText(_columnIndexOfNama)
          val _tmpTipe: String
          _tmpTipe = _stmt.getText(_columnIndexOfTipe)
          val _tmpJenisKelamin: String?
          if (_stmt.isNull(_columnIndexOfJenisKelamin)) {
            _tmpJenisKelamin = null
          } else {
            _tmpJenisKelamin = _stmt.getText(_columnIndexOfJenisKelamin)
          }
          val _tmpTanggalLahirMillis: Long?
          if (_stmt.isNull(_columnIndexOfTanggalLahirMillis)) {
            _tmpTanggalLahirMillis = null
          } else {
            _tmpTanggalLahirMillis = _stmt.getLong(_columnIndexOfTanggalLahirMillis)
          }
          val _tmpHphtMillis: Long?
          if (_stmt.isNull(_columnIndexOfHphtMillis)) {
            _tmpHphtMillis = null
          } else {
            _tmpHphtMillis = _stmt.getLong(_columnIndexOfHphtMillis)
          }
          val _tmpBudgetPerPorsiRp: Int?
          if (_stmt.isNull(_columnIndexOfBudgetPerPorsiRp)) {
            _tmpBudgetPerPorsiRp = null
          } else {
            _tmpBudgetPerPorsiRp = _stmt.getLong(_columnIndexOfBudgetPerPorsiRp).toInt()
          }
          val _tmpAktif: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfAktif).toInt()
          _tmpAktif = _tmp != 0
          val _tmpDibuatPadaMillis: Long
          _tmpDibuatPadaMillis = _stmt.getLong(_columnIndexOfDibuatPadaMillis)
          _result = ProfileEntity(_tmpId,_tmpNama,_tmpTipe,_tmpJenisKelamin,_tmpTanggalLahirMillis,_tmpHphtMillis,_tmpBudgetPerPorsiRp,_tmpAktif,_tmpDibuatPadaMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deactivateAll() {
    val _sql: String = "UPDATE profiles SET aktif = 0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setActive(id: Long) {
    val _sql: String = "UPDATE profiles SET aktif = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setBudget(id: Long, budget: Int?) {
    val _sql: String = "UPDATE profiles SET budgetPerPorsiRp = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (budget == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, budget.toLong())
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
