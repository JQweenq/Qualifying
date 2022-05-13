package com.josty.qualifying.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.josty.qualifying.R

class ExchangesDialog(): DialogFragment() {
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
            println("[Item clicked] $i")
        }

        val dialog = builder.create()

        return dialog
    }
}