package com.josty.qualifying.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.mainActivity.MainActivity
import com.josty.qualifying.R
import io.finnhub.api.models.StockSymbol
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

class StocksAdapter(ctx: MainActivity): RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    private var models: List<StockSymbol> = listOf()
    private var ws: WebSocket
    init{
        val log = HttpLoggingInterceptor()
        log.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=${ctx.token}")
            .build()

        ws = okHttpClient.newWebSocket(request, WebSocketListener())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(m: List<StockSymbol>) {
        this.models = m
        println("[RecyclerView] new models: $models")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.model_stocks, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        println("[Recycler] binding: ${models.get(position).symbol}")
//        println("[Recycler] binding: ${models[position]}")

        holder.price.text = models.get(position).symbol

//        ws.send("{\"type\":\"subscribe\",\"symbol\":\"${models.get(position).symbol}\"}")

    }

    override fun getItemCount(): Int = models.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val price: TextView

        init {
            price = itemView.findViewById(R.id.price)
        }
    }

    class WebSocketListener: okhttp3.WebSocketListener(){

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            println("[onClosed] $code")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            println("[onMessage] $text")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            println("[onOpen]")
        }
    }
}