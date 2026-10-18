#!/usr/bin/env python3
"""Assemble paste-ready store listings from the fastlane texts.

Source of truth: fastlane/metadata/android/{en-US,ru-RU}/*.txt. Edit those, then run

    python3 store/listings/build.py

which rewrites store/listings/{google-play,rustore,appgallery}.md and checks every
field against the store's limit (characters or UTF-8 bytes). Store-specific texts that have no
fastlane equivalent (RuStore FAQ) live in this file.
Limits verified against official documentation on 2026-10-07; see README.md for sources.
"""
from __future__ import annotations

import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
META = ROOT / "fastlane" / "metadata" / "android"
OUT = Path(__file__).resolve().parent
VERSION_CODE = 5  # release notes file: changelogs/<versionCode>.txt


def read(locale: str, name: str) -> str:
    return (META / locale / name).read_text(encoding="utf-8").rstrip("\n")


EN = {k: read("en-US", f"{k}.txt") for k in ("title", "short_description", "full_description")}
RU = {k: read("ru-RU", f"{k}.txt") for k in ("title", "short_description", "full_description")}
EN["whats_new"] = read("en-US", f"changelogs/{VERSION_CODE}.txt")
RU["whats_new"] = read("ru-RU", f"changelogs/{VERSION_CODE}.txt")

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

# ---------------------------------------------------------------- AppGallery
# 0.3.1 was rejected on 2026-10-14 under guideline 4.1: "Your app has a single feature (metronome)".
# The AppGallery brief introduction and reviewer note therefore lead with all the CPR functions,
# not the word "metronome". The other stores keep the fastlane short description.
APPGALLERY_BRIEF_EN = "CPR helper: compression pace, 30:2 count with breaths, rescuer-switch timer"
APPGALLERY_BRIEF_RU = "Помощник для СЛР: темп компрессий, счёт 30:2 с вдохами, таймер смены спасателя"
APPGALLERY_REVIEWER_NOTE = (
    "CPR assistant, not a plain metronome: 100/110/120 pace, 30:2 mode counts 30 compressions then "
    "paces 2 breaths, rescuer-switch countdown, CPR start time and duration for EMS handover. "
    "Original code. No medical service, not a medical device, no data collected. No login: tap START."
)
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
questionnaire → **16+** (the result on 2026-10-10); compatible devices → Mobile phone; price → Free; privacy policy URL
(validated, must open without login); contact e-mail (rejection mails go there). Review takes
3–5 working days. Only the app category (App vs Game) is fixed after creation.

Policy notes: name without special characters, price or promo words (1.1–1.5); no "official /
authoritative" claims (1.14); no names or logos of other platforms or device brands (1.16); no
beta/test wording unless "Version for open testing" is used; guideline 1.13 expects localized
listings to match a localized app: the app has a Russian interface from 0.3.0. Guideline 11.4
reserves "healthcare services" for legal entities, so the reviewer note says the app provides no
medical service, is not a medical device and collects no data. Guideline 4.1 rejected 0.3.1 on
2026-10-14 as "a single feature (metronome)": the brief introduction and the note list the CPR
functions instead. Guideline 1.2 bans professional terms in names; if "CPR" is questioned, the
name has to change in the APK too.
Guideline 7.1 also wants an in-app privacy policy link, and 11.2 in-app contact information: About
has both from 0.2.0 (privacy policy https://alessioalcini.github.io/cpr-beat/privacy/).

## Reviewer note

{field("Remarks for the reviewer", APPGALLERY_REVIEWER_NOTE, 300, note="English; entered in the console when submitting.")}
## English (US, default)

{field("App name", EN["title"], 30, note="Form accepts 64, Review Guidelines 1.1 cap non-Chinese names at 30.")}
{field("Brief introduction", APPGALLERY_BRIEF_EN, 80, note="Only the first ~35 characters show in most lists.")}
{field("Full introduction", EN["full_description"], 8000)}
{field("New features", EN["whats_new"], 500)}
## Russian

{field("App name", RU["title"], 30)}
{field("Brief introduction", APPGALLERY_BRIEF_RU, 80)}
{field("Full introduction", RU["full_description"], 8000)}
{field("New features", RU["whats_new"], 500)}
""",
)

if errors:
    print("LIMIT ERRORS:\n  " + "\n  ".join(errors), file=sys.stderr)
    sys.exit(1)
print("ok: google-play.md rustore.md appgallery.md")
