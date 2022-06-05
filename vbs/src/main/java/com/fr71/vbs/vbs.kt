package com.fr71.vbs

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


/**
 * 绑定文本，支持数字绑定
 * [text] 绑定的内容
 * [richTexts] 富文本配置 可选
 */
@BindingAdapter(value = ["vbs_bindText", "vbs_richTexts"], requireAll = false)
fun TextView.bindTextViewContent(text: Any, richTexts: List<VBsTextBean>? = null) {
    this.text = "$text"
    this.text.isNotEmpty().yes {
        richTexts.isNullOrEmpty().no {
            val mSpannable = SpannableString(this.text)
            richTexts!!.forEach {

                val cusText = "${it.text}"
                val startIndex = this.text.indexOf(cusText)

                mSpannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        it.clickListener?.onClick(widget, cusText, it.tag)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = it.showUnderLine
                        ds.color = Color.parseColor(it.color)
                        it.textSize?.let {
                            ds.textSize = it
                        }
                    }
                }, startIndex, startIndex + cusText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            }
            this.text = mSpannable
            this.movementMethod = LinkMovementMethod.getInstance()
            this.highlightColor = this.getColorFromRes(android.R.color.transparent)
        }
    }
}

/**
 * 将时间绑定到文本框
 * [td] 可以为Date/时间戳
 * [formatStr] 格式化 可选  默认yyyy-MM-dd HH:mm:ss
 */
@BindingAdapter(value = ["vbs_bindTime", "vbs_timeFormat"], requireAll = false)
fun TextView.bindTimeText(td: Any, formatStr: String? = null) {
    when (td) {
        is Date -> this.text =
            SimpleDateFormat(formatStr ?: "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(td)
        is Int, is Long -> this.text =
            SimpleDateFormat(
                formatStr ?: "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date(td.toString().toLong()))
    }
}

/**
 * 绑定图片
 * [imgData] 图片地址/路径/资源ID
 * [default] 默认资源 默认为空
 */
@BindingAdapter(value = ["vbs_bindImageData", "vbs_placeholderResId"], requireAll = false)
fun ImageView.bindImageSource(imgData: Any, default: Int? = null) {
    loadImg(this, imgData, default)
}

/**
 * view显示与隐藏
 * [isVisibility] 是否显示
 * [occupy] 隐藏时有效，隐藏时是否占位隐藏 可选，默认false
 */
@BindingAdapter(value = ["vbs_isVisibility", "vbs_occupy"], requireAll = false)
fun View.bindViewIsVisibility(isVisibility: Boolean, occupy: Boolean? = false): Unit {
    isVisibility.yes {
        this.visibility = View.VISIBLE
    }.otherwise {
        this.visibility = if (occupy == true) View.INVISIBLE else View.GONE
    }
}
