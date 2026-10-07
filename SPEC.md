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
Visual direction: the ChatGPT concept in `design/chatgpt/design_v1.png` (blue / purple / amber
palette); interaction detail and sizes: the Claude Design prototype in `design/claude-handoff/`
(START/STOP 160 dp tall, 30-tick ring, GET READY zone, breath marks, SWITCH RESCUER banner).
Typography: system font (Roboto), no bundled fonts; digits use tabular figures.

```
┌──────────────────────────────┐
│ CPR Beat                 ⚙  │  ← settings gear, small, top-right
│                              │
│          02:00               │  ← rescuer-switch countdown
│                              │
│ [ Compressions ] [  30:2  ]  │  ← mode selector
│                              │
│        ╭──────╮              │  ← idle: big amber START circle
│        │ START│              │     running: pulsing beat disk
│        ╰──────╯              │     (1…30 counter in 30:2)
│        ( STOP )              │  ← running only: outlined red STOP
│   CPR 04:12 · started 14:32  │  ← running/after stop: elapsed + start time
│                              │
│ [ 100 ]  [ 110 ]  [ 120 ]    │  ← rate selector, default highlighted
└──────────────────────────────┘
```

## 5. Behavior

### 5.1 Start / Stop

- START begins the metronome immediately with the current on-screen rate and mode and
  starts the countdown. The button becomes STOP.
- While running the circle is inert; only the STOP button stops, so a stray touch during CPR
  cannot silence the metronome.
- STOP silences everything, resets the counter and the countdown. The CPR elapsed time and the
  START clock time stay on screen until the next START (handover to paramedics).
- Idle layout: the beat circle itself is one large amber START button (about 300 dp). Running
  layout: the same spot is the pulsing beat indicator; a smaller outlined red STOP (64 dp high,
  about 220 dp wide) sits under it; under STOP a single line shows `CPR mm:ss · started HH:MM`.
- Beat pulse: on every click the disk flashes amber and squeezes to 93 % for 240 ms while a ring
  expands and fades. At 120 bpm that is 2 Hz, within the 3 Hz limit of 5.8.
- While running, the screen does not turn off (`FLAG_KEEP_SCREEN_ON`).
- Leaving the app (Home, lock button, incoming call) stops the metronome. No foreground
  service in 0.1.0.

### 5.2 Rate

- Three large buttons: 100, 110, 120 bpm. Default comes from Settings (factory default 110).
- Changing the rate while running takes effect from the next click, without stopping.

### 5.3 Modes

- **Compressions only** (hands-only CPR). Continuous clicks. Factory default.
- **30:2.** Clicks 1–30, then a breath pause, then clicks 1–30 again.
  - A large counter shows the current compression number 1…30 inside the disk, with a ring of
    30 ticks around it that fill as compressions are counted.
  - The last five compressions (26–30) are the warning zone: their five ticks are longer and
    turn solid red as each is reached (dim like the others before that), the disk outline and the label "GET READY" turn
    red while in the zone, and the clicks use a higher pitch.
  - Breath pause length: default 5 s, adjustable 3–8 s in Settings. The pause is split into two
    equal halves, one per breath: a "breath" tone opens each half, a short "resume" tone sounds
    0.5 s before the pause ends, then click 1 at the end of the pause. So at 8 s the breaths are
    paced 4 s apart, at 4 s they are 2 s apart.
- Switching mode while running takes effect immediately; the counter restarts at 1.

### 5.4 Rescuer-switch countdown

- Counts down from the default set in Settings. Options: Off, 1, 2, 3, 5 minutes.
  Factory default 2 minutes. With Off, the digits are replaced by the word OFF; the block keeps
  its place so the layout never shifts.
- Tapping the countdown block cycles Off → 1 → 2 → 3 → 5 → Off. This is a per-session override
  like rate and mode (section 5.5). While running, the new value applies at once and the
  countdown restarts from it.
