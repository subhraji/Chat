package com.example.chatapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.R

class PDFViewActivity : AppCompatActivity() {

    private var pdfUrl: String? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)

        fileName = intent.getStringExtra("pdfName")
        pdfUrl = intent.getStringExtra("pdfUrl")

    }
}