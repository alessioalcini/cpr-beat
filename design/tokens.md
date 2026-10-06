# CPR Beat design tokens

Final visual direction, agreed 2026-10-06. Source: the Claude Design prototype (amber palette),
adjusted by the decisions in `SPEC.md` (idle START circle, outlined STOP, red warning zone,
CPR elapsed line, system font). `design/mock/start-circle-variants.html` shows the result.

```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CprColor {
    val Background    = Color(0xFF0A0B0D)
    val Surface       = Color(0xFF16181C)
    val SurfaceHigh   = Color(0xFF1B1D21)   // beat disk at rest
    val Outline       = Color(0xFF5A606B)   // unselected buttons
    val OutlineSubtle = Color(0xFF3A3E46)   // disk outline, dividers
    val OnBackground  = Color(0xFFF5F6F7)
    val OnMuted       = Color(0xFFA9AEB7)
    val Selected      = Color(0xFFF5F6F7)   // selected segment / rate button
    val OnSelected    = Color(0xFF0A0B0D)
    val Beat          = Color(0xFFFFC233)   // START circle, pulse, banner
    val OnBeat        = Color(0xFF1A1300)
    val TickIdle      = Color(0xFF2E3137)   // ticks 1-25 not yet reached
    val TickDone      = Color(0xFFF5F6F7)   // ticks 1-25 done
    val Warning       = Color(0xFFFF3B30)   // ticks 26-30, disk outline, GET READY
    val Stop          = Color(0xFFD32F2F)   // STOP outline and label
    val Breathe       = Color(0xFF5CC8FF)   // BREATHE label, breath marks, outline
}

object CprType {   // system font (Roboto); tabular figures for all digits
    val Count    = 120.sp  // 30:2 number, Bold
    val Timer    = 88.sp   // countdown, SemiBold
    val Start    = 56.sp   // START inside the circle, ExtraBold, +0.08em
    val Stop     = 26.sp   // STOP, ExtraBold, +0.08em
    val Cue      = 36.sp   // BREATHE; banner 34.sp
    val Rate     = 32.sp   // 100 / 110 / 120, Bold
    val Title    = 24.sp   // Settings app bar
    val Elapsed  = 22.sp   // CPR elapsed and start time digits, Bold; labels 17.sp
    val Segment  = 20.sp   // mode selector, Bold
    val Body     = 17.sp   // settings rows
    val Label    = 15.sp   // caps labels, Bold, +0.10em
}

object CprSpace {
    val Xs    = 4.dp
    val S     = 8.dp
    val M     = 12.dp   // gap between rate buttons
    val L     = 16.dp
    val Edge  = 24.dp   // screen side padding
    val Group = 28.dp   // between settings groups
}

object CprSize {
    val MinTouch       = 64.dp
    val Gear           = 64.dp   // icon 28.dp
    val TimerBlock     = 128.dp
    val Segmented      = 64.dp   // corner 32.dp, 2.dp outline
    val SwitchBanner   = 88.dp   // corner 24.dp
    val StartCircle    = 300.dp  // idle START button
    val BeatArea       = 272.dp  // ticks at radius 124.dp
    val BeatDisk       = 216.dp  // 4.dp outline
    val StopHeight     = 64.dp   // outlined, about 220.dp wide, corner 32.dp
    val RateButton     = 80.dp   // corner 24.dp
    val SettingsOption = 64.dp   // corner 20.dp
    val ToggleRow      = 72.dp
    val SliderThumb    = 36.dp   // track 8.dp, touch 64.dp
}

object CprMotion {
    const val BeatPulseMs   = 240   // once per click: disk flashes Beat and squeezes to 93 %
    const val SwitchFlashMs = 500   // single white flash at 00:00
    const val BannerSec     = 10
}
```

Rules that go with the tokens:

- Every state change also changes a label or a shape, never only a colour.
- Nothing flashes faster than 3 Hz. The beat pulse is 1.7–2 Hz at 100–120 bpm.
- Selected = filled light, unselected = outline.
