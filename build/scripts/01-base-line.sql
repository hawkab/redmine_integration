create table if not exists cur_presentation.users (
    id BIGSERIAL not null
        constraint users_pk
            primary key,
    login varchar(50) not null,
    display_name varchar(254),
    email varchar(100) not null,
    phone_number bigint,
    reg_date timestamp default current_timestamp,
    redmine_id integer
);
create unique index if not exists idx_users_login_uniq on cur_presentation.users (login);

comment on table cur_presentation.users is 'Пользователи системы';
comment on column cur_presentation.users.id is 'Идентификатор пользователя';
comment on column cur_presentation.users.login is 'Логин пользователя';
comment on column cur_presentation.users.display_name is 'Отображаемое имя пользователя';
comment on column cur_presentation.users.email is 'Электронный адрес пользователя';
comment on column cur_presentation.users.phone_number is 'Номер телефона пользователя';
comment on column cur_presentation.users.reg_date is 'Дата создания пользователя';
comment on column cur_presentation.users.redmine_id is 'Идентификатор региона в Redmine';


create table if not exists cur_presentation.appeal_kind (
    code varchar(50) not null
        constraint appeal_pk
            primary key,
    display_name varchar(254),
    sort_weight integer,
    redmine_category_id integer
);

comment on table cur_presentation.appeal_kind is 'Категории для обращения в службу технической поддержки';
comment on column cur_presentation.appeal_kind.code is 'Уникальный символьный код категории';
comment on column cur_presentation.appeal_kind.display_name is 'Отображаемое имя категории';
comment on column cur_presentation.appeal_kind.sort_weight is 'Вес для создания нестандартной сортировки отображения в списке';
comment on column cur_presentation.appeal_kind.redmine_category_id is 'Идентификатор категории в Redmine';

insert into cur_presentation.appeal_kind (code, display_name, sort_weight, redmine_category_id) values
('INCORRECT_REPORT_DATA', 'Некорректные данные в отчёте', 0, 26),
('FUNCTIONALITY_DOES_NOT_WORK', 'Не работает заявленный функционал', 1, 26),
('INCORRECT_MUNICIPAL_MAP', 'Некорректный вывод муниципалитетов на карте', 2, 26),
('INCORRECT_MUNICIPAL_NAME', 'Некорректное название муниципалитетов', 3, 26),
('FEATURE', 'Предложения по доработке', 4, 28),
('CONNECTION_PROBLEM', 'Проблема с подключением', 5, 26),
('DISPLAYING_LOADING_PROBLEM', 'Проблема с отображением отчёта (загрузкой)', 6, 26),
('CREATION_ACCOUNT', 'Создание учётной записи', 7, 27),
('MISSING_DATA', 'Отсутствуют данные', 8, 26),
('OTHER', 'Другое', 9, 26) ON CONFLICT DO NOTHING;

create table if not exists cur_presentation.user_appeal (
    id BIGSERIAL not null
        constraint user_appeal_pk
            primary key,
    user_id bigint not null references cur_presentation.users,
    report varchar(100),
    appeal_kind varchar(30) references cur_presentation.appeal_kind,
    appeal_message varchar not null,
    has_delivered boolean default false,
    redmine_id integer,
    appeal_status varchar(30) default 'CREATED',
    created_datetime timestamp default current_timestamp,
    updated_datetime timestamp default current_timestamp,
    error_message varchar
);

create index if not exists idx_user_id on cur_presentation.user_appeal (user_id);
create index if not exists idx_appeal_kind on cur_presentation.user_appeal (appeal_kind);
create index if not exists idx_select_appeals on cur_presentation.user_appeal (user_id asc, created_datetime desc);

