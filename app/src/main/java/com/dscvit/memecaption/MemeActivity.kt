package com.dscvit.memecaption

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_meme.*


private lateinit var uri: Uri

class MemeActivity : AppCompatActivity() {
    private var text: String? = null
    private var boldCount = 0
    private var italicCount = 0
    private var boldItalicCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        //get uri of the image from main activity
        val uriStr = intent.getStringExtra("uri")
        uri = Uri.parse(uriStr)
        memeIV.setImageURI(uri)

        captionTV.setOnClickListener {
            captionET.requestFocus()
            captionET.isFocusableInTouchMode = true
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(captionET, InputMethodManager.SHOW_FORCED)
        }

        addBtn.setOnClickListener {
            captionTV.text = captionET.text.toString()
        }

        applyTextStyle()


    }

    override fun onResume() {
        applyTextStyle()
        super.onResume()
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
}