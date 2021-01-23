package com.example.mainapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.registered_tour_item.view.*


class RegisteredTourAdapter(private val context: Context, private val listener: (Tours) -> Unit): RecyclerView.Adapter<RegisteredTourAdapter.ViewHolder>(){

    private var tours : ArrayList<Tours>? = null
    private val app1 = FirebaseApp.getInstance("first")
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app1)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app1)

    private val app = FirebaseApp.getInstance("secondary")
    private var toursDatabase = FirebaseDatabase.getInstance(app)
    private var toursRef = toursDatabase.getReference("tours")
    private var toursAuth = FirebaseAuth.getInstance(app)


    private var storage: FirebaseStorage = FirebaseStorage.getInstance(app)
    private var storageRef = storage.getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Tours, listener: (Tours) -> Unit) = with(itemView) {
            RegTourNameTv.text = "Название: "+item.tourName
            RegPriceTv.text ="Цена: " +item.price
            RegDateTv.text = "Дата: " +item.dateAndTime
            RegPhoneTv.text = "Связь: "+ item.phone
            RegDescriptionTv.text = "Описание: " +item.description
            RegPeopleSizeTv.text= "Размер группы: "+item.numbersOfPeople
            RegCompanyNameTv.text = item.companyName
            item.imageId?.let {
                storageRef.child(item.imageId.toString()).downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it)
                        .thumbnail(0.1f).into(RegTourImage)
                }.addOnCanceledListener {
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegisteredTourAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.registered_tour_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegisteredTourAdapter.ViewHolder, position: Int) {
        tours?.get(position)?.let { tour->
            holder.bind(tour, listener)
        }
        holder.itemView.cancelTourBt.setOnClickListener {
            tours?.get(position)?.let { tour ->
                showDialog(tour)
            }
        }
    }

    override fun getItemCount(): Int {
        return tours?.size?:0
    }



    fun setTours(tourList: ArrayList<Tours>){
        tours = tourList
        notifyDataSetChanged()
    }

    @SuppressLint("ShowToast")
    private fun showDialog(tour: Tours) {
        val dialog = AlertDialog.Builder(context)
        dialog.setCancelable(false)
        dialog.setTitle("")
        dialog.setMessage("Вы действительно хотите отменить тур?")
        dialog.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->

            tours?.remove(tour)


            if (tours != null) {

                myRef.child(mAuth.currentUser?.uid!!).child("bookedTours").child(tour.id!!).removeValue()
                myRef.child(mAuth.currentUser?.uid!!).child("regData").child(tour.id!!).removeValue()



                toursRef.child(tour.id!!).child("regData").child(mAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        toursRef.child(tour.id!!).child("regData").child(mAuth.currentUser?.uid!!).removeValue()
                        //tour.regPeople = tour.regPeople!!.minus(snapshot.childrenCount.toInt())
                        val map = mutableMapOf<String, Int>()
                        map["regPeople"] = tour.regPeople!!-snapshot.childrenCount.toInt()
                        toursRef.child(tour.id!!).updateChildren(map as Map<String, Any>)
                        //updateRegPeople(counter,tour)

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

            }
            notifyDataSetChanged()
        })
        dialog.setNegativeButton("Нет") { dialogInterface, i ->
            dialogInterface.cancel()
        }
        dialog.show()
    }

    private fun updateRegPeople(counter: Int,tour:Tours) {

        toursRef.child(tour.id!!).child("regPeople").setValue(tour.regPeople!!-counter)

        Toast.makeText(context, "Тур был успешно отменён", Toast.LENGTH_SHORT).show()

        Log.d("my", "$counter")
    }

}