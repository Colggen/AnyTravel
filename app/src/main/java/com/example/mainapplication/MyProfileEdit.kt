package com.example.mainapplication

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import kotlinx.android.synthetic.main.fragment_my_profile_edit.*
import kotlinx.android.synthetic.main.fragment_my_profile_edit.view.*
import kotlinx.android.synthetic.main.tour_item.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileEdit.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class MyProfileEdit : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mAuth: FirebaseAuth? = null

    private val app = FirebaseApp.getInstance("first")

    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null


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
        val view = inflater.inflate(R.layout.fragment_my_profile_edit, container, false)
        view.cancelTv.setOnClickListener { findNavController().navigate(R.id.action_myProfileEdit2_to_myProfileFragment) }

        mAuth = FirebaseAuth.getInstance(app)
        database = FirebaseDatabase.getInstance(app)
        myRef = database!!.getReference("users")
        val uid = mAuth?.uid!!


        myRef?.child(uid!!)?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                view.nameEt.setText(user?.name)
                view.cityEt.setText(user?.city)
                view.phoneEt.setText(user?.phone)

                view.nameEt.setTextColor(Color.BLACK)
                view.cityEt.setTextColor(Color.BLACK)
                view.phoneEt.setTextColor(Color.BLACK)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        view.saveTv.setOnClickListener{

            var allow = true

            if(!App.isCorrectPhone(phoneEt.text.toString())){
                phoneEt.error = "Введите корректный номер"
                allow = false
            }
            if(!App.isCorrectName(nameEt.text.toString()) ){
                nameEt.error = "Введите верное имя и фамилию"
                allow = false
            }
            if(!App.isCorrectCity(cityEt.text.toString())){
                cityEt.error = "Введите правильно ваш город"
                allow = false
            }

            myRef!!.child(uid!!).child("city").setValue(view.cityEt.text.toString())
            myRef!!.child(uid!!).child("name").setValue(view.nameEt.text.toString())
            myRef!!.child(uid!!).child("phone").setValue(view.phoneEt.text.toString())

            if(allow) {
                findNavController().navigate(R.id.action_myProfileEdit2_to_myProfileFragment)
            }

        }

        return view

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileEdit.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileEdit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}