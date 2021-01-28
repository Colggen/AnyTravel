package com.example.mainapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage

class SliderAdapter: PagerAdapter{

    var context:Context
    var images:ArrayList<String>
    private val app = FirebaseApp.getInstance("secondary")
    var storage: FirebaseStorage = FirebaseStorage.getInstance(app)
    var storageRef = storage.getReferenceFromUrl("gs://anytravel-ef9c8.appspot.com")
    var tourId:String
    lateinit var inflater:LayoutInflater
    constructor(context: Context,images:ArrayList<String>,tourId:String):super(){
        this.context = context
        this.images = images
        this.tourId = tourId
    }
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as CardView

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view:View = inflater.inflate(R.layout.slider_item,container,false)
        val image:ImageView = view.findViewById(R.id.tourImages)


            storageRef.child(images[position]).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).thumbnail(0.1f).into(image)
            }.addOnCanceledListener {
                Log.d("My0", "canceles")
            }.addOnFailureListener {

                Log.d("My0", "canceles ${it.toString()}")

            }

//        image.setBackgroundResource()
        container!!.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container!!.removeView(`object` as CardView)
    }
}
