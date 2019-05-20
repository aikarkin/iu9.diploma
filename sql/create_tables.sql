CREATE TABLE "subject" (
    subject_id serial primary key,
    subject_name char(120) not null
);

CREATE TABLE class_type (
    "type_id" serial primary key,
    "type_name" char(80) not null,
    UNIQUE("type_name")
);

CREATE TABLE classroom(
    room_id serial primary key,
    room_number char(5) not null,
    capacity integer check (capacity >= 0),
    UNIQUE(room_number)
);

CREATE TABLE lecturer(
    lecturer_id serial primary key,
    lecturer_email char(120),
    first_name char(120) not null,
    middle_name char(120) not null,
    last_name char(120) not null,
    edu_degree text
);

CREATE TABLE day_of_weak(
    short_title char(3) primary key,
    full_title char(12) not null,
    UNIQUE(full_title)
);

CREATE TABLE class_time(
    class_time_id serial primary key,
    no_of_class integer not null,
    starts_at time not null,
    ends_at time not null,
    UNIQUE(starts_at, ends_at),
    CHECK(starts_at < ends_at)
);


CREATE TABLE term(
    term_id serial primary key,
    term_no integer not null check(term_no > 0)
);

CREATE TABLE edu_degree(
    degree_id serial primary key,
    degree_name char(14) not null,
    min_number_of_study_years integer not null check(min_number_of_study_years > 0),
    UNIQUE(degree_name)
);

CREATE TABLE faculty(
	faculty_cipher char(8) primary key,
	title varchar(80) not null
);

CREATE TABLE department(
    department_id serial primary key,
    faculty_cipher char(8) references faculty(faculty_cipher),
    department_number integer not null check(department_number > 0),
    title varchar(80) not null
);

CREATE TABLE specialization(
    spec_code char(8) primary key,
    department_id integer references department(department_id),
    degree_id integer references edu_degree(degree_id),
    title char(80) not null
);

CREATE TABLE study_flow(
    flow_id serial primary key,
    spec_code char(8) references specialization(spec_code),
    start_year integer not null check(start_year > 1900 and start_year < 2100),
    UNIQUE(spec_code, start_year)
);


CREATE TABLE study_group(
    group_id serial primary key,
    flow_id integer references study_flow(flow_id),
    term_id integer references term(term_id),
    group_number integer not null check(group_number > 0),
    students_count integer,
    UNIQUE(flow_id, group_number)
);

CREATE TABLE schedule_day(
    day_id serial primary key,
    day_of_weak char(3) references day_of_weak(short_title),
    group_id integer references study_group(group_id),
    UNIQUE(day_of_weak, group_id)
);

CREATE TABLE schedule_item(
    schedule_item_id serial primary key,
    day_id integer references schedule_day(day_id),
    class_time_id integer references class_time(class_time_id),
    UNIQUE(day_id, class_time_id)
);

CREATE TABLE schedule_item_parity(
    schedule_item_parity_id serial primary key,
    schedule_item_id integer references schedule_item(schedule_item_id),
    day_parity char(5)
        check(day_parity = 'ЧС' or day_parity = 'ЗН' or day_parity = 'ЧС/ЗН')
        DEFAULT 'ЧС/ЗН',
    classroom_id integer references classroom(room_id),
    class_type_id integer references class_type("type_id"),
    subject_id integer references "subject"(subject_id),

    CONSTRAINT unq_cr UNIQUE(schedule_item_id, day_parity, classroom_id),
    CONSTRAINT unq_subj UNIQUE(schedule_item_id, day_parity, subject_id),
    CONSTRAINT unq_cl_type UNIQUE(schedule_item_id, day_parity, class_type_id),
    CONSTRAINT unq_parity UNIQUE(schedule_item_id, day_parity)
);

CREATE TABLE schedule_item_parity_to_lecturer(
    id serial primary key,
    schedule_item_parity_id integer references schedule_item_parity(schedule_item_parity_id),
    lecturer_id integer references lecturer(lecturer_id)
);

CREATE TABLE calendar_item(
    calendar_item_id serial primary key,
    study_flow_id integer references study_flow(flow_id),
    subject_id integer references "subject"(subject_id)
);

CREATE TABLE calendar_item_cell(
    cell_id serial primary key,
    calendar_item_id integer references calendar_item(calendar_item_id),
    term_id integer references term(term_id),
    UNIQUE(calendar_item_id, term_id)
);

CREATE TABLE hours_per_class(
    hours_id serial primary key,
    calendar_cell_id integer references calendar_item_cell(cell_id),
    class_type_id integer references class_type("type_id"),
    no_of_hours integer not null check (no_of_hours > 0),
    UNIQUE(calendar_cell_id, class_type_id)
);


