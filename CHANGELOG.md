# Changelog

All notable changes to CPR Beat are listed here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), versions follow
[Semantic Versioning](https://semver.org/). Store release notes
(`fastlane/metadata/android/*/changelogs/<versionCode>.txt`) are written from the
matching entry at release time.

## [Unreleased]

### Added

- Russian interface.
- Settings → Language: Auto (the phone language), English or Русский. On Android 13 and later
  the same choice also appears in the system's per-app language settings.

### Fixed

- Status bar and navigation bar icons stay light on phones that use a light system theme; they
  used to be dark and nearly invisible on the app's dark background.

## [0.2.0] - 2026-10-09

### Added

- The main screen shows a short note under the rate buttons: pacing aid, not a medical device,
  call the emergency number first. It stays visible while the metronome runs.
- About shows a privacy statement (no data collected, no internet permission), a link to
  the privacy policy and the developer's contact e-mail.

### Changed

- The SWITCH RESCUER banner now appears in the countdown block, covering the digits for
  5 seconds instead of occupying the mode selector for 10 seconds. The mode buttons stay
  on screen while the banner is shown, and the countdown keeps running underneath it.
- About screen and store descriptions cite the ERC 2025 guidelines instead of ERC 2021;
  the 100–120 per minute rate is unchanged.
- License: GPL-3.0-or-later instead of MIT. About shows the copyright, the no-warranty notice
  and a link to the license; the name and icon are reserved (README, section 7 terms).

### Fixed

- Nothing on the main screen moves on START or STOP any more: the mode buttons used to jump up
  and the CPR time line down. START turns into the beat indicator in place.
- The main screen fits short screens: old 16:9 phones, a larger display size or font, a
  three-button navigation bar. The circle shrinks instead of squashing the rate buttons or
  pushing the disclaimer off screen; on very small screens the screen scrolls.

### Removed

- The yellow first-launch hints on the main screen. "How to use" in About stays.

## [0.1.0] - 2026-10-06

### Added

- Metronome at 100, 110 or 120 bpm with an alarm-stream click and optional automatic
  maximum volume.
- Compressions-only and 30:2 modes; 30:2 counts 1–30 with a tick ring, a red warning zone
  on 26–30 and a configurable breath pause of 3–8 seconds with breath and resume tones.
- Rescuer-switch countdown (off, 1, 2, 3 or 5 minutes) with a screen flash and a
  SWITCH RESCUER banner on expiry.
- CPR elapsed time and start time, kept after STOP for handover.
- Settings stored on the device; rate, mode and countdown taps on the main screen are
  per-session overrides.
- First-launch hints, dark theme, English only, fully offline with no permissions.

[Unreleased]: https://github.com/alessioalcini/cpr-beat/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/alessioalcini/cpr-beat/compare/9579741...v0.2.0
[0.1.0]: https://github.com/alessioalcini/cpr-beat/tree/9579741b4fa99711d1abe26ec629907182a655fb
