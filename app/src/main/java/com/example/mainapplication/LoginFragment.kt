package com.example.mainapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.mainapplication.App.Companion.isValidEmail
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.loginEt
import kotlinx.android.synthetic.main.fragment_login.passwordEditTV
import kotlinx.android.synthetic.main.fragment_login.view.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var mAuth: FirebaseAuth? = null
    private var layoutView: View? = null

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
        layoutView = inflater.inflate(R.layout.fragment_login, container, false)
        val app = FirebaseApp.getInstance("first")
        mAuth = FirebaseAuth.getInstance(app)
        layoutView?.RegTv?.setOnClickListener{findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)}
        layoutView?.forgotTv?.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment) }
        layoutView?.buttonNext?.setOnClickListener {
            layoutView?.progressBar?.visibility = View.VISIBLE

            var allow = true

            if(!isValidEmail(loginEt.text)){
                loginEt.error = "Заполните поле"
                allow = false
            }
            if(passwordEditTV.text.isEmpty() || passwordEditTV.text.length <6 || passwordEditTV.text.length>=15){
                passwordEditTV.error = "Заполните поле"
            allow = false
        }


            if(allow){
//                activity?.let { it1 -> App.writeSharedPreferences(it1,"isLogged","true") }
//                startActivity(Intent(activity,MainActivity::class.java))
                activity?.let { context ->
                    // get id}
                    mAuth?.signInWithEmailAndPassword(layoutView?.loginEt?.text.toString(), layoutView?.passwordEditTV?.text.toString())?.addOnCompleteListener { task->
                        layoutView?.progressBar?.visibility = View.GONE
                        if (task.isSuccessful){
                            App.writeSharedPreferences(context, "login", layoutView?.loginEt?.text?.toString()!!)
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                        else{
                            Toast.makeText(context, "Вы ввели не правильный пароль или адрес, попробуйте еще раз",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }else{
                layoutView?.progressBar?.visibility = View.GONE
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
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}