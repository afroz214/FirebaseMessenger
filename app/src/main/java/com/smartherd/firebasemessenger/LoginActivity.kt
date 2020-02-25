package com.smartherd.firebasemessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login_login.setOnClickListener {

            val email = email_login.text.toString()
            val password = password_login.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(this, "Enter ur email and password", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful){
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Failed to login ur Account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
