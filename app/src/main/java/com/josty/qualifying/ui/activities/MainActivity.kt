package com.josty.qualifying.ui.activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.ui.adapters.StockItemDetailsLookup
import com.josty.qualifying.ui.adapters.StocksAdapter
import com.josty.qualifying.ui.dialogs.ExchangesDialog
import com.josty.qualifying.ui.dialogs.NetworkDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.models.StockSymbol
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var stocksAdapter: StocksAdapter<StockSymbol>
    private lateinit var searchAdapter: StocksAdapter<SymbolLookupInfo>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var stockModels: List<StockSymbol>
    private lateinit var searchModels: List<SymbolLookupInfo>
    private val apiClient: DefaultApi = App::apiClient.invoke(App())
    private var connection = false
    private lateinit var selectedStockModels: List<StockSymbol>
    private lateinit var selectedSearchModels: List<SymbolLookupInfo>
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var searchTracker: SelectionTracker<Long>
    private val appBar: AppBarLayout get() = binding.appbar


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
        binding.refresh.setOnRefreshListener {}*/
        layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.stocks.layoutManager = layoutManager

        stocksAdapter = StocksAdapter(this, apiClient)
        searchAdapter = StocksAdapter(this)
        binding.stocks.adapter = stocksAdapter

        GlobalScope.launch {
            stockModels = if (connection)
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
                stocksAdapter.setModels(stockModels)
                binding.progressBar.visibility = View.GONE
            }
        }

        searchTracker = SelectionTracker.Builder(
            "Search",
            binding.stocks,
            StableIdKeyProvider(binding.stocks),
            StockItemDetailsLookup(binding.stocks),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker = SelectionTracker.Builder(
            "mySelection",
            binding.stocks,
            StableIdKeyProvider(binding.stocks),
            StockItemDetailsLookup(binding.stocks),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        stocksAdapter.tracker = tracker
        searchAdapter.tracker = searchTracker

        val observer = object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                if (binding.stocks.adapter == stocksAdapter) {
                    val items = tracker.selection.size()
                    selectedStockModels =
                        tracker.selection.map { l: Long? -> stockModels[l!!.toInt()] }

                    Log.d("[Debug]", "StockObserver $selectedStockModels")
                }
                if (binding.stocks.adapter == searchAdapter) {
                    val items = tracker.selection.size()
                    selectedSearchModels =
                        tracker.selection.map { l: Long? -> searchModels[l!!.toInt()] }

                    Log.d("[Debug]", "StockObserver $selectedSearchModels")
                }
            }
        }

        tracker.addObserver(observer)
        searchTracker.addObserver(observer)

        binding.search.addTextChangedListener {
            fun onTextChanged(text: CharSequence) {
                if (text.isEmpty()) {
                    binding.stocks.swapAdapter(stocksAdapter, false)
                }
            }
        }
        binding.search.setOnKeyListener { view, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                GlobalScope.launch {
                    searchModels = apiClient.symbolSearch(binding.search.text.toString()).result!!
                    runOnUiThread {
                        searchAdapter.setModels(searchModels)
                        binding.stocks.swapAdapter(searchAdapter, false)
                    }
                }
            }
            true
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
                stockModels = apiClient.stockSymbols(exchange, mic, securityType, currency)
            } catch (e: ClientException) {
                Log.e("[Finnhub]", "ClientError: 429")
            }
            runOnUiThread {
                stocksAdapter.setModels(stockModels)
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