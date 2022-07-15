package com.dscvit.memecaption

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dscvit.memecaption.Instance.api
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.File
import java.lang.Exception

private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"
private lateinit var uri: Uri

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBtn.isEnabled = false
        captureBtn.isEnabled = false

        //permissions
        //gallery permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        }else{
            uploadBtn.isEnabled = true
        }

        //camera permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 3)
        }else{
            captureBtn.isEnabled = true
        }

        //upload image
        uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        //capture image
        captureBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(this, "com.dscvit.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            startActivityForResult(intent, 4)
        }

        makeBtn.setOnClickListener {
            if(uri != null){
                uploadImage(uri)
            }else{
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, MemeActivity::class.java)
            intent.putExtra("uri", uri.toString())
            startActivity(intent)
        }

    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            //gallery
            photoIV.setImageURI(data?.data)
            uri = data?.data!!
        }else if(requestCode == 4 && resultCode == Activity.RESULT_OK){
            //camera
            val img = BitmapFactory.decodeFile(photoFile.absolutePath)
            photoIV.setImageBitmap(img)
            uri = Uri.parse(photoFile.absolutePath)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImage(uri: Uri) {
        lifecycleScope.launch {
            val stream = contentResolver.openInputStream(uri) ?: return@launch
            val request = UploadStreamRequestBody("image/*", stream, onUploadProgress = {
                Log.d("this", "On upload progress $it")
            })

            val filePart = MultipartBody.Part.createFormData(
                "file",
                "sample.jpg",
                request
            )
//            try{
//                //api.postImage(filePart)
//                val response = try
//            }
//            catch (e: Exception){
//                Log.d("Exception", "Something went wrong")
//                return@launch
//            }

            val response = try {
                Instance.api.postImage(filePart)
            }catch (e: Exception){
                Log.d("Exception", "Something went wrong")
                return@launch
            }
            //Log.d("this", "Uploaded")

            if(response.isSuccessful && response.body() != null){
                Log.d("Success", response.code().toString())
                Log.d("Success", "Uploaded Successfully")
            }else{
                Log.d("Failed", response.code().toString())
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            uploadBtn.isEnabled = true
        }else if(requestCode == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            captureBtn.isEnabled = true
        }
    }
}