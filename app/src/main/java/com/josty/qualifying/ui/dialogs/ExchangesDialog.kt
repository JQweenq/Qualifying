package com.josty.qualifying.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.josty.qualifying.R
import com.josty.qualifying.ui.activities.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Exchanges {
    val exchanges: Array<Exchange> = arrayOf(
        Exchange("BA", "Фондовая биржа Буэнос-Айреса"),
        Exchange("BC", "Колумбийская фондовая биржа"),
        Exchange("CN", "Канадская национальная фондовая биржа"),
        Exchange("CR", "Каракасская фондовая биржа"),
        Exchange("MX", "Мексиканская фондовая биржа"),
        Exchange("NE", "НЕО биржа"),
        Exchange("SA", "Фондовая биржа Сан-Паулу"),
        Exchange("SN", "Фондовая биржа Торонто"),
        Exchange("TO", "Нью-Йоркская фондовая биржа"),
        Exchange("US", "Канадская фондовая биржа")
    )

    fun getNames(): Array<CharSequence> {
        var names = emptyArray<CharSequence>()
        exchanges.forEach { exchange -> names += exchange.name }
        return names
    }

    fun get(pos: Int): Exchange = exchanges[pos]

    data class Exchange(val code: String, val name: CharSequence)
}

class ExchangesDialog(
    val fn: (String, String, String, String) -> Unit,
    val activity: MainActivity
) : DialogFragment() {
    private val exchanges = Exchanges()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Выбирите биржу")
        builder.setItems(exchanges.getNames()) { dialogInterface, i ->
            GlobalScope.launch {
                fn(exchanges.get(i).code, "", "", "")
            }
        }

        val dialog = builder.create()

        return dialog
    }
}
