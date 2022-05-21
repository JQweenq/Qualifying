package com.josty.qualifying.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.ui.activities.MainActivity
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.models.StockSymbol
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import kotlin.reflect.KMutableProperty1

class StocksAdapter(private val ctx: Activity, private val client: DefaultApi) :
    RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

    private var models: List<StockSymbol> = listOf()
//    private var ws: WebSocket
    private lateinit var job: Job

    /*init {
        val log = HttpLoggingInterceptor()
        log.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=${ctx.token}")
            .build()

        ws = okHttpClient.newWebSocket(request, WebSocketListener())
    }*/

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

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        println("job canceled")
        job.cancel()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.title.text = models[position].displaySymbol
        job = GlobalScope.launch {
            var price = "Error"
            try {
                price =
                    "${models[position].currency}: ${client.quote(models[position].symbol.toString()).c}"
            } catch (ex: SocketTimeoutException) {
                Log.e("[Finnhub]", "SocketTimeout")
            } catch (e: ClientException) {
                Log.e("[Finnhub]", "ClientError: 429")
            }
            ctx.runOnUiThread {
                holder.price.text = price
            }
        }
        println("job started")
    }

    override fun getItemCount(): Int = models.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)

    }

    class WebSocketListener : okhttp3.WebSocketListener() {

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