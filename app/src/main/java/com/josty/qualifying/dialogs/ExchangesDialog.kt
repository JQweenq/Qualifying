package com.josty.qualifying.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ExchangesDialog(private val exchanges: Array<CharSequence>): DialogFragment() {
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