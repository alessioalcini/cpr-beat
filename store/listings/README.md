# Store listings

Paste-ready texts for the four stores, generated from the fastlane metadata by `build.py`.
Edit `fastlane/metadata/android/{en-US,ru-RU}/*.txt`, run `python3 store/listings/build.py`,
and the four files below are rewritten with every field measured against the store's limit.

| File | Languages on the card | Notes |
|---|---|---|
| `google-play.md` | English (default) + Russian | **not in use**: Play closed for this account (organization rule) |
| `rustore.md` | Russian only | single-language card, FAQ block, no AHA/ERC names (§6.4) |
| `galaxy-store.md` | English (default) + Russian | **not in use**: Android apps need a D-U-N-S corporate seller |
| `appgallery.md` | English US (default) + Russian | 8000-character description, 500-character release notes |

## Languages: where Russian goes

Russian is not RuStore-only. Three of the four stores take several listing languages, and the
device language decides which one a user sees:

- **Google Play**: add ru-RU under Manage translations. A device set to Russian sees the Russian
  card, others see English (or an automated translation). Russia must be ticked in the release's
  country list.
- **Galaxy Store**: English is mandatory as the default when more than one country is selected;
  Russian is an added language tab. Russia is in the sale-country list (CIS group).
- **AppGallery**: default English (US), Russian added under Localization. Guideline 1.13 expects
  the app to be localized too: it is, from 0.3.0.
- **RuStore**: one card, Russian by rule. The interface is Russian from 0.3.0 (ФЗ-168).

Both descriptions carry one line about the interface languages (English and Russian, Settings →
Language). Screenshots: `en-US` shows the English UI (Samsung A52), `ru-RU` the Russian UI.

## Limits at a glance (official docs, checked 2026-10-07)

| Field | Google Play | RuStore | Galaxy Store | AppGallery |
|---|---|---|---|---|
| Name | 30 | 30 (console; API 50) | 100 bytes | 30 (guidelines; form 64) |
| Short description | 80 | 80 | **40 bytes** | 80 |
| Full description | 4000 | 4000 (collapses at 2000) | 4000 bytes | 8000 |
| Release notes | 500 per language | 5000 | 4000 bytes | 500 |
| Keywords / tags | 5 from Google's list | 5 from RuStore's list | tags, count unpublished | none |
| Extra text fields | none | FAQ 10 × (120 / 500) | major-change note 300 bytes | none |
| Icon | 512 PNG, 1 MB | 512 PNG/JPG, 3 MB, opaque | 512 PNG, 1 MB | 216 or 512 PNG, 2 MB |
| Screenshots | 2–8, 320–3840 px | 3–10, cropped to 9:16, 3 MB (use `store/screenshots/9x16/`) | 4–8, 320–3840 px, ≤2:1 | 3–8, 450×800 rec., 5 MB (9:16) |
| Feature graphic | 1024×500 required | none | none (hero image games only) | none |
| Category | Tools (Medical needs an organization account) | Здоровье (no Медицина exists) | Health | Sports & health → Health |
| Age rating | IARC questionnaire → 3+ | 0+ self-declared | All (0) self-declared | 3+ questionnaire |
| Health declaration | Medical → Emergency and First Aid | none | none | none |
| Privacy policy URL | required, plus in-app statement | only if data is processed | optional, Y/N flag required, shown publicly | required, validated, plus in-app link |

Text rules shared by all four: no emoji, no CAPS beyond brand and acronyms, no ranking or price
promotion, no competitor or other-store names, no beta/test wording, descriptions must match the
real app. The medical disclaimer paragraph stays in every language on every store.

## Account facts worth knowing before stage 4

- Google Play: US$25 once, identity verification, personal accounts show legal name and country.
  Personal accounts created after 2023-11-13 need a closed test with 12 testers for 14 days
  before production. Free apps can be published and downloaded in Russia; paid cannot.
- RuStore: individuals register for free and instantly with a VK ID; no ID check unless monetization
  is switched on, but the agreement (§12.3) lets RuStore ask for documents later. Non-residents
  sign a separate agreement. Review within 72 hours (rules §10.2).
- Galaxy Store (not in use): Commercial Seller status is required even for free apps (business address, ID,
  bank or PayPal, D-U-N-S or equivalent, documents in English). Seller address and phone are
  public. Pre-review about 1 business day, device test 2–4 more.
- AppGallery: free account, real-name verification, review 3–5 working days. AAB uploads
  require enabling App Signing in AppGallery Connect first; an APK upload does not.

## Sources

Google Play: support.google.com/googleplay/android-developer answers 113469 (limits),
9844778 (languages), 9859152 (listing), 1078870 (graphics), 9898842 (metadata policy),
12261419 (health apps), 14151465 (testing requirement), 11926878 (target API).
RuStore: rustore.ru/help/developers/publishing-and-verifying-apps/app-publication,
requirement-apps (Review Guidelines, RU version is current), work-with-rustore-api
create-draft-version and upload-screenshot-v2.
Galaxy Store: developer.samsung.com/galaxy-store Content Publish API reference, Seller Portal
guide PDF, App Distribution Guide (policies 1.3.x, 2.3.4, 3.2.5).
AppGallery: developer.huawei.com/consumer/en/doc/18527283 (release doc with language appendix),
AppGallery Review Guidelines 1.x, 7.1, 11.2, 11.4, agc-help-app-material-requirement.
