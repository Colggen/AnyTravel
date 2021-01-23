package com.example.mainapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_list_of_virtual_tours.*

class ListOfVirtualTours : AppCompatActivity() {

    private var adapter:VirtualTourAdapter ?= null
    private var listName = arrayOf("Charyn")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_virtual_tours)

        adapter = VirtualTourAdapter(listName,this)
        virtualToursRv.adapter = adapter
        virtualToursRv.setHasFixedSize(true)

    }
}