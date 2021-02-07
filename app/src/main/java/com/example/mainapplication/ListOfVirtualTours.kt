package com.example.mainapplication

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_list_of_virtual_tours.*
import kotlinx.android.synthetic.main.activity_tour.*

class ListOfVirtualTours : AppCompatActivity() {

    private var adapter:VirtualTourAdapter ?= null
    private var listName = arrayOf("Чарын и Большой Алматинский пик")
    private lateinit var dotsLayout:LinearLayout
    private var custom_position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle("Список виртуальных туров")
        setContentView(R.layout.activity_list_of_virtual_tours)
        adapter = VirtualTourAdapter(listName, this)
        virtualToursRv.adapter = adapter
        virtualToursRv.setHasFixedSize(true)
        backVirtList.setOnClickListener {
            finish()
        }
    }


}