package com.josty.qualifying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.adapters.StocksAdapter
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.dialogs.ExchangesDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.models.Quote
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StocksAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var apiClient: DefaultApi
    private lateinit var models: List<StockSymbol>
    val token = "c9aj2eiad3i8qngr305g"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        ApiClient.apiKey["token"] = token

        apiClient = DefaultApi()
        val exchangesDialog = ExchangesDialog(arrayOf("zxc", "asd", "qwe"))

        binding.showExchanges.setOnClickListener {
            println("[Clicked]")
            exchangesDialog.show(supportFragmentManager, "exchanges")
        }

        val log = HttpLoggingInterceptor()
        log.setLevel(HttpLoggingInterceptor.Level.BODY)


        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=$token")
            .build()

        val ws = okHttpClient.newWebSocket(request, WebSocketListener())


/*        Thread {
            println("[Exchanges] ${apiClient.forexExchanges()}")
//                println(apiClient.stockSymbols("US", "", "", "")) // получить символы акций

//                println(apiClient.quote("AAPL")) // получить акцию
            *//*
            * c - текущая цена
            * d - сдача
            * dp - процентное изменение
            * h - наибольшая цена за сутки
            * l - наименьшая цена за сутки
            * o - цена в начале дня
            * pc - предыдущая цена
            *//*
        }.start()*/

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.stocks.layoutManager = layoutManager

        adapter = StocksAdapter(this)
        binding.stocks.adapter = adapter

        adapter.setModels(models)
    }

    suspend fun getQuoteAsync(stock: String): Quote{
        return withContext(Dispatchers.IO) {
            apiClient.quote(stock)
        }
    }

    suspend fun getStocksAsync(): List<StockSymbol> {
        return withContext(Dispatchers.IO) {
            apiClient.stockSymbols("US", "", "", "")
        }
    }
    private class WebSocketListener: okhttp3.WebSocketListener(){

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