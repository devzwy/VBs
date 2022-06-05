package com.fr71.vbs

import android.app.*
import android.app.job.JobScheduler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.PowerManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private val gson = Gson()

/**
 * Return system service which type is [T]
 */
inline fun <reified T> Context.getSystemService(): T? =
    ContextCompat.getSystemService(this, T::class.java)

val Context.windowManager get() = getSystemService<WindowManager>()
val Context.clipboardManager get() = getSystemService<ClipboardManager>()
val Context.layoutInflater get() = getSystemService<LayoutInflater>()
val Context.activityManager get() = getSystemService<ActivityManager>()
val Context.powerManager get() = getSystemService<PowerManager>()
val Context.alarmManager get() = getSystemService<AlarmManager>()
val Context.notificationManager get() = getSystemService<NotificationManager>()
val Context.keyguardManager get() = getSystemService<KeyguardManager>()
val Context.locationManager get() = getSystemService<LocationManager>()
val Context.searchManager get() = getSystemService<SearchManager>()
val Context.storageManager get() = getSystemService<StorageManager>()
val Context.vibrator get() = getSystemService<Vibrator>()
val Context.connectivityManager get() = getSystemService<ConnectivityManager>()
val Context.wifiManager get() = getSystemService<WifiManager>()
val Context.audioManager get() = getSystemService<AudioManager>()
val Context.mediaRouter get() = getSystemService<MediaRouter>()
val Context.telephonyManager get() = getSystemService<TelephonyManager>()
val Context.sensorManager get() = getSystemService<SensorManager>()
val Context.subscriptionManager get() = getSystemService<SubscriptionManager>()
val Context.carrierConfigManager get() = getSystemService<CarrierConfigManager>()
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()
val Context.uiModeManager get() = getSystemService<UiModeManager>()
val Context.downloadManager get() = getSystemService<DownloadManager>()
val Context.batteryManager get() = getSystemService<BatteryManager>()
val Context.jobScheduler get() = getSystemService<JobScheduler>()
val Context.accessibilityManager get() = getSystemService<AccessibilityManager>()

sealed class BooleanExt<out T> constructor(val boolean: Boolean)

object Otherwise : BooleanExt<Nothing>(true)

class WithData<out T>(val data: T) : BooleanExt<T>(false)

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> = when {
    this -> {
        WithData(block())
    }
    else -> Otherwise
}

inline fun <T> Boolean.no(block: () -> T) = when {
    this -> Otherwise
    else -> {
        WithData(block())
    }
}

inline infix fun <T> BooleanExt<T>.otherwise(block: () -> T): T = when (this) {
    is Otherwise -> block()
    is WithData<T> -> this.data
}

inline operator fun <T> Boolean.invoke(block: () -> T) = yes(block)

private fun toast(context: Context, text: String, duration: Int) {
    Toast.makeText(context, text, duration).show()
}

fun Context.toastShort(text: String) {
    toast(this, text, Toast.LENGTH_SHORT)
}

fun Context.toastLong(text: String) {
    toast(this, text, Toast.LENGTH_LONG)
}


fun View.toastShort(text: String) {
    toast(this.context, text, Toast.LENGTH_SHORT)
}

fun View.toastLong(text: String) {
    toast(this.context, text, Toast.LENGTH_LONG)
}


/**
 * dp值转换为px
 */
fun Context.dp2px(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * px值转换成dp
 */
fun Context.px2dp(px: Int): Int {
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

/**
 * dp值转换为px
 */
fun View.dp2px(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * px值转换成dp
 */
fun View.px2dp(px: Int): Int {
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

/**
 * 复制文本到粘贴板
 */
fun Context.copyToClipboard(text: String, label: String = "vbs") {
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager?.setPrimaryClip(clipData)
}

/**
 * 快捷获取颜色
 */
fun Context.getColorFromRes(@ColorRes id: Int) = ContextCompat.getColor(this, id)

/**
 * 快捷获取颜色
 */
fun View.getColorFromRes(@ColorRes id: Int) = ContextCompat.getColor(this.context, id)

/**
 * json字符串转对象
 */
fun <T> String.toObject(clazz: Class<T>): T {
    return gson.fromJson(this, clazz)
}

/**
 * json字符串转对象
 */
fun <T> String.toObjectArray(): ArrayList<T> {
    return gson.fromJson(this, object : TypeToken<ArrayList<T>>() {}.type)
}

/**
 * 对象转json字符串
 */
fun Any.toJson() = gson.toJson(this)


private fun loadImg(
    imageView: ImageView,
    imgData: Any,
    placeholderResId: Int? = null
) {
    Glide.with(imageView)
        .load(imgData).let {
            placeholderResId?.let { phr ->
                it.placeholder(phr)
            }
            it.into(imageView)
        }
}

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
