package com.josty.qualifying.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.josty.qualifying.ui.activities.MainActivity
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.infrastructure.ServerException
import io.finnhub.api.models.StockSymbol
import io.finnhub.api.models.SymbolLookupInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import kotlin.reflect.KMutableProperty1

@DelicateCoroutinesApi
class StocksAdapter<T>(ctx: Activity) :
    RecyclerView.Adapter<StocksAdapter.ViewHolder>() {


    private val ctx: Activity
    private var client: DefaultApi? = null
    private var models: List<T>? = null
    private var job: Job? = null
    lateinit var tracker: SelectionTracker<Long>

    init {
        this.ctx = ctx
        setHasStableIds(true)
    }

    constructor(ctx: Activity, client: DefaultApi) : this(ctx) {
        this.client = client
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setModels(m: List<T>) {
        this.models = m
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.model_stocks, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = models?.get(position)
        if (symbol is StockSymbol) {
            holder.title.text = symbol.displaySymbol
            tracker.let {
                holder.setActivated(it.isSelected(position.toLong()))
            }

            job = GlobalScope.launch {
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
            }
        }
        if (symbol is SymbolLookupInfo) {
            holder.title.text = symbol.displaySymbol
            holder.price.text = symbol.description
        }
    }

    override fun getItemCount(): Int = if (models.isNullOrEmpty()) 0 else models!!.size
    override fun getItemId(position: Int) = position.toLong()
    override fun onViewDetachedFromWindow(holder: ViewHolder) =
        if (job != null) job!!.cancel() else Unit

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val price: TextView = itemView.findViewById(R.id.price)

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