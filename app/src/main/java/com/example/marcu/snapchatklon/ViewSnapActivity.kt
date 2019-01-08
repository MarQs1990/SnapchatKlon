package com.example.marcu.snapchatklon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_view_snap.*
import java.lang.IllegalArgumentException

import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ViewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var myImage: Bitmap? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        messageTextView = findViewById(R.id.messageTextView)
        //val snapImageView = findViewById<ImageView>(R.id.SnapImageView)

        messageTextView?.text = intent.getStringExtra("message")

        val task = ImageDownloader()

        try{
            FirebaseStorage.getInstance().getReference()
                .child("images").child(intent.getStringExtra("imageName")).downloadUrl.addOnSuccessListener{tmpUri ->

                task.execute(tmpUri.toString())
                //snapImageView?.setImageBitmap(myImage)
            }

        }catch(e:java.lang.Exception){
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        try{
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid)
                .child("snaps").child(intent.getStringExtra("snapKey")).removeValue()
            FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
        }catch(e: IllegalArgumentException){
            e.printStackTrace()
        }

    }


    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {

        override fun onPostExecute(result: Bitmap?) {
            val snapImageView = findViewById<ImageView>(R.id.SnapImageView)
            snapImageView.setImageBitmap(result)
        }

        override fun doInBackground(vararg urls: String?): Bitmap? {
            try{
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpsURLConnection
                connection.connect()
                val inputStream = connection.inputStream



                return BitmapFactory.decodeStream(inputStream)
            }catch(e: Exception){
                e.printStackTrace()
                return null
            }
        }

    }
}
