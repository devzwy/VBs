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
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

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
 * 绑定文本，支持数字绑定
 * [text] 绑定的内容
 * [richTexts] 富文本配置 可选
 */
@BindingAdapter(value = ["vbs_bindText", "vbs_bindRichTexts"], requireAll = false)
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
