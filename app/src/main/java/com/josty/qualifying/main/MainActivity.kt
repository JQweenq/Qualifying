package com.josty.qualifying.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.ActivityMainBinding
import com.josty.qualifying.main.dialogs.NetworkDialog
import com.josty.qualifying.search.SearchFragment
import com.josty.qualifying.websocket.WebSocketFragment
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var searchFragment: SearchFragment? = null
    private val appBar: AppBarLayout get() = binding.appbar
    private val pagerAdapter: FragmentStateAdapter = ViewPagerAdapter(this)

    companion object {
        var selectedList: List<StockSymbol>? = null
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        appBar.setExpanded(true)
        /*setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)*/

        binding.pager.adapter = pagerAdapter
        binding.pager.setCurrentItem(1, false)
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = getString(
                when (position) {
                    0 -> R.string.search
                    1 -> R.string.main
                    2 -> R.string.real_time
                    else -> 0
                }
            )
        }.attach()

        if (!App.hasConnection(this))
            NetworkDialog().show(supportFragmentManager, "network")

        binding.search.setOnFocusChangeListener { _, bool ->
            if (bool) binding.pager.setCurrentItem(
                0,
                true
            )
        }

        binding.search.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchFragment?.search(binding.search.text.toString())
            }
            true
        }
    }

    /*fun setModels(exchange: String, mic: String, securityType: String, currency: String) {
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
    }*/
/*
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
//                ExchangesDialog(::setModels, this).show(supportFragmentManager, "exchanges")
                super.onOptionsItemSelected(item)
            }
            R.id.search -> {
                true
            }
            else -> {
                true
            }
        }
    }*/

    private inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> {
                this@MainActivity.searchFragment = SearchFragment()
                this@MainActivity.searchFragment!!
            }
            1 -> MainFragment()
            2 -> WebSocketFragment()
            else -> {
                MainFragment()
            }
        }
    }
}