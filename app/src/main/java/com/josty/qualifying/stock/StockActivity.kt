package com.josty.qualifying.stock

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.josty.qualifying.R
import com.josty.qualifying.application.App
import com.josty.qualifying.databinding.ActivityStockBinding
import com.josty.qualifying.main.dialogs.NetworkDialog
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ClientException
import io.finnhub.api.infrastructure.ServerException
import io.finnhub.api.models.Quote
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

@DelicateCoroutinesApi
class StockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockBinding
    private val apiClient: DefaultApi = App::apiClient.invoke(App())
    private var price: Float = 0F
    private var mic: String? = null
    private var symbol: String? = null
    private var dSymbol: String? = null
    private var description: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)

        /*
        * search -> symbol, description
        * main -> symbol, mic
        * */

        symbol = intent.extras!!.getString("SYMBOL")
        mic = intent.extras!!.getString("MIC", null)
        description = intent.extras!!.getString("DESCRIPTION", null)

        if (App.hasConnection(this))
            GlobalScope.launch {
                try {
                    symbol?.let { this@StockActivity.price = apiClient.quote(it).c!! }
                } catch (ex: SocketTimeoutException) {
                    Log.e("[Finnhub]", "SocketTimeout")
                } catch (e: ClientException) {
                    Log.e("[Finnhub]", "ClientError: 429")
                } catch (e: ServerException) {
                    Log.e("[Finnhub]", "Server error: 503")
                }
                if (this@StockActivity.description.isNullOrEmpty())
                    try {
                        mic?.let {
                            val result = apiClient.symbolSearch(it).result?.get(0)
                            this@StockActivity.description = result?.description.toString()
                            this@StockActivity.dSymbol = result?.displaySymbol.toString()
                        }
                    } catch (ex: SocketTimeoutException) {
                        Log.e("[Finnhub]", "SocketTimeout")
                    } catch (e: ClientException) {
                        Log.e("[Finnhub]", "ClientError: 429")
                    } catch (e: ServerException) {
                        Log.e("[Finnhub]", "Server error: 503")
                    }
                this@StockActivity.runOnUiThread {
                    binding.price.text = "${getString(R.string.price)}: $price"
                    binding.description.text = description
                    if (dSymbol.isNullOrEmpty())
                        binding.symbol.text = this@StockActivity.symbol
                    else
                        binding.symbol.text = dSymbol
                    if (mic.isNullOrEmpty())
                        binding.mic.visibility = View.GONE
                    else
                        binding.mic.text = "mic: $mic"

                }
            }
        else
            NetworkDialog().show(supportFragmentManager, "network")
        setContentView(binding.root)
    }
}