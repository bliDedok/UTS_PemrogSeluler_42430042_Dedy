package com.example.utspemrogseluler

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PanelActivity : AppCompatActivity() {

    private lateinit var tvSapaan: TextView
    private lateinit var tvBadgeStatus: TextView
    private lateinit var tvJumlahPreview: TextView
    private lateinit var etJumlahMahasiswa: EditText
    private lateinit var etRataRataNilai: EditText
    private lateinit var btnProses: Button
    private lateinit var btnReset: Button
    private lateinit var tvStatusKelas: TextView
    private lateinit var tableAbsen: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel)

        tvSapaan = findViewById(R.id.tvSapaan)
        tvBadgeStatus = findViewById(R.id.tvBadgeStatus)
        tvJumlahPreview = findViewById(R.id.tvJumlahPreview)
        etJumlahMahasiswa = findViewById(R.id.etJumlahMahasiswa)
        etRataRataNilai = findViewById(R.id.etRataRataNilai)
        btnProses = findViewById(R.id.btnProses)
        btnReset = findViewById(R.id.btnReset)
        tvStatusKelas = findViewById(R.id.tvStatusKelas)
        tableAbsen = findViewById(R.id.tableAbsen)

        val namaDosen = intent.getStringExtra("NAMA_DOSEN") ?: "-"
        tvSapaan.text = "Selamat bertugas, Dosen $namaDosen"

        btnProses.setOnClickListener {
            prosesData()
        }

        btnReset.setOnClickListener {
            etJumlahMahasiswa.text.clear()
            etRataRataNilai.text.clear()
            tvBadgeStatus.text = "Belum Diproses"
            tvJumlahPreview.text = "0"
            tvStatusKelas.text = "Status Kelas: -"
            clearTableRows()
        }
    }

    private fun prosesData() {
        val jumlahStr = etJumlahMahasiswa.text.toString().trim()
        val rataStr = etRataRataNilai.text.toString().trim()

        if (jumlahStr.isEmpty() || rataStr.isEmpty()) {
            Toast.makeText(this, "Semua input wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahMahasiswa = jumlahStr.toIntOrNull()
        val rataRataNilai = rataStr.toDoubleOrNull()

        if (jumlahMahasiswa == null || rataRataNilai == null) {
            Toast.makeText(this, "Masukkan angka yang valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (jumlahMahasiswa <= 0) {
            Toast.makeText(this, "Jumlah mahasiswa harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (rataRataNilai !in 0.0..100.0) {
            Toast.makeText(this, "Rata-rata nilai harus 0 sampai 100", Toast.LENGTH_SHORT).show()
            return
        }

        val statusKelas = if (rataRataNilai >= 80) {
            "Sangat Baik"
        } else if (rataRataNilai >= 60) {
            "Cukup"
        } else {
            "Kurang"
        }

        tvBadgeStatus.text = statusKelas
        tvJumlahPreview.text = jumlahMahasiswa.toString()
        tvStatusKelas.text = "Status Kelas: $statusKelas"

        clearTableRows()

        for (i in 1..jumlahMahasiswa) {
            val row = TableRow(this)

            val params = TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = dp(8)
            row.layoutParams = params

            val kehadiran = when {
                i % 10 == 0 -> "Alfa"
                i % 5 == 0 -> "Izin"
                else -> "Hadir"
            }

            val nilai = when {
                i % 7 == 0 -> rataRataNilai - 8
                i % 5 == 0 -> rataRataNilai - 4
                i % 3 == 0 -> rataRataNilai + 3
                else -> rataRataNilai + 1
            }.coerceIn(0.0, 100.0)

            row.addView(createCell(i.toString(), 56))
            row.addView(createCell("Mahasiswa $i", 170))
            row.addView(createCell(kehadiran, 120))
            row.addView(createCell(String.format("%.0f", nilai), 90))

            tableAbsen.addView(row)
        }
    }

    private fun clearTableRows() {
        while (tableAbsen.childCount > 1) {
            tableAbsen.removeViewAt(1)
        }
    }

    private fun createCell(textValue: String, minWidthDp: Int): TextView {
        val tv = TextView(this)
        val lp = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        lp.marginEnd = dp(8)
        tv.layoutParams = lp
        tv.text = textValue
        tv.minWidth = dp(minWidthDp)
        tv.setPadding(dp(12), dp(12), dp(12), dp(12))
        tv.background = ContextCompat.getDrawable(this, R.drawable.bg_table_cell)
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        tv.textSize = 14f
        return tv
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}