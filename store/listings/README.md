# Store listings

Paste-ready texts for the three stores, generated from the fastlane metadata by `build.py`.
Edit `fastlane/metadata/android/{en-US,ru-RU}/*.txt`, run `python3 store/listings/build.py`,
and the three files below are rewritten with every field measured against the store's limit.

| File | Languages on the card | Notes |
|---|---|---|
| `google-play.md` | English (default) + Russian | **not in use**: Play closed for this account (organization rule) |
| `rustore.md` | Russian only | single-language card, FAQ block, no AHA/ERC names (§6.4) |
| `appgallery.md` | English US (default) + Russian | 8000-character description, 500-character release notes |

## Languages: where Russian goes

Russian is not RuStore-only. Two of the three stores take several listing languages, and the
device language decides which one a user sees:

- **Google Play**: add ru-RU under Manage translations. A device set to Russian sees the Russian
  card, others see English (or an automated translation). Russia must be ticked in the release's
  country list.
- **AppGallery**: default English (US), Russian added under Localization. Guideline 1.13 expects
  the app to be localized too: it is, from 0.3.0.
- **RuStore**: one card, Russian by rule. The interface is Russian from 0.3.0 (ФЗ-168).

Both descriptions carry one line about the interface languages (English and Russian, Settings →
Language). Screenshots: `en-US` shows the English UI (Samsung A52), `ru-RU` the Russian UI.

## Limits at a glance (official docs, checked 2026-10-07)

| Field | Google Play | RuStore | AppGallery |
|---|---|---|---|
| Name | 30 | 30 (console; API 50) | 30 (guidelines; form 64) |
| Short description | 80 | 80 | 80 |
| Full description | 4000 | 4000 (collapses at 2000) | 8000 |
| Release notes | 500 per language | 5000 | 500 |
| Keywords / tags | 5 from Google's list | 5 from RuStore's list | none |
| Extra text fields | none | FAQ 10 × (120 / 500) | none |
| Icon | 512 PNG, 1 MB | 512 PNG/JPG, 3 MB, opaque | 216 or 512 PNG, 2 MB |
| Screenshots | 2–8, 320–3840 px | 3–10, cropped to 9:16, 3 MB (use `store/screenshots/9x16/`) | 3–8, 450×800 rec., 5 MB (9:16) |
| Feature graphic | 1024×500 required | none | none |
| Category | Tools (Medical needs an organization account) | Здоровье (no Медицина exists) | Sports & health → Health |
| Age rating | IARC questionnaire → 3+ | 0+ self-declared | 16+ from the questionnaire |
| Health declaration | Medical → Emergency and First Aid | none | none |
| Privacy policy URL | required, plus in-app statement | only if data is processed | required, validated, plus in-app link |

Text rules shared by all three: no emoji, no CAPS beyond brand and acronyms, no ranking or price
promotion, no competitor or other-store names, no beta/test wording, descriptions must match the
real app. The medical disclaimer paragraph stays in every language on every store.

## Account facts worth knowing before stage 4

- Google Play: US$25 once, identity verification, personal accounts show legal name and country.
  Personal accounts created after 2023-11-13 need a closed test with 12 testers for 14 days
  before production. Free apps can be published and downloaded in Russia; paid cannot.
- RuStore: individuals register for free and instantly with a VK ID; no ID check unless monetization
  is switched on, but the agreement (§12.3) lets RuStore ask for documents later. Non-residents
  sign a separate agreement. Review within 72 hours (rules §10.2).
- AppGallery: free account, real-name verification, review 3–5 working days. AAB uploads
  require enabling App Signing in AppGallery Connect first; an APK upload does not.

## Sources

Google Play: support.google.com/googleplay/android-developer answers 113469 (limits),
9844778 (languages), 9859152 (listing), 1078870 (graphics), 9898842 (metadata policy),
12261419 (health apps), 14151465 (testing requirement), 11926878 (target API).
RuStore: rustore.ru/help/developers/publishing-and-verifying-apps/app-publication,
requirement-apps (Review Guidelines, RU version is current), work-with-rustore-api
create-draft-version and upload-screenshot-v2.
AppGallery: developer.huawei.com/consumer/en/doc/18527283 (release doc with language appendix),
AppGallery Review Guidelines 1.x, 7.1, 11.2, 11.4, agc-help-app-material-requirement.
