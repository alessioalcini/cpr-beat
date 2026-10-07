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
per minute recommended by current resuscitation guidelines (AHA 2025, ERC 2025). CPR Beat is an
independent open-source project. It is not affiliated with, sponsored by, or endorsed by the
American Heart Association or the European Resuscitation Council.

## Status

Version 0.2.0 is being prepared for the app stores, starting with a closed test on Google Play;
testers are recruited in the Telegram channel below. [SPEC.md](SPEC.md) holds the agreed scope and
decisions, [Issues](https://github.com/alessioalcini/cpr-beat/issues) hold the backlog.

## News and feedback

- Telegram channel [@cprbeat_app](https://t.me/cprbeat_app): news, tester recruiting and
  feedback in the comments, in English and Russian.
- Bugs and feature ideas: [GitHub Issues](https://github.com/alessioalcini/cpr-beat/issues).
- [Privacy policy](https://alessioalcini.github.io/cpr-beat/privacy/): the app collects no data.

## Build

```
./gradlew assembleDebug
```

Requires JDK 17 and the Android SDK (platform 37). The APK lands in
`app/build/outputs/apk/debug/`.

## License

Copyright (C) 2026 Alessio Alcini

CPR Beat is free software: you can redistribute it and/or modify it under the terms of the
[GNU General Public License](LICENSE) as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version. It is distributed WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.

Additional terms under section 7 of the GPL:

- (e) The license grants no rights to use the name "CPR Beat" or the app icon as trademarks.
- (c) Modified versions you distribute must be marked as different from the original: give them
  a different name and icon, and do not present them as CPR Beat.

Third-party components are listed in `THIRD_PARTY_NOTICES.md`.