- At 00:00: one single screen flash (about 300 ms) and the countdown restarts immediately. The
  banner **SWITCH RESCUER** covers the countdown digits for 5 seconds, then the running countdown
  shows through again. Taps on the banner are ignored. The mode buttons stay on screen and
  usable. 5 s matches the guideline window for a rescuer switch (ERC/AHA: change over within
  about 5 s) and gives a rescuer who looked up after the flash time to read it; the covered
  digits carry no information because the countdown has just restarted. The metronome is not
  affected.
- No sound is attached to the countdown. The only sounds in the app are the metronome
  clicks and the 30:2 breath cues.

### 5.5 Settings persist, main-screen taps do not

- Settings are stored permanently on the device. The user configures the app once (rate,
  countdown, breath pause, auto volume) and every later launch starts with exactly those values.
  Nothing has to be re-selected at launch.
- Rate and mode buttons and the countdown tap on the main screen are a per-session override: they take effect at
  once and last until STOP. They never change the stored Settings, so an accidental tap during
  CPR cannot alter the next launch.
- README explains this distinction to users in one paragraph.

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

### 5.7 First-launch hints

- On the very first launch the main screen shows three short on-screen labels with pointers:
  "Tap to start" at the circle, "Tap to change the switch interval" at the countdown, and
  "Compression rate" at the rate buttons.
- They never block anything: START and every other control work underneath them. Any tap, or
  the first START, dismisses them for good (flag stored in Settings).
- About contains a short "How to use" section with the same information for later reading.
- No modal onboarding, no multi-step wizard: the first launch may be the emergency itself.

### 5.8 Visual safety

- The beat indicator is a smooth pulse animation, not a strobe.
- Nothing on screen flashes faster than 3 Hz. The countdown expiry is a single flash.

## 6. Settings screen

Opened via the gear icon. Items:

1. Default rate: 100 / 110 / 120 (factory 110).
2. Default countdown: Off / 1 / 2 / 3 / 5 min (factory 2).
3. Breath pause in 30:2: 3–8 s (factory 5).
4. Auto max volume: on / off (factory on).
5. Click sound: Clean 1000 (factory default), Wood 880, Low 660. Three timbres that all passed
   the owner's listening test on a Samsung A52; the choice is persistent like the other settings.
