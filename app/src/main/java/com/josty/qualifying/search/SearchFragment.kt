package com.josty.qualifying.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.FragmentSearchBinding
import com.josty.qualifying.main.MainActivity
import com.josty.qualifying.search.adapter.SearchAdapter
import com.josty.qualifying.search.adapter.SearchItemDetailsLookup
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.StockSymbol
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private lateinit var adapter: SearchAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var list: List<SymbolLookupInfo>
    private lateinit var selectedList: List<StockSymbol>
    private lateinit var tracker: SelectionTracker<Long>
    private val apiClient: DefaultApi = App::apiClient.invoke(App())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = layoutManager

        adapter = SearchAdapter()
        binding.recycler.adapter = adapter

        tracker = SelectionTracker.Builder(
            "Search",
            binding.recycler,
            StableIdKeyProvider(binding.recycler),
            SearchItemDetailsLookup(binding.recycler),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    selectedList =
                        tracker.selection.map { l: Long? ->
                            StockSymbol(
                                list[l!!.toInt()].description,
                                list[l.toInt()].displaySymbol,
                                list[l.toInt()].symbol,
                                list[l.toInt()].type
                            )
                        }
                    MainActivity.selectedList = selectedList
                    Log.d("[Debug]", "StockObserver $selectedList")
                }
            }
        )
        return binding.root
    }

    fun search(query: String) {
        GlobalScope.launch {
            list = apiClient.symbolSearch(query).result!!
            requireActivity().runOnUiThread {
                adapter.setModels(list)
            }
        }
    }
}
