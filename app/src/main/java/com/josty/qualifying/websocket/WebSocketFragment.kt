package com.josty.qualifying.websocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.databinding.FragmentWebSocketBinding
import com.josty.qualifying.main.MainActivity
import com.josty.qualifying.websocket.adapter.WebSocketAdapter
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class WebSocketFragment : Fragment() {
    private lateinit var binding: FragmentWebSocketBinding
    private lateinit var adapter: WebSocketAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWebSocketBinding.inflate(layoutInflater)
        layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        adapter = WebSocketAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = layoutManager

        MainActivity.selectedList?.let { adapter.setModels(it) }

        return binding.root
    }
}