package it.asf.tariffe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SolutionsAdapter(private var items: List<Solution>) : RecyclerView.Adapter<SolutionsAdapter.VH>() {

    fun submit(newItems: List<Solution>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvLinee: TextView = v.findViewById(R.id.tvLinee)
        val tvTariffa: TextView = v.findViewById(R.id.tvTariffa)
        val tvVia: TextView = v.findViewById(R.id.tvVia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_solution, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        val badge = if (s.isPrimary) " (PRINCIPALE)" else ""
        holder.tvLinee.text = (if (s.lines.isNotEmpty()) s.lines.joinToString(" + ") else "—") + badge
        holder.tvTariffa.text = "Tariffa: " + (s.tariff?.toString() ?: "—") + " | Cambi: " + (s.changes?.toString() ?: "—")
        holder.tvVia.text = if (s.via.isNotEmpty()) "Via: " + s.via.joinToString(" → ") else ""
    }
}
