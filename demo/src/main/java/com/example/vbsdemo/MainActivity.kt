package com.example.vbsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.vbsdemo.databinding.ActivityMainBinding
import com.fr71.vbs.OnVbsTextClickListener
import com.fr71.vbs.VBsTextBean
import com.fr71.vbs.dp2px
import com.fr71.vbs.toastShort

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mDataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        mDataBinding.data = Data(
            "vbs是一款快速开发的utils"
        )

        mDataBinding.data2 = Data(
            "vbs是一款快速开发的utils",
            listOf(
                VBsTextBean(
                    color = "#FF6200EE",
                    text = "vbs",
                    tag = null,
                    textSize = dp2px(35).toFloat()
                ),
                VBsTextBean(
                    color = "#FF3700B3",
                    text = "快速开发的utils",
                    tag = this,
                    textSize = null,
                    showUnderLine = true,
                    clickListener = object : OnVbsTextClickListener {
                        override fun onClick(view: View, clickText: String, clickTag: Any?) {
                            toastShort("$clickTag")
                        }
                    }),
            )
        )
    }

    inner class Data(val text: Any, val vbsTexts: List<VBsTextBean>? = null)
}