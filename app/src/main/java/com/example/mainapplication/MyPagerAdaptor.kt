package com.example.mainapplication

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdaptor(fm : FragmentManager) :FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                Log.d("second", " gert itrm travellers")
                TravellersFragment()
            }
            else -> {
                Log.d("second", " gert itrm trips")

                return TripsFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
         return when(position){
             0-> "Забронированные"
             else -> return "Сохраненные"
         }
    }

}