package com.josty.qualifying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.josty.qualifying.adapters.StocksAdapter
import com.josty.qualifying.databinding.ActivityMainBinding
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ApiClient.apiKey["token"] = "c9aj2eiad3i8qngr305g"
        val apiClient = DefaultApi()

        Thread(
            Runnable {
                println(apiClient.stockSymbols("US", "", "", "")) // получить символы акций

                println(apiClient.quote("AAPL")) // получить акцию
                /*
                * c - текущая цена
                * d - сдача
                * dp - процентное изменение
                * h - наибольшая цена за сутки
                * l - наименьшая цена за сутки
                * o - цена в начале дня
                * pc - предыдущая цена
                */
            }
        ).start()
    }
}