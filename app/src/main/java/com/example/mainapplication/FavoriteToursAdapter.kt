package com.example.mainapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.tour_item.view.*

class FavoriteToursAdapter(private val context: Context,
                           private val listener: (Tours) -> Unit): RecyclerView.Adapter<FavoriteToursAdapter.ViewHolder>(){

    private var tours : ArrayList<Tours>? = null

    private val appFirst = FirebaseApp.getInstance("first")
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(appFirst)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(appFirst)

    private val app = FirebaseApp.getInstance("secondary")

    private var storage: FirebaseStorage = FirebaseStorage.getInstance(app)

    private var storageRef = storage.getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Tours, listener: (Tours) -> Unit) = with(itemView) {
            tourNameTv.text = "Название: " + item.tourName
            priceTv.text = "Цена: " + item.price
            dateTv.text = "Дата: " + " "+item.dateAndTime
            phoneTv.text = "Связь: "+ " "+item.phone
            peopleSizeTv.text= "Размер группы: "+item.numbersOfPeople
            descriptionTv.text = "Описание:" +item.description
           // tourImage.setImageResource(R.drawable.gora)
            companyNameTv.text = item.companyName
            item.imageId?.let {
                storageRef.child(item.imageId.toString()).downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it)
                        .thumbnail(0.1f).into(tourImage)
                }.addOnCanceledListener {
                    //imageView.setImageResource(R.drawable.fc4021e469579b3af7b090a932bd9dba)
                }
            }
        }
    }

    private fun showDialog(tour: Tours) {
        val dialog = AlertDialog.Builder(context)
        dialog.setCancelable(false)
        dialog.setTitle("")
        dialog.setMessage("Вы действительно хотите удалить тур из понравившихся?")
        dialog.setPositiveButton("Да", DialogInterface.OnClickListener { dialog, id ->

            tours?.remove(tour)
            if(tours!=null) {
                myRef.child(mAuth.currentUser?.uid!!).child("savedTours").child(tour.id!!).removeValue()
            }

            notifyDataSetChanged()
        })
        dialog.setNegativeButton("Нет") { dialogInterface, i ->
            dialogInterface.cancel()
        }
        dialog.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteToursAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_item,parent,false)
            return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:FavoriteToursAdapter.ViewHolder, position: Int) {
        tours?.get(position)?.let {
            holder.bind(it,listener)
        }

        ContextCompat.getDrawable(context, R.drawable.save_like)?.let {
            holder.itemView.likeIm.setImageDrawable(it)
        }

        holder.itemView.likeIm.setOnClickListener {
            tours?.get(position)?.let { tour ->
                showDialog(tour)
            }
        }
    }

    override fun getItemCount(): Int {
        return tours?.size?:0
    }

    fun setTours(tourList : ArrayList<Tours>){
        tours = tourList
        notifyDataSetChanged()
    }
}