package com.dscvit.memecaption

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File


private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"
private lateinit var uri: Uri

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

            if (photoIV.drawable == null) {
                Toast.makeText(this, "chaina", Toast.LENGTH_SHORT).show()
            } else {

                upload()
                val intent = Intent(this, MemeActivity::class.java)
                intent.putExtra("uri", uri.toString())
                startActivity(intent)
            }

        }

    }


    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(columnIndex)
        cursor.close()
        return s
    }

    private fun upload() {
        //file
        var file = File(cacheDir, "myImage.jpg")
        file.createNewFile()
        file = getPath(uri)?.let { File(it) }!!
        uploadFile(file)
    }

    private fun uploadFile(file: File) {
        val repository = FileRepository()
        lifecycleScope.launch {
            repository.uploadFile(file)
        }
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
            uri = data?.data!!
            photoIV.setImageURI(uri)

        } else if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            //camera
            val img = BitmapFactory.decodeFile(photoFile.absolutePath)
            val bytes = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String =
                MediaStore.Images.Media.insertImage(contentResolver, img, "Title", null)
            uri = Uri.parse(path)
            photoIV.setImageURI(uri)
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