package com.josty.qualifying.mainActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.adapters.StocksAdapter
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.dialogs.ExchangesDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.models.StockSymbol
import java.util.concurrent.Executors


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

        // api client for finnhub
        ApiClient.apiKey["token"] = token
        apiClient = DefaultApi()

        val exchangesDialog = ExchangesDialog(arrayOf("zxc", "asd", "qwe"))

        binding.showExchanges.setOnClickListener {
            exchangesDialog.show(supportFragmentManager, "exchanges")
        }

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


        Executors.newSingleThreadExecutor().execute {
            models = apiClient.stockSymbols("US", "", "", "")
            runOnUiThread {
                adapter.setModels(models)
            }
        }
    }
}