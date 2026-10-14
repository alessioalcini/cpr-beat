# Huawei AppGallery listing

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

**Remarks for the reviewer** (279 chars / 279 bytes, limit 300 chars)  
_English; entered in the console when submitting._

```text
CPR assistant, not a plain metronome: 100/110/120 pace, 30:2 mode counts 30 compressions then paces 2 breaths, rescuer-switch countdown, CPR start time and duration for EMS handover. Original code. No medical service, not a medical device, no data collected. No login: tap START.
```

## English (US, default)

**App name** (8 chars / 8 bytes, limit 30 chars)  
_Form accepts 64, Review Guidelines 1.1 cap non-Chinese names at 30._

```text
CPR Beat
```

**Brief introduction** (75 chars / 75 bytes, limit 80 chars)  
_Only the first ~35 characters show in most lists._

```text
CPR helper: compression pace, 30:2 count with breaths, rescuer-switch timer
```

**Full introduction** (1766 chars / 1784 bytes, limit 8000 chars)

```text
CPR Beat keeps your chest compressions at the right pace when it matters. One big button starts a loud, steady click at 100, 110 or 120 compressions per minute. Switch to 30:2 and the app counts thirty compressions, warns before the last five, then paces two breaths. A countdown reminds the team to switch rescuers, and the elapsed CPR time with the start time stays on screen for handover to paramedics.

Works fully offline. No account, no ads, no analytics, no network permission, no data collected. Free and open source (GPL-3.0).

Features
• Rates 100 / 110 / 120 bpm, changeable mid-session without losing the beat
• Compressions-only or 30:2 mode with a breath pause of 3–8 seconds
• Rescuer-switch countdown: off, 1, 2, 3 or 5 minutes, with a flash and SWITCH RESCUER banner
• CPR elapsed time and start time, kept after STOP
• Plays through the alarm channel, so it is heard in silent mode; optional automatic maximum volume
• Three click sounds; settings remembered between launches
• Dark, high-contrast screen designed for gloves and daylight

Interface in English and Russian: follows the phone language, or pick one in Settings.

The 100–120 compressions per minute range follows current resuscitation guidelines (AHA 2025, ERC 2025).

Important: CPR Beat is a pacing aid only. It is not a medical device and does not diagnose, treat, cure or prevent any medical condition. It is not a substitute for certified CPR training. In an emergency, call your local emergency number first and follow the dispatcher's instructions. Consult a healthcare professional for medical advice.

CPR Beat is an independent open-source project. It is not affiliated with, sponsored by, or endorsed by the American Heart Association or the European Resuscitation Council.
```

**New features** (160 chars / 162 bytes, limit 500 chars)

```text
Version 0.3.1
• STOP returns the rate, mode and rescuer-switch countdown chosen on the main screen to your Settings. They used to stay until the app was closed.
```

## Russian

**App name** (8 chars / 8 bytes, limit 30 chars)

```text
CPR Beat
```

**Brief introduction** (78 chars / 138 bytes, limit 80 chars)

```text
Помощник для СЛР: темп компрессий, счёт 30:2 с вдохами, таймер смены спасателя
```

**Full introduction** (1773 chars / 3101 bytes, limit 8000 chars)

```text
CPR Beat задаёт правильный темп непрямого массажа сердца, когда счёт идёт на секунды. Одна большая кнопка запускает громкий ровный щелчок с частотой 100, 110 или 120 компрессий в минуту. В режиме 30:2 приложение считает тридцать компрессий, предупреждает перед последними пятью и задаёт паузу на два вдоха. Таймер напоминает команде о смене реаниматора, а время с начала СЛР и время старта остаются на экране для передачи медикам.

Работает полностью офлайн. Без аккаунта, рекламы и аналитики, без разрешения на интернет, данные не собираются. Бесплатно, открытый исходный код (GPL-3.0).

Возможности
• Темп 100 / 110 / 120 в минуту, смена на ходу без сбоя ритма
• Режим «только компрессии» или 30:2 с паузой на вдохи 3–8 секунд
• Таймер смены реаниматора: выкл, 1, 2, 3 или 5 минут, со вспышкой и баннером «СМЕНА»
• Время с начала СЛР и время старта сохраняются после остановки
• Звук идёт через канал будильника и слышен в беззвучном режиме; авто-максимум громкости по желанию
• Три варианта щелчка; настройки запоминаются
• Тёмный контрастный экран для работы в перчатках и на солнце

Интерфейс на русском и английском: по языку телефона или по выбору в настройках.

Диапазон 100–120 компрессий в минуту соответствует действующим рекомендациям по реанимации (AHA 2025, ERC 2025).

Важно: CPR Beat — только вспомогательный метроном. Это не медицинское изделие, приложение не диагностирует, не лечит и не предотвращает заболевания и не заменяет сертифицированное обучение СЛР. В экстренной ситуации сначала вызовите скорую помощь (112 или 103) и следуйте указаниям диспетчера. За медицинской консультацией обращайтесь к врачу.

CPR Beat — независимый проект с открытым кодом. Не связан с American Heart Association и European Resuscitation Council и не поддерживается ими.
```

**New features** (159 chars / 286 bytes, limit 500 chars)

```text
Версия 0.3.1
• СТОП возвращает темп, режим и таймер смены, выбранные на главном экране, к значениям из настроек. Раньше они сохранялись до закрытия приложения.
```
