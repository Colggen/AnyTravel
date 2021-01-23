package com.example.mainapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Patterns
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.util.*
import java.util.regex.Pattern

class App: Application() {


    override fun onCreate() {
        super.onCreate()


        val options = FirebaseOptions.Builder()
            .setApplicationId("1:1006934621784:android:1db9269a3ec5907727942d")
            .setApiKey("AIzaSyBFGulQwHTqT8PcOrmeI9mvlor5bq_IQYU")
            .setDatabaseUrl("https://anytravel-ef9c8.firebaseio.com/")
            .build()


        val options2 = FirebaseOptions.Builder()
            .setApplicationId("1:1022762460227:android:641ffde1567a8e5a7c60f6")
            .setApiKey("AIzaSyAPsHBJbrTHVQxs3L_VDsWKFlAuxwFeQXs")
            .setDatabaseUrl("https://myapp-69087.firebaseio.com/")
            .build()

        FirebaseApp.initializeApp(applicationContext, options2, "first")

        FirebaseApp.initializeApp(applicationContext, options, "secondary")

    }

    companion object {




        @SuppressLint("ApplySharedPref")
        public fun writeSharedPreferences(context: Context, key: String, value: String) {

            val preferences = PreferenceManager.getDefaultSharedPreferences((context))
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.commit()
        }

        public fun generateUniCode(type: String):String{
            val numberRandom = Random().nextInt()

            return type.plus(numberRandom).plus(System.currentTimeMillis())
        }



        public fun isCorrectPhone(input: String): Boolean{
            var pattern = Pattern.compile("^\\d{11,12}$")

            if(input!!.first()=='+' && input.length==12){
                return pattern.matcher(input.substring(1)).matches()
            }

            return pattern.matcher(input).matches() && input!!.length==11
        }

        public fun isCorrectName(input: String): Boolean{

            val names: Array<String> = input?.split(" ")!!.toTypedArray()
            for (i in names.indices) {
                if (names[i].trim() == "") {
                    names[i] = null.toString()
                }
            }
            var element = 0
            for( i in names.indices){
                if(names[i]!=null){
                    element++
                }
            }

            return Pattern.matches(".*[a-zA-Z]+.*[a-zA-Z]", input ) && element==2
        }

        public fun isCorrectCity(input: String?):Boolean{
            return Pattern.matches(".*[a-zA-Z]+.*[a-zA-Z]", input )
        }

        public fun checkName(input: String?):String{
            var counter = 0
            for( i in input!!.indices){
                if(input[i] ==' '){
                    counter =  i
                }
            }
            return input?.first()!!.toUpperCase()+input?.substring(1,counter+1)+input?.substring(counter+1).capitalize()
        }




        public fun readSharedPreferences(context: Context, key: String): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences((context))
            return preferences.getString(key, "Null")
        }
        public fun generateReservationNumber(length: Int) : String {
            val allowedChars = ('A'..'Z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }


        public fun isValidEmail(email: CharSequence): Boolean {
            return if(TextUtils.isEmpty(email)){
                false
            } else{
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }
    }
}





