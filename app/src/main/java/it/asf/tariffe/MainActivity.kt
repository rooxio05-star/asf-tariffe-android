package it.asf.tariffe

import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var etOrigin: MaterialAutoCompleteTextView
    private lateinit var etDestination: MaterialAutoCompleteTextView
    private lateinit var btnSearch: Button
    private lateinit var rv: RecyclerView
    private lateinit var tvStatus: TextView

    private lateinit var db: Db
    private lateinit var adapter: SolutionsAdapter
    private var stopList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etOrigin = findViewById(R.id.etOrigin)
        etDestination = findViewById(R.id.etDestination)
        btnSearch = findViewById(R.id.btnSearch)
        rv = findViewById(R.id.rvSolutions)
        tvStatus = findViewById(R.id.tvStatus)

        adapter = SolutionsAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        db = Db(this)

        loadStops()

        btnSearch.setOnClickListener {
            val oIn = etOrigin.text?.toString().orEmpty()
            val dIn = etDestination.text?.toString().orEmpty()
            if (oIn.isBlank() || dIn.isBlank()) {
                tvStatus.text = "Inserisci Origine e Destinazione."
                adapter.submit(emptyList())
                return@setOnClickListener
            }
            val solutions = findSolutions(oIn, dIn)
            tvStatus.text = "Soluzioni trovate: ${solutions.size}"
            adapter.submit(solutions)
        }
    }

    private fun loadStops() {
        val conn = db.open()
        val list = ArrayList<String>()
        conn.rawQuery("SELECT stop_name FROM stops ORDER BY stop_name", null).use { c ->
            while (c.moveToNext()) list.add(c.getString(0))
        }
        conn.close()
        stopList = list

        val aa = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stopList)
        etOrigin.setAdapter(aa)
        etDestination.setAdapter(aa)
        etOrigin.threshold = 1
        etDestination.threshold = 1
    }

    private fun normalize(input: String): String {
        val s = input.trim().uppercase()
        val conn = db.open()
        val canonical = conn.rawQuery("SELECT stop_name FROM aliases WHERE alias = ? LIMIT 1", arrayOf(s)).use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }
        conn.close()
        return canonical ?: s
    }

    private fun parseJsonList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            val out = ArrayList<String>(arr.length())
            for (i in 0 until arr.length()) out.add(arr.getString(i))
            out
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun findSolutions(originIn: String, destinationIn: String): List<Solution> {
        val origin = normalize(originIn)
        val destination = normalize(destinationIn)

        val conn = db.open()
        val out = ArrayList<Solution>()
        val sql = """
            SELECT origin, destination, tariff, via_json, lines_json, changes, source, is_primary
            FROM solutions
            WHERE origin = ? AND destination = ?
            ORDER BY is_primary DESC, changes ASC, tariff ASC
        """.trimIndent()

        conn.rawQuery(sql, arrayOf(origin, destination)).use { c ->
            while (c.moveToNext()) out.add(readSolution(c))
        }
        conn.close()
        return out
    }

    private fun readSolution(c: Cursor): Solution {
        val origin = c.getString(0)
        val destination = c.getString(1)
        val tariff = if (c.isNull(2)) null else c.getInt(2)
        val via = parseJsonList(if (c.isNull(3)) null else c.getString(3))
        val lines = parseJsonList(if (c.isNull(4)) null else c.getString(4))
        val changes = if (c.isNull(5)) null else c.getInt(5)
        val source = if (c.isNull(6)) null else c.getString(6)
        val isPrimary = !c.isNull(7) && c.getInt(7) == 1
        return Solution(origin, destination, tariff, lines, via, changes, isPrimary, source)
    }
}
