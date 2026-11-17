package com.veltix.praktikum04

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class   FormActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private lateinit var etNim: EditText
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etNomorHp: EditText
    private lateinit var btnSimpan: Button
    private var editId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        db = DatabaseHelper(this)

        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etJurusan = findViewById(R.id.etJurusan)
        etNomorHp = findViewById(R.id.etNomorHp)
        btnSimpan = findViewById(R.id.btnSimpan)

        val judul = findViewById<TextView>(R.id.tvJudulForm)
        val id = intent.getIntExtra(EXTRA_ID, -1)
        val sumber = intent.getStringExtra(EXTRA_SUMBER) ?: "tidak diketahui"

        if (id > 0) {
            db.getMahasiswaById(id)?.let { isiForm(it) }
        }

        if (editId == null) {
            judul.text = "Tambah Data (dari $sumber)"
        } else {
            judul.text = "Ubah Data (dari $sumber)"
        }

        btnSimpan.setOnClickListener { simpan() }
        findViewById<Button>(R.id.btnBatal).setOnClickListener { finish() }
    }

    private fun isiForm(mhs: Mahasiswa) {
        editId = mhs.id
        etNim.setText(mhs.nim)
        etNama.setText(mhs.nama)
        etAlamat.setText(mhs.alamat)
        etJurusan.setText(mhs.jurusan)
        etNomorHp.setText(mhs.nomorHp)
        btnSimpan.text = "Perbarui"
    }

    private fun simpan() {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val jurusan = etJurusan.text.toString().trim()
        val nomorHp = etNomorHp.text.toString().trim()

        if (!validasi(nim, nama, nomorHp)) return

        val mhs = Mahasiswa(
            id = editId ?: 0,
            nim = nim,
            nama = nama,
            alamat = alamat,
            jurusan = jurusan,
            nomorHp = nomorHp
        )

        val berhasil = if (editId == null) {
            db.insertMahasiswa(mhs) != -1L
        } else {
            db.updateMahasiswa(mhs) > 0
        }

        if (berhasil) {
            val aksi = if (editId == null) "ditambahkan" else "diperbarui"
            Toast.makeText(this, "Data berhasil $aksi", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validasi(nim: String, nama: String, nomorHp: String): Boolean {
        etNim.error = null
        etNama.error = null
        etNomorHp.error = null

        var valid = true

        if (nim.isEmpty()) {
            etNim.error = "NIM wajib diisi"
            valid = false
        } else if (nim.length < 5 || !nim.all { it.isDigit() }) {
            etNim.error = "NIM minimal 5 digit angka"
            valid = false
        } else if (db.isNimTerdaftar(nim, editId ?: 0)) {
            etNim.error = "NIM sudah terdaftar"
            valid = false
        }

        if (nama.isEmpty()) {
            etNama.error = "Nama wajib diisi"
            valid = false
        }

        if (nomorHp.isNotEmpty() && !hpValid(nomorHp)) {
            etNomorHp.error = "Nomor HP tidak valid (contoh 081234567890)"
            valid = false
        }

        return valid
    }

    private fun hpValid(hp: String): Boolean =
        hp.startsWith("08") &&
                hp.length in 10..13 &&
                hp.all { it.isDigit() }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_SUMBER = "sumber"
    }
}