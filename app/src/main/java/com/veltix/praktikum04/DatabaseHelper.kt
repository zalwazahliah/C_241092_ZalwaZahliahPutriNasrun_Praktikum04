package com.veltix.praktikum04

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_MAHASISWA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nim TEXT NOT NULL UNIQUE,
                nama TEXT NOT NULL,
                alamat TEXT,
                jurusan TEXT,
                nomor_hp TEXT
            )
            """.trimIndent()
        )

        seedData(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAHASISWA")
        onCreate(db)
    }

    private fun toValues(mhs: Mahasiswa): ContentValues =
        ContentValues().apply {
            put("nim", mhs.nim)
            put("nama", mhs.nama)
            put("alamat", mhs.alamat)
            put("jurusan", mhs.jurusan)
            put("nomor_hp", mhs.nomorHp)
        }

    private fun seedData(db: SQLiteDatabase) {
        val data = listOf(
            Mahasiswa(
                nim = "21001",
                nama = "Ahmad Fauzi",
                alamat = "Jl. Pettarani No. 5",
                jurusan = "Sistem Informasi",
                nomorHp = "081234567890"
            ),
            Mahasiswa(
                nim = "21002",
                nama = "Siti Nurhaliza",
                alamat = "Jl. Perintis Kemerdekaan",
                jurusan = "Sistem Informasi",
                nomorHp = "082198765432"
            ),
            Mahasiswa(
                nim = "21003",
                nama = "Budi Santoso",
                alamat = "Jl. Rappocini No. 12",
                jurusan = "Sistem Informasi",
                nomorHp = "081345678901"
            )
        )

        data.forEach { db.insert(TABLE_MAHASISWA, null, toValues(it)) }
    }

    fun insertMahasiswa(mhs: Mahasiswa): Long =
        writableDatabase.insert(TABLE_MAHASISWA, null, toValues(mhs))

    fun updateMahasiswa(mhs: Mahasiswa): Int =
        writableDatabase.update(
            TABLE_MAHASISWA,
            toValues(mhs),
            "id = ?",
            arrayOf(mhs.id.toString())
        )

    fun deleteMahasiswa(id: Int): Int =
        writableDatabase.delete(
            TABLE_MAHASISWA,
            "id = ?",
            arrayOf(id.toString())
        )

    fun isNimTerdaftar(nim: String, kecualiId: Int = 0): Boolean {
        val cursor = readableDatabase.query(
            TABLE_MAHASISWA,
            arrayOf("id"),
            "nim = ? AND id != ?",
            arrayOf(nim, kecualiId.toString()),
            null,
            null,
            null
        )
        return cursor.use { it.moveToFirst() }
    }

    fun getAllMahasiswa(): List<Mahasiswa> {
        val list = mutableListOf<Mahasiswa>()
        val cursor = readableDatabase.query(
            TABLE_MAHASISWA,
            null,
            null,
            null,
            null,
            null,
            "nama ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                list.add(toMahasiswa(it))
            }
        }
        return list
    }

    fun getMahasiswaById(id: Int): Mahasiswa? {
        val cursor = readableDatabase.query(
            TABLE_MAHASISWA,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) return toMahasiswa(it)
        }
        return null
    }

    private fun toMahasiswa(cursor: Cursor) = Mahasiswa(
        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
        nim = cursor.getString(cursor.getColumnIndexOrThrow("nim")),
        nama = cursor.getString(cursor.getColumnIndexOrThrow("nama")),
        alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat")) ?: "",
        jurusan = cursor.getString(cursor.getColumnIndexOrThrow("jurusan")) ?: "",
        nomorHp = cursor.getString(cursor.getColumnIndexOrThrow("nomor_hp")) ?: ""
    )

    companion object {
        private const val DB_NAME = "praktikum.db"
        private const val DB_VERSION = 1
        const val TABLE_MAHASISWA = "mahasiswa"
    }
}