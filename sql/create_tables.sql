CREATE DATABASE bmstu_schedule WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';

ALTER DATABASE bmstu_schedule OWNER TO admin;

\connect bmstu_schedule

CREATE TABLE "subject"
(
  subject_id   SERIAL PRIMARY KEY,
  subject_name TEXT NOT NULL
);

CREATE TABLE class_type
(
  "type_id"   SERIAL PRIMARY KEY,
  "type_name" TEXT NOT NULL,
  UNIQUE ("type_name")
);

CREATE TABLE classroom
(
  room_id     SERIAL PRIMARY KEY,
  room_number VARCHAR(10) NOT NULL,
  capacity    INTEGER CHECK (capacity >= 0),
  UNIQUE (room_number)
);

CREATE TABLE lecturer
(
  lecturer_id    SERIAL PRIMARY KEY,
  lecturer_email TEXT,
  first_name     TEXT NOT NULL,
  middle_name    TEXT NOT NULL,
  last_name      TEXT NOT NULL,
  edu_degree     TEXT
);

CREATE TABLE day_of_weak
(
  weak_id     SERIAL PRIMARY KEY,
  short_title CHAR(3),
  full_title  CHAR(12) NOT NULL,
  UNIQUE (full_title),
  UNIQUE (short_title)
);

CREATE TABLE class_time
(
  class_time_id SERIAL PRIMARY KEY,
  no_of_class   INTEGER NOT NULL,
  starts_at     time    NOT NULL,
  ends_at       time    NOT NULL,
  UNIQUE (starts_at, ends_at),
  CHECK (starts_at < ends_at)
);


CREATE TABLE term
(
  term_id SERIAL PRIMARY KEY,
  term_no INTEGER NOT NULL CHECK (term_no > 0)
);

CREATE TABLE edu_degree
(
  degree_id                 SERIAL PRIMARY KEY,
  degree_name               TEXT    NOT NULL,
  min_number_of_study_years INTEGER NOT NULL CHECK (min_number_of_study_years > 0),
  UNIQUE (degree_name)
);

CREATE TABLE faculty
(
  faculty_id     SERIAL PRIMARY KEY,
  faculty_cipher CHAR(8),
  title          TEXT NOT NULL,
  UNIQUE (faculty_cipher)
);

CREATE TABLE department
(
  department_id     SERIAL PRIMARY KEY,
  faculty_id        INTEGER REFERENCES faculty (faculty_id),
  department_number INTEGER NOT NULL CHECK (department_number > 0),
  title             TEXT    NULL
);

CREATE TABLE speciality
(
  id        SERIAL PRIMARY KEY,
  code      CHAR(8) CHECK (char_length(code) = 8 AND code LIKE '%.%.%'),
  degree_id INTEGER REFERENCES edu_degree (degree_id),
  title     TEXT NOT NULL,
  UNIQUE (code)
);

CREATE TABLE specialization
(
  id                   SERIAL PRIMARY KEY,
  speciality_id        INTEGER REFERENCES speciality (id),
  number_in_speciality INT,
  title                TEXT NOT NULL,
  UNIQUE (speciality_id, number_in_speciality)
);


CREATE TABLE department_to_specialization
(
  id                SERIAL PRIMARY KEY,
  department_id     INTEGER REFERENCES department (department_id),
  specialization_id INTEGER REFERENCES specialization (id)
);

CREATE TABLE department_subject
(
  id            SERIAL PRIMARY KEY,
  department_id INTEGER REFERENCES department (department_id),
  subject_id    INTEGER REFERENCES subject (subject_id),
  UNIQUE (department_id, subject_id)
);

CREATE TABLE lecturer_subject
(
  id                       SERIAL PRIMARY KEY,
  lecturer_id              INTEGER REFERENCES lecturer (lecturer_id),
  subject_on_department_id INTEGER REFERENCES department_subject (id),
  class_type_id            INTEGER REFERENCES class_type (type_id),
  UNIQUE (lecturer_id, subject_on_department_id, class_type_id)
);

CREATE TABLE calendar
(
  id              SERIAL PRIMARY KEY,
  dept_to_spec_id INTEGER REFERENCES department_to_specialization (id),
  start_year      INTEGER NOT NULL CHECK (start_year > 1900 AND start_year < 2100),
  UNIQUE (dept_to_spec_id, start_year)
);


