package com.dscvit.memecaption

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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

        resetCapBtn.setOnClickListener {
            captionTV.isVisible = true
            captionTV.x = memeIV.x
            captionTV.y = memeIV.y
        }

        captionTV.setOnTouchListener { v, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    Log.d("Action Down", "down")
                }

                MotionEvent.ACTION_MOVE -> {
                    val xCorr: Float = event.rawX - relativeImageLayout.x - captionTV.width / 2.0f
                    v.x = xCorr
                    v.y = event.rawY - relativeImageLayout.y - captionTV.height / 2.0f - 220f

                    //makes the caption disappear when it goes out of boundary
                    if (xCorr > memeIV.width) {
                        captionTV.isVisible = false
                    } else if (xCorr < memeIV.x) {
                        captionTV.isVisible = false
                    }

                    if (v.y > memeIV.height) {
                        captionTV.isVisible = false
                    } else if (v.y < memeIV.y) {
                        captionTV.isVisible = false
                    }


                }

            }
            true
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