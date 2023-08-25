package com.base.canvasanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.bt1
import kotlinx.android.synthetic.main.activity_main.bt2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt1.setOnClickListener {
            val transactionTooLargeException = supportFragmentManager.beginTransaction()
            transactionTooLargeException.replace(R.id.fl, TestAnimCanvasFragment())
            transactionTooLargeException.addToBackStack("TestAnimCanvasFragment")
            transactionTooLargeException.commitAllowingStateLoss()
        }
        bt2.setOnClickListener {
            val transactionTooLargeException = supportFragmentManager.beginTransaction()
            transactionTooLargeException.replace(R.id.fl, TestAnimCanvasFragment2())
            transactionTooLargeException.addToBackStack("TestAnimCanvasFragment2")
            transactionTooLargeException.commitAllowingStateLoss()
        }
    }
}