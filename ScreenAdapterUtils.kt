import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.util.DisplayMetrics

object ScreenAdapterUtils {

// 设计稿规定的基准宽度（以 dp 为单位，例如 375dp 对应标准 iPhone/Android 设计稿）
private const val DESIGN_WIDTH_DP = 375f

private var sNonCompatDensity: Float = 0f
private var sNonCompatScaledDensity: Float = 0f

fun register(application: Application, activity: Activity) {
val appDisplayMetrics = application.resources.displayMetrics
val activityDisplayMetrics = activity.resources.displayMetrics
if (sNonCompatDensity == 0f) {
sNonCompatDensity = appDisplayMetrics.density
sNonCompatScaledDensity = appDisplayMetrics.scaledDensity
// 监听系统字体大小变化：不能回读 metrics 里的 scaledDensity——
// 一旦适配生效，那里存的是改写后的值，回读会污染基准。正确做法：原始密度 × 新 fontScale。
application.registerComponentCallbacks(object : ComponentCallbacks {
override fun onConfigurationChanged(newConfig: Configuration) {
if (newConfig.fontScale > 0) {
sNonCompatScaledDensity = sNonCompatDensity * newConfig.fontScale
}
}
override fun onLowMemory() {}
})
}

// 计算目标 Density：宽度取 Activity 自己的像素宽，
// 否则分屏/小窗模式下会按整屏宽度算错比例。
val targetDensity = activityDisplayMetrics.widthPixels / DESIGN_WIDTH_DP
val targetScaledDensity = targetDensity * (sNonCompatScaledDensity / sNonCompatDensity)
val targetDensityDpi = (targetDensity * 160).toInt()

// Application 与 Activity 的 DisplayMetrics 必须一起改：
// 只改 Activity 时，用 Application 上下文渲染的 Dialog、第三方库界面会比例不一致。
appDisplayMetrics.density = targetDensity
appDisplayMetrics.scaledDensity = targetScaledDensity
appDisplayMetrics.densityDpi = targetDensityDpi
activityDisplayMetrics.density = targetDensity
activityDisplayMetrics.scaledDensity = targetScaledDensity
activityDisplayMetrics.densityDpi = targetDensityDpi
}
}
