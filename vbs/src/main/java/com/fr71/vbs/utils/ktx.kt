package com.fr71.vbs

import android.app.*
import android.app.job.JobScheduler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.*
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncryptUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable


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

fun loadImg(
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

/** dp和px转换 **/
inline val Float.dp
    get() = ConvertUtils.dp2px(this)
inline val Float.sp
    get() = ConvertUtils.sp2px(this)
inline val Float.pt
    get() = AdaptScreenUtils.pt2Px(this)
inline val Int.dp
    get() = ConvertUtils.dp2px(this.toFloat())
inline val Int.sp
    get() = ConvertUtils.sp2px(this.toFloat())
inline val Int.pt
    get() = AdaptScreenUtils.pt2Px(this.toFloat())

fun Context.dp2px(dpValue: Float): Int {
    return ConvertUtils.dp2px(dpValue)
}

fun Context.sp2px(spValue: Float): Int {
    return ConvertUtils.sp2px(spValue)
}

fun Fragment.dp2px(dpValue: Float): Int {
    return context!!.dp2px(dpValue)
}

fun Fragment.sp2px(dpValue: Float): Int {
    return context!!.sp2px(dpValue)
}

fun View.dp2px(dpValue: Float): Int {
    return context!!.dp2px(dpValue)
}

fun View.sp2px(dpValue: Float): Int {
    return context!!.sp2px(dpValue)
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


/**
 * 解析url的查询参数
 * @return 返回字典
 */
fun String.splitParamsFromUrl(): Map<String, String> {
    return hashMapOf<String, String>().also { map ->
        val index = lastIndexOf("?") + 1
        if (index > 0) {
            substring(index, length).split("&").forEach {
                if (it.contains("=")) {
                    val arr = it.split("=")
                    if (arr.size > 1) {
                        map[arr[0]] = arr[1]
                    }
                }
            }
        }
    }
}

fun String.md5() = EncryptUtils.encryptMD5ToString(this)

fun String.sha1() = EncryptUtils.encryptSHA1ToString(this)

fun String.sha256() = EncryptUtils.encryptSHA256ToString(this)

fun String.sha512() = EncryptUtils.encryptSHA512ToString(this)

/**
 * 随机数增强的md5算法
 * @param salt 加盐的值
 */
fun String.md5Hmac(salt: String) = EncryptUtils.encryptHmacMD5ToString(this, salt)

fun String.sha1Hmac(salt: String) = EncryptUtils.encryptHmacSHA1ToString(this, salt)

fun String.sha256Hmac(salt: String) = EncryptUtils.encryptHmacSHA256ToString(this, salt)

/**
 * DES对称加密
 * @param key 长度必须是8位
 */
fun String.encryptDES(key: String, transformation: String = "DES/CBC/PKCS5Padding") =
    Base64.encodeToString(
        EncryptUtils.encryptDES(
            this.toByteArray(),
            key.toByteArray(),
            "DES/CBC/PKCS5Padding",
            null
        ),
        Base64.NO_WRAP
    )


/**
 * DES对称解密
 * @param key 长度必须是8位
 */
fun String.decryptDES(key: String, transformation: String = "DES/CBC/PKCS5Padding") = String(
    EncryptUtils.decryptDES(
        Base64.decode(this, Base64.NO_WRAP),
        key.toByteArray(),
        transformation,
        null
    )
)

/**
 * AES对称加密
 * @param key 长度必须是16位
 */
fun String.encryptAES(key: String, transformation: String = "AES/ECB/PKCS5Padding") =
    Base64.encodeToString(
        EncryptUtils.encryptAES(this.toByteArray(), key.toByteArray(), transformation, null),
        Base64.NO_WRAP
    )

/**
 * AES对称解密
 * @param key 长度必须是16位
 */
fun String.decryptAES(key: String, transformation: String = "AES/ECB/PKCS5Padding") = String(
    EncryptUtils.decryptAES(
        Base64.decode(this, Base64.NO_WRAP),
        key.toByteArray(),
        transformation,
        null
    )
)


/**
 * 数组转bundle
 */
fun Array<out Pair<String, Any?>>.toBundle(): Bundle? {
    return Bundle().apply {
        forEach { it ->
            val value = it.second
            when (value) {
                null -> putSerializable(it.first, null as Serializable?)
                is Int -> putInt(it.first, value)
                is Long -> putLong(it.first, value)
                is CharSequence -> putCharSequence(it.first, value)
                is String -> putString(it.first, value)
                is Float -> putFloat(it.first, value)
                is Double -> putDouble(it.first, value)
                is Char -> putChar(it.first, value)
                is Short -> putShort(it.first, value)
                is Boolean -> putBoolean(it.first, value)
                is Serializable -> putSerializable(it.first, value)
                is Parcelable -> putParcelable(it.first, value)

                is IntArray -> putIntArray(it.first, value)
                is LongArray -> putLongArray(it.first, value)
                is FloatArray -> putFloatArray(it.first, value)
                is DoubleArray -> putDoubleArray(it.first, value)
                is CharArray -> putCharArray(it.first, value)
                is ShortArray -> putShortArray(it.first, value)
                is BooleanArray -> putBooleanArray(it.first, value)

                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> putCharSequenceArray(
                        it.first,
                        value as Array<CharSequence>
                    )
                    value.isArrayOf<String>() -> putStringArray(it.first, value as Array<String>)
                    value.isArrayOf<Parcelable>() -> putParcelableArray(
                        it.first,
                        value as Array<Parcelable>
                    )
                }
            }
        }
    }

}


inline fun <reified T> Fragment.start(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    val intent = Intent(activity, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivity(intent)
}

inline fun <reified T> Fragment.startForResult(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    val intent = Intent(activity, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivityForResult(intent, requestCode)
}

inline fun <reified T> Context.startActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    val intent = Intent(this, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (this !is Activity) {
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivity(intent)
}

inline fun <reified T> View.startActivity(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    context.startActivity<T>(flag, bundle)
}

inline fun <reified T> View.startForResult(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    (context as Activity).startForResult<T>(flag, bundle, requestCode)
}

inline fun <reified T> Activity.startForResult(
    flag: Int = -1,
    bundle: Array<out Pair<String, Any?>>? = null,
    requestCode: Int = -1
) {
    val intent = Intent(this, T::class.java).apply {
        if (flag != -1) {
            this.addFlags(flag)
        }
        if (bundle != null) putExtras(bundle.toBundle()!!)
    }
    startActivityForResult(intent, requestCode)
}


/**
 * fragment批处理，自动commit
 */
fun FragmentActivity.fragmentManager(action: FragmentTransaction.() -> Unit) {
    supportFragmentManager.beginTransaction()
        .apply { action() }
        .commitAllowingStateLoss()
}

fun FragmentActivity.replace(
    layoutId: Int,
    f: Fragment,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    if (bundle != null) f.arguments = bundle.toBundle()
    supportFragmentManager.beginTransaction()
        .replace(layoutId, f)
        .commitAllowingStateLoss()
}

fun FragmentActivity.add(
    layoutId: Int,
    f: Fragment,
    bundle: Array<out Pair<String, Any?>>? = null
) {
    if (bundle != null) f.arguments = bundle.toBundle()
    supportFragmentManager.beginTransaction()
        .add(layoutId, f)
        .commitAllowingStateLoss()
}

fun FragmentActivity.hide(f: Fragment) {
    supportFragmentManager.beginTransaction()
        .hide(f)
        .commitAllowingStateLoss()
}

fun FragmentActivity.show(f: Fragment) {
    supportFragmentManager.beginTransaction()
        .show(f)
        .commitAllowingStateLoss()
}

fun FragmentActivity.remove(f: Fragment) {
    supportFragmentManager.beginTransaction()
        .remove(f)
        .commitAllowingStateLoss()
}


fun Fragment.replace(layoutId: Int, f: Fragment, bundle: Array<out Pair<String, Any?>>? = null) {
    if (bundle != null) f.arguments = bundle.toBundle()
    childFragmentManager.beginTransaction()
        .replace(layoutId, f)
        .commitAllowingStateLoss()
}

fun Fragment.add(layoutId: Int, f: Fragment, bundle: Array<out Pair<String, Any?>>? = null) {
    if (bundle != null) f.arguments = bundle.toBundle()
    childFragmentManager.beginTransaction()
        .add(layoutId, f)
        .commitAllowingStateLoss()
}

fun Fragment.hide(f: Fragment) {
    childFragmentManager.beginTransaction()
        .hide(f)
        .commitAllowingStateLoss()
}

fun Fragment.show(f: Fragment) {
    childFragmentManager.beginTransaction()
        .show(f)
        .commitAllowingStateLoss()
}

fun Fragment.remove(f: Fragment) {
    childFragmentManager.beginTransaction()
        .remove(f)
        .commitAllowingStateLoss()
}
