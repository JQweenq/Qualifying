package com.josty.qualifying.application

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient

class App : Application() {
    companion object {
        const val TOKEN = "c9aj2eiad3i8qngr305g"

        fun hasConnection(ctx: Context): Boolean {
            val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (wifiInfo != null && wifiInfo.isConnected) {
                return true
            }
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (wifiInfo != null && wifiInfo.isConnected) {
                return true
            }
            wifiInfo = cm.activeNetworkInfo
            if (wifiInfo != null && wifiInfo.isConnected) {
                return true
            }
            return false
        }
    }

    var apiClient: DefaultApi

    init {
        ApiClient.apiKey["token"] = TOKEN
        apiClient = DefaultApi()
    }
}