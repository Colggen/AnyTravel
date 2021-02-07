package com.example.mainapplication

import android.content.DialogInterface
import android.media.Image
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import kotlinx.android.synthetic.main.activity_virtual_tour.*

class VirtualTourActivity : AppCompatActivity() {

    private var panoWidgetView: VrPanoramaView? = null
    private var backgroundImageLoaderTask: ImageLoaderTask? = null
    private var mPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_tour)
        panoWidgetView = findViewById(R.id.pano_view)
        val type:String? = intent.getStringExtra("type")
        if(type=="Чарын и Большой Алматинский пик") {
            loadPanoImage("chraynfirst.jpg","chraynsecond.jpg","charynthird.jpg","charynfourth.jpg")
        }
    }


    override fun onPause() {
        super.onPause()
//
//        val mPlayer = MediaPlayer.create(this,R.raw.example)
//        mPlayer.stop()

        panoWidgetView?.pauseRendering()

    }

    override fun onStop() {
        super.onStop()
        //mPlayer?.release()
        //mPlayer = null

    }


    override fun onDestroy() {
        super.onDestroy()
//
//        val mPlayer = MediaPlayer.create(this,R.raw.example)
//        mPlayer.stop()

        panoWidgetView?.shutdown()

    }

    @Synchronized
    private fun loadPanoImage(firstImage: String, secondImage:String, thirdImage:String,fourthImage:String) {
       // mPlayer = MediaPlayer.create(this,R.raw.example)
        var task = backgroundImageLoaderTask
        if (task != null && !task.isCancelled) {
            // Cancel any task from a previous loading.
            task.cancel(true)
        }

        // pass in the name of the image to load from assets.
        val viewOptions = VrPanoramaView.Options()
        viewOptions.inputType = VrPanoramaView.Options.TYPE_MONO

        // use the name of the image in the assets/ directory.
        // val panoImageName = "fav_im.jpg"
//        val firstImage = "charyn_first.jpg"
//        val secondImage = "charyn_second.jpg"
//        val thirdImage = "main.jpg"
        val thirdImagee = "Coridor.jpg"

        // create the task passing the widget view and call execute to start.

        var counter = 0

        ImageLoaderTask(panoWidgetView, viewOptions, firstImage).execute(this.assets!!)
        //mPlayer?.start()
        counter++

        imageNt.setOnClickListener {
            when (counter) {
                1 -> {
                    ImageLoaderTask(panoWidgetView, viewOptions, secondImage).execute(this.assets!!)
                    counter++
                }
                2->{
                    ImageLoaderTask(panoWidgetView, viewOptions, thirdImage).execute(this.assets!!)
                    counter++
                }
                3->{
                    ImageLoaderTask(panoWidgetView, viewOptions, fourthImage).execute(this.assets!!)
                    counter++
                }
                else-> {
                    //mPlayer?.stop()
                    showDialog()
                }
            }
        }


        imageNtBack.setOnClickListener {
            when(counter){

                1->{
                    finish()
                    //mPlayer?.stop()
                }
                2 -> {
                    ImageLoaderTask(panoWidgetView, viewOptions, firstImage).execute(this.assets!!)
                    counter--
                }
                3-> {
                    ImageLoaderTask(panoWidgetView, viewOptions, secondImage).execute(this.assets!!)
                    counter--
                }
                4->{
                    ImageLoaderTask(panoWidgetView, viewOptions, thirdImage).execute(this.assets!!)
                    counter--
                }
            }
        }
    }

    private fun showDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("")
        dialog.setMessage("Виртуальный тур успешно завершен!")
        dialog.setPositiveButton("Ок", DialogInterface.OnClickListener { dialog, id ->
            //mPlayer?.release()
            //mPlayer = null
            finish()
        })
        dialog.show()
    }
}