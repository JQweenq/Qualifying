package com.josty.qualifying.application

import android.app.Application
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient

class App : Application() {
    val token = "c9aj2eiad3i8qngr305g"
    var apiClient: DefaultApi

    init{
        ApiClient.apiKey["token"] = token
        apiClient = DefaultApi()
    }
}