CREATE OR REPLACE FUNCTION min(a time, b time)
RETURNS TIME
AS
$BODY$
BEGIN
IF a < b THEN
	RETURN a;
ELSE
	RETURN b;
END IF;
END
$BODY$
LANGUAGE 'plpgsql' ;


CREATE OR REPLACE FUNCTION max(a time, b time)
RETURNS TIME
AS
$BODY$
BEGIN
IF a > b THEN
	RETURN a;
ELSE
	RETURN b;
END IF;
END
$BODY$
LANGUAGE 'plpgsql' ;


-- Check if class time items are intersected.
CREATE OR REPLACE FUNCTION is_class_time_valid(time_id integer, s TIME, e TIME)
RETURNS BOOLEAN AS
$BODY$
DECLARE
    r class_time%rowtype;
BEGIN
    FOR r IN (SELECT * FROM class_time)
    LOOP
        IF (r.class_time_id != time_id) AND ( max(s, r.starts_at) < min(e, r.ends_at) ) THEN
            RETURN FALSE;
		END IF;
    END LOOP;

    RETURN TRUE;
END
$BODY$
LANGUAGE 'plpgsql' ;


CREATE OR REPLACE FUNCTION check_class_time()
  RETURNS trigger AS
$$
DECLARE
	cur_starts_at time;
	cur_ends_at time;
	cur_id integer;
BEGIN
	cur_id = NEW.class_time_id;

	IF (TG_OP = 'INSERT') OR (NEW.starts_at IS DISTINCT FROM OLD.starts_at) THEN
		cur_starts_at = NEW.starts_at;
	ELSE
		cur_starts_at = OLD.starts_at;
	END IF;

	IF (TG_OP = 'INSERT') OR (NEW.ends_at IS DISTINCT FROM OLD.ends_at) THEN
		cur_ends_at = NEW.ends_at;
	ELSE
		cur_ends_at = OLD.ends_at;
	END IF;

	IF NOT(is_class_time_valid(cur_id, cur_starts_at, cur_ends_at)) THEN
	   RAISE EXCEPTION 'Занятие не может пересекаться с другими занятиями по времени.';
	   ROLLBACK;
	END IF;
	RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';


CREATE TRIGGER tirgger_upd_classtime
    AFTER UPDATE OR INSERT ON class_time
    FOR EACH ROW
    EXECUTE PROCEDURE check_class_time();


-- Check schedule item on valid parity
CREATE OR REPLACE FUNCTION check_schedule_item_parity()
  RETURNS trigger AS
$$
DECLARE
    r schedule_item_parity%rowtype;
BEGIN

    FOR r IN (SELECT * FROM schedule_item_parity WHERE schedule_item_id = NEW.schedule_item_id)
    LOOP
        IF (
                ((NEW.day_parity = 'ЧС/ЗН') AND (r.day_parity = 'ЧС' OR r.day_parity = 'ЗН'))
                    OR
                ((NEW.day_parity = 'ЧС' OR NEW.day_parity = 'ЗН') AND (r.day_parity = 'ЧС/ЗН'))
           )
        THEN
            RAISE EXCEPTION
                'Невозможно присвоить данному занятию значение "%", т. к. для него уже указано занчение "%".',
                NEW.day_parity,
                r.day_parity;
            ROLLBACK;
        END IF;
    END LOOP;
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';


CREATE TRIGGER tirgger_upd_schedule_item_parity
    AFTER UPDATE OR INSERT ON schedule_item_parity
    FOR EACH ROW
    EXECUTE PROCEDURE check_schedule_item_parity();


-- Преподователь не может одновремменно: 
--   а) находится в двух аудиториях; 
--   б) вести два предмета;
--   в) вести один и тот же предмет разных типов(семинар/лекция).
-- Два преподователя не могут одновременно вести занятия по разным предметам в одной и той же аудитории

-- 1.   Create view "Lecturer classes" of type: 
--  schedule_item_parity_to_lecturer.lecturer_id,
--  (schedule_day.weak_short_title, schedule_item_parity.day_parity, schedule_item.class_time_id),
--  schedule_item_parity.classroom_id, [*]
--  schedule_item_parity.subject_id, [*]
--  schedule_item_parity.classes_type_id [*]
-- 
--  -> join of thre tables: schedule_day, schedule_item, schedule_item_parity
-- 

