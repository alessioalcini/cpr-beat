# Publishing CPR Beat to the stores

Everything the four stores ask for, and where it lives in this repo. Facts verified in October
2026; re-check the consoles, they change often.

## What every store gets

| Item | Where |
|---|---|
| Release build | `./gradlew bundleRelease assembleRelease` with `keystore.properties` present. AAB in `app/build/outputs/bundle/release/`, APK in `app/build/outputs/apk/release/`. Also attached to the GitHub release. |
| Signing key | `~/.cprbeat/release.jks`, alias `cprbeat`, PKCS12. Password in the password manager. **Back it up off the laptop.** Certificate SHA-256 `12:9F:19:E1:CA:54:F1:79:FA:58:E0:F0:60:1F:6B:8B:F3:AA:9F:A4:35:C5:7D:3F:77:F1:E0:F0:64:BF:C5:23`. |
| App id / version | `dev.alcini.cprbeat`, versionCode 1, versionName 0.1.0. Bump both in `app/build.gradle.kts` for every store upload; stores reject a reused versionCode. |
| Icon 512×512 | `store/icon-512.png` |
| Feature graphic 1024×500 | `store/feature-graphic-1024x500.png` (Play only, others optional) |
| Screenshots | `fastlane/metadata/android/en-US/images/phoneScreenshots/` (1080×2400, from the Samsung A52) |
| Texts EN | `fastlane/metadata/android/en-US/{title,short_description,full_description}.txt` |
| Texts RU | `fastlane/metadata/android/ru-RU/…` |
| Privacy policy | Public HTML page: "CPR Beat collects, stores and shares no data and has no network permission." Host it on GitHub Pages once the repo is public, or on any page you control; every store wants the URL. |
| Category | Medical (Play, AppGallery) / Health (RuStore, Galaxy Store) |
| Contact | alessio.alcini.it@gmail.com |
| Content rating | Everyone / 3+; the only questionnaire topic is "health or medical information": answer yes, no diagnosis. |

Disclaimers are already inside the full descriptions: not a medical device, not a substitute for
training, call emergency services first, no AHA/ERC affiliation. Keep them in every store.

## Google Play

1. Play Console account: US$25 once, government ID and payment profile. Personal accounts show
   the legal name, country and e-mail publicly.
2. Create the app → upload the **AAB** → accept Play App Signing (Google re-signs with its own key;
   our key becomes the upload key).
3. App content: privacy policy URL, Data safety ("no data collected or shared"), **Health apps
   declaration** → feature "Emergency and First Aid" (not "Medical Device Apps"), ads "no",
   content rating questionnaire, target audience "18+ or everyone".
4. Store listing: texts, icon, feature graphic, at least 2 phone screenshots.
5. Personal account created after 2023-11-13: **closed test with ≥ 12 testers opted in for 14
   consecutive days**, then "apply for production access". Friends with the APK can be those
   testers: add their Google e-mails to the closed-test list.
6. Target API 36 is required for new apps since 2026-08-31; we target 36.

## RuStore (rustore.ru)

1. Developer account at console.rustore.ru (individual or company; Gosuslugi or SMS verification).
2. New app → upload the **APK** (AAB also accepted) → category "Здоровье".
3. Russian listing from `ru-RU`, screenshots, icon, privacy policy URL, age rating 0+.
4. No special health declaration; moderation usually 1–3 days.

## Samsung Galaxy Store

1. Seller Portal account (seller.samsungapps.com), private seller is fine; commercial seller status
   is only needed for paid apps.
2. Add new app → binary: **APK**, signed; select "All devices" or Galaxy phones.
3. Listing: English texts, icon 512, screenshots (they prefer Galaxy devices: the A52 shots qualify),
   privacy policy URL, age rating.
4. Review takes several days; they test on real devices, so the first launch must not crash.

## Huawei AppGallery

1. AppGallery Connect account (developer.huawei.com), identity verification 1–2 days.
2. My apps → New app → Android → upload **APK** (or AAB); no HMS Core needed.
3. App information: privacy policy URL, copyright/qualification "not required", content rating,
   open-source statement optional.
4. Review 1–3 days; they check that the app runs on EMUI/HarmonyOS phones without Google services —
   we use none.

## F-Droid (later, optional)

Repository must be public. `fastlane/metadata` is already in the layout F-Droid reads. Submit via
a merge request to fdroiddata with the build recipe (Gradle, `assembleRelease`, no proprietary
dependencies).
