package com.example.mainapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_list_of_virtual_tours.*

class ListOfVirtualTours : AppCompatActivity() {

    private var adapter:TourAdapter ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_virtual_tours)
        virtualToursRv.adapter = adapter
        virtualToursRv.setHasFixedSize(true)
    }
}