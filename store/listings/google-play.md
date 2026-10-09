# Google Play listing

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

**App name** (8 chars / 8 bytes, limit 30 chars)

```text
CPR Beat
```

**Short description** (68 chars / 70 bytes, limit 80 chars)

```text
Offline CPR metronome: 100–120 bpm, 30:2 mode, rescuer-switch timer.
```

**Full description** (1708 chars / 1726 bytes, limit 4000 chars)

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

Interface language: English.

The 100–120 compressions per minute range follows current resuscitation guidelines (AHA 2025, ERC 2025).

Important: CPR Beat is a pacing aid only. It is not a medical device and does not diagnose, treat, cure or prevent any medical condition. It is not a substitute for certified CPR training. In an emergency, call your local emergency number first and follow the dispatcher's instructions. Consult a healthcare professional for medical advice.

CPR Beat is an independent open-source project. It is not affiliated with, sponsored by, or endorsed by the American Heart Association or the European Resuscitation Council.
```

**Release notes** (457 chars / 467 bytes, limit 500 chars)  
_Enter inside <en-US> … </en-US> tags in the release form._

```text
Version 0.2.0
• The main screen now carries a short safety note: pacing aid, not a medical device, call emergency services first.
• The SWITCH RESCUER banner now covers the countdown for 5 seconds and no longer hides the mode buttons.
• The main screen fits smaller screens and a larger display size or font.
• About screen now cites the ERC 2025 resuscitation guidelines.
• About now shows the privacy statement, a privacy policy link and a contact e-mail.
```

## Russian (ru-RU)

**App name** (8 chars / 8 bytes, limit 30 chars)

```text
CPR Beat
```

**Short description** (68 chars / 113 bytes, limit 80 chars)

```text
Офлайн-метроном для СЛР: 100–120 в минуту, режим 30:2, таймер смены.
```

**Full description** (1741 chars / 3032 bytes, limit 4000 chars)

```text
CPR Beat задаёт правильный темп непрямого массажа сердца, когда счёт идёт на секунды. Одна большая кнопка запускает громкий ровный щелчок с частотой 100, 110 или 120 компрессий в минуту. В режиме 30:2 приложение считает тридцать компрессий, предупреждает перед последними пятью и задаёт паузу на два вдоха. Таймер напоминает команде о смене реаниматора, а время с начала СЛР и время старта остаются на экране для передачи медикам.

Работает полностью офлайн. Без аккаунта, рекламы и аналитики, без разрешения на интернет, данные не собираются. Бесплатно, открытый исходный код (GPL-3.0).

Возможности
• Темп 100 / 110 / 120 в минуту, смена на ходу без сбоя ритма
• Режим «только компрессии» или 30:2 с паузой на вдохи 3–8 секунд
• Таймер смены реаниматора: выкл, 1, 2, 3 или 5 минут, со вспышкой и баннером SWITCH RESCUER
• Время с начала СЛР и время старта сохраняются после остановки
• Звук идёт через канал будильника и слышен в беззвучном режиме; авто-максимум громкости по желанию
• Три варианта щелчка; настройки запоминаются
• Тёмный контрастный экран для работы в перчатках и на солнце

Интерфейс приложения на английском языке.

Диапазон 100–120 компрессий в минуту соответствует действующим рекомендациям по реанимации (AHA 2025, ERC 2025).

Важно: CPR Beat — только вспомогательный метроном. Это не медицинское изделие, приложение не диагностирует, не лечит и не предотвращает заболевания и не заменяет сертифицированное обучение СЛР. В экстренной ситуации сначала вызовите скорую помощь (112 или 103) и следуйте указаниям диспетчера. За медицинской консультацией обращайтесь к врачу.

CPR Beat — независимый проект с открытым кодом. Не связан с American Heart Association и European Resuscitation Council и не поддерживается ими.
```

**Release notes** (465 chars / 822 bytes, limit 500 chars)  
_Enter inside <ru-RU> … </ru-RU> tags in the release form._

```text
Версия 0.2.0
• На главном экране появилась короткая памятка: это помощник для темпа, а не медицинское изделие; сначала вызовите скорую.
• Баннер SWITCH RESCUER теперь на 5 секунд закрывает таймер, а не кнопки режима.
• Главный экран помещается на небольших экранах и при крупном масштабе или шрифте.
• В разделе «О приложении» указаны рекомендации ERC 2025.
• В разделе «О приложении» появились заявление о конфиденциальности, ссылка на политику и e-mail для связи.
```