CREATE OR REPLACE VIEW time_of_classes AS
    SELECT
            schedule_day.day_of_weak,
            schedule_item_parity.day_parity,
            schedule_item.class_time_id,
            schedule_item_parity.schedule_item_parity_id,
            schedule_item_parity.classroom_id,
            schedule_item_parity.subject_id,
            schedule_item_parity.class_type_id
        FROM schedule_item_parity
        INNER JOIN schedule_item
            ON (schedule_item.schedule_item_id = schedule_item_parity.schedule_item_id)
        INNER JOIN schedule_day
            ON (schedule_day.day_id = schedule_item.day_id);



CREATE OR REPLACE VIEW time_of_lecturer_classes AS
    SELECT 
            schedule_item_parity_to_lecturer.lecturer_id, 
            time_of_classes.day_of_weak,
            time_of_classes.day_parity,
            time_of_classes.class_time_id,
            time_of_classes.classroom_id,
            time_of_classes.subject_id,
            time_of_classes.class_type_id
        FROM time_of_classes
        INNER JOIN schedule_item_parity_to_lecturer
            ON (schedule_item_parity_to_lecturer.schedule_item_parity_id = time_of_classes.schedule_item_parity_id);

-- 
-- 2.   On update/insert create trigger, which checks constraints on view

CREATE OR REPLACE FUNCTION is_lecturer_class_not_valid(cur_lec_class time_of_lecturer_classes)
RETURNS BOOLEAN AS
$BODY$
DECLARE
    r_iter time_of_lecturer_classes%rowtype;
    is_valid boolean;
BEGIN
-- The same lecturer already has another lecture at current time
    RETURN (EXISTS (SELECT * FROM time_of_lecturer_classes 
            WHERE lecturer_id = _lecturer_id
            AND (cur_lec_class.schedule_item_parity_id != schedule_item_parity_id)
            AND (cur_lec_class.day_of_weak = day_of_weak)
            AND (cur_lec_class.day_parity = day_parity)
            AND (cur_lec_class.class_time_id = class_time_id)));
END
$BODY$
LANGUAGE 'plpgsql' ;

CREATE OR REPLACE FUNCTION check_item_parity_to_lecturer()
  RETURNS trigger AS
$$
BEGIN
    IF is_lecturer_class_not_valid(NEW) THEN
        RAISE EXCEPTION 'У преподователя с lecturer_id="" уже есть занятие в данное время.';
        ROLLBACK;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';

CREATE TRIGGER tirgger_upd_item_parity_to_lecturer
    AFTER UPDATE OR INSERT ON schedule_item_parity_to_lecturer
    FOR EACH ROW
    EXECUTE PROCEDURE check_item_parity_to_lecturer();


-- Не может быть двух занятий в одной и той же адуитории в одно время (только если не совпадают предметы и типы занятий)
-- => не могут две группы одновременно оказаться в одной адитории (только если не один и тот же предмет одного и того же типа)
-- => не могут два преподователя одновременно оказаться в одной аудитори ...
CREATE OR REPLACE FUNCTION is_schedule_item_parity_not_valid(_item_parity_id integer)
RETURNS BOOLEAN AS
$BODY$
DECLARE
	cur_time_of_class time_of_classes%rowtype;
BEGIN
    SELECT * FROM time_of_classes 
        INTO cur_time_of_class
        WHERE schedule_item_parity_id = _item_parity_id;
    RETURN
        (EXISTS
            (SELECT * FROM time_of_classes
                WHERE 
                    (cur_time_of_class.schedule_item_parity_id != _item_parity_id)
                AND
                    (cur_time_of_class.subject_id != subject_id OR cur_time_of_class.class_type_id != class_type_id)
                AND
                    (cur_time_of_class.classroom_id = classroom_id)
                AND
                    (cur_time_of_class.day_of_weak = day_of_weak)
                AND 
                    (cur_time_of_class.day_parity = day_parity)
                AND 
                    (cur_time_of_class.class_time_id = class_time_id)
            )
        );
END
$BODY$
LANGUAGE 'plpgsql' ;


CREATE OR REPLACE FUNCTION check_item_parity()
  RETURNS trigger AS
$$
BEGIN
    IF is_schedule_item_parity_not_valid(NEW) THEN
        RAISE EXCEPTION 
            'Данной занятие не может проходить в аудитории c room_id="%", так как в этой аудитории в указанное время уже проходит другое занятие.',
            NEW.classroom_id;
        ROLLBACK;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE 'plpgsql';


CREATE TRIGGER tirgger_mod_schedule_item_parity
    AFTER UPDATE OR INSERT ON schedule_item_parity
    FOR EACH ROW
    EXECUTE PROCEDURE check_item_parity();