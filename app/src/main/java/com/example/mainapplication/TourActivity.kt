package com.example.mainapplication

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_tour.*


class TourActivity : AppCompatActivity() {

    private val app1 = FirebaseApp.getInstance("first")
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app1)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app1)
    private val app = FirebaseApp.getInstance("secondary")
    private var toursRef = FirebaseDatabase.getInstance(app).getReference("tours")

    var storage: FirebaseStorage = FirebaseStorage.getInstance(app)
    var storageRef = storage.getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour)
        val tour: Tours? = intent.getSerializableExtra("tour") as Tours?
        myRef.child(mAuth.currentUser?.uid!!).child("bookedTours").child(tour?.id!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    tourBt.text = "Вы уже зарегестрированы"
                    tourBt.isEnabled = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })




        FirebaseDatabase.getInstance(app).getReference("tourImages").child(tour.id!!).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val images: ArrayList<String> = ArrayList()
                    snapshot.children.forEach {
                        images.add(it.value as String)
                    }
                    val adapter:PagerAdapter = SliderAdapter(this@TourActivity,images,tour.id!!)

                    viewpager.adapter = adapter
                }else{
                    Toast.makeText(this@TourActivity, "Doesnt exist", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })




        
        tourBt.setOnClickListener {
            showInputDialog()
        }


        detailNameTv.text = "Имя тура: " + tour.tourName
        detailCompanyNameTv.text = tour.companyName
        detalePeopleSizeTv.text = "Размер группы: " + tour?.numbersOfPeople.toString()
        detailDateTv.text = "Дата: " + tour?.dateAndTime
        detailDescrTv.text = "Описание: " + tour?.description
        detailPhoneTv.text = "Связь: " + tour?.phone
        detailPriceTv.text = "Цена: " + tour?.price.toString()


        if (tour?.numbersOfPeople != null && tour?.regPeople != null) {
            regPeople.text = "Осталось мест: ${(tour?.numbersOfPeople!! - tour.regPeople!!)}"
        }
        else{
            regPeople.text = "Осталось мест: ${tour?.numbersOfPeople!!}"

        }
//
//        storageRef.child(tour?.imageId.toString()).downloadUrl.addOnSuccessListener {
//            Glide.with(this).load(it)
//                .thumbnail(0.1f).into(detailImageOfTour)
//        }.addOnCanceledListener {
//        }

    }


    //получить bookedTours через он дата ченж , где все туры forEach{ if(Bookedid)tourId = intentGetStringExtra button set tex вы зареганы buttonsetDisabled false }


    private fun showInputDialog(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите количество человек ")
        val customLayout = layoutInflater.inflate(R.layout.input_dialog, null)
        builder.setView(customLayout)

        var opened = true

        builder.setPositiveButton("OK") { dialog, id ->

            val tour = intent.getSerializableExtra("tour") as Tours
            val editText = customLayout.findViewById<EditText>(R.id.editText)


            if (editText.text.toString().isNotEmpty() && editText.text.toString()!="0" && editText.text.toString().toInt() < 50) {

                //sendDialogDataToActivity(editText.text.toString())

                toursRef.child(tour.id!!).child("regPeople")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("SetTextI18n")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {

                                val numberOfPeople = snapshot.getValue<Int>()

                                if((tour.numbersOfPeople!!.minus( tour.regPeople!!)) - editText.text.toString().toInt() < 0){
                                    Toast.makeText(
                                        this@TourActivity,
                                        "Не хватает мест на тур",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    if (numberOfPeople != null) {

                                        regPeople.text = "Осталось мест: ${(tour.numbersOfPeople!! - numberOfPeople)}"

                                        tour.regPeople = numberOfPeople + editText.text.toString().toInt()

                                        val bookedId = generateReservationNumber(6)
                                        tour.bookedId = bookedId
                                        myRef.child(mAuth.currentUser?.uid!!).child("bookedTours").child(tour.id!!).setValue(tour)
                                        toursRef.child(tour.id!!).child("regPeople").setValue(numberOfPeople + editText.text.toString().toInt())

                                        finish()
                                    } else {
                                        regPeople.text ="Осталось мест: "+tour.numbersOfPeople!!.toString()

                                        tour.regPeople = editText.text.toString().toInt()
                                        toursRef.child(tour.id!!).child("regPeople").setValue(
                                            numberOfPeople +
                                                    editText.text.toString()
                                                        .toInt() + numberOfPeople.toString()
                                                .toInt()
                                        )
                                        myRef.child(mAuth.currentUser?.uid!!).child("bookedTours")
                                            .child(tour.id!!)
                                            .setValue(tour)
                                        finish()
                                    }
                                    if(opened) {
                                        opened = false
                                        val intent = Intent(
                                            this@TourActivity,
                                            UsersRegistrationActivity::class.java
                                        )
                                        intent.putExtra(
                                            "quantityRegPeople",
                                            editText.text.toString().toInt()
                                        )
                                        intent.putExtra("bookedId", tour!!.bookedId)
                                        intent.putExtra("tour", tour)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
            else{
                Toast.makeText(
                    this@TourActivity,
                    "Пожалуйста, укажите количество человек не более 50",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun generateReservationNumber(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }






}