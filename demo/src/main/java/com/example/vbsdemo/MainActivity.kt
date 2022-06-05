package com.example.vbsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.helper.widget.MotionPlaceholder
import androidx.databinding.DataBindingUtil
import com.example.vbsdemo.databinding.ActivityMainBinding
import com.fr71.vbs.*
import java.util.*
import kotlin.collections.ArrayList

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

        println(TestData().toJson())

        val tt =
            "{\"a\":\"你好\",\"b\":[\"你好。\",\"臭宝\"],\"c\":[{\"color\":\"#fdfdfd\",\"showUnderLine\":false,\"text\":\"啦啦啦啦啦\"}]}"

        println(tt.toObject(TestData::class.java))
        System.currentTimeMillis()

        println(
            arrayListOf(
                VBsTextBean("#fdfdfd", "啦啦啦啦啦"),
                VBsTextBean("#fdfdfd", "sdsdsdsds"),
            ).toJson()
        )

        val tt2 =
            "[{\"color\":\"#fdfdfd\",\"showUnderLine\":false,\"text\":\"啦啦啦啦啦\"},{\"color\":\"#fdfdfd\",\"showUnderLine\":false,\"text\":\"sdsdsdsds\"}]"

        println(tt2.toObjectArray<VBsTextBean>())

        mDataBinding.imgData = ImgData(
            "https://download.wdsf.top/dev/image/demo.png",
            R.drawable.ic_launcher_background
        )

        mDataBinding.imgData2 = ImgData(R.drawable.demo, R.mipmap.ic_launcher)

        mDataBinding.date = Date(System.currentTimeMillis() + 10000)

        mDataBinding.dt = System.currentTimeMillis()
    }


    inner class Data(val text: Any, val vbsTexts: List<VBsTextBean>? = null)
    inner class ImgData(val imgData: Any, val placeholder: Int? = null)



    fun aaaa(){
        toastShort("")
        toastLong("")
        dp2px(100)
        px2dp(100)
        copyToClipboard("1234567")
        getColorFromRes(R.color.black)
        TestData().toJson()
        "{...}".toObject(TestData::class.java).a
        "[...]".toObjectArray<String>().get(0)
        "[{},{}]".toObjectArray<TestData>().get(0)

    }


}

data class TestData(
    val a: String = "你好",
    val b: ArrayList<String> = arrayListOf<String>("你好。", "臭宝"),
    val c: ArrayList<VBsTextBean> = arrayListOf(
        VBsTextBean("#fdfdfd", "啦啦啦啦啦")
    )
)