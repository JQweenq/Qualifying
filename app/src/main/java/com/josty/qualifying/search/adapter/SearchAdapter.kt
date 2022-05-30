package com.josty.qualifying.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.R
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job

@DelicateCoroutinesApi
class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var list: List<SymbolLookupInfo>? = null
    private var job: Job? = null
    lateinit var tracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(l: List<SymbolLookupInfo>) {
        this.list = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = list?.get(position)

        holder.title.text = symbol?.displaySymbol
        holder.description.text = symbol?.description
        tracker.let {
            holder.setActivated(it.isSelected(position.toLong()))
        }
    }

    override fun getItemCount(): Int = if (list.isNullOrEmpty()) 0 else list!!.size
    override fun getItemId(position: Int) = position.toLong()
    override fun onViewDetachedFromWindow(holder: ViewHolder) =
        if (job != null) job!!.cancel() else Unit

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long = itemId
            }

        fun setActivated(isActivated: Boolean = false) {
            itemView.isActivated = isActivated
        }
    }
}