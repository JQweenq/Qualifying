package com.josty.qualifying.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.MainActivity
import com.josty.qualifying.R
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StocksAdapter(val ctx: MainActivity): RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    private lateinit var models: List<StockSymbol>

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(m: List<StockSymbol>) {
        this.models = m
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_stocks, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlobalScope.launch {
            holder.price.text = models[position].symbol?.let { ctx.getQuoteAsync(it).c.toString() }
        }

    }

    override fun getItemCount() = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val price: TextView

        init {
            price = itemView.findViewById(R.id.price)
        }
    }
}