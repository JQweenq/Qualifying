package com.josty.qualifying.application

import android.app.Application
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient

class App : Application() {
    companion object{
        const val TOKEN = "c9aj2eiad3i8qngr305g"
    }

    var apiClient: DefaultApi

    init{
        ApiClient.apiKey["token"] = TOKEN
        apiClient = DefaultApi()
    }
}