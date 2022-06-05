package com.fr71.vbs

/**
 * 对一段文字中需要特殊标注文字颜色和大小时使用的实体类
 * [color]文本的颜色
 * [text]需要标注的文本内容
 * [tag] 标注文本附加数据，在点击该文本时回传
 * [textSize]需要标注的文本大小 默认不改变大小
 * [showUnderLine]是否显示下划线 默认不显示
 * [clickListener]需要标注的文本的点击事件，默认无
 */
data class VBsTextBean(
    val color: String,
    val text: Any,
    val tag: Any? = null,
    val textSize: Float? = null,
    val showUnderLine: Boolean = false,
    val clickListener: OnVbsTextClickListener? = null
)