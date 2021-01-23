package com.example.mainapplication

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_users_registration.*

class UsersRegistrationActivity : AppCompatActivity() {


    private val app1 = FirebaseApp.getInstance("first")
    // private var database: FirebaseDatabase = FirebaseDatabase.getInstance(app1)
    private val app = FirebaseApp.getInstance("secondary")

    private var myRef: DatabaseReference = FirebaseDatabase.getInstance(app1).getReference("users")
    private var toursRef = FirebaseDatabase.getInstance(app).getReference("tours")

    private var tourAuth: FirebaseAuth = FirebaseAuth.getInstance(app)
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance(app1)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_registration)

        val list = ArrayList<BookedUsers>()
        val tour = intent.getSerializableExtra("tour") as Tours

        val quantity = intent.getIntExtra("quantityRegPeople", 1)


        var counter = 1
        numberRegPeopleTv.text = "$counter/$quantity"

        nameField.setEndIconOnClickListener {
            namePers.setText("")
        }
        phoneField.setEndIconOnClickListener {
            phonePerson.setText("")
        }

        if (counter == quantity) {
            save_button.text = "Завершить"
        }

        save_button.setOnClickListener {

            var check = true
            if (save_button.text.toString() == "Завершить") {
                if (namePers.text.toString().isEmpty()) {
                    namePers.error = "Введите правильные имя и фамилию"
                    check = false
                }
                if((!phonePerson.text.toString().isDigitsOnly() || phonePerson.text.toString().length != 11)){
                    phonePerson.error = "Введите правильный номер телефона"
                    check = false
                }
                if(phonePerson.text.toString().isEmpty() && !checkPhone.isChecked){
                    phonePerson.error = "Если у вас нету телефона, заполните поле ниже"
                }
                if(checkPhone.isChecked && namePers.text.toString().isNotEmpty()){
                    phonePerson.error = null
                    phonePerson.setText("")
                    check = true
                }
                if(check) {

                    val userName = namePers.text.toString()
                    val userPhone= phonePerson.text.toString()

                    namePers.setText("")
                    phonePerson.setText("")
                    checkPhone.isChecked = false
                    counter++

                    val bookedUsers = BookedUsers()
                    bookedUsers.name = userName
                    bookedUsers.phone = userPhone

                    bookedUsers.bookedId = App.generateReservationNumber(6)

                    list.add(bookedUsers)

                    list.forEach {
                        toursRef.child(tour.id!!).child("regData").child(mAuth.currentUser?.uid!!).push().setValue(it)
                        myRef.child(mAuth.currentUser?.uid!!).child("regData").child(tour.id!!).push().setValue(it)
                    }

                    showDialog()
                }
            } else {

                if (namePers.text.toString().isEmpty()) {
                    namePers.error = "Введите правильные имя и фамилию"
                    check = false
                }
                if((!phonePerson.text.toString().isDigitsOnly() || phonePerson.text.toString().length != 11)){
                    phonePerson.error = "Введите правильный номер телефона"
                    check = false
                }
                if(phonePerson.text.toString().isEmpty() && !checkPhone.isChecked){
                    phonePerson.error = "Если у вас нету телефона, заполните поле ниже"
                }
                if(checkPhone.isChecked && namePers.text.toString().isNotEmpty()){
                    phonePerson.error = null
                    phonePerson.setText("")
                    check = true
                }
                if(check) {

                    val userName = namePers.text.toString()
                    val userPhone= phonePerson.text.toString()

                    namePers.setText("")
                    phonePerson.setText("")
                    checkPhone.isChecked = false
                    counter++

                    val bookedUsers = BookedUsers()
                    bookedUsers.name = userName
                    bookedUsers.phone = userPhone

                    bookedUsers.bookedId = App.generateReservationNumber(6)

                    list.add(bookedUsers)

                    if (counter == quantity) {
                        save_button.text = "Завершить"
                    }

                    numberRegPeopleTv.text = "$counter/$quantity"
                }
            }
        }
    }

    private fun showDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("")
        dialog.setMessage("Вы успешно зарегестрировались на тур!")
        dialog.setPositiveButton("Ок", DialogInterface.OnClickListener { dialog, id ->
            finish()
        })
        dialog.show()
    }

}