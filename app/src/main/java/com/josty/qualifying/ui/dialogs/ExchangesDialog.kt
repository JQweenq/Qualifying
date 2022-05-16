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

class ExchangesDialog(val fn: (String, String, String, String) -> Unit, val activity: MainActivity): DialogFragment() {
    private val exchanges: Array<CharSequence> = arrayOf(
        "Фондовая биржа Буэнос-Айреса",
        "Колумбийская фондовая биржа",
        "Канадская национальная фондовая биржа",
        "Каракасская фондовая биржа",
        "Мексиканская фондовая биржа",
        "НЕО биржа",
        "Фондовая биржа Сан-Паулу",
        "Фондовая биржа Торонто",
        "Нью-Йоркская фондовая биржа",
        "Фондовая биржа Торонто"
    )
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Выбирите биржу")
        builder.setItems(exchanges) { dialogInterface, i ->
            GlobalScope.launch {
                fn("ME", "", "", "")
            }
        }

        val dialog = builder.create()

        return dialog
    }
}
