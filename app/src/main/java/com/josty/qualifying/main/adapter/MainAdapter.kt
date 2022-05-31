package com.josty.qualifying.main.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.R
import com.josty.qualifying.stock.StockActivity
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.StockSymbol
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job

@DelicateCoroutinesApi
class MainAdapter(ctx: FragmentActivity) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private val ctx: FragmentActivity
    private var client: DefaultApi? = null
    private var list: List<StockSymbol>? = null
    private var job: Job? = null
    lateinit var tracker: SelectionTracker<Long>

    init {
        this.ctx = ctx
        setHasStableIds(true)
    }

    constructor(ctx: FragmentActivity, client: DefaultApi) : this(ctx) {
        this.client = client
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(l: List<StockSymbol>) {
        this.list = l
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = list?.get(position)
        holder.title.text = "SYM: ${symbol?.displaySymbol}"
        holder.description.text = symbol?.description
        holder.mic.text = "MIC: ${symbol?.mic}"
        holder.itemView.setOnClickListener {
            val int = Intent(ctx, StockActivity::class.java)
            int.putExtra("SYMBOL", list?.get(position)!!.symbol)
            int.putExtra("MIC", list?.get(position)!!.mic)
            ctx.startActivity(int)
        }
        tracker.let {
            holder.setActivated(it.isSelected(position.toLong()))
        }

        /*job = GlobalScope.launch {
            var price = "Error"
            try {
                price =
                    "${symbol.currency}: ${client?.quote(symbol.symbol.toString())!!.c}"
            } catch (ex: SocketTimeoutException) {
                Log.e("[Finnhub]", "SocketTimeout")
            } catch (e: ClientException) {
                Log.e("[Finnhub]", "ClientError: 429")
            } catch (e: ServerException) {
                Log.e("[Finnhub]", "Server error: 503")
            }
            ctx.runOnUiThread {
                holder.price.text = price
            }
        }*/

    }

    override fun getItemCount(): Int = if (list.isNullOrEmpty()) 0 else list!!.size
    override fun getItemId(position: Int) = position.toLong()
    override fun onViewDetachedFromWindow(holder: ViewHolder) =
        if (job != null) job!!.cancel() else Unit

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val mic: TextView = itemView.findViewById(R.id.mic)

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