package com.josty.qualifying.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.josty.qualifying.R

class NetworkDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(R.string.no_connection)
        builder.setMessage(R.string.needed_connection)
        builder.setNegativeButton(R.string.ok, null)

        val dialog = builder.create()

        return dialog
    }
}