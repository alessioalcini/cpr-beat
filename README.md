# CPR Beat

A free, offline CPR metronome for Android.

CPR Beat paces chest compressions at 100, 110 or 120 per minute, guides the 30:2
compressions-to-breaths cycle, and counts down the two-minute rescuer-switch interval.
It has no network access, no ads, no analytics and collects no data.

It is a pacing aid, not CPR training and not medical advice. In an emergency call your
local emergency number first.

## Status

v0.1.0 in development. See [SPEC.md](SPEC.md) for the agreed scope and decisions.

## Build

```
./gradlew assembleDebug
```

Requires JDK 17 and the Android SDK (platform 36). The APK lands in
`app/build/outputs/apk/debug/`.

## License

[MIT](LICENSE). Third-party components are listed in `THIRD_PARTY_NOTICES.md`.
