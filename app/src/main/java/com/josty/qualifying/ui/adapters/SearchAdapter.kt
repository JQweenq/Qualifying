package com.josty.qualifying.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.R
import io.finnhub.api.models.SymbolLookupInfo

class SearchAdapter(private val ctx: Activity) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var models: List<SymbolLookupInfo> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(m: List<SymbolLookupInfo>) {
        this.models = m
        println("[RecyclerView] new models: $models")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_search, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.title.text = models[position].displaySymbol
        holder.description.text = models[position].description
    }

    override fun getItemCount(): Int = models.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)

    }
}