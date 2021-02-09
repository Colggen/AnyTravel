package com.example.mainapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_first.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    private var adapter: TourAdapter? = null

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
        val view = inflater.inflate(R.layout.fragment_first, container, false)




        activity?.let {
            val app = FirebaseApp.getInstance("secondary")
            database = FirebaseDatabase.getInstance(app)
            myRef = database?.getReference("tours")
        }

        adapter = activity?.let {
            TourAdapter(it) {
                Log.d("FirstFragment", "clicked")
                val intent = Intent(activity, TourActivity::class.java)
                startActivity(intent)
            }
        }


//получить bookedTOurs через он дата ченж , где все туры forEach{ all tours forEach Booked tours{ if(toursId==bookedTours } }

        view.toursRv.adapter = adapter
        view.toursRv.setHasFixedSize(true)

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirstFragment", "start")
                val list = ArrayList<Tours>()
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        var tour = it.getValue(Tours::class.java)
                        tour?.id = it.key
                        tour?.let {
                            list.add(tour)
                        }
                    }
                    if (list.size == 0) {
                        view.toursRv.visibility = View.GONE
                        view.noItemLl.visibility = View.VISIBLE
                    } else {
                        view.toursRv.visibility = View.VISIBLE
                        view.noItemLl.visibility = View.GONE
                    }
                    Log.d("FirstFragment", list[0].companyName.toString())
                    adapter?.setTours(list)
                } else {
                    view.toursRv.visibility = View.GONE
                    view.noItemLl.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirstFragment", error.toString())
            }

        })

        return view

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}