comment on table cur_presentation.user_appeal is 'Пользовательские обращения';
comment on column cur_presentation.user_appeal.id is 'Идентификатор обращения';
comment on column cur_presentation.user_appeal.user_id is 'Идентификатор пользователя';
comment on column cur_presentation.user_appeal.report is 'Отчёт по которому обращение';
comment on column cur_presentation.user_appeal.appeal_kind is 'Категория обращения';
comment on column cur_presentation.user_appeal.appeal_message is 'Текст обращения';
comment on column cur_presentation.user_appeal.has_delivered is 'Флаг, определяющий, что обращение было доставлено до Redmine';
comment on column cur_presentation.user_appeal.redmine_id is 'Идентификатор issue в Redmine';
comment on column cur_presentation.user_appeal.appeal_status is 'Статус обращения в Redmine';
comment on column cur_presentation.user_appeal.created_datetime is 'Дата создания обращения';
comment on column cur_presentation.user_appeal.updated_datetime is 'Дата изменения обращения';
comment on column cur_presentation.user_appeal.error_message is 'Текст ошибки, которая произошла во время обработки обращения';


create table if not exists cur_presentation.attachments (
    id BIGSERIAL not null
        constraint attachments_pk
            primary key,
    appeal_id bigint not null references cur_presentation.user_appeal,
    file_name varchar not null,
    file_content bytea,
    uploaded_token varchar,
    error_message varchar
);
create index if not exists idx_appeal_id on cur_presentation.attachments (appeal_id);

comment on table cur_presentation.attachments is 'Вложения к пользовательскому обращению (файлы)';
comment on column cur_presentation.attachments.id is 'Идентификатор вложения';
comment on column cur_presentation.attachments.appeal_id is 'Идентификатор обращения';
comment on column cur_presentation.attachments.file_name is 'Имя файла';
comment on column cur_presentation.attachments.file_content is 'Контент файла';
comment on column cur_presentation.attachments.uploaded_token is 'Внешний идентификатор файла в системе Redmine';
comment on column cur_presentation.attachments.error_message is 'Текст ошибки, которая произошла во время загрузки файла на Redmine сервер';

create table if not exists cur_presentation.settings (
    code varchar(50) not null
        constraint settings_pk
            primary key,
    value varchar,
    description varchar(254)
);

comment on table cur_presentation.settings is 'Настройки системы';
comment on column cur_presentation.settings.code is 'Уникальный символьный код настройки';
comment on column cur_presentation.settings.value is 'Значение настройки';
comment on column cur_presentation.settings.description is 'Описание';

insert into cur_presentation.settings (code, value, description)
values('REDMINE_PROJECT_ID', '144', 'Идентификатор проекта в Redmine, где будет создан Issue');

insert into cur_presentation.settings (code, value, description)
values('REDMINE_ISSUE_DESCRIPTION_MESSAGE', '<p>Отчёт: <b>${appeal.report}</b></p>
<p>Категория обращения: <b>${appeal.appealKind.displayName}</b></p>
<p>Логин: <b>${appeal.user.login}</b> / email: <b>${appeal.user.email}</b> / тел: <b>+${appeal.user.phoneNumber}</b></p>
<p>Текст обращения:</p>
<blockquote>${appeal.appealMessage}</blockquote>', 'Текст, который будет отображён в Redmine Issue в поле Description');

DROP FUNCTION IF EXISTS cur_presentation.create_appeal(text, text, text, text, text, text, text, text[], bytea[], integer);
CREATE OR REPLACE FUNCTION cur_presentation.create_appeal(
                            user_login TEXT,
                            user_display_name TEXT,
                            user_email TEXT,
                            user_phone_number TEXT,
                            report_name TEXT,
                            appeal_type TEXT,
                            appeal_text TEXT,
                            attachment_name TEXT[],
                            attachment_content BYTEA[],
                            region_redmine_id INTEGER)
    RETURNS BIGINT AS $$
DECLARE
--  Входные параметры:
--  user_login          - Логин пользователя обращения в pentaho
--  user_email          - Электронный адрес пользователя
--  user_display_name   - Отображаемое имя пользователя
--  user_phone_number   - Номер телефона пользователя
--  report_name         - Отчёт
--  appeal_type         - Тип обращения
--  appeal_text         - Текст обращения
--  attachment_name     - Массив имён файлов
--  attachment_content  - Массив содержимого файлов
--  region_redmine_id   - Идентификатор региона в redmine
    usr_id BIGINT; /*Идентификатор пользователя*/
    app_id BIGINT; /*Идентификатор обращения*/
    i INT; /*Индекс массива для итерации*/
    phone BIGINT; /*Номер телефона*/
BEGIN
    IF COALESCE(user_login, '') = '' or COALESCE(user_email, '') = '' or COALESCE(appeal_text, '') = '' or COALESCE(appeal_type, '') = '' THEN
        RETURN 1;
    END IF;

    IF REGEXP_REPLACE(COALESCE(user_phone_number, ''), '[^\d]+', '', 'g') != '' THEN
        phone := CAST(REGEXP_REPLACE(user_phone_number, '[^\d]+', '', 'g') as BIGINT);
    END IF;

    INSERT INTO cur_presentation.users (login, display_name, email, phone_number, redmine_id)
    VALUES (user_login, user_display_name, user_email, phone, region_redmine_id)
    ON CONFLICT (login) DO UPDATE SET
      display_name = user_display_name,
      email = user_email,
      phone_number = phone,
      redmine_id = region_redmine_id
    RETURNING id INTO usr_id;

    INSERT INTO cur_presentation.user_appeal (user_id, report, appeal_kind, appeal_message)
    VALUES (usr_id, report_name, appeal_type, appeal_text) RETURNING id INTO app_id;

    i := 1;
    IF array_length(attachment_name::text[], 1) > 0 THEN
        LOOP
            IF attachment_name[i] IS NOT NULL THEN
                INSERT INTO cur_presentation.attachments(appeal_id, file_name, file_content)
                VALUES (app_id, attachment_name[i], attachment_content[i]);
                IF array_length(attachment_name::text[], 1) <= i THEN
                    EXIT;  -- exit loop
                END IF;
            ELSE
                EXIT;
            END IF;
            i := i+1;
        END LOOP;
    END IF;

    RETURN 0;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

comment on function cur_presentation.create_appeal(user_login TEXT, user_display_name TEXT, user_email TEXT, user_phone_number TEXT, report_name TEXT, appeal_type TEXT, appeal_text TEXT, attachment_name TEXT[], attachment_content BYTEA[], region_redmine_id INTEGER) is 'Функция создания нового обращения пользователя';


create table if not exists cur_presentation.redmine_comments (
 id BIGSERIAL not null
     constraint redmine_comments_pk
         primary key,
 author_name varchar(100) not null,
 message varchar,
 appeal_id bigint references cur_presentation.user_appeal,
 reg_date timestamp default current_timestamp,
 redmine_id integer,
 readed boolean default false
);
create index if not exists idx_appeal_id on cur_presentation.redmine_comments (appeal_id);

comment on table cur_presentation.redmine_comments is 'История комментариев к обращению';
comment on column cur_presentation.redmine_comments.id is 'Идентификатор комментария';
comment on column cur_presentation.redmine_comments.author_name is 'Имя автора комментария';
comment on column cur_presentation.redmine_comments.message is 'Текст комментария';
comment on column cur_presentation.redmine_comments.appeal_id is 'Идентификатор обращения';
comment on column cur_presentation.redmine_comments.reg_date is 'Дата комментария';
comment on column cur_presentation.redmine_comments.redmine_id is 'Внешний идентификатор комментария из Redmine';
comment on column cur_presentation.redmine_comments.readed is 'Флаг, определяющий, что пользователь надлежащим образом уведомлён';




DROP FUNCTION IF EXISTS cur_presentation.get_appeal_notes(bigint);
CREATE OR REPLACE FUNCTION cur_presentation.get_appeal_notes(appeal bigint)
    RETURNS TABLE (id bigint, author_name varchar(100), reg_date text, message varchar) AS $$
DECLARE
--  Входные параметры:
--  appeal_id           - Идентификатор обращения
BEGIN

    update cur_presentation.redmine_comments r
    set readed = true
    where r.id in (select rc.id from cur_presentation.redmine_comments rc  where rc.appeal_id = coalesce(appeal, 0));

    RETURN QUERY
        select rc.id, rc.author_name, to_char(rc.reg_date, 'DD.MM.YYYY HH24:MI:SS'), rc.message
        from cur_presentation.redmine_comments rc
        where rc.appeal_id = coalesce(appeal, 0)
        order by rc.reg_date DESC;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

comment on function cur_presentation.get_appeal_notes(appeal bigint) is 'Функция вычитывания комментариев по обращению';


