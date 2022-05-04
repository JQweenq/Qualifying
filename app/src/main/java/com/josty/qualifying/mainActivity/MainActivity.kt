package com.josty.qualifying.mainActivity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.adapters.StocksAdapter
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.dialogs.ExchangesDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StocksAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var apiClient: DefaultApi
    private lateinit var models: List<StockSymbol>

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


        val exchangesDialog = ExchangesDialog()

        binding.showExchanges.setOnClickListener {
            exchangesDialog.show(supportFragmentManager, "exchanges")
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

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.stocks.layoutManager = layoutManager

        adapter = StocksAdapter(this, apiClient)
        binding.stocks.adapter = adapter

        GlobalScope.launch {
            models = apiClient.stockSymbols("US", "", "", "")
            runOnUiThread {
                adapter.setModels(models)
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}