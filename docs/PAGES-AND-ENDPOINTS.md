# Страницы платформы и используемые endpoint'ы

Этот документ описывает страницы платформы с точки зрения фронтенда и кратко показывает, какие endpoint'ы используются на каждой странице.

## 1. Лендинг и вход

### Что видит пользователь
- общая информация о платформе;
- кнопки регистрации и входа;
- переход в профиль, если пользователь уже авторизован.

### Endpoint'ы
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/user/profile`

## 2. Профиль пользователя

### Что находится на странице
- ФИО;
- никнейм;
- контакты;
- bio;
- email;
- активные проекты;
- архив проектов.

### Поведение
- пользователь редактирует свои данные;
- ниже видит свои проекты, сгруппированные на активные и архивные.

### Endpoint'ы
- `GET /api/v1/user/profile`
- `PATCH /api/v1/user/profile`
- `GET /api/v1/projects/my`

## 3. Панель организатора

### Что находится на странице
- список конкурсов, где пользователь является организатором;
- быстрый переход в карточку конкурса;
- создание нового конкурса.

### Endpoint'ы
- `GET /api/v1/organizer/contests`
- `POST /api/v1/organizer/contests`

## 4. Публичный список конкурсов

### Что находится на странице
- все опубликованные конкурсы;
- краткая карточка конкурса;
- переход в публичную страницу конкурса.

### Endpoint'ы
- `GET /api/v1/contests`
- `GET /api/v1/contests/{contestId}`

## 5. Публичная страница конкурса

### Что находится на странице
- название и описание конкурса;
- правила;
- сроки;
- формат участия;
- список этапов;
- регистрация в конкурс;
- если конкурс командный, переход к командам и участникам.

### Поведение
- участник может зарегистрироваться в конкурс;
- участник видит этапы конкурса;
- участник открывает отдельный этап;
- после регистрации в командном конкурсе участник видит других зарегистрированных участников.

### Endpoint'ы
- `GET /api/v1/contests/{contestId}`
- `GET /api/v1/contests/{contestId}/stages`
- `POST /api/v1/contests/{contestId}/registrations`
- `GET /api/v1/contests/{contestId}/participants`

## 6. Конструктор конкурса для организатора

### Что находится на странице
- общие настройки конкурса;
- список этапов;
- publish;
- удаление конкурса.

### Поведение
- организатор создает конкурс как draft;
- редактирует базовые поля;
- публикует конкурс, когда настройка завершена.

### Endpoint'ы
- `GET /api/v1/contests/{contestId}`
- `PATCH /api/v1/organizer/contests/{contestId}`
- `POST /api/v1/organizer/contests/{contestId}/publish`
- `DELETE /api/v1/organizer/contests/{contestId}`

## 7. Раздел "Организаторы и роли"

### Что находится на странице
- список организаторов конкурса;
- роли каждого;
- форма добавления нового организатора;
- удаление роли.

### Поведение
- creator/admin видит текущие назначения;
- добавляет организатора по `userId`;
- может удалить роль, кроме creator.

### Endpoint'ы
- `GET /api/v1/organizer/contests/{contestId}/organizers`
- `POST /api/v1/organizer/contests/{contestId}/organizers`
- `DELETE /api/v1/organizer/contests/{contestId}/organizers/{organizerId}`

### Пример запроса на добавление
```json
{
  "userId": 12,
  "role": "EXPERT"
}
```

## 8. Раздел "Участники конкурса"

### Что находится на странице
- список зарегистрированных участников;
- базовая информация по каждому;
- контактные данные для организатора.

### Поведение
- organizer читает полный список зарегистрированных участников;
- participant в командном конкурсе видит ограниченную версию этого списка без email.

### Endpoint'ы
- `GET /api/v1/organizer/contests/{contestId}/participants`
- `GET /api/v1/contests/{contestId}/participants`

## 9. Раздел "Этапы конкурса"

### Что находится на странице
- список этапов;
- создание нового этапа;
- редактирование этапа;
- удаление этапа;
- просмотр публичной и организаторской версии этапа.

### Endpoint'ы для организатора
- `POST /api/v1/organizer/contests/{contestId}/stages`
- `GET /api/v1/organizer/contests/{contestId}/stages`
- `GET /api/v1/organizer/stages/{stageId}`
- `PATCH /api/v1/organizer/stages/{stageId}`
- `DELETE /api/v1/organizer/stages/{stageId}`

### Endpoint'ы для участников
- `GET /api/v1/contests/{contestId}/stages`
- `GET /api/v1/contests/stages/{stageId}`

## 10. Конструктор формы сдачи этапа

### Что находится на странице
- список полей этапа;
- изменение порядка;
- создание нового поля;
- редактирование существующего поля;
- удаление поля.

### Поведение
- organizer видит полную модель поля, включая `criteriaDescription` и `expertNote`;
- participant видит только пользовательскую часть формы.

### Endpoint'ы
- `GET /api/v1/organizer/stages/{stageId}/fields`
- `GET /api/v1/contests/stages/{stageId}/fields`
- `POST /api/v1/organizer/stages/{stageId}/fields`
- `PATCH /api/v1/organizer/stages/{stageId}/fields/{fieldId}`
- `DELETE /api/v1/organizer/stages/{stageId}/fields/{fieldId}`

## 11. Ресурсы этапа

### Что находится на странице
- материалы этапа;
- ссылки;
- файлы;
- текстовые инструкции.

### Поведение
- organizer управляет ресурсами поштучно;
- participant читает готовый набор ресурсов этапа.

### Endpoint'ы
- `GET /api/v1/stages/{stageId}/resources`
- `POST /api/v1/organizer/stages/{stageId}/resources`
- `PATCH /api/v1/organizer/stages/{stageId}/resources/{resourceId}`
- `DELETE /api/v1/organizer/stages/{stageId}/resources/{resourceId}`

## 12. Командообразование

### Что находится на странице
- список существующих команд;
- создание команды;
- join by invite;
- отправка join request;
- список входящих join request для лидера;
- approve/reject.

### Endpoint'ы
- `GET /api/v1/contests/teams/{contestId}`
- `POST /api/v1/contests/teams/{contestId}`
- `POST /api/v1/contests/teams/join-by-invite/{inviteToken}`
- `POST /api/v1/contests/teams/{teamId}/join-requests`
- `GET /api/v1/contests/teams/{teamId}/join-requests`
- `POST /api/v1/contests/teams/team-join-requests/{requestId}/approve`
- `POST /api/v1/contests/teams/team-join-requests/{requestId}/reject`

## 13. Страница проекта участника или команды

### Что находится на странице
- данные проекта;
- список этапов;
- статус каждого этапа;
- значения уже заполненных полей;
- переход к форме конкретного этапа.

### Поведение
- участник создает project workspace;
- открывает его;
- видит текущий progress по этапам;
- в командном конкурсе только лидер публикует финальную submission.

### Endpoint'ы
- `POST /api/v1/projects`
- `GET /api/v1/projects/{projectId}`

## 14. Страница сдачи этапа

### Что находится на странице
- информация об этапе;
- форма полей;
- текущие draft значения;
- кнопка финальной отправки.

### Поведение
- участник сохраняет значения полей;
- после submit данные блокируются;
- stage submission становится единицей проверки.

### Endpoint'ы
- `GET /api/v1/contests/stages/{stageId}`
- `GET /api/v1/contests/stages/{stageId}/fields`
- `GET /api/v1/stages/{stageId}/resources`
- `POST /api/v1/projects/{projectId}/stages/{stageId}/values`
- `POST /api/v1/projects/{projectId}/stages/{stageId}/submit`

## 15. Просмотр stage submission организатором

### Что находится на странице
- данные этапа;
- все значения полей;
- статус submission.

### Endpoint
- `GET /api/v1/projects/organizer/stage-submissions/{submissionId}`

## 16. Кабинет эксперта

### Что находится на странице
- список назначенных ему проверок;
- фильтр по статусу;
- открытие конкретной сдачи;
- draft score/comment;
- публикация итоговой оценки.

### Endpoint'ы
- `GET /api/v1/expert/reviews`
- `GET /api/v1/expert/reviews/{assignmentId}/submission`
- `PUT /api/v1/expert/reviews/{assignmentId}/draft`
- `POST /api/v1/expert/reviews/{assignmentId}/publish`

## 17. Панель организатора для назначения экспертов

### Что находится на странице
- список submission;
- назначение экспертов;
- запуск AI review.

### Endpoint'ы
- `POST /api/v1/organizer/evaluations/assignments`
- `POST /api/v1/organizer/evaluations/{submissionId}/ai-review`

### Пример назначения эксперта
```json
{
  "submissionId": 44,
  "expertUserId": 18
}
```

## 18. Кабинет ментора

### Что находится на странице
- команды ментора;
- календарь созвонов;
- чтение submission команды;
- комментарии к submission;
- создание новых созвонов.

### Endpoint'ы
- `POST /api/v1/organizer/mentors/assignments`
- `GET /api/v1/mentor/teams`
- `GET /api/v1/mentor/calls`
- `GET /api/v1/mentor/stage-submissions/{stageSubmissionId}`
- `GET /api/v1/stage-submissions/{stageSubmissionId}/mentor-comments`
- `POST /api/v1/mentor/comments`
- `POST /api/v1/mentor/calls`

### Пример назначения ментора
```json
{
  "teamId": 7,
  "mentorUserId": 21
}
```

## 19. Страница команды с ментором

### Что находится на странице
- ближайшие созвоны;
- весь календарь созвонов;
- комментарии ментора по каждому stage submission.

### Endpoint'ы
- `GET /api/v1/teams/{teamId}/mentor-calls`
- `GET /api/v1/stage-submissions/{stageSubmissionId}/mentor-comments`

## 20. Аналитика и экспорт

### Что находится на странице
- аналитика по конкурсу;
- экспорт рейтинга;
- создание кастомной выгрузки.

### Endpoint'ы
- `GET /api/v1/organizer/contests/{contestId}/analytics`
- `GET /api/v1/organizer/contests/{contestId}/exports/ranking?format=CSV`
- `GET /api/v1/organizer/contests/{contestId}/exports/ranking?format=XLSX`
- `POST /api/v1/organizer/contests/{contestId}/exports/custom`

## 21. Загрузка файлов

### Что находится на странице
- upload файлов для ресурсов;
- upload файлов для submission;
- получение временной ссылки на скачивание или просмотр.

### Endpoint'ы
- `POST /api/v1/files`
- `GET /api/v1/files/{fileId}/download-url`

## 22. Минимальные пользовательские сценарии

### Участник
1. Регистрируется
2. Редактирует профиль
3. Открывает конкурс
4. Регистрируется в конкурс
5. В командном конкурсе создает команду или входит в существующую
6. Создает project workspace
7. Заполняет этапы и отправляет submission
8. Смотрит mentor comments и созвоны

### Организатор
1. Создает конкурс
2. Настраивает этапы
3. Настраивает поля формы этапов
4. Добавляет ресурсы этапов
5. Назначает роли
6. Публикует конкурс
7. Следит за участниками и командами
8. Назначает экспертов и менторов
9. Смотрит аналитику и делает экспорт

### Эксперт
1. Получает роль в конкурсе
2. Открывает назначенные submission
3. Сохраняет draft review
4. Публикует итоговую оценку

### Ментор
1. Получает назначение на команду
2. Видит свои команды
3. Открывает stage submission команды
4. Оставляет комментарии
5. Планирует созвоны
