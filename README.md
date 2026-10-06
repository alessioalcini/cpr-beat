# CPR Beat

A free, offline CPR metronome for Android.

CPR Beat paces chest compressions at 100, 110 or 120 per minute, guides the 30:2
compressions-to-breaths cycle, and counts down the two-minute rescuer-switch interval.
It has no network access, no ads, no analytics and collects no data.

## How settings work

Settings are saved on the device. Configure the app once (rate, rescuer-switch countdown,
breath pause, auto volume) and every launch starts with exactly those values. The rate and mode
buttons on the main screen are a temporary override for the current session: they take effect
immediately and last until you press STOP, and they never change the saved settings.

## Disclaimer

CPR Beat is a pacing aid. It is not a medical device, does not diagnose, treat, cure or prevent
any condition, and is not a substitute for certified CPR training. In an emergency call your
local emergency number first. Its 100, 110 and 120 bpm options cover the 100–120 compressions
per minute recommended by current resuscitation guidelines (AHA 2025, ERC 2021). CPR Beat is an
independent open-source project. It is not affiliated with, sponsored by, or endorsed by the
American Heart Association or the European Resuscitation Council.

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
