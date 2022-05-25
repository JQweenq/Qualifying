package com.josty.qualifying.ui.activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.ui.adapters.StocksAdapter
import com.josty.qualifying.ui.dialogs.ExchangesDialog
import com.josty.qualifying.ui.dialogs.NetworkDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StocksAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var models: List<StockSymbol>
    private val apiClient: DefaultApi = App::apiClient.invoke(App())
    private var connection = false
    val appBar: AppBarLayout
        get() = binding.appbar


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        appBar.setExpanded(true)
        /*setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)*/

        if (!hasConnection())
            NetworkDialog().show(supportFragmentManager, "network")

        /*binding.refresh.setColorSchemeColors(R.color.red_500)
        binding.refresh.setOnRefreshListener {

        }*/
        layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.stocks.layoutManager = layoutManager

        adapter = StocksAdapter(this, apiClient)
        binding.stocks.adapter = adapter

        GlobalScope.launch {
            models = if (connection)
                try {
                    apiClient.stockSymbols("US", "", "", "")
                } catch (e: ClientException) {
                    Log.e("[Finnhub]", "ClientError: 429")
                    delay(60000)
                    apiClient.stockSymbols("US", "", "", "")
                }
            else
                listOf()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_exchanges -> {
                ExchangesDialog(::setModels, this).show(supportFragmentManager, "exchanges")
                super.onOptionsItemSelected(item)
            }
            R.id.search -> {
                true
            }
            else -> {
                true
            }
        }
    }
}