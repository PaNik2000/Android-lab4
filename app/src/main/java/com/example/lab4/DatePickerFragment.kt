package com.example.lab4

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val widgetID : Int) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(context!!, this, year, month, day)
        datePicker.datePicker.minDate = c.timeInMillis + 24*60*60*1000
        return datePicker
    }

    override fun onDestroy() {
        requireActivity().finish()
        super.onDestroy()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c1 = Calendar.getInstance()
        c1.set(year, month, dayOfMonth)
        var days = 0L
        while (c1.after(Calendar.getInstance())) {
            c1.add(Calendar.DAY_OF_MONTH, -1)
            ++days
        }

        val intent = Intent(context, MyAppWidgetProvider::class.java)
        intent.action = "DATE_SET"
        intent.putExtra("days", days)
        intent.putExtra("widgetID", widgetID)
        context?.sendBroadcast(intent)
    }
}