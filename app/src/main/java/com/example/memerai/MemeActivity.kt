package com.example.memerai

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_meme.*

private lateinit var uri: Uri

class MemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        //get uri of the image from main activity
        val uriStr = intent.getStringExtra("uri")
        uri = Uri.parse(uriStr)
        memeIV.setImageURI(uri)

        captionTV.setOnClickListener{
            captionET.requestFocus()
            captionET.isFocusableInTouchMode = true
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(captionET, InputMethodManager.SHOW_FORCED)
        }

        addBtn.setOnClickListener {
            captionTV.text = captionET.text.toString()
        }


    }
}