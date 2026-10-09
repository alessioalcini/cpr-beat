#!/usr/bin/env python3
"""Assemble paste-ready store listings from the fastlane texts.

Source of truth: fastlane/metadata/android/{en-US,ru-RU}/*.txt. Edit those, then run

    python3 store/listings/build.py

which rewrites store/listings/{google-play,rustore,galaxy-store,appgallery}.md and checks every
field against the store's limit (characters or UTF-8 bytes). Store-specific texts that have no
fastlane equivalent (Galaxy Store 40-byte short description, RuStore FAQ) live in this file.
Limits verified against official documentation on 2026-10-07; see README.md for sources.
"""
from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
META = ROOT / "fastlane" / "metadata" / "android"
OUT = Path(__file__).resolve().parent
VERSION_CODE = 3  # release notes file: changelogs/<versionCode>.txt


def read(locale: str, name: str) -> str:
    return (META / locale / name).read_text(encoding="utf-8").rstrip("\n")


EN = {k: read("en-US", f"{k}.txt") for k in ("title", "short_description", "full_description")}
RU = {k: read("ru-RU", f"{k}.txt") for k in ("title", "short_description", "full_description")}
EN["whats_new"] = read("en-US", f"changelogs/{VERSION_CODE}.txt")
RU["whats_new"] = read("ru-RU", f"changelogs/{VERSION_CODE}.txt")

# Galaxy Store short description is capped at 40 UTF-8 bytes (about 20 Cyrillic letters).
GALAXY_SHORT_EN = "CPR metronome: 100-120 bpm, 30:2 mode"
GALAXY_SHORT_RU = "Метроном для СЛР, 30:2"

# RuStore FAQ: question <= 120 chars, answer <= 500 chars, up to 10 pairs. Shown on the web card.
RUSTORE_FAQ = [
    (
        "Это медицинское изделие?",
        "Нет. CPR Beat — вспомогательный метроном, который задаёт темп компрессий. Приложение не "
        "диагностирует, не лечит и не заменяет обучение сердечно-лёгочной реанимации. В экстренной "
        "ситуации сначала вызовите скорую помощь (112 или 103).",
    ),
    (
        "Нужен ли интернет?",
        "Нет. Приложение работает полностью офлайн, не запрашивает разрешение на интернет, не "
        "содержит рекламы и аналитики и не собирает данные.",
    ),
    (
        "Как сменить язык интерфейса?",
        "Интерфейс на русском и английском. По умолчанию приложение берёт язык телефона; в "
        "«Настройки → Язык» можно выбрать English или Русский.",
    ),
    (
        "Будет ли щелчок слышен в беззвучном режиме?",
        "Да. Звук идёт через канал будильника, который не зависит от беззвучного режима. В "
        "настройках можно включить автоматический максимум громкости на время работы метронома.",
    ),
]

# RuStore rules §6.4 ban third-party names on the card, so its copy of the Russian description
# drops the AHA/ERC citation and the affiliation paragraph. About and the other stores keep them.
RUSTORE_GUIDELINES = (
    "Диапазон 100–120 компрессий в минуту соответствует действующим рекомендациям по реанимации "
    "(AHA 2025, ERC 2025)."
)
RUSTORE_AFFILIATION = (
    "\n\nCPR Beat — независимый проект с открытым кодом. Не связан с American Heart Association и "
    "European Resuscitation Council и не поддерживается ими."
)
if RUSTORE_GUIDELINES not in RU["full_description"] or RUSTORE_AFFILIATION not in RU["full_description"]:
    sys.exit("ru-RU full_description changed: update RUSTORE_GUIDELINES / RUSTORE_AFFILIATION in build.py")
RUSTORE_DESCRIPTION = (
    RU["full_description"]
    .replace(RUSTORE_GUIDELINES, "Диапазон 100–120 компрессий в минуту соответствует действующим международным "
             "рекомендациям по реанимации.")
    .replace(RUSTORE_AFFILIATION, "")
)

errors: list[str] = []


def field(label: str, text: str, limit: int | None, unit: str = "chars", note: str = "") -> str:
    """Render one listing field as a Markdown block with its length against the store limit."""
    chars, nbytes = len(text), len(text.encode("utf-8"))
    used = nbytes if unit == "bytes" else chars
    status = ""
    if limit is not None:
        status = f", limit {limit} {unit}"
        if used > limit:
            status += " **OVER LIMIT**"
            errors.append(f"{label}: {used} {unit} > {limit}")
    head = f"**{label}** ({chars} chars / {nbytes} bytes{status})"
    if note:
        head += f"  \n_{note}_"
    return f"{head}\n\n```text\n{text}\n```\n"


