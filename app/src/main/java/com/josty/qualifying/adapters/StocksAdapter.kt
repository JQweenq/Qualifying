package com.josty.qualifying.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.R

class StocksAdapter(val ctx: Context, val models: Array<String>): RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_stocks, parent)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.price.text = "text"
    }

    override fun getItemCount() = models.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val price: TextView

        init {
            price = itemView.findViewById(R.id.price)
        }
    }
}