package com.dscvit.memecaption

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_meme.*


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

        captionBtn.setOnClickListener {
            bottomLL.isVisible = false
            captionLayout.isVisible = true
            captionET.setText(captionTV.text, TextView.BufferType.EDITABLE)
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
            closeFormatTextBtn.setOnClickListener {
                captionTV.textSize = fontET.text.toString().toFloat()
                formatLayout.isVisible = false
                bottomLL.isVisible = true
            }
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