package com.example.mainapplication

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO = 100
private const val INTENT_PERMISSION_REQUEST_CAMERA = 120
private const val INTENT_TAKE_PHOTO_RESULT = 150
private const val INTENT_CHOOSE_PHOTO_FROM_GALLERY = 170


/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var imageFilePath: String? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var actualProImagePath:String?=null
    private var storageReference: StorageReference? = null


    private var mAuth: FirebaseAuth? = null

    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var uid:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        view.virtualTourBt.setOnClickListener{

            val intent = Intent(activity,ListOfVirtualTours::class.java)
            startActivity(intent)
        }

        val app = FirebaseApp.getInstance("first")
        mAuth = FirebaseAuth.getInstance(app)
        database = FirebaseDatabase.getInstance(app)
        myRef = database!!.getReference("users")
        uid = mAuth?.uid!!
        storageReference = FirebaseStorage.getInstance(app).getReferenceFromUrl("gs://myapp-69087.appspot.com")


        myRef?.child(uid!!)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()

                view.locationTv.text = user?.city?.capitalize()

                if(user?.name!!.isNotEmpty()) {
                    view.nameOfUser.text = user?.name
                }
                if(user?.phone!!.isNotEmpty()){
                    view.phoneOfUser.text = user?.phone
                }




                user?.imageId?.let {
                    storageReference?.child(user?.imageId!!)?.downloadUrl?.addOnSuccessListener {
                        try{
                            Glide.with(activity!!).load(it).apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.1f).into(profileIv)
                        }catch (e:Exception){ }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        bottomSheetBehavior = BottomSheetBehavior.from<View>(view.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {}
            override fun onSlide(view: View, v: Float) {}
        })

        view.takePhotoLl.setOnClickListener {
            requestCamera(1);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        view.chooseFromGalleryLl.setOnClickListener {
            requestCamera(2);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }


        view.profileIv.setOnClickListener {

            choosePhoto()
        }






        //view.descriptionOfProvileTv.setOnClickListener { findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEdit2) }
        //view.add.setOnClickListener { findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEdit2) }

        view.buttonEdit.setOnClickListener { findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEdit2) }


        view.settingsImage.setOnClickListener {
            startActivityForResult(
                Intent(
                    activity,
                    SettingsActivity::class.java
                ),101
            )
        }



        view.locationTv.setOnClickListener { findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEdit2) }
        view.place.setOnClickListener { findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEdit2) }
        return view

    }


    private fun choosePhoto() {

        activity?.let { context ->

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestCameraPermission(INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO);

            } else {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

        }
    }

    private fun requestCameraPermission(request: Int) {
        activity?.let { context ->
            if (request == INTENT_PERMISSION_REQUEST_CAMERA_FOR_PHOTO) {
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    request
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_TAKE_PHOTO_RESULT) {
            if (resultCode == RESULT_OK) {
                actualProImagePath = imageFilePath
                Glide.with(this).load(imageFilePath).apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f).into(profileIv)
                uploadPhoto()
            }
        }

        if (requestCode == INTENT_CHOOSE_PHOTO_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    activity?.let { context ->
                        val imageUri: Uri? = data?.data
                        val imageStream: InputStream? = context.contentResolver.openInputStream(
                            imageUri!!
                        )
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        actualProImagePath = getPathFromURI(imageUri)
                        Glide.with(this).asBitmap().load(selectedImage)
                            .apply(RequestOptions.circleCropTransform()).thumbnail(0.1f)
                            .into(profileIv)

                        uploadPhoto()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        if(requestCode==101){
            findNavController().navigate(R.id.action_myProfileFragment_to_secondFragment3)
        }
    }

    private fun uploadPhoto(){
        actualProImagePath?.let {
            val file = File(it)
            val uri = Uri.fromFile(file)
            val reference = storageReference!!.child(file.name)

            reference.putFile(uri).addOnSuccessListener {
                myRef?.child(uid!!)?.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue<User>()
                        user?.imageId = file.name
                        myRef?.child(uid!!)?.child("imageId")?.setValue(file.name)
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }

    }

    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        activity?.let { context ->
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = context.contentResolver.query(contentUri!!, proj, null, null, null)
            cursor?.let {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    res = cursor.getString(columnIndex)
                }
            }
            cursor?.close()
        }
        return res
    }


    private fun requestCamera(type: Int) {
        activity?.let { context ->
            val pictureIntent: Intent
            if (type == 1) {
                pictureIntent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )
                if (pictureIntent.resolveActivity(context.packageManager) != null) {
                    //Create a file to store the image
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "com.example.mainapplication.provider",
                            photoFile
                        )
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(
                            pictureIntent,
                            INTENT_TAKE_PHOTO_RESULT
                        )
                    }
                }
            } else {
                pictureIntent = Intent(Intent.ACTION_PICK)
                pictureIntent.type = "image/*"
                startActivityForResult(
                    pictureIntent,
                    INTENT_CHOOSE_PHOTO_FROM_GALLERY
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        var image: File? = null
        activity?.let { context ->
            val imageFileName: String = App.generateUniCode("p")
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            imageFilePath = image?.absolutePath
        }
        return image
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}