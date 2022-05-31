package com.josty.qualifying.websocket.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor

@DelicateCoroutinesApi
class WebSocketAdapter : RecyclerView.Adapter<WebSocketAdapter.ViewHolder>() {

    private var list: List<StockSymbol>? = null

//    private var ws: WebSocket
    private lateinit var job: Job

    init {
        /*val log = HttpLoggingInterceptor()
        log.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=${App.TOKEN}")
            .build()

        ws = okHttpClient.newWebSocket(request, WebSocketListener())*/
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(l: List<StockSymbol>) {
        this.list = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)

        return ViewHolder(view)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        /*println("job canceled")
        job.cancel()*/
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list?.get(position)!!.displaySymbol

        /*job = GlobalScope.launch {
            ws.send("{\"type\":\"subscribe\",\"symbol\":\"${list?.get(position)!!.symbol}\"}")
        }*/
    }

    override fun getItemCount(): Int = if (list.isNullOrEmpty()) 0 else list!!.size
    override fun getItemViewType(position: Int): Int = position

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
    }

    private inner class WebSocketListener : okhttp3.WebSocketListener() {
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