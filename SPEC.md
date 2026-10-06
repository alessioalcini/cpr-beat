# CPR Beat — Specification

Status: v0.1.0 scope frozen on 2026-10-06. Changes go through the decision log at the bottom.

## 1. Purpose

CPR Beat is a chest-compression metronome for cardiopulmonary resuscitation (CPR).
It paces the rescuer at 100, 110 or 120 compressions per minute, optionally guides the
30:2 compressions-to-breaths cycle, and counts down the two-minute rescuer-switch interval.

It is an Android analog of the iOS app "CPR Now" (Keegan Sauer, 2022). It is a pacing aid,
not CPR training and not medical advice.

## 2. Non-negotiable requirements

1. **Free to use and free to redistribute.** App code is MIT-licensed. Every dependency
   must be under a permissive license (Apache-2.0, MIT, BSD). No proprietary SDKs:
   no Google Play Services, no Firebase, no ad or analytics SDKs.
2. **Fully offline.** No network code. The manifest must not declare the `INTERNET`
   permission, so the OS blocks any network access. Works identically in airplane mode,
   without SIM, without Wi-Fi.
3. **No data collection.** No accounts, no telemetry, no crash reporting in 0.1.0.
4. **No permissions in 0.1.0.** Vibration is not implemented, so `VIBRATE` is not requested.
   Keeping the screen on uses a window flag, not `WAKE_LOCK`. (AndroidX adds one app-private
   signature permission, `dev.alcini.cprbeat.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`; it
   grants the app nothing and is not shown to users.)
5. **English only** in 0.1.0.

## 3. Guideline basis

American Heart Association, 2025 Guidelines for CPR and ECC, Part 7: Adult Basic Life Support:

- Compression rate 100–120 per minute.
- Compression-to-ventilation ratio 30:2 for adult CPR without an advanced airway.
- Pause compressions for less than 10 seconds to deliver 2 breaths; each breath over 1 second.
- With two or more rescuers, rotate the compressor about every 2 minutes.

The app quotes these numbers. It must not use AHA or ERC names in a way that implies
endorsement, and must not use their logos.

## 4. Main screen

Single screen, portrait only, dark high-contrast theme, large touch targets usable with gloves.

```
┌──────────────────────────────┐
│ CPR Beat                 ⚙  │  ← settings gear, small, top-right
│                              │
│          02:00               │  ← rescuer-switch countdown
│                              │
│ [ Compressions ] [  30:2  ]  │  ← mode selector
│                              │
│          ●                   │  ← beat pulse (and 1…30 counter in 30:2)
│      ┌────────┐              │
│      │ START  │              │  ← start / stop
│      └────────┘              │
│                              │
│ [ 100 ]  [ 110 ]  [ 120 ]    │  ← rate selector, default highlighted
└──────────────────────────────┘
```

## 5. Behavior

### 5.1 Start / Stop

- START begins the metronome immediately with the current on-screen rate and mode and
  starts the countdown. The button becomes STOP.
- STOP silences everything, resets the counter and the countdown.
- While running, the screen does not turn off (`FLAG_KEEP_SCREEN_ON`).
- Leaving the app (Home, lock button, incoming call) stops the metronome. No foreground
  service in 0.1.0.

### 5.2 Rate

- Three large buttons: 100, 110, 120 bpm. Default comes from Settings (factory default 110).
- Changing the rate while running takes effect from the next click, without stopping.

### 5.3 Modes

- **Compressions only** (hands-only CPR). Continuous clicks. Factory default.
- **30:2.** Clicks 1–30, then a breath pause, then clicks 1–30 again.
  - A large counter shows the current compression number 1…30.
  - Clicks 26–30 use a higher pitch to warn that the pause is coming.
  - Breath pause length: default 5 s, adjustable 3–8 s in Settings. Within the pause:
    a long "breath" tone at 0.5 s, a second long "breath" tone at 2.0 s, a short "resume"
    tone 0.5 s before the pause ends, then click 1 at the end of the pause.
- Switching mode while running takes effect immediately; the counter restarts at 1.

### 5.4 Rescuer-switch countdown

- Counts down from the default set in Settings. Options: Off, 1, 2, 3, 5 minutes.
  Factory default 2 minutes.
- At 00:00: one single screen flash (about 300 ms), the banner **SWITCH RESCUER** is shown
  for 10 seconds, and the countdown restarts immediately. The metronome is not affected.
- No sound is attached to the countdown. The only sounds in the app are the metronome
  clicks and the 30:2 breath cues.

### 5.5 Session vs. settings

- Every START uses the defaults stored in Settings.
- Rate, mode and countdown changed on the main screen apply to the current session only
  and are not persisted.

### 5.6 Sound

- Audio usage `USAGE_ALARM` with content type sonification: plays in silent mode and under
  default Do Not Disturb rules, like an alarm clock. This addresses the "volume issue"
  reported by users of the iOS original.
- While the app is in the foreground, the hardware volume keys control the alarm stream.
- **Auto max volume** (Settings, on by default): on START, if the alarm stream is below
  70 % of maximum, raise it to maximum; restore the previous level on STOP.
- Timing: the click is a pre-rendered PCM buffer of exactly one beat period, looped by
  `AudioTrack`. Zero drift, sample-accurate. The buffer is regenerated on rate change.
  Handler- or coroutine-driven scheduling is not acceptable for the beat itself.

### 5.7 Visual safety

- The beat indicator is a smooth pulse animation, not a strobe.
- Nothing on screen flashes faster than 3 Hz. The countdown expiry is a single flash.

