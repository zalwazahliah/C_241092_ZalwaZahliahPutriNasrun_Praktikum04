package com.veltix.praktikum04

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelper
    private var mahasiswa: Mahasiswa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = DatabaseHelper(this)

        findViewById<Button>(R.id.btnUbah).setOnClickListener {
            val id = idDariIntent()
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra(FormActivity.EXTRA_ID, id)
            intent.putExtra(FormActivity.EXTRA_SUMBER, "detail")
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnHapus).setOnClickListener {
            konfirmasiHapus()
        }

        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanData()
    }

    private fun idDariIntent(): Int =
        intent.getIntExtra(EXTRA_ID, -1)

    private fun tampilkanData() {
        val mhs = db.getMahasiswaById(idDariIntent())
        if (mhs == null) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mahasiswa = mhs
        findViewById<TextView>(R.id.tvNim).text = mhs.nim
        findViewById<TextView>(R.id.tvNama).text = mhs.nama
        findViewById<TextView>(R.id.tvAlamat).text = mhs.alamat
        findViewById<TextView>(R.id.tvJurusan).text = mhs.jurusan
        findViewById<TextView>(R.id.tvNomorHp).text = mhs.nomorHp
    }

    private fun konfirmasiHapus() {
        val mhs = mahasiswa ?: return

        AlertDialog.Builder(this)
            .setTitle("Hapus data")
            .setMessage("Hapus ${mhs.nama} (${mhs.nim})?")
            .setPositiveButton("Hapus") { _, _ ->
                if (db.deleteMahasiswa(mhs.id) > 0) {
                    Toast.makeText(
                        this,
                        "Data berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal menghapus data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}