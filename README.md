# Android Screen Adapter (API 14 → 36+)

Screen adaptation utilities spanning Android 4.0 (API 14) to Android 17:
density-based global scaling for design-spec fidelity, edge-to-edge
WindowInsets handling for Android 15+, and window size classes for
phones / foldables / tablets.

从 Android 4.0 到 17 的屏幕适配工具集：密度等比缩放（设计稿像素级还原）+ Android 15+ Edge-to-Edge 安全区适配 + 窗口尺寸类响应式判断（手机/折叠屏/平板）。

## The three utilities

### 1. `ScreenAdapterUtils` — global density scaling

Rewrites `DisplayMetrics.density` so a design spec drawn at a fixed width
(e.g. 375dp) scales proportionally on every screen — the classic approach
popularized by large Chinese apps, implemented here with no third-party
library. Both the Application and Activity metrics are rewritten
(activity-only overrides leave dialogs and third-party UIs unscaled), the
target density is computed from the Activity's own width (split-screen
safe), and font-scale changes are recomputed from the pristine baseline
density × the new `fontScale`, so system font-size settings still apply
without corrupting the baseline.

Usage — call first in every Activity:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ScreenAdapterUtils.register(application, this)
    setContentView(...)
}
```

### 2. `WindowInsetsHelper` — edge-to-edge safe areas (Android 15+)

Android 15 forces edge-to-edge: content draws behind the status bar and
the gesture navigation bar. This helper applies system-bar + display-cutout
insets as padding on a root view, with independent top/bottom switches.
Side cutouts in landscape are padded too, and the insets are passed through
to child views rather than consumed, so nested views can still react to
them (e.g. the IME).

```kotlin
WindowInsetsHelper.applySystemBarInsets(binding.root)
```

### 3. `WindowSizeHelper` — window size classes

Classifies the current window via Jetpack WindowManager
(`WindowMetricsCalculator`) into COMPACT (<600dp), MEDIUM (600–840dp,
foldable unfolded / portrait tablet) and EXPANDED (≥840dp), so one layout
codebase can respond to phones, foldables and tablets. The width is
computed from `Configuration.densityDpi` — the system value, unaffected by
the density adapter above — so the two utilities can be combined safely.

## Version pitfalls （分版本避坑）

- **Android 4.x–5.x**: avoid heavy property animations (weak performance,
  leak-prone); hardware acceleration has quirks on some old devices —
  disable it per-Activity in the manifest where needed.
- **Android 9–11**: declare `windowLayoutInDisplayCutoutMode` (e.g.
  `shortEdges`) so content may extend into notch areas deliberately.
- **Android 15+**: stop abusing `fitsSystemWindows=true`; consume insets
  dynamically with `WindowInsetsCompat`, or you get large white bars /
  overlaps under the forced edge-to-edge mode.

## Files

- `ScreenAdapterUtils.kt` — density scaling against a 375dp design baseline
- `WindowInsetsHelper.kt` — system-bar + cutout insets as root padding
- `WindowSizeHelper.kt` — COMPACT / MEDIUM / EXPANDED window classification
