package com.example.mainapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_trips.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TripsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TripsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val app = FirebaseApp.getInstance("first")

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app)
    private var myRef: DatabaseReference = database.getReference("users")
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app)



    private var adapter:FavoriteToursAdapter ? =null

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
        val view = inflater.inflate(R.layout.fragment_trips, container, false)

        activity?.let {
            adapter = FavoriteToursAdapter(it) {
                Log.d("FirstFragment", "clicked")
                val intent = Intent(activity, TourActivity::class.java)
                startActivity(intent)

            }
        }

        view.favToursRv.adapter = adapter
        view.favToursRv.setHasFixedSize(true)

        myRef.child(mAuth.currentUser?.uid!!).child("savedTours").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<Tours>()
                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val tour = it.getValue<Tours>()
                        tour?.id = it.key
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

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d("second", "trips on resume")

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TripsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TripsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}