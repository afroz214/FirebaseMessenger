package com.smartherd.firebasemessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var firebaseAuth:FirebaseAuth? = null
    var firebaseDatabase:DatabaseReference? = null

    var images:Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_register.setOnClickListener {

           RegisterUser()
        }
        already_have_an_account_main.setOnClickListener {

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        select_photo_main?.setOnClickListener {

            PickImageFromGallery()

        }
    }

    private fun RegisterUser(){

        val email = email_main.text.toString()
        val password = password_main.text.toString()

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){

            Toast.makeText(this, "This Field Cannot be Empty", Toast.LENGTH_SHORT).show()
        }else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Accont Created", Toast.LENGTH_SHORT).show()
                    uploadImagesToFirebaseStorage()

                }else{
                    Toast.makeText(this, "ERROR",Toast.LENGTH_SHORT).show()

                }
            }
        }

    }
    //Picking up the image from gallery

    private fun PickImageFromGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            images = data.data
            select_photo_main?.setImageURI(images)

        }
    }
    private fun uploadImagesToFirebaseStorage(){

        if (images == null) return

        val filename = UUID.randomUUID().toString()
       val ref =  FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(images!!).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener {

                savedUserToFirebaseDatabase(it.toString())
            }

        }
    }
    private fun savedUserToFirebaseDatabase(profileImageUrl:String) {

        val uids = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uids")

        val user = User(uids, email_main.text.toString(), profileImageUrl)
        ref.setValue(user).addOnSuccessListener {

            Log.d("MainActivity", "Saved user Successfully")
            val intent = Intent(this@MainActivity, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }

    }

}

