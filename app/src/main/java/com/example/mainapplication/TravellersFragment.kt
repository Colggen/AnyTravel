package com.example.mainapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.android.synthetic.main.fragment_travellers.view.*
import kotlinx.android.synthetic.main.registered_tour_item.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TravellersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TravellersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val app1 = FirebaseApp.getInstance("first")
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app1)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app1)


    private var adapter:RegisteredTourAdapter ?= null

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
        val view =  inflater.inflate(R.layout.fragment_travellers, container, false)

        adapter = activity?.let {
            RegisteredTourAdapter(it) {
                Log.d("FirstFragment", "clicked")
                val intent = Intent(activity,TourActivity::class.java)
                startActivity(intent)
            }
        }

        view.regTourRv.adapter = adapter
        view.regTourRv.setHasFixedSize(true)

        myRef.child(mAuth.currentUser?.uid!!).child("bookedTours").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Tours>()
                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val tour = it.getValue<Tours>()
                        tour?.let {
                            list.add(tour)
                        }
                    }
                    adapter?.setTours(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirstFragment",error.toString())
            }

        })
        Log.d("second", "travellers on create")

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d("second", "travellers on resume")

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TravellersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TravellersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}