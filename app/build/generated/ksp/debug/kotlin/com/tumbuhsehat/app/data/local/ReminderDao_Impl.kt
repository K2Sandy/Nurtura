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
public class ReminderDao_Impl(
  __db: RoomDatabase,
) : ReminderDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfReminderEntity: EntityInsertAdapter<ReminderEntity>

  private val __updateAdapterOfReminderEntity: EntityDeleteOrUpdateAdapter<ReminderEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfReminderEntity = object : EntityInsertAdapter<ReminderEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `reminders` (`id`,`profileId`,`judul`,`deskripsi`,`tanggalPengingatMillis`,`sudahSelesai`,`tipe`,`workRequestId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ReminderEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.profileId)
        statement.bindText(3, entity.judul)
        val _tmpDeskripsi: String? = entity.deskripsi
        if (_tmpDeskripsi == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDeskripsi)
        }
        statement.bindLong(5, entity.tanggalPengingatMillis)
        val _tmp: Int = if (entity.sudahSelesai) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.tipe)
        val _tmpWorkRequestId: String? = entity.workRequestId
        if (_tmpWorkRequestId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpWorkRequestId)
        }
      }
    }
    this.__updateAdapterOfReminderEntity = object : EntityDeleteOrUpdateAdapter<ReminderEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `reminders` SET `id` = ?,`profileId` = ?,`judul` = ?,`deskripsi` = ?,`tanggalPengingatMillis` = ?,`sudahSelesai` = ?,`tipe` = ?,`workRequestId` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ReminderEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.profileId)
        statement.bindText(3, entity.judul)
        val _tmpDeskripsi: String? = entity.deskripsi
        if (_tmpDeskripsi == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDeskripsi)
        }
        statement.bindLong(5, entity.tanggalPengingatMillis)
        val _tmp: Int = if (entity.sudahSelesai) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindText(7, entity.tipe)
        val _tmpWorkRequestId: String? = entity.workRequestId
        if (_tmpWorkRequestId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpWorkRequestId)
        }
        statement.bindLong(9, entity.id)
      }
    }
  }

  public override suspend fun insert(reminder: ReminderEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfReminderEntity.insertAndReturnId(_connection, reminder)
    _result
  }

  public override suspend fun update(reminder: ReminderEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfReminderEntity.handle(_connection, reminder)
  }

  public override fun observeForProfile(profileId: Long): Flow<List<ReminderEntity>> {
    val _sql: String = "SELECT * FROM reminders WHERE profileId = ? ORDER BY tanggalPengingatMillis ASC"
    return createFlow(__db, false, arrayOf("reminders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProfileId: Int = getColumnIndexOrThrow(_stmt, "profileId")
        val _columnIndexOfJudul: Int = getColumnIndexOrThrow(_stmt, "judul")
        val _columnIndexOfDeskripsi: Int = getColumnIndexOrThrow(_stmt, "deskripsi")
        val _columnIndexOfTanggalPengingatMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalPengingatMillis")
        val _columnIndexOfSudahSelesai: Int = getColumnIndexOrThrow(_stmt, "sudahSelesai")
        val _columnIndexOfTipe: Int = getColumnIndexOrThrow(_stmt, "tipe")
        val _columnIndexOfWorkRequestId: Int = getColumnIndexOrThrow(_stmt, "workRequestId")
        val _result: MutableList<ReminderEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ReminderEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProfileId: Long
          _tmpProfileId = _stmt.getLong(_columnIndexOfProfileId)
          val _tmpJudul: String
          _tmpJudul = _stmt.getText(_columnIndexOfJudul)
          val _tmpDeskripsi: String?
          if (_stmt.isNull(_columnIndexOfDeskripsi)) {
            _tmpDeskripsi = null
          } else {
            _tmpDeskripsi = _stmt.getText(_columnIndexOfDeskripsi)
          }
          val _tmpTanggalPengingatMillis: Long
          _tmpTanggalPengingatMillis = _stmt.getLong(_columnIndexOfTanggalPengingatMillis)
          val _tmpSudahSelesai: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSudahSelesai).toInt()
          _tmpSudahSelesai = _tmp != 0
          val _tmpTipe: String
          _tmpTipe = _stmt.getText(_columnIndexOfTipe)
          val _tmpWorkRequestId: String?
          if (_stmt.isNull(_columnIndexOfWorkRequestId)) {
            _tmpWorkRequestId = null
          } else {
            _tmpWorkRequestId = _stmt.getText(_columnIndexOfWorkRequestId)
          }
          _item = ReminderEntity(_tmpId,_tmpProfileId,_tmpJudul,_tmpDeskripsi,_tmpTanggalPengingatMillis,_tmpSudahSelesai,_tmpTipe,_tmpWorkRequestId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeNextUpcoming(profileId: Long): Flow<ReminderEntity?> {
    val _sql: String = "SELECT * FROM reminders WHERE profileId = ? AND sudahSelesai = 0 ORDER BY tanggalPengingatMillis ASC LIMIT 1"
    return createFlow(__db, false, arrayOf("reminders")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, profileId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfProfileId: Int = getColumnIndexOrThrow(_stmt, "profileId")
        val _columnIndexOfJudul: Int = getColumnIndexOrThrow(_stmt, "judul")
        val _columnIndexOfDeskripsi: Int = getColumnIndexOrThrow(_stmt, "deskripsi")
        val _columnIndexOfTanggalPengingatMillis: Int = getColumnIndexOrThrow(_stmt, "tanggalPengingatMillis")
        val _columnIndexOfSudahSelesai: Int = getColumnIndexOrThrow(_stmt, "sudahSelesai")
        val _columnIndexOfTipe: Int = getColumnIndexOrThrow(_stmt, "tipe")
        val _columnIndexOfWorkRequestId: Int = getColumnIndexOrThrow(_stmt, "workRequestId")
        val _result: ReminderEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpProfileId: Long
          _tmpProfileId = _stmt.getLong(_columnIndexOfProfileId)
          val _tmpJudul: String
          _tmpJudul = _stmt.getText(_columnIndexOfJudul)
          val _tmpDeskripsi: String?
          if (_stmt.isNull(_columnIndexOfDeskripsi)) {
            _tmpDeskripsi = null
          } else {
            _tmpDeskripsi = _stmt.getText(_columnIndexOfDeskripsi)
          }
          val _tmpTanggalPengingatMillis: Long
          _tmpTanggalPengingatMillis = _stmt.getLong(_columnIndexOfTanggalPengingatMillis)
          val _tmpSudahSelesai: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSudahSelesai).toInt()
          _tmpSudahSelesai = _tmp != 0
          val _tmpTipe: String
          _tmpTipe = _stmt.getText(_columnIndexOfTipe)
          val _tmpWorkRequestId: String?
          if (_stmt.isNull(_columnIndexOfWorkRequestId)) {
            _tmpWorkRequestId = null
          } else {
            _tmpWorkRequestId = _stmt.getText(_columnIndexOfWorkRequestId)
          }
          _result = ReminderEntity(_tmpId,_tmpProfileId,_tmpJudul,_tmpDeskripsi,_tmpTanggalPengingatMillis,_tmpSudahSelesai,_tmpTipe,_tmpWorkRequestId)
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
