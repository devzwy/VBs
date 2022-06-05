# ViewBinding
kotlin常用扩展函数 自用

```kotlin
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
```

```xml
 <androidx.appcompat.widget.AppCompatTextView
          vbs_richTexts="@{data2.vbsTexts}/可选" 
            vbs_bindText="@{data2.text}"
            android:layout_width="wrap_content"
            android:layout_height="45dp"
            android:gravity="center_vertical"
            android:textSize="15sp" />


<androidx.appcompat.widget.AppCompatTextView
android:layout_width="wrap_content"
vbs_bindTime="@{date/1654401273}"
vbs_timeFormat="@{`yyyy-MM-dd`}/可选"
android:layout_height="wrap_content"
android:gravity="center_vertical"
android:textSize="15sp" />



<androidx.appcompat.widget.AppCompatImageView
vbs_bindImageData="@{url/resId/path}"
vbs_placeholderResId="@{placeholderResId/可选}"
android:scaleType="centerCrop"
android:layout_width="200dp"
android:layout_height="200dp"
android:layout_marginStart="5dp" />
```