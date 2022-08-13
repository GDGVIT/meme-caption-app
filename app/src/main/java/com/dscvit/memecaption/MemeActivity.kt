package com.dscvit.memecaption

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_meme.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


private lateinit var uri: Uri

class MemeActivity : AppCompatActivity() {
    private var text: String? = null
    private var boldCount = 0
    private var italicCount = 0
    private var boldItalicCount = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)
        supportActionBar?.hide()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        //get uri of the image from main activity
        val uriStr = intent.getStringExtra("uri")
        uri = Uri.parse(uriStr)
        memeIV.setImageURI(uri)

        bottomLL.isVisible = true
        captionLayout.isVisible = false
        formatLayout.isVisible = false

        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        captionTV.setOnClickListener {
            bottomLL.isVisible = false
            captionLayout.isVisible = true
            captionET.setText(captionTV.text, TextView.BufferType.EDITABLE)

            captionET.requestFocus()
            captionET.isFocusableInTouchMode = true
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(captionET, InputMethodManager.SHOW_FORCED)

            captionET.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    captionTV.text = captionET.text
                }
                false
            }

            backCaptionBtn.setOnClickListener {
                captionTV.text = captionET.text
                captionLayout.isVisible = false
                bottomLL.isVisible = true
            }
        }

        captionBtn.setOnClickListener {
            bottomLL.isVisible = false
            captionLayout.isVisible = true
            captionET.setText(captionTV.text, TextView.BufferType.EDITABLE)

            captionET.requestFocus()
            captionET.isFocusableInTouchMode = true
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(captionET, InputMethodManager.SHOW_FORCED)

            captionET.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    captionTV.text = captionET.text
                }
                false
            }

            backCaptionBtn.setOnClickListener {
                captionTV.text = captionET.text
                captionLayout.isVisible = false
                bottomLL.isVisible = true
            }

        }

        formatTextBtn.setOnClickListener {
            bottomLL.isVisible = false
            formatLayout.isVisible = true
            applyTextStyle()

            fontET.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    captionTV.textSize = fontET.text.toString().toFloat()
                }
                false
            }

            closeFormatTextBtn.setOnClickListener {
                captionTV.textSize = fontET.text.toString().toFloat()
                formatLayout.isVisible = false
                bottomLL.isVisible = true
            }
        }

        saveBtn.setOnClickListener {
            val bitmap = getScreenShotFromView(memeCard)

            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
        }

    }


    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return screenshot
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { contentResolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                fos = imageUri?.let { contentResolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, fileName)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Added to your gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyTextStyle() {

        //text style drop down
        val textStyles = resources.getStringArray(R.array.textStyle)
        val arrayAdapter = ArrayAdapter(this, R.layout.textstyledropdown_item, textStyles)
        textStyleTV.setAdapter(arrayAdapter)

        //set text appearance
        textStyleTV.setOnItemClickListener { _, _, i, _ ->
            text = arrayAdapter.getItem(i).toString()

            when (text) {
                "Bold" -> {
                    italicCount = 0
                    boldItalicCount = 0

                    if (boldCount % 2 == 0) {
                        captionTV.setTypeface(null, Typeface.BOLD)
                    } else {
                        captionTV.setTypeface(null, Typeface.NORMAL)
                    }
                    boldCount++

                }
                "Italic" -> {
                    boldCount = 0
                    boldItalicCount = 0

                    if (italicCount % 2 == 0) {
                        captionTV.setTypeface(null, Typeface.ITALIC)
                    } else {
                        captionTV.setTypeface(null, Typeface.NORMAL)
                    }
                    italicCount++
                }
                else -> {
                    boldCount = 0
                    italicCount = 0

                    if (boldItalicCount % 2 == 0) {
                        captionTV.setTypeface(null, Typeface.BOLD_ITALIC)
                    } else {
                        captionTV.setTypeface(null, Typeface.NORMAL)
                    }

                    boldItalicCount++
                }
            }


        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        memeIV.setImageURI(uri)
        bottomLL.isVisible = true
        captionLayout.isVisible = false
        formatLayout.isVisible = false
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        memeIV.setImageURI(uri)
        bottomLL.isVisible = true
        captionLayout.isVisible = false
        formatLayout.isVisible = false
        supportActionBar?.hide()
    }
}