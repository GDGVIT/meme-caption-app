package com.dscvit.memecaption

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.loader.content.CursorLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"
private lateinit var uri: Uri
private var part_image: String? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBtn.isEnabled = false
        captureBtn.isEnabled = false

        //permissions
        permissions()
        image()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            finish()
            startActivity(intent)
            return
        }

        makeBtn.setOnClickListener {

            if(photoIV.drawable == null){
                Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show()
            }else{
                uploadImage()
            }
            val intent = Intent(this, MemeActivity::class.java)
            intent.putExtra("uri", uri.toString())
            startActivity(intent)
        }

    }

    private fun uploadImage() {
        //https://demonuts.com/android-upload-image-to-server-using-retrofit/
        //to bitmap
        val drawable = photoIV.drawable as BitmapDrawable

        //to base64
        val stream = ByteArrayOutputStream()
        val bitmap : Bitmap = drawable.bitmap

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val image = stream.toByteArray()
        val base64String = Base64.encodeToString(image, Base64.DEFAULT)

        val call: Call<String> = Instance.api.uploadImage(base64String)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful && response.body() != null){
                    Log.d("base64Success", response.code().toString())
                    Log.d("base64Success", base64String)
                }else{
                    Log.d("base64Fail", response.code().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun permissions() {
        //gallery permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        } else {
            uploadBtn.isEnabled = true
        }

        //camera permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 3)
        } else {
            captureBtn.isEnabled = true
        }
    }

    private fun image() {
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

            val fileProvider =
                FileProvider.getUriForFile(this, "com.dscvit.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            startActivityForResult(intent, 4)
        }
    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //gallery
            // photoIV.setImageURI(data?.data)
            uri = data?.data!!

            val imageProjection = arrayOf<String>(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(uri, imageProjection, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                var indexImage: Int = cursor.getColumnIndex(imageProjection[0])
                part_image = cursor.getString(indexImage)
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                photoIV.setImageBitmap(bitmap)
            }


        } else if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            //camera
            val img = BitmapFactory.decodeFile(photoFile.absolutePath)
            photoIV.setImageBitmap(img)
            uri = Uri.parse(photoFile.absolutePath)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadBtn.isEnabled = true
        } else if (requestCode == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureBtn.isEnabled = true
        }
    }
}