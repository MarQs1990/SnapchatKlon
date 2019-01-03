package com.example.marcu.snapchatklon

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var createSnapImageView: ImageView? = null
    var messageEditText: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageText)
    }

    fun chooseImageClicked(view: View){
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }else{
            getPhoto()
        }
    }

    fun getPhoto(){
        val pm = this.packageManager
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose Source")
            builder.setMessage("Want to take a new photo or choose one from the gallery?")
            builder.setPositiveButton("Camera"){dialog, which ->
                Intent(MediaStore.ACTION_IMAGE_CAPTURE). also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(takePictureIntent, 1)
                    }
                }
            }
            builder.setNegativeButton("Gallery") {dialog, which ->
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI). also { pickPictureIntent ->
                    pickPictureIntent.resolveActivity(packageManager)?.also {
                        startActivityForResult(pickPictureIntent,1)
                    }
                }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }else{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val selectedImage = data!!.data

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            }catch(e: Exception){
                e.printStackTrace()
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto()
            }
        }
    }

    fun nextClicked(view: View){
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = FirebaseStorage.getInstance().reference.child("images").child(imageName).putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this,"Upload fehlgeschlagen", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {taskSnapshot ->
            Toast.makeText(this,"Upload erfolgreich", Toast.LENGTH_LONG).show()
            val downloadUrl = taskSnapshot.storage.downloadUrl
            Log.i("Upload", "erfolgreich")
            val intent = Intent(this, ChooseUserActivity::class.java)
            intent.putExtra("imageUrl", downloadUrl.toString())
            intent.putExtra("imageName", imageName)
            intent.putExtra("message", messageEditText?.text.toString())
            startActivity(intent)
        }
    }
}
