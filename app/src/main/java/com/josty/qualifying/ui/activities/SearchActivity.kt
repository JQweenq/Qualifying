package com.josty.qualifying.ui.activities

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.ActivitySearchBinding
import com.josty.qualifying.ui.adapters.SearchAdapter
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var models: List<SymbolLookupInfo>
    private val apiClient = App::apiClient.invoke(App())
    private var connection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        handleIntent(intent)

        layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = layoutManager

        adapter = SearchAdapter(this)
        binding.recycler.adapter = adapter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query != null)
                GlobalScope.launch {
                    models = apiClient.symbolSearch(query).result!!
                    runOnUiThread {
                        adapter.setModels(models)
                    }
                }
        }
    }
}