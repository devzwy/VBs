# ViewBinding

kotlin常用扩展函数 自用

不定时更新

```kotlin
//toast 支持 view/context
toastShort("")
toastLong("")
//支持 view/context
dp2px(100)
px2dp(100)
//复制文本到剪切板
copyToClipboard("1234567")
//读取颜色
getColorFromRes(R.color.black)
//对象转json字符串
TestData().toJson()
//json字符串转对象
"{...}".toObject(TestData::class.java).a
//json字符串转集合-文本集合
"[...]".toObjectArray<String>().get(0)
//json字符串转集合-对象集合
"[{},{}]".toObjectArray<TestData>().get(0)
//减少if过程
isVisibility.yes {
    //true
    this.visibility = View.VISIBLE
}.otherwise {
    //false
    this.visibility = if (occupy == true) View.INVISIBLE else View.GONE
}

//减少if过程
isVisibility.no {
    //false
    this.visibility = View.VISIBLE
}.otherwise {
    //true
    this.visibility = if (occupy == true) View.INVISIBLE else View.GONE
}

```

```xml

<androidx.appcompat.widget.AppCompatTextView vbs_isVisibility="@{是否显示true/false}"
    vbs_occupy="@{隐藏时是否占位}/可选" android:layout_width="wrap_content" android:layout_height="45dp"
    android:gravity="center_vertical" android:textSize="15sp" />

<androidx.appcompat.widget.AppCompatTextView vbs_richTexts="@{富文本配置/大小/下划线/颜色/点击事件/tag}/可选"
vbs_bindText="@{绑定文本/数字}" android:layout_width="wrap_content" android:layout_height="45dp"
android:gravity="center_vertical" android:textSize="15sp" />


<androidx.appcompat.widget.AppCompatTextView android:layout_width="wrap_content"
vbs_bindTime="@{date/1654401273}" vbs_timeFormat="@{`yyyy-MM-dd`}/可选"
android:layout_height="wrap_content" android:gravity="center_vertical" android:textSize="15sp" />


<androidx.appcompat.widget.AppCompatImageView vbs_bindImageData="@{url/resId/path}"
vbs_placeholderResId="@{placeholderResId/可选}" android:scaleType="centerCrop"
android:layout_width="200dp" android:layout_height="200dp" android:layout_marginStart="5dp" />
```