CREATE TABLE study_group
(
  group_id       SERIAL PRIMARY KEY,
  calendar_id    INTEGER REFERENCES calendar (id),
  term_id        INTEGER REFERENCES term (term_id),
  group_number   INTEGER NOT NULL CHECK (group_number > 0),
  students_count INTEGER,
  UNIQUE (calendar_id, term_id, group_number)
);

CREATE TABLE schedule_day
(
  day_id   SERIAL PRIMARY KEY,
  weak_id  INTEGER REFERENCES day_of_weak (weak_id),
  group_id INTEGER REFERENCES study_group (group_id),
  UNIQUE (weak_id, group_id)
);

CREATE TABLE schedule_item
(
  schedule_item_id SERIAL PRIMARY KEY,
  day_id           INTEGER REFERENCES schedule_day (day_id),
  class_time_id    INTEGER REFERENCES class_time (class_time_id),
  UNIQUE (day_id, class_time_id)
);

CREATE TABLE schedule_item_parity
(
  schedule_item_parity_id SERIAL PRIMARY KEY,
  schedule_item_id        INTEGER REFERENCES schedule_item (schedule_item_id),
  day_parity              CHAR(5)
    CHECK (day_parity = 'ЧС' OR day_parity = 'ЗН' OR day_parity = 'ЧС/ЗН')
    DEFAULT 'ЧС/ЗН',
  classroom_id            INTEGER REFERENCES classroom (room_id),
  class_type_id           INTEGER REFERENCES class_type ("type_id"),
  lec_subj_id             INTEGER REFERENCES "lecturer_subject" (id),

  CONSTRAINT unq_cr UNIQUE (schedule_item_id, day_parity, classroom_id),
  CONSTRAINT unq_subj UNIQUE (schedule_item_id, day_parity, lec_subj_id),
  CONSTRAINT unq_cl_type UNIQUE (schedule_item_id, day_parity, class_type_id),
  CONSTRAINT unq_parity UNIQUE (schedule_item_id, day_parity)
);


CREATE TABLE calendar_item
(
  calendar_item_id      SERIAL PRIMARY KEY,
  calendar_id           INTEGER REFERENCES calendar (id),
  department_subject_id INTEGER REFERENCES department_subject (id)
);

CREATE TABLE calendar_item_cell
(
  cell_id          SERIAL PRIMARY KEY,
  calendar_item_id INTEGER REFERENCES calendar_item (calendar_item_id),
  term_id          INTEGER REFERENCES term (term_id),
  UNIQUE (calendar_item_id, term_id)
);

CREATE TABLE hours_per_class
(
  hours_id         SERIAL PRIMARY KEY,
  calendar_cell_id INTEGER REFERENCES calendar_item_cell (cell_id),
  class_type_id    INTEGER REFERENCES class_type ("type_id"),
  no_of_hours      INTEGER NOT NULL CHECK (no_of_hours > 0),
  UNIQUE (calendar_cell_id, class_type_id)
);

CREATE OR REPLACE FUNCTION min(a time, b time)
  RETURNS TIME
AS
$BODY$
BEGIN
  IF a < b
  THEN
    RETURN a;
  ELSE
    RETURN b;
  END IF;
END
$BODY$
  LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION max(a time, b time)
  RETURNS TIME
AS
$BODY$
BEGIN
  IF a > b
  THEN
    RETURN a;
  ELSE
    RETURN b;
  END IF;
END
$BODY$
  LANGUAGE 'plpgsql';

-- -- check if class time items are intersected.
-- CREATE OR REPLACE FUNCTION is_class_time_valid(time_id INTEGER, s TIME, e TIME)
--   RETURNS BOOLEAN AS
-- $BODY$
-- DECLARE
--   r class_time%rowtype;
-- BEGIN
--   FOR r IN (SELECT *
--             FROM class_time)
--     LOOP
--       IF (r.class_time_id != time_id) AND (max(s, r.starts_at) < min(e, r.ends_at))
--       THEN
--         RETURN FALSE;
--       END IF;
--     END LOOP;
--   RETURN TRUE;
-- END
-- $BODY$
--   LANGUAGE 'plpgsql';
--
-- CREATE OR REPLACE FUNCTION check_class_time()
--   RETURNS trigger AS
-- $$
-- DECLARE
--   cur_starts_at time;
--   cur_ends_at   time;
--   cur_id        INTEGER;
-- BEGIN
--   cur_id = NEW.class_time_id;
--   IF (TG_OP = 'INSERT') OR (NEW.starts_at IS DISTINCT FROM OLD.starts_at)
--   THEN
--     cur_starts_at = NEW.starts_at;
--   ELSE
--     cur_starts_at = OLD.starts_at;
--   END IF;
--   IF (TG_OP = 'INSERT') OR (NEW.ends_at IS DISTINCT FROM OLD.ends_at)
--   THEN
--     cur_ends_at = NEW.ends_at;
--   ELSE
--     cur_ends_at = OLD.ends_at;
--   END IF;
--   IF NOT (is_class_time_valid(cur_id, cur_starts_at, cur_ends_at))
--   THEN
--     RAISE EXCEPTION 'Занятие не может пересекаться с другими занятиями по времени.';
--   END IF;
--   RETURN NEW;
-- END;
--
-- $$
--   LANGUAGE 'plpgsql';
-- CREATE TRIGGER tirgger_upd_classtime
--   AFTER UPDATE OR INSERT
--   ON class_time
--   FOR EACH ROW
-- EXECUTE PROCEDURE check_class_time();
-- -- check schedule item on valid parity
-- CREATE OR REPLACE FUNCTION check_schedule_item_parity()
--   RETURNS trigger AS
-- $$
-- DECLARE
--   r schedule_item_parity%rowtype;
-- BEGIN
--   FOR r IN (SELECT *
--             FROM schedule_item_parity
--             WHERE schedule_item_id = NEW.schedule_item_id)
--     LOOP
--       IF (
--           ((NEW.day_parity = 'ЧС/ЗН') AND (r.day_parity = 'ЧС' OR r.day_parity = 'ЗН'))
--           OR
--           ((NEW.day_parity = 'ЧС' OR NEW.day_parity = 'ЗН') AND (r.day_parity = 'ЧС/ЗН'))
--         )
--       THEN
--         RAISE EXCEPTION
--           'Невозможно присвоить данному занятию значение "%", т. к. для него уже указано занчение "%".',
--           NEW.day_parity,
--           r.day_parity;
--         ROLLBACK;
--       END IF;
--     END LOOP;
--   RETURN NEW;
-- END;
-- $$
--   LANGUAGE 'plpgsql';
--
-- CREATE TRIGGER tirgger_upd_schedule_item_parity
--   AFTER UPDATE OR INSERT
--   ON schedule_item_parity
--   FOR EACH ROW
-- EXECUTE PROCEDURE check_schedule_item_parity();
-- -- Преподователь не может одновремменно:
-- --   а) находится в двух аудиториях;
-- --   б) вести два предмета;
-- --   в) вести один и тот же предмет разных типов(семинар/лекция).
-- -- Два преподователя не могут одновременно вести занятия по разным предметам в одной и той же аудитории
-- -- 1.   Create view "Lecturer classes" of type:
-- --  schedule_item_parity_to_lecturer.lecturer_id,
-- --  (schedule_day.weak_shORt_title, schedule_item_parity.day_parity, schedule_item.class_time_id),
-- --  schedule_item_parity.classroom_id, [*]
-- --  schedule_item_parity.subject_id, [*]
-- --  schedule_item_parity.classes_type_id [*]
-- --
-- --  -> join of three tables: schedule_day, schedule_item, schedule_item_parity
-- --
-- CREATE OR REPLACE VIEW time_of_classes AS
-- SELECT schedule_day.weak_id as day_of_weak,
--        schedule_item_parity.day_parity,
--        schedule_item.class_time_id,
--        schedule_item_parity.schedule_item_parity_id,
--        schedule_item_parity.classroom_id,
--        schedule_item_parity.subject_id,
--        schedule_item_parity.class_type_id
-- FROM schedule_item_parity
--        INNER JOIN schedule_item
--                   ON (schedule_item.schedule_item_id = schedule_item_parity.schedule_item_id)
--        INNER JOIN schedule_day
--                   ON (schedule_day.day_id = schedule_item.day_id);
--
-- CREATE OR REPLACE VIEW time_of_lecturer_classes AS
-- SELECT schedule_item_parity_to_lecturer.lecturer_id,
--        time_of_classes.day_of_weak,
--        time_of_classes.day_parity,
--        time_of_classes.class_time_id,
--        time_of_classes.classroom_id,
--        time_of_classes.subject_id,
--        time_of_classes.class_type_id,
--        time_of_classes.schedule_item_parity_id
-- FROM time_of_classes
--        INNER JOIN schedule_item_parity_to_lecturer
--                   ON (schedule_item_parity_to_lecturer.schedule_item_parity_id =
--                       time_of_classes.schedule_item_parity_id);
--
-- --
--   -- 2.   On update/insert create trigger, which checks constraints on view
-- CREATE OR REPLACE FUNCTION is_lecturer_class_not_valid(cur_lec_class time_of_lecturer_classes)
--   RETURNS BOOLEAN AS
-- $BODY$
-- DECLARE
--   r_iter   time_of_lecturer_classes%rowtype;
--   is_valid boolean;
-- BEGIN
--   -- The same lecturer already has another lecture at current time
--   RETURN (EXISTS(SELECT *
--                  FROM time_of_lecturer_classes
--                  WHERE lecturer_id = cur_lec_class.lecturer_id
--                    AND (cur_lec_class.schedule_item_parity_id != schedule_item_parity_id)
--                    AND (cur_lec_class.day_of_weak = day_of_weak)
--                    AND (cur_lec_class.day_parity = day_parity)
--                    AND (cur_lec_class.class_time_id = class_time_id)));
-- END
-- $BODY$
--   LANGUAGE 'plpgsql';
--
-- CREATE OR REPLACE FUNCTION check_item_parity_to_lecturer()
--   RETURNS trigger AS
-- $$
-- BEGIN
--   IF is_lecturer_class_not_valid(NEW)
--   THEN
--     RAISE EXCEPTION 'У преподователя с lecturer_id="" уже есть занятие в данное время.';
--   END IF;
--   RETURN NEW;
-- END;
-- $$
--   LANGUAGE 'plpgsql';
--
-- CREATE TRIGGER tirgger_upd_item_parity_to_lecturer
--   AFTER UPDATE OR INSERT
--   ON schedule_item_parity_to_lecturer
--   FOR EACH ROW
-- EXECUTE PROCEDURE check_item_parity_to_lecturer();
-- -- Не может быть двух занятий в одной и той же адуитории в одно время (только если не совпадают предметы и типы занятий)
-- -- => не могут две группы одновременно оказаться в одной адитории (только если не один и тот же предмет одного и того же типа)
-- -- => не могут два преподователя одновременно оказаться в одной аудитори ...
--
-- CREATE OR REPLACE FUNCTION is_schedule_item_parity_not_valid(_item_parity_id INTEGER)
--   RETURNS BOOLEAN AS
-- $BODY$
-- DECLARE
--   cur_time_of_class time_of_classes%rowtype;
-- BEGIN
--   SELECT *
--   FROM time_of_classes tof INTO cur_time_of_class
--     WHERE
--   schedule_item_parity_id = _item_parity_id;
--   RETURN
--     (EXISTS
--       (SELECT *
--        FROM time_of_classes
--        WHERE (cur_time_of_class.schedule_item_parity_id != _item_parity_id)
--          AND (cur_time_of_class.subject_id != subject_id OR cur_time_of_class.class_type_id != class_type_id)
--          AND (cur_time_of_class.classroom_id = classroom_id)
--          AND (cur_time_of_class.day_of_weak = day_of_weak)
--          AND (cur_time_of_class.day_parity = day_parity)
--          AND (cur_time_of_class.class_time_id = class_time_id)
--       )
--       );
-- END
-- $BODY$
--   LANGUAGE 'plpgsql';
--
-- CREATE OR REPLACE FUNCTION check_item_parity()
--   RETURNS trigger AS
-- $$
-- BEGIN
--   IF is_schedule_item_parity_not_valid(NEW)
--   THEN
--     RAISE EXCEPTION
--       'Данной занятие не может проходить в аудитории c room_id="%", так как в этой аудитории в указанное время уже проходит другое занятие.',
--       NEW.classroom_id;
--   END IF;
--   RETURN NEW;
-- END;
-- $$
--   LANGUAGE 'plpgsql';
--
-- CREATE TRIGGER trigger_mod_schedule_item_parity
--   AFTER UPDATE OR INSERT
--   ON schedule_item_parity
--   FOR EACH ROW
-- EXECUTE PROCEDURE check_item_parity();