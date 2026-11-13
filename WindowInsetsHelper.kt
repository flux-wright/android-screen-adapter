import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object WindowInsetsHelper {

/**
* 自动处理状态栏（顶端）、导航栏/手势条（底端）与左右挖孔（横屏）的防遮挡边距
* @param view 需要应用内边距的根视图或容器
* @param applyTop 是否适配顶部状态栏
* @param applyBottom 是否适配底部导航栏
*/
fun applySystemBarInsets(view: View, applyTop: Boolean = true, applyBottom: Boolean = true) {
val initialLeft = view.paddingLeft
val initialTop = view.paddingTop
val initialRight = view.paddingRight
val initialBottom = view.paddingBottom

ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
val insets: Insets = windowInsets.getInsets(
WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
)

v.setPadding(
initialLeft + insets.left,
if (applyTop) initialTop + insets.top else initialTop,
initialRight + insets.right,
if (applyBottom) initialBottom + insets.bottom else initialBottom
)

// 不要 CONSUMED：子 View 可能还需要 insets（如底部输入框要监听键盘高度），透传下去。
windowInsets
}
}
}