## 6. Settings screen

Opened via the gear icon. Items:

1. Default rate: 100 / 110 / 120 (factory 110).
2. Default countdown: Off / 1 / 2 / 3 / 5 min (factory 2).
3. Breath pause in 30:2: 3–8 s (factory 5).
4. Auto max volume: on / off (factory on).
5. About: app version, MIT license, third-party notices (Apache-2.0 NOTICE texts),
   disclaimer ("pacing aid, not training or medical advice; call emergency services").

Storage: AndroidX DataStore Preferences.

## 7. Out of scope for 0.1.0 (backlog)

- Vibration with on/off toggle (planned 0.2.0; off by default because the phone may lie on
  the patient).
- Foreground service so the metronome survives screen lock and calls.
- Voice prompts; emergency call button (112/911 by locale); home-screen widget and
  Quick Settings tile; Wear OS; Russian and Italian localization; signed release builds
  and store listings (Google Play, F-Droid).

## 8. Technical stack

| Item | Choice |
|---|---|
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| applicationId | `dev.alcini.cprbeat` |
| minSdk / targetSdk / compileSdk | 26 / 36 / 37 |
| versionName / versionCode | 0.1.0 / 1 |
| Build | Gradle wrapper, Kotlin DSL, version catalog, JDK 17 for the Gradle daemon |
| Persistence | DataStore Preferences |
| Audio | `AudioTrack` looped PCM, `USAGE_ALARM` |
| Tests | Unit tests for the timing engine; minimal Compose UI test for start/stop |
| CI | GitHub Actions: build debug APK on every push |

Versions pinned in `gradle/libs.versions.toml`, verified against Google Maven and Maven
Central on 2026-10-06:

| Component | Version |
|---|---|
| Android Gradle Plugin | 9.4.1 (built-in Kotlin; no `kotlin-android` plugin) |
| Gradle (wrapper) | 9.7.1 |
| Kotlin + Compose compiler plugin | 2.4.20 |
| Compose BOM | 2026.09.00 (Compose 1.12.1, Material 3 1.4.0) |
| activity-compose / core / lifecycle / datastore | 1.13.0 / 1.19.1 / 2.11.0 / 1.2.1 |
| SDK packages | platforms;android-37.0, build-tools;36.0.0, platform-tools (stable channel only) |
| JDK for the Gradle daemon | 17 |

## 9. Architecture

Package `dev.alcini.cprbeat`:

- `engine/MetronomeEngine` — pure Kotlin, no Android types: beat period, 30:2 cycle state,
  breath-pause schedule, PCM synthesis parameters. Fully unit-tested.
- `audio/ClickPlayer` — `AudioTrack` wrapper: renders the one-period buffer, loops it,
  swaps buffers on rate change, plays breath cues.
- `session/SessionViewModel` — state machine Idle / Running; owns countdown, mode, rate,
  counter; talks to `ClickPlayer` and `VolumeController`.
- `settings/SettingsRepository` — DataStore access and factory defaults.
- `ui/MainScreen`, `ui/SettingsScreen`, `ui/theme`.

## 10. Repository and identity

- GitHub: https://github.com/alessioalcini/cpr-beat
- Author of commits: Alessio Alcini <alessio.alcini.it@gmail.com>, set per repository.
- License: MIT, copyright Alessio Alcini.
- Repository files: `README.md`, `LICENSE`, `SPEC.md`, `THIRD_PARTY_NOTICES.md`,
  `.github/workflows/build.yml`.

## 11. Decision log

| Date | Decision | Why |
|---|---|---|
| 2026-10-06 | Name "CPR Beat", id `dev.alcini.cprbeat`, no domain registered | No exact-name conflicts found in Play / App Store; reverse-DNS id needs no domain ownership |
| 2026-10-06 | MIT for app code, permissive-only dependencies | Owner requirement: free to use and redistribute |
| 2026-10-06 | No INTERNET permission, no network code | Must work in airplane mode; nothing to leak |
| 2026-10-06 | Vibration not implemented in 0.1.0, toggle later | Phone may lie on the patient; keeps 0.1.0 permission-free |
| 2026-10-06 | Alarm audio stream + auto max volume | iOS original had user reports of inaudible clicks |
| 2026-10-06 | Breath pause 5 s default, 3–8 s range | AHA: pause under 10 s, each breath 1 s; 4 s is tight for untrained rescuers |
| 2026-10-06 | Countdown expiry: single flash + SWITCH RESCUER banner, no sound, auto-restart | Owner wants no sounds other than the metronome; inform, do not insist |
| 2026-10-06 | Every START uses Settings defaults; on-screen changes are per session | Predictability under stress |
| 2026-10-06 | Metronome stops when the app leaves the foreground | Keeps 0.1.0 simple; foreground service is backlog |
| 2026-10-06 | English only | Owner decision for 0.1.0 |
| 2026-10-06 | compileSdk 37, targetSdk stays 36 | Compose 1.12 / core 1.19 / lifecycle 2.11 require API 37 to compile; behaviour is still defined by targetSdk 36 (Android 16) |
| 2026-10-06 | Only stable SDK packages; preview tokens removed | Packages under `android-sdk-preview-license` forbid shipping apps built with them |
| 2026-10-06 | AGP 9.4.1 with built-in Kotlin, Gradle 9.7.1, JDK 17 | Latest stable set that runs on JDK 17; Gradle 9.7.x is inside Kotlin 2.4.20's tested range |
