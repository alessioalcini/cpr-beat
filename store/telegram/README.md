# Telegram channel

Texts and avatar for the project's Telegram channel. Channel type: public channel with comments
switched on (a linked discussion group), so subscribers do not see each other but can reply
under every post.

| Field | Value | Limit |
|---|---|---|
| Name | CPR Beat | 128 |
| Username | `cprbeat_app`, https://t.me/cprbeat_app (public, created 2026-10-07) | 5–32, a–z 0–9 _ |
| Avatar | `avatar-1024.png` (source `avatar.html`, the launcher icon scaled for the circle crop) | square, ≥ 512 px |
| Description | three variants below: English 207, bilingual 200, Russian | 255 |

## Description

Language: bilingual (owner, 2026-10-07). Posts carry English first, then `* * *`, then Russian.
Bot @cprbeat_app_bot is admin of the channel and of the linked "CPR Beat Feedback" group; its token
lives in `~/.cprbeat/telegram.env`, never in the repository.

### English (207 characters)

News from CPR Beat, a free CPR metronome for Android: 100–120 compressions per minute, 30:2 mode, rescuer-switch timer. Testing, updates, feedback. Not a medical device and not a substitute for CPR training.

### Bilingual (200 characters, live since 2026-10-07)

CPR Beat: free CPR metronome for Android. 100–120/min, 30:2, rescuer-switch timer. News, testing, feedback. Not a medical device.
Бесплатный метроном для СЛР на Android. Новости, тестирование, отзывы.

### Russian (first version, replaced)

Новости CPR Beat, бесплатного метронома для сердечно-лёгочной реанимации на Android: 100–120 нажатий в минуту, режим 30:2, таймер смены спасателя. Тестирование, обновления, отзывы. Не медицинское изделие и не замена обучению СЛР.

## Pinned welcome post

Posted and pinned on 2026-10-07 as message 4, English and Russian in one post.

### English

Hi! This is the CPR Beat channel.

CPR Beat is a CPR metronome for Android. Clicks set a compression rate of 100–120 per minute,
30:2 mode counts the compressions and the pause for two breaths, and a timer reminds you to switch
rescuers. Free, no ads, collects no data, works offline.

Here you will find:
• news and updates;
• tester recruiting before the Google Play release;
• questions and feedback in the comments under each post.

Source code and issues: https://github.com/alessioalcini/cpr-beat

CPR Beat helps you keep the pace but does not replace CPR training. In an emergency, call your
local emergency number first.

### Russian

Привет! Это канал CPR Beat.

CPR Beat — метроном для сердечно-лёгочной реанимации на Android. Щелчки задают темп нажатий
100–120 в минуту, режим 30:2 считает компрессии и паузу на два вдоха, таймер напоминает сменить
спасателя. Приложение бесплатное, без рекламы, не собирает данные и работает без интернета.

Здесь будут:
• новости и обновления;
• набор тестировщиков перед выходом в Google Play;
• вопросы и отзывы — в комментариях под постами.

Исходный код и задачи: https://github.com/alessioalcini/cpr-beat

CPR Beat помогает держать темп, но не заменяет обучение СЛР. В экстренной ситуации сначала
звоните 112.

## Testers post

Channel direct messages are on (2026-10-07): testers write to the channel, not to the owner's personal account.

### English

How to help release CPR Beat on Google Play:
1. You need an Android phone with Google Play.
2. Send the channel a direct message with the Gmail address you use for Google Play. Do not post it in the comments: everyone can see them.
3. When I send you the link, open it on your phone and tap "Become a tester".
4. Install CPR Beat from Google Play using the same link.
5. Stay in the test and keep the app installed for 14 days.
6. Open it a few times, try both modes and leave feedback on Google Play or in the comments here.

### Russian

Как помочь выпустить CPR Beat в Google Play:
1. Нужен Android-телефон с Google Play.
2. Напишите каналу в личные сообщения Gmail, с которым вы входите в Google Play. Не пишите адрес в комментариях: их видят все.
3. Когда пришлю ссылку, откройте её с телефона и нажмите «Стать тестировщиком».
4. Установите CPR Beat из Google Play по той же ссылке.
5. 14 дней не выходите из теста и не удаляйте приложение.
6. Откройте его несколько раз, попробуйте оба режима и напишите отзыв в Google Play или здесь в комментариях.

## Wording rules

Same as the store listings (SPEC 11.2, 11.4): no outcome claims such as "saves lives", the app
is a pacing aid and software, never a course or training, no AHA/ERC logos.

## Closed-test recruiting posts

Posted on 2026-10-09 as messages 6 (English) and 7 (Russian). Two separate posts instead of one
bilingual post, so each can be forwarded on its own (owner, 2026-10-09); the last line links the
channel because a forwarded post has no "next post". The opt-in link
https://play.google.com/apps/testing/dev.alcini.cprbeat goes in a follow-up post once Google
approves the closed test. Both posts were edited the same day to open with a plain-language
explanation of what the app does, after feedback that the first version was unclear to
non-medical readers.

### English

```text
Closed testing is open

What the app is. CPR Beat helps you do CPR. When someone is unconscious and not breathing, they need chest compressions until the ambulance arrives: pushing hard on the chest 100–120 times a minute. Keeping that pace under stress is hard, and most people push too fast or too slow. The app clicks loudly like a metronome, and you push in time with it.

The app also has:
• a 30:2 mode that counts 30 compressions, then pauses for 2 breaths;
• a timer that reminds you to swap every 2 minutes when more than one person is helping.

It's free, works offline, has no ads, no sign-up and collects no data. It doesn't replace a first-aid course or calling emergency services (112/911).

Why we need testers. Google will publish the app in Play only after a closed test: at least 12 people must keep it installed for 14 days. We're still a few people short. Any Android phone will do, and you don't need any medical knowledge. All you need to do is install the app and keep it for two weeks. If anything is unclear, tell us.

How to join. Join the testers group with the Google account you use in Play on your phone:
https://groups.google.com/g/cprbeat-testers

Group membership is private, so no one sees your e-mail. The download link will come in the next post once Google approves the test (usually 1–3 days).

Please forward this post to friends, family and colleagues.

News and the download link: https://t.me/cprbeat_app
```

### Russian

```text
Открыт набор в закрытое тестирование

Что это за приложение. CPR Beat помогает делать сердечно-лёгочную реанимацию. Если человек без сознания и не дышит, до приезда скорой ему нужно давить на грудь 100–120 раз в минуту. В стрессе держать такой темп трудно: обычно давят то слишком быстро, то слишком медленно. Приложение громко щёлкает, как метроном, и вы просто давите в такт.

Ещё в приложении есть:
• режим 30:2 — считает 30 нажатий и даёт паузу на 2 вдоха;
• таймер, который каждые 2 минуты напоминает смениться, если помогают несколько человек.

Приложение бесплатное, работает без интернета, в нём нет рекламы и регистрации, и оно не собирает никаких данных. Курсы первой помощи и звонок в скорую (112) оно не заменяет.

Зачем нужны тестировщики. Google выпустит приложение в Play только после закрытого теста: минимум 12 человек должны держать его установленным 14 дней. Нам не хватает нескольких человек. Подойдёт любой Android-телефон, разбираться в медицине не нужно. От вас нужно установить приложение и не удалять его две недели. Если что-то окажется непонятным, напишите нам.

Как записаться. Вступите в группу тестировщиков с того Google-аккаунта, под которым вы вошли в Play на телефоне:
https://groups.google.com/g/cprbeat-testers

Членство в группе скрыто, ваш e-mail никто не увидит. Ссылка на скачивание появится в следующем посте, когда Google одобрит тест (обычно через 1–3 дня).

Перешлите этот пост друзьям, родным и коллегам.

Новости и ссылка на скачивание: https://t.me/cprbeat_app
```