def write(name: str, body: str) -> None:
    (OUT / name).write_text(body.rstrip("\n") + "\n", encoding="utf-8")


# ---------------------------------------------------------------- Google Play
write(
    "google-play.md",
    f"""# Google Play listing

> **Not in use.** Play is closed for this personal account since 2026-10-09: any Medical health
> declaration needs an organization account (see store/STORE_GUIDE.md). Kept for reference.

Console: Grow users → Store presence → Main store listing. Default language en-US; add
ru-RU via "Manage translations → Select languages". Users in Russia see the Russian listing,
everyone else the English one (or an automated Google Translate view). Graphics fall back to the
default language, so screenshots and the feature graphic are uploaded once.

Store settings: type App, category **Tools** (Medical needs an organization account: rejected
2026-10-09, see store/STORE_GUIDE.md), one tag: **Clock, alarm and timer** (no Medical or
Health & Fitness tags, so the organization-account check sees nothing medical in the listing
apart from the declaration). App content:
Health apps declaration → Medical → **Emergency and First Aid**; Data safety → no data collected
or shared; Content rating → IARC questionnaire, expect Everyone / PEGI 3 / 3+; Ads → no;
privacy policy URL https://alessioalcini.github.io/cpr-beat/privacy/ (About carries the statement and the link from 0.2.0).

Policy notes: no emoji, no ALL CAPS beyond the brand and acronyms, no "best / #1 / new / free for
a limited time", no unattributed testimonials, no competitor names. Short description: one line,
no line breaks. Release notes are not promotional. Limits per language.

## English (en-US, default)

{field("App name", EN["title"], 30)}
{field("Short description", EN["short_description"], 80)}
{field("Full description", EN["full_description"], 4000)}
{field("Release notes", EN["whats_new"], 500, note="Enter inside <en-US> … </en-US> tags in the release form.")}
## Russian (ru-RU)

{field("App name", RU["title"], 30)}
{field("Short description", RU["short_description"], 80)}
{field("Full description", RU["full_description"], 4000)}
{field("Release notes", RU["whats_new"], 500, note="Enter inside <ru-RU> … </ru-RU> tags in the release form.")}
""",
)

# ---------------------------------------------------------------- RuStore
faq_md = "\n".join(
    field(f"FAQ {i} — question", q, 120) + field(f"FAQ {i} — answer", a, 500)
    for i, (q, a) in enumerate(RUSTORE_FAQ, 1)
)
write(
    "rustore.md",
    f"""# RuStore card

Console: console.rustore.ru → Приложения → Новое приложение. The card has **one language**:
there is no "add language" control. Rules §6.2 require the description to be in Russian; the
interface is Russian from 0.3.0 (ФЗ-168). Screenshots: `store/screenshots/9x16/ru-RU/` — RuStore
crops every shot to 9:16, so the 9:20 fastlane set would lose the status bar and the rate buttons.

Settings: тип — приложение; категория **Здоровье**, без дополнительной (Медицины в RuStore нет,
первая помощь указана в примерах Здоровья; к Здоровью нельзя добавлять Образ жизни и Полезные
инструменты); возраст **0+**; поисковые теги — до 5 из списка RuStore; способ связи — e-mail (обязателен); политика
конфиденциальности — ссылка нужна только при обработке персональных данных, но раздел
«Безопасность данных» заполняется всегда (данные не собираются, опасных разрешений нет).

Policy notes (§6.4): no «лучший / единственный / самый / официальный», no emoji, no links to other
stores, no CAPS abuse, no promises about unreleased features, no third-party names or trademarks
(so this card cites "international resuscitation guidelines" without naming AHA/ERC; About in
the app names them). App name should match the launcher
label; RuStore recommends (does not require) a Russian name unless trademarked, so «CPR Beat»
passes but a moderator may ask to add the purpose in the short description (already done).
Full description collapses after 2000 characters; the essentials come first.

## Карточка (русский)

{field("Название", RU["title"], 30, note="Console help says 30, API accepts 50; stay under 30.")}
{field("Краткое описание", RU["short_description"], 80)}
{field("Подробное описание", RUSTORE_DESCRIPTION, 4000)}
{field("Что нового", RU["whats_new"], 5000)}
## FAQ (вопрос — ответ, до 10 пар, виден в веб-версии карточки)

{faq_md}
""",
)

# ---------------------------------------------------------------- Galaxy Store
write(
    "galaxy-store.md",
    f"""# Samsung Galaxy Store listing

> **Not in use.** Android apps need a corporate seller with a D-U-N-S number, even free ones
> (checked 2026-10-09); the owner has no legal entity. Kept for reference.

Seller Portal: Apps → Add New App → Android → App Information, **Advanced mode**. Default
language must be **English** when more than one country is selected; add **Russian** as an
additional language tab (title, description, what's new, optional screenshots). "Supported
languages" is a separate field about the app UI: tick English only.

Limits are in **UTF-8 bytes**: a Cyrillic letter costs 2, so the Russian description may be about
2000 letters and the short description about 20. The short description field may or may not be
offered per language; if the Russian tab has one, use the Russian text below.

Settings: category **Health** (there is no Medical category; Tools is the alternative); age
rating **All (0)**; paid: No; privacy policy URL optional for non-Kids apps but the Y/N flag is
required and the URL is shown publicly, so give one; open-source URL → the GitHub repo; support
e-mail required and public. Commercial Seller status is required even for free apps (business
address, ID, bank or PayPal; a public-domain e-mail such as gmail needs an explanation). Only
one 16:9 image among the screenshots makes the app eligible for store promotion.

Policy notes: metadata suitable for all ages; description must say which languages the app
supports; no mention of other stores or platforms (1.3.7); no beta/test wording; no other apps'
names; medical information must carry an "could be inaccurate / not a substitute" notice (2.3.4),
covered by the disclaimer paragraph. Keep the title to plain letters and digits.

## English (default)

{field("App title", EN["title"], 100, "bytes")}
{field("Short description", GALAXY_SHORT_EN, 40, "bytes")}
{field("Description", EN["full_description"], 4000, "bytes")}
{field("New feature", EN["whats_new"], 4000, "bytes")}
## Russian (added language)

{field("App title", RU["title"], 100, "bytes")}
{field("Short description", GALAXY_SHORT_RU, 40, "bytes", note="Only if the Russian tab offers the field.")}
{field("Description", RU["full_description"], 4000, "bytes")}
{field("New feature", RU["whats_new"], 4000, "bytes")}
""",
)

# ---------------------------------------------------------------- AppGallery
write(
    "appgallery.md",
    f"""# Huawei AppGallery listing

AppGallery Connect: My apps → New app → Android; default language **English (US)**; then App
information → Localization → Language → Add → **Russian**, fill all mandatory text for it. Visual
assets fall back to the default language, so icon and screenshots are uploaded once (icon 216×216
or 512×512 PNG, 3–8 screenshots 450×800 recommended, PNG rather than WebP: use
`store/screenshots/9x16/`, which is 9:16).

Settings: category **Apps → Sports & health → Health** (there is no Medical; avoid "Healthcare",
which the guidelines treat as a controlled service for legal entities); content rating
questionnaire → **3+**; compatible devices → Mobile phone; price → Free; privacy policy URL
(validated, must open without login); contact e-mail (rejection mails go there). Review takes
3–5 working days. Only the app category (App vs Game) is fixed after creation.

Policy notes: name without special characters, price or promo words (1.1–1.5); no "official /
authoritative" claims (1.14); no names or logos of other platforms or device brands (1.16); no
beta/test wording unless "Version for open testing" is used; guideline 1.13 expects localized
listings to match a localized app: the app has a Russian interface from 0.3.0. Guideline 11.4
reserves "healthcare services" for legal entities; in the reviewer notes say the app is an
offline metronome and timer that provides no medical service, is not a medical device and
collects no data. Guideline 1.2 bans professional terms in names; if "CPR" is questioned, the
name has to change in the APK too.
Guideline 7.1 also wants an in-app privacy policy link, and 11.2 in-app contact information: About
has both from 0.2.0 (privacy policy https://alessioalcini.github.io/cpr-beat/privacy/).

## English (US, default)

{field("App name", EN["title"], 30, note="Form accepts 64, Review Guidelines 1.1 cap non-Chinese names at 30.")}
{field("Brief introduction", EN["short_description"], 80, note="Only the first ~35 characters show in most lists.")}
{field("Full introduction", EN["full_description"], 8000)}
{field("New features", EN["whats_new"], 500)}
## Russian

{field("App name", RU["title"], 30)}
{field("Brief introduction", RU["short_description"], 80)}
{field("Full introduction", RU["full_description"], 8000)}
{field("New features", RU["whats_new"], 500)}
""",
)

if errors:
    print("LIMIT ERRORS:\n  " + "\n  ".join(errors), file=sys.stderr)
    sys.exit(1)
print("ok: google-play.md rustore.md galaxy-store.md appgallery.md")
