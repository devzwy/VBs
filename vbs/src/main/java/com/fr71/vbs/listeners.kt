package com.fr71.vbs

import android.view.View

interface OnVbsTextClickListener {
    /**
     * 文本点击回调
     * [view] 当前textView
     * [clickText]点击的文本内容
     * [clickTag] 点击的文本内容标识，构造[VBsTextBean.tag]时传入，这里直接回传，内部不做任何处理
     */
    fun onClick(view: View, clickText: String, clickTag: Any?)
}