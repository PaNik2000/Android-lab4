package com.example.lab4

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = DatePickerFragment(intent.getIntExtra("widgetID", 0))
        fragment.show(supportFragmentManager, "datePicker")
    }
}