6. About: app version, MIT license, disclaimer ("pacing aid, not training or medical advice;
   call emergency services first"), the affiliation disclaimer from section 11.4, a link to the
   source repository, and the third-party licenses as an expandable section inside About
   (text shipped in assets from `THIRD_PARTY_NOTICES.md`). No separate licenses screen:
   the app has three screens, Main, Settings, About.

Storage: AndroidX DataStore Preferences.

### 6.1 Launcher icon

Adaptive icon, variant v2 chosen on 2026-10-06: amber background (`#FFC233`), black heart
(`#1A1300`), white pulse line that runs beyond the heart like a monitor trace. Source SVGs and
the launcher-mask preview are in `design/icon/`; the vector drawables live in `app/src/main/res`.
Monochrome layer reuses the foreground. Must stay distinctive enough not to resemble the
"One Beat CPR" training brand (section 11.4).

## 7. Out of scope for 0.1.0 (backlog)

- Vibration with on/off toggle (planned 0.2.0; off by default because the phone may lie on
  the patient).
- "Keep running in background" setting (off by default). When on, the metronome keeps
  clicking after the app is minimised or the screen locks, so the rescuer can dial emergency
  services from the same phone while the ticks continue. Needs a foreground service with a
  persistent notification (Android 14+ requires a declared service type, likely
  `mediaPlayback`), plus a decision on what happens to the clicks during the call itself
  (audio focus, `USAGE_ALARM` vs in-call routing, speaker vs earpiece). Research how reliably
  this works on real phones before committing (owner, 2026-10-07).
- Voice prompts; emergency call button (112/911 by locale); home-screen widget and
  Quick Settings tile; Wear OS; Russian and Italian localization; signed release builds
  and store listings (Google Play, F-Droid).
- A proper first-launch tutorial. 0.1.0 ships only the non-blocking hints of 5.7; how a fuller
  tutorial should look is undecided (owner, 2026-10-07).
- Light theme or a theme switch. 0.1.0 is dark only by design (contrast outdoors, no
  surprises in an emergency); dynamic (Material You) colors are disabled for the same reason.

### 7.1 User feedback under observation

Single reports from early users. Policy: collect feedback and act only on what repeats; one
opinion is not a reason to redesign (owner, 2026-10-07). Each item records the current
behavior, what the user expected, and the open question.

- **Switching Compressions → 30:2 restarts the ring from zero.** Current behavior is by design
  (5.3: the counter restarts at 1 on a mode switch). The user had already been compressing for a
  while and expected the ring to credit the clicks already made. Open question: what should the
  ring show when the number of compressions before the switch is unknown to the user, e.g. count
  the clicks of the current session modulo 30, or start at 1 as now. Reported once, 2026-10-07.

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

## 11. Publishing requirements (verified 2026-10-06)

Findings of the license and store-policy audit. None of them blocks a free release; all are
process or wording requirements.

### 11.1 Licenses

- Everything compiled into the APK is permissively licensed: Kotlin stdlib and
  kotlinx-coroutines (Apache-2.0; the stdlib embeds small BSD-3-Clause and Boost-licensed
  parts), AndroidX core / activity / lifecycle / datastore, Jetpack Compose, Material 3 and
  Material Icons (Apache-2.0), and `androidx.datastore:datastore-preferences-external-protobuf`
  (BSD-3-Clause, a repackaged protobuf-lite pulled in by DataStore).
- Apache-2.0 section 4(a) requires giving APK recipients a copy of the license:
  `THIRD_PARTY_NOTICES.md` in the repo and the same text in the in-app "Third-party licenses"
  screen. None of the shipped artifacts carries a NOTICE file, so no NOTICE propagation is due.
- Build-only tools (Gradle, AGP, Kotlin compiler, OpenJDK 17, Homebrew, gh) leave nothing in the
  APK and impose nothing on it. The Android SDK license is royalty-free, Google claims no rights
  in apps built with it, and the SDK itself must never be committed or redistributed.
- Only stable SDK packages may be used for builds: anything under the preview license forbids
  shipping apps built with it.
- MIT for the app is compatible with all of the above.

### 11.2 Google Play

- Health Content and Services policy applies. Complete the Health apps declaration and declare
  "Emergency and First Aid"; do not declare "Medical Device Apps".
- Store description must say the app is not a medical device and does not diagnose, treat,
  cure or prevent any condition, and must tell users to consult a healthcare professional.
  No outcome claims ("saves lives", "improves survival") and no compression-measurement claims.
- Privacy policy is mandatory even with zero data collection: a public HTML page (GitHub
  Pages) plus a link or text inside the app. Data safety form: "no data collected or shared".
- Target API: apps submitted after 2026-08-31 must target API 36 or higher. We target 36.
- Personal developer account: one-time US$25 fee, government-ID verification, and a closed
  test with at least 12 testers opted in for 14 consecutive days before production access.
  Google shows the account holder's legal name, country and developer e-mail publicly.
- An organization account needs a D-U-N-S number.

### 11.3 F-Droid

- Eligible: MIT app, Apache/BSD dependencies from Google Maven and Maven Central, Android SDK
  builds are explicitly allowed. No Play Services, Firebase, ads or analytics, ever.
- Add `fastlane/metadata/android/en-US` (title, descriptions, icon, screenshots) and tag
  releases with versionCode/versionName bumps in the tagged commit.
- Aim for reproducible builds: pinned tool versions, deterministic R8, no baseline-profile
  generation.

### 11.4 Brand and trademarks

- Quoting the 100–120 per minute rate and naming AHA/ERC as the source is factual, nominative
  use. Required wording in README, listing and About: "CPR Beat is an independent open-source
  project. It is not affiliated with, sponsored by, or endorsed by the American Heart
  Association or the European Resuscitation Council."
- Never use AHA/ERC logos, course artwork, the phrase "AHA-approved" or the AHA slogan
  "Be the Beat"; keep AHA/ERC out of the title, icon, package name and store keywords.
- No exact "CPR Beat" mark was found on Google Play, the App Store, or in the TMview
  aggregate (USPTO/EUIPO/WIPO), but the register check could not be repeated by an independent
  verifier. A live US mark "ONE BEAT CPR" exists for CPR training services. Mitigation: present
  the app as software, never as training, avoid "One Beat" wording, and make the icon
  distinctive rather than a generic heart-with-pulse-line.

### 11.5 GitHub

- Public repositories get free Actions minutes; private ones get 2,000 minutes and 500 MB of
  artifact storage per month. Releases can host the signed APK, SHA-256 sums and changelog.
- Keep the signing keystore and passwords out of the repository.

## 12. Decision log

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
| 2026-10-07 | SWITCH RESCUER banner moved into the countdown block, 5 s instead of 10 s | User report: the banner hid the mode buttons; 5 s matches the guideline switch window, and the covered digits had just reset |
| 2026-10-06 | AGP 9.4.1 with built-in Kotlin, Gradle 9.7.1, JDK 17 | Latest stable set that runs on JDK 17; Gradle 9.7.x is inside Kotlin 2.4.20's tested range |
| 2026-10-06 | Visuals from the ChatGPT concept, behaviour and sizes from the Claude Design prototype | Owner preference; the Claude prototype encodes shape-based state changes and a 160 dp START |
| 2026-10-06 | System font only, no bundled font | Smaller APK, no font-loading risk on odd devices |
| 2026-10-06 | Main-screen rate/mode taps are per session; Settings are the persistent defaults | Settings persist so nothing is re-selected at launch; a stray tap during CPR must not change the next launch |
| 2026-10-06 | Beat circle starts the metronome when idle; only STOP stops | Biggest target on screen gets tapped first; accidental stop during CPR must be impossible |
| 2026-10-06 | Idle: circle is the START button; running: pulsing disk + small outlined STOP; CPR elapsed time and START clock time under STOP, kept after STOP | Owner: STOP too loud, circle must be the tap target; elapsed and start time are what paramedics ask at handover |
| 2026-10-06 | Warning zone = last 5 compressions: five solid-red ticks, red outline, label GET READY only | Owner: the rescuer must see "five left before breaths"; the zone lasts a couple of seconds, so no counting text |
| 2026-10-06 | Mock variants A (idle START circle) and B (outlined STOP) accepted as final layout | Owner decision |
| 2026-10-06 | Launcher icon v2 (heart + monitor-style pulse line, amber/black/white) | Owner choice; reads best at 32 px among four candidates |
| 2026-10-07 | Countdown Off shows OFF in place; tapping the countdown cycles Off/1/2/3/5 per session | Layout must never shift; quick change without opening Settings |
| 2026-10-07 | First-launch hints as non-blocking on-screen labels, dismissed by any tap; "How to use" in About | Owner: hidden taps need a tutorial; a modal would stand between the user and START in an emergency |
| 2026-10-07 | Breath cues open each half of the pause instead of fixed 0.5 s / 2.0 s offsets | Owner: with an 8 s pause the two cues came back to back and then silence; halves pace the breaths |
| 2026-10-07 | Click timbre is a setting (Clean 1000 default; Wood 880, Low 660) | All three passed the phone listening test; owner wants the choice persistent |
| 2026-10-07 | Mode/rate switch: build the new track first, read the playback position last | Review measured 30–200 ms of rendering and allocation between reading the position and starting the new track, which shifted the beat grid on every Compressions → 30:2 switch |
| 2026-10-07 | Release signing: PKCS12 keystore outside the repo (`~/.cprbeat/release.jks`), `keystore.properties` git-ignored, same key for every store; Play App Signing on top | One certificate for RuStore, Galaxy Store and AppGallery; losing the key would orphan the app id |
