package com.josty.qualifying.ui.activities

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.ui.adapters.StocksAdapter
import com.josty.qualifying.ui.dialogs.ExchangesDialog
import com.josty.qualifying.ui.dialogs.NetworkDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StocksAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var apiClient: DefaultApi
    private lateinit var models: List<StockSymbol>
    private var connection = false

    val token = "c9aj2eiad3i8qngr305g"

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        // api client for finnhub
        ApiClient.apiKey["token"] = token
        apiClient = DefaultApi()

        if (!hasConnection())
            NetworkDialog().show(supportFragmentManager, "network")

        binding.showExchanges.setOnClickListener {
            ExchangesDialog(::setModels, this).show(supportFragmentManager, "exchanges")
        }

        /* apiClient.stockSymbols("US", "", "", "") // получить символы акций
        apiClient.quote("AAPL")) // получить акцию
            * c - текущая цена
            * d - сдача
            * dp - процентное изменение
            * h - наибольшая цена за сутки
            * l - наименьшая цена за сутки
            * o - цена в начале дня
            * pc - предыдущая цена */

//        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.stocks.layoutManager = layoutManager

        adapter = StocksAdapter(this, apiClient)
        binding.stocks.adapter = adapter

        GlobalScope.launch {
            if (connection)
                try {
                    models = apiClient.stockSymbols("US", "", "", "")
                } catch (e: ClientException) {
                    Log.e("[Finnhub]", "ClientError: 429")
                    delay(60000)
                    models = apiClient.stockSymbols("US", "", "", "")
                }
            else
                models = listOf()
            runOnUiThread {
                adapter.setModels(models)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun hasConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            this.connection = true
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            this.connection = true
            return true
        }
        wifiInfo = cm.activeNetworkInfo
        if (wifiInfo != null && wifiInfo.isConnected) {
            this.connection = true
            return true
        }
        this.connection = false
        return false
    }

    fun setModels(exchange: String, mic: String, securityType: String, currency: String) {
        if (connection) {
            try {
                models = apiClient.stockSymbols(exchange, mic, securityType, currency)
            } catch (e: ClientException) {
                Log.e("[Finnhub]", "ClientError: 429")
            }
            runOnUiThread {
                adapter.setModels(models)
            }
        }
    }
}