package com.example.mainapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_tour.*
import kotlinx.android.synthetic.main.tour_item.view.*

class TourAdapter(private val context: Context, private val listener: (Tours) -> Unit) :
    RecyclerView.Adapter<TourAdapter.ViewHolder>() {


    private var tours: ArrayList<Tours>? = null
    private val app1 = FirebaseApp.getInstance("first")

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app1)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app1)

    private val app = FirebaseApp.getInstance("secondary")

    private var storage: FirebaseStorage = FirebaseStorage.getInstance(app)

    private var storageRef = storage.getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Tours, listener: (Tours) -> Unit) = with(itemView) {
            tourNameTv.text = "Название: " + item.tourName
            priceTv.text = "Цена: " + item.price
            dateTv.text = "Дата: " + item.dateAndTime
            phoneTv.text = "Связь: " + item.phone
            peopleSizeTv.text = "Размер группы: " + item.numbersOfPeople
            descriptionTv.text = "Описание: " + item.description
            //tourImage.setImageResource(R.drawable.gora)
            companyNameTv.text = item.companyName
            item.imageId?.let {
                storageRef.child(item.imageId.toString()).downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it)
                        .thumbnail(0.1f).into(tourImage)
                }.addOnCanceledListener {
                    //imageView.setImageResource(R.drawable.fc4021e469579b3af7b090a932bd9dba)
                }
            }


            //likeIm.setImageResource(ContextCompat.getDrawable(context,R.drawable.like))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tours?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tours?.get(position)?.let { tour ->
            holder.bind(tour, listener)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, TourActivity::class.java)
                intent.putExtra("tour", tour)
                context.startActivity(intent)
            }


            ContextCompat.getDrawable(context, R.drawable.like)?.let {
                holder.itemView.likeIm.setImageDrawable(it)
            }

            myRef.child(mAuth.currentUser?.uid!!).child("savedTours").child(tour?.id!!)
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            ContextCompat.getDrawable(context, R.drawable.save_like)?.let {
                                holder.itemView.likeIm.setImageDrawable(it)
                                holder.itemView.likeIm.isEnabled = false
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            holder.itemView.likeIm.setOnClickListener {

//            ContextCompat.getDrawable(context, R.drawable.save_like)?.let {
//                holder.itemView.likeIm.setImageDrawable(it)
//            }

                val dialog = AlertDialog.Builder(context)
                dialog.setCancelable(false)
                dialog.setTitle("")
                dialog.setMessage("Вы действительно хотите добавить тур в сохранённые?")
                dialog.setPositiveButton("Да", DialogInterface.OnClickListener { dialog, id ->

                    // tours?.remove(tour)

                    notifyDataSetChanged()


                    mAuth.currentUser?.getIdToken(true)
                    myRef.child(mAuth.currentUser?.uid!!).child("savedTours").child(tour.id!!)
                        .setValue(tour)
                    Toast.makeText(context, "Тур был успешно добавлен!", Toast.LENGTH_SHORT).show()

                })
                dialog.setNegativeButton("Нет") { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                dialog.show()

            }
        }
    }


    fun setTours(tourList: ArrayList<Tours>) {
        tours = tourList
        notifyDataSetChanged()
    }


}