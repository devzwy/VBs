package com.fr71.vbs.utils

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.app.*
import android.app.job.JobScheduler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.*
import androidx.transition.*
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lxj.easyadapter.EasyAdapter
import com.lxj.easyadapter.ItemDelegate
import com.lxj.easyadapter.MultiItemTypeAdapter
import com.lxj.easyadapter.ViewHolder
import java.io.Serializable
import java.util.*
import com.google.android.flexbox.FlexboxLayoutManager

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
//    is WithData<T> -> this.data
    else -> (this as WithData<T>).data
}

inline operator fun <T> Boolean.invoke(block: () -> T) = yes(block)

/**
 * 短吐司
 */
fun Any.toast(text: String?) {
    if (!text.isNullOrEmpty()) ToastUtils.showShort(text)
}

/**
 * 长吐司
 */
fun Any.toastLong(text: String?) {
    if (!text.isNullOrEmpty()) ToastUtils.showLong(text)
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
fun Array<out Pair<String, Any?>>.toBundle(): Bundle {
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
        if (bundle != null) putExtras(bundle.toBundle())
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
        if (bundle != null) putExtras(bundle.toBundle())
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
        if (bundle != null) putExtras(bundle.toBundle())
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
        if (bundle != null) putExtras(bundle.toBundle())
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

/**
 * 设置View的高度
 */
fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View高度，限制在min和max范围之内
 * @param h
 * @param min 最小高度
 * @param max 最大高度
 */
fun View.limitHeight(h: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    when {
        h < min -> params.height = min
        h > max -> params.height = max
        else -> params.height = h
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度
 */
fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

/**
 * 设置View宽度，限制在min和max范围之内
 * @param w
 * @param min 最小宽度
 * @param max 最大宽度
 */
fun View.limitWidth(w: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    when {
        w < min -> params.width = min
        w > max -> params.width = max
        else -> params.width = w
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度和高度
 * @param width 要设置的宽度
 * @param height 要设置的高度
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View的margin
 * @param leftMargin 默认保留原来的
 * @param topMargin 默认是保留原来的
 * @param rightMargin 默认是保留原来的
 * @param bottomMargin 默认是保留原来的
 */
fun View.margin(
    leftMargin: Int = Int.MAX_VALUE,
    topMargin: Int = Int.MAX_VALUE,
    rightMargin: Int = Int.MAX_VALUE,
    bottomMargin: Int = Int.MAX_VALUE
): View {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    if (leftMargin != Int.MAX_VALUE)
        params.leftMargin = leftMargin
    if (topMargin != Int.MAX_VALUE)
        params.topMargin = topMargin
    if (rightMargin != Int.MAX_VALUE)
        params.rightMargin = rightMargin
    if (bottomMargin != Int.MAX_VALUE)
        params.bottomMargin = bottomMargin
    layoutParams = params
    return this
}

/**
 * 设置宽度，带有过渡动画
 * @param targetValue 目标宽度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidth(
    targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置高度，带有过渡动画
 * @param targetValue 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateHeight(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置宽度和高度，带有过渡动画
 * @param targetWidth 目标宽度
 * @param targetHeight 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/*** 可见性相关 ****/
fun View.gone() {
    visibility = View.GONE
}

fun View.animateGone(duration: Long = 250, fade: Boolean = true, move: Boolean = true) {
    TransitionManager.beginDelayedTransition(
        parent as ViewGroup, TransitionSet()
            .setDuration(duration)
            .apply {
                if (move) addTransition(MaterialSharedAxis(MaterialSharedAxis.Y, true))
                if (fade) addTransition(Fade())
            }
            .addTransition(ChangeScroll())
            .addTransition(ChangeBounds())
    )
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.animateVisible(duration: Long = 250, fade: Boolean = true, move: Boolean = true) {
    TransitionManager.beginDelayedTransition(
        parent as ViewGroup, TransitionSet()
            .setDuration(duration)
            .apply {
                if (move) addTransition(MaterialSharedAxis(MaterialSharedAxis.Y, false))
                if (fade) addTransition(Fade())
            }
            .addTransition(ChangeScroll())
            .addTransition(ChangeBounds())
    )
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.animateInvisible(duration: Long = 250) {
    TransitionManager.beginDelayedTransition(
        parent as ViewGroup, TransitionSet()
            .setDuration(duration)
            .addTransition(Fade())
            .addTransition(ChangeBounds())
    )
    visibility = View.INVISIBLE
}

val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/**
 * 切换View的可见性
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}


/**
 * 获取View的截图, 支持获取整个RecyclerView列表的长截图
 * 注意：调用该方法时，请确保View已经测量完毕，如果宽高为0，则将抛出异常
 */
fun View.toBitmap(): Bitmap {
    if (measuredWidth == 0 || measuredHeight == 0) {
        throw RuntimeException("调用该方法时，请确保View已经测量完毕，如果宽高为0，则抛出异常以提醒！")
    }
    return when (this) {
        is RecyclerView -> {
            this.scrollToPosition(0)
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val bmp = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)

            //draw default bg, otherwise will be black
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            this.draw(canvas)
            //恢复高度
            this.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
            )
            bmp //return
        }
        else -> {
            val screenshot =
                Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(screenshot)
            if (background != null) {
                background.setBounds(0, 0, width, measuredHeight)
                background.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            draw(canvas)// 将 view 画到画布上
            screenshot //return
        }
    }
}


// 所有子View
inline val ViewGroup.children
    get() = (0 until childCount).map { getChildAt(it) }


/**
 * 设置View不可用
 */
fun View.disable(value: Float = 0.5f) {
    isEnabled = false
    alpha = value
}

fun View.disableAll() {
    isEnabled = false
    alpha = 0.55f
    if (this is ViewGroup) {
        children.forEach {
            it.disableAll()
        }
    }
}

/**
 * 设置View不可用
 */
fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.enableAll() {
    isEnabled = true
    alpha = 1f
    if (this is ViewGroup) {
        children.forEach {
            it.enableAll()
        }
    }
}


/**
 * 将一段文字中指定range的文字改变大小
 * @param range 要改变大小的文字的范围
 * @param scale 缩放值，大于1，则比其他文字大；小于1，则比其他文字小；默认是1.5
 */
fun CharSequence.toSizeSpan(range: IntRange, scale: Float = 1.5f): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            RelativeSizeSpan(scale),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字改变前景色
 * @param range 要改变前景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字改变背景色
 * @param range 要改变背景色的文字的范围
 * @param color 要改变的颜色，默认是红色
 */
fun CharSequence.toBackgroundColorSpan(range: IntRange, color: Int = Color.RED): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            BackgroundColorSpan(color),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字添加删除线
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStrikeThrougthSpan(range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            StrikethroughSpan(),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 将一段文字中指定range的文字添加颜色和点击事件
 * @param range 目标文字的范围
 */
fun CharSequence.toClickSpan(
    range: IntRange,
    color: Int = Color.RED,
    isUnderlineText: Boolean = false,
    clickAction: () -> Unit
): CharSequence {
    return SpannableString(this).apply {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickAction()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = color
                ds.isUnderlineText = isUnderlineText
            }
        }
        setSpan(clickableSpan, range.start, range.endInclusive, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 将一段文字中指定range的文字添加style效果
 * @param range 要添加删除线的文字的范围
 */
fun CharSequence.toStyleSpan(style: Int = Typeface.BOLD, range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            StyleSpan(style),
            range.start,
            range.endInclusive,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/** TextView的扩展 **/
fun TextView.sizeSpan(str: String = "", range: IntRange, scale: Float = 1.5f): TextView {
    text = (if (str.isEmpty()) text else str).toSizeSpan(range, scale)
    return this
}

fun TextView.appendSizeSpan(str: String = "", scale: Float = 1.5f): TextView {
    append(str.toSizeSpan(0..str.length, scale))
    return this
}

fun TextView.colorSpan(str: String = "", range: IntRange, color: Int = Color.RED): TextView {
    text = (if (str.isEmpty()) text else str).toColorSpan(range, color)
    return this
}

fun TextView.appendColorSpan(str: String = "", color: Int = Color.RED): TextView {
    append(str.toColorSpan(0..str.length, color))
    return this
}

fun TextView.backgroundColorSpan(
    str: String = "",
    range: IntRange,
    color: Int = Color.RED
): TextView {
    text = (if (str.isEmpty()) text else str).toBackgroundColorSpan(range, color)
    return this
}

fun TextView.appendBackgroundColorSpan(str: String = "", color: Int = Color.RED): TextView {
    append(str.toBackgroundColorSpan(0..str.length, color))
    return this
}

fun TextView.strikeThrougthSpan(str: String = "", range: IntRange): TextView {
    text = (if (str.isEmpty()) text else str).toStrikeThrougthSpan(range)
    return this
}

fun TextView.appendStrikeThrougthSpan(str: String = ""): TextView {
    append(str.toStrikeThrougthSpan(0..str.length))
    return this
}

fun TextView.clickSpan(
    str: String = "", range: IntRange,
    color: Int = Color.RED, isUnderlineText: Boolean = false, clickAction: () -> Unit
): TextView {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT  // remove click bg color
    text =
        (if (str.isEmpty()) text else str).toClickSpan(range, color, isUnderlineText, clickAction)
    return this
}

fun TextView.appendClickSpan(
    str: String = "", color: Int = Color.RED,
    isUnderlineText: Boolean = false, clickAction: () -> Unit
): TextView {
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT  // remove click bg color
    append(str.toClickSpan(0..str.length, color, isUnderlineText, clickAction))
    return this
}

fun TextView.styleSpan(str: String = "", range: IntRange, style: Int = Typeface.BOLD): TextView {
    text = (if (str.isEmpty()) text else str).toStyleSpan(style = style, range = range)
    return this
}

fun TextView.appendStyleSpan(str: String = "", style: Int = Typeface.BOLD): TextView {
    append(str.toStyleSpan(style = style, range = 0..str.length))
    return this
}


fun Context.color(id: Int) = ContextCompat.getColor(this, id)

fun Context.string(id: Int, vararg arg: Any? = arrayOf()) =
    if (arg.isEmpty()) resources.getString(id)
    else resources.getString(id, *arg)

fun Context.stringArray(id: Int) = resources.getStringArray(id)

fun Context.drawable(id: Int) = ContextCompat.getDrawable(this, id)

fun Context.dimenPx(id: Int) = resources.getDimensionPixelSize(id)


fun View.color(id: Int) = context.color(id)

fun View.string(id: Int, vararg arg: Any? = arrayOf()) = context?.string(id = id, arg = arg)

fun View.stringArray(id: Int) = context.stringArray(id)

fun View.drawable(id: Int) = context.drawable(id)

fun View.dimenPx(id: Int) = context.dimenPx(id)


fun Fragment.color(id: Int) = context?.color(id)

fun Fragment.string(id: Int, vararg arg: Any? = arrayOf()) = context?.string(id = id, arg = arg)

fun Fragment.stringArray(id: Int) = context?.stringArray(id)

fun Fragment.drawable(id: Int) = context?.drawable(id)

fun Fragment.dimenPx(id: Int) = context?.dimenPx(id)


fun RecyclerView.ViewHolder.color(id: Int) = itemView.color(id)

fun RecyclerView.ViewHolder.string(id: Int, vararg arg: Any? = arrayOf()) =
    itemView.string(id = id, arg = arg)

fun RecyclerView.ViewHolder.stringArray(id: Int) = itemView.stringArray(id)

fun RecyclerView.ViewHolder.drawable(id: Int) = itemView.drawable(id)

fun RecyclerView.ViewHolder.dimenPx(id: Int) = itemView.dimenPx(id)


/**
 * 设置分割线
 * @param color 分割线的颜色，默认是#DEDEDE
 * @param size 分割线的大小，默认是1px
 * @param isReplace 是否覆盖之前的ItemDecoration，默认是true
 *
 */
fun RecyclerView.divider(
    color: Int = Color.parseColor("#f5f5f5"),
    size: Int = 1f.dp,
    isReplace: Boolean = true
): RecyclerView {
    val decoration = RecyclerViewDivider(context, orientation)
    decoration.setDrawable(GradientDrawable().apply {
        setColor(color)
        shape = GradientDrawable.RECTANGLE
        setSize(size, size)
    })
    if (isReplace && itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
    return this
}

fun RecyclerView.flexbox(): RecyclerView {
    layoutManager = FlexboxLayoutManager(context)
    return this
}

fun RecyclerView.vertical(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = SafeLinearLayoutManager(context, RecyclerView.VERTICAL, false)
    if (spanCount != 0) {
        layoutManager = SafeGridLayoutManager(context, spanCount)
    }
    if (isStaggered) {
        layoutManager =
            SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
    return this
}

fun RecyclerView.horizontal(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = SafeLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    if (spanCount != 0) {
        layoutManager =
            SafeGridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false)
    }
    if (isStaggered) {
        layoutManager =
            SafeStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL)
    }
    return this
}


inline val RecyclerView.data
    get() = (adapter as EasyAdapter<*>).data

inline val RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is LinearLayoutManager -> orientation
            is GridLayoutManager -> orientation
            is StaggeredGridLayoutManager -> orientation
            else -> -1
        }
    }


/**
 * onPayloads: 取出变化的数据，只修改变化的
 */
fun <T> RecyclerView.bindData(
    data: List<T>, layoutId: Int,
    onPayloadsChange: ((holder: ViewHolder, t: T, position: Int, payloads: List<Any>) -> Unit)? = null,
    bindFn: (holder: ViewHolder, t: T, position: Int) -> Unit,
): RecyclerView {
    adapter = object : EasyAdapter<T>(data, layoutId) {
        override fun bind(holder: ViewHolder, t: T, position: Int) {
            bindFn(holder, t, position)
        }

        override fun bindWithPayloads(
            holder: ViewHolder,
            t: T,
            position: Int,
            payloads: List<Any>
        ) {
            onPayloadsChange?.invoke(holder, t, position, payloads)
        }
    }
    return this
}

fun <T> RecyclerView.diffUpdate(diffCallback: DiffCallback<T>) {
    val adapter = adapter as? EasyAdapter<T>? ?: return
    DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
}

/**
 * 必须在bindData之后调用，并且需要hasHeaderOrFooter为true才起作用
 */
fun RecyclerView.addHeader(headerView: View): RecyclerView {
    adapter?.apply {
        (this as EasyAdapter<*>).addHeaderView(headerView)
    }
    return this
}

/**
 * 必须在bindData之后调用，并且需要hasHeaderOrFooter为true才起作用
 */
fun RecyclerView.addFooter(footerView: View): RecyclerView {
    adapter?.apply {
        (this as EasyAdapter<*>).addFootView(footerView)
    }
    return this
}

fun <T> RecyclerView.multiTypes(data: List<T>, itemDelegates: List<ItemDelegate<T>>): RecyclerView {
    adapter = MultiItemTypeAdapter<T>(data).apply {
        itemDelegates.forEach { addItemDelegate(it) }
    }
    return this
}

fun <T> RecyclerView.itemClick(listener: (data: List<T>, holder: RecyclerView.ViewHolder, position: Int) -> Unit): RecyclerView {
    adapter?.apply {
        (adapter as MultiItemTypeAdapter<*>).setOnItemClickListener(object :
            MultiItemTypeAdapter.SimpleOnItemClickListener() {
            override fun onItemClick(view: View, holder: RecyclerView.ViewHolder, position: Int) {
                listener(data as List<T>, holder, position)
            }
        })
    }
    return this
}

fun <T> RecyclerView.itemLongClick(listener: (data: List<T>, holder: RecyclerView.ViewHolder, position: Int) -> Unit): RecyclerView {
    adapter?.apply {
        (adapter as MultiItemTypeAdapter<*>).setOnItemClickListener(object :
            MultiItemTypeAdapter.SimpleOnItemClickListener() {
            override fun onItemLongClick(
                view: View,
                holder: RecyclerView.ViewHolder,
                position: Int
            ): Boolean {
                listener(data as List<T>, holder, position)
                return super.onItemLongClick(view, holder, position)
            }
        })
    }
    return this
}

fun RecyclerView.smoothScrollToEnd() {
    if (adapter != null && adapter!!.itemCount > 0) {
        smoothScrollToPosition(adapter!!.itemCount - 1)
    }
}

fun RecyclerView.scrollToEnd() {
    if (adapter != null && adapter!!.itemCount > 0) {
        scrollToPosition(adapter!!.itemCount - 1)
    }
}

/**
 * 滚动置顶，只支持线性布局
 */
fun RecyclerView.scrollTop(position: Int) {
    if (layoutManager is LinearLayoutManager) {
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }
}

/**
 * 启用条目拖拽，必须在设置完adapter之后调用
 * @param isDisableLast 是否禁用最后一个拖拽
 */
fun RecyclerView.enableItemDrag(
    isDisableLast: Boolean = false,
    onDragFinish: (() -> Unit)? = null
) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (adapter == null) return 0
            if (isDisableLast && viewHolder.adapterPosition == (adapter!!.itemCount - 1)) return 0
            return if (recyclerView.layoutManager is GridLayoutManager) {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                makeMovementFlags(dragFlags, swipeFlags)
            } else {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                makeMovementFlags(dragFlags, swipeFlags)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            if (adapter == null) return false
            //得到当拖拽的viewHolder的Position
            val fromPosition = viewHolder.adapterPosition
            //拿到当前拖拽到的item的viewHolder
            val toPosition = target.adapterPosition
            if (isDisableLast && toPosition == (adapter!!.itemCount - 1)) return false
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap((adapter as EasyAdapter<*>).data, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap((adapter as EasyAdapter<*>).data, i, i - 1)
                }
            }
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            onDragFinish?.invoke()
        }

    }).attachToRecyclerView(this)
}

fun RecyclerView.disableItemAnimation(): RecyclerView {
    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    return this
}

/**
 * 边界模糊
 */
fun RecyclerView.fadeEdge(
    length: Int = AdaptScreenUtils.pt2Px(25f),
    isHorizontal: Boolean = false
): RecyclerView {
    if (isHorizontal) isHorizontalFadingEdgeEnabled = true
    else isVerticalFadingEdgeEnabled = true
    overScrollMode = View.OVER_SCROLL_ALWAYS
    setFadingEdgeLength(length)
    return this
}

/**
 * 示例代码如下：
 * class UserDiffCallback(var oldData: List<User>?, var newData: List<User>?) : DiffUtil.Callback() {
override fun getOldListSize() = oldData?.size ?: 0
override fun getNewListSize() = newData?.size ?: 0

override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
if(oldData.isNullOrEmpty() || newData.isNullOrEmpty()) return false
return oldData!![oldItemPosition].id == newData!![newItemPosition].id
}

override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
return oldData!![oldItemPosition].name == newData!![newItemPosition].name
}

//局部更新 areItemsTheSame==true && areContentsTheSame==false 调用
override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
val oldItem = oldData!![oldItemPosition]
val newItem = newData!![newItemPosition]
val bundle = Bundle()
if(oldItem.name != newItem.name){
bundle.putString("name", newItem.name)
}
return bundle
}
}
 *
 */
open class DiffCallback<T>(var oldData: List<T>?, var newData: List<T>?) : DiffUtil.Callback() {
    override fun getOldListSize() = oldData?.size ?: 0
    override fun getNewListSize() = newData?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) return false
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) return false
        return false
    }

    //局部更新
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? = null
}
