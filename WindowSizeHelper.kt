import android.app.Activity
import androidx.window.layout.WindowMetricsCalculator

object WindowSizeHelper {

enum class WindowType {
COMPACT, // 手机竖屏 (Width < 600dp)
MEDIUM,  // 折叠屏展开 / 平板竖屏 (600dp <= Width < 840dp)
EXPANDED // 大平板 / 电脑横屏 (Width >= 840dp)
}

fun getWindowType(activity: Activity): WindowType {
val metrics = WindowMetricsCalculator.getOrCreate()
.computeCurrentWindowMetrics(activity)

val bounds = metrics.bounds
// 密度不能取 resources.displayMetrics：若项目同时用了密度等比缩放（ScreenAdapterUtils），
// 那里的 density 已被改写，窗口宽度会被算错。Configuration.densityDpi 不受其影响，始终是系统值，
// 因此两个工具可以安全地组合使用。
val density = activity.resources.configuration.densityDpi / 160f
val widthDp = bounds.width() / density

return when {
widthDp < 600f -> WindowType.COMPACT
widthDp < 840f -> WindowType.MEDIUM
else -> WindowType.EXPANDED
}
}
}
