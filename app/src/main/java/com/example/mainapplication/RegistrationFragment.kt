package com.example.mainapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mainapplication.App.Companion.isValidEmail
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_my_profile_edit.*
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.phoneTv
import kotlinx.android.synthetic.main.fragment_registration.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    //private val MY_REQUEST_CODE: Int = 5

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private var mAuth: FirebaseAuth? = null

    private var myRef: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var layoutView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        layoutView = inflater.inflate(R.layout.fragment_registration, container, false)

        val app = FirebaseApp.getInstance("first")
        mAuth = FirebaseAuth.getInstance(app)
        database = FirebaseDatabase.getInstance(app)

        myRef = database!!.reference


        //activity?.let { FirebaseApp.initializeApp(it) }
        layoutView?.proceedBt?.setOnClickListener {

            var allow = true
            activity?.let { it1 -> App.writeSharedPreferences(it1, "isLogged", "true") }

            if(!isValidEmail(loginEt.text)){
                loginEt.error = "Заполните поле"
                allow = false
            }
            if(passwordEditTV.text.isEmpty() || passwordEditTV.text.length <6 || passwordEditTV.text.length>15 ){
                passwordEditTV.error = "Заполните поле"
                allow = false
            }
            if(confirmTv.text.toString() != passwordEditTV.text.toString()) {
                confirmTv.error = "Заполните поле"
                allow = false
            }
            if(nameTv.text.toString().isEmpty() || !App.isCorrectName(nameTv.text.toString())){
                allow = false
                nameTv.error = "Введите правильные имя и фамилию"
            }
            if(phoneTv.text.toString().isEmpty() || !phoneTv.text.toString().isDigitsOnly() ){
                phoneTv.error = "Введите корректный номер"
                allow = false
            }

            if(allow){
                //findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                createAccount(
                    layoutView?.loginEt?.text.toString(),
                    layoutView?.passwordEditTV?.text.toString()
                )
            }
        }


        return layoutView

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun createAccount(email: String, password: String){

        layoutView?.progressBarID?.visibility = View.VISIBLE

        val user = User()

        user.email = loginEt.text.toString()
        user.name = nameTv.text.toString()
        user.phone = phoneTv.text.toString()



        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {

            layoutView?.progressBarID?.visibility = View.GONE
            if(it.isSuccessful){
                Toast.makeText(activity, "Вы успешно зарегестрировались", Toast.LENGTH_SHORT).show()
                it.result?.user?.uid?.let { uid->
                    myRef?.child("users")?.child(uid)?.setValue(user)
                    findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                }
            }

        }?.addOnFailureListener {
            //Log.d("RegistrationFragment", "Регистрация не прошла успешно, попробуйте еще раз")
            layoutView?.progressBarID?.visibility= View.GONE
            Toast.makeText(activity,"Регистрация не прошла успешно, попробуйте еще раз", Toast.LENGTH_SHORT).show()
        }
    }

}