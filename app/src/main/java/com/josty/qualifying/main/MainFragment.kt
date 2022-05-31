package com.josty.qualifying.main

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
import com.josty.qualifying.databinding.FragmentMainBinding
import com.josty.qualifying.main.adapter.MainItemDetailsLookup
import com.josty.qualifying.main.adapter.MainAdapter
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.infrastructure.ServerException
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private lateinit var adapter: MainAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var list: List<StockSymbol>
    private lateinit var selectedList: List<StockSymbol>
    private lateinit var tracker: SelectionTracker<Long>

    private val apiClient: DefaultApi = App::apiClient.invoke(App())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)

        layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        binding.recycler.layoutManager = layoutManager

        adapter = MainAdapter(requireActivity(), apiClient)
        binding.recycler.adapter = adapter

        if(App.hasConnection(requireContext()))
            GlobalScope.launch {
                list = try {
                    apiClient.stockSymbols("US", "", "", "")
                } catch (e: ClientException) {
                    Log.e("[Finnhub]", "ClientError: 429")
                    delay(60000)
                    apiClient.stockSymbols("US", "", "", "")
                } catch (e: ServerException) {
                    Log.e("[Finnhub]", "Server error: 502")
                    delay(60000)
                    apiClient.stockSymbols("US", "", "", "")
                }
                requireActivity().runOnUiThread {
                    adapter.setModels(list)
                    binding.progress.visibility = View.GONE
                }
            }
        else {
            adapter.setModels(list)
            binding.progress.visibility = View.GONE
        }

        tracker = SelectionTracker.Builder(
            "Main",
            binding.recycler,
            StableIdKeyProvider(binding.recycler),
            MainItemDetailsLookup(binding.recycler),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val items = tracker.selection.size()
                    selectedList =
                        tracker.selection.map { l: Long? -> list[l!!.toInt()] }

                    Log.d("[Debug]", "StockObserver $selectedList")
                }
            }
        )
        return binding.root
    }
}