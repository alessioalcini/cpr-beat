# Changelog

All notable changes to CPR Beat are listed here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), versions follow
[Semantic Versioning](https://semver.org/). Store release notes
(`fastlane/metadata/android/*/changelogs/<versionCode>.txt`) are written from the
matching entry at release time.

## [Unreleased]

### Added

- About shows a privacy statement (no data collected, no internet permission), a link to
  the privacy policy and the developer's contact e-mail.

### Changed

- The SWITCH RESCUER banner now appears in the countdown block, covering the digits for
  5 seconds instead of occupying the mode selector for 10 seconds. The mode buttons stay
  on screen while the banner is shown, and the countdown keeps running underneath it.
- About screen and store descriptions cite the ERC 2025 guidelines instead of ERC 2021;
  the 100–120 per minute rate is unchanged.

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

[Unreleased]: https://github.com/alessioalcini/cpr-beat/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/alessioalcini/cpr-beat/releases/tag/v0.1.0
