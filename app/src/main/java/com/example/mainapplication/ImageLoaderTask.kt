package com.example.mainapplication
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import java.io.IOException
import java.lang.ref.WeakReference


class ImageLoaderTask(
    view: VrPanoramaView?,
    viewOptions: VrPanoramaView.Options,
    assetName: String
) :
    AsyncTask<AssetManager?, Void?, Bitmap?>() {
    private val assetName: String = assetName
    private val viewReference: WeakReference<VrPanoramaView?> = WeakReference(view)
    private val viewOptions: VrPanoramaView.Options = viewOptions


    override fun onPostExecute(bitmap: Bitmap?) {
        val vw: VrPanoramaView? = viewReference.get()
        if (vw != null && bitmap != null) {
            vw.loadImageFromBitmap(bitmap, viewOptions)
        }
    }

    companion object {
        private const val TAG = "ImageLoaderTask"
        private var lastBitmap: WeakReference<Bitmap?> = WeakReference(null)
        private var lastName: String? = null
    }

    override fun doInBackground(vararg params: AssetManager?): Bitmap? {
        val assetManager = params[0]
        if (assetName == lastName && lastBitmap.get() != null) {
            return lastBitmap.get()
        }
        try {
            assetManager?.open(assetName).use { istr ->
                val b = BitmapFactory.decodeStream(istr)
                lastBitmap = WeakReference(b)
                lastName = assetName
                return b
            }
        } catch (e: IOException) {
            Log.e(TAG, "Could not decode default bitmap: $e")
            return null
        }
    }
}