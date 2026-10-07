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
