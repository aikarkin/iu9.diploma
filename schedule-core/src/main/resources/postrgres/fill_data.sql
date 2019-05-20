DELETE FROM schedule_item_parity;
DELETE FROM schedule_item;
DELETE FROM schedule_day;
DELETE FROM class_type;
DELETE FROM study_group;
DELETE FROM study_flow;
DELETE FROM specialization;
DELETE FROM department;
DELETE FROM edu_degree;
DELETE FROM faculty;
DELETE FROM term;
DELETE FROM day_of_weak;
DELETE FROM class_time;
DELETE FROM "subject";
DELETE FROM classroom;


DO $$
DECLARE
    _faculty_cipher char(8);
    _department_id integer;
    _degree_id integer;
    _spec_code char(8);
    _flow_id integer;
    _term_id integer;
    _group_id integer;
    _weak_short_title char(3);
    _schedule_day_id integer;
    _class_time_id integer;
    _subject_id integer;
    _schedule_item_id integer;
    _class_room_id integer;
    _class_type_id integer;

BEGIN
    INSERT INTO faculty
        (faculty_cipher, title)
        VALUES
        ('ИУ', 'Информатика и системы управления')
        RETURNING faculty_cipher INTO _faculty_cipher;

    INSERT INTO department
        (faculty_cipher, department_number, title)
        VALUES
        (_faculty_cipher, 9, 'Компьютерные науки и теоретическая информатика')
        RETURNING department_id INTO _department_id;

    INSERT INTO edu_degree
        (degree_name, min_number_of_study_years)
        VALUES
        ('бакалавариат', 4)
        RETURNING degree_id INTO _degree_id;

    INSERT INTO specialization
        (spec_code, department_id, degree_id, title)
        VALUES
        ('01.03.02', _department_id, _degree_id, 'Прикладная математика и информатика')
        RETURNING spec_code INTO _spec_code;

    INSERT INTO study_flow
        (spec_code, start_year)
        VALUES
        (_spec_code, 2015)
        RETURNING flow_id INTO _flow_id;

    INSERT INTO term
        (term_no)
        VALUES
        (1)
        RETURNING term_id INTO _term_id;

    INSERT INTO study_group
        (flow_id, term_id, group_number, students_count)
        VALUES
        (_flow_id, _term_id, 1, 15)
        RETURNING group_id INTO _group_id;

    INSERT INTO day_of_weak
        (short_title, full_title)
        VALUES
        ('ПН', 'Понедельник')
        RETURNING short_title INTO _weak_short_title;

    INSERT INTO schedule_day
        (day_of_weak, group_id)
        VALUES
        (_weak_short_title, _group_id)
        RETURNING day_id INTO _schedule_day_id;

    INSERT INTO class_time
        (no_of_class, starts_at, ends_at)
        VALUES
        (2, '10:15', '11:50')
        RETURNING class_time_id INTO _class_time_id;

    INSERT INTO schedule_item
        (day_id, class_time_id)
        VALUES
        (_schedule_day_id, _class_time_id)
        RETURNING schedule_item_id INTO _schedule_item_id;

    INSERT INTO "subject"
        (subject_name)
        VALUES
        ('физика')
        RETURNING  subject_id INTO _subject_id;

    INSERT INTO classroom
        (room_number)
        VALUES
        ('330ю')
        RETURNING room_id INTO _class_room_id;

    INSERT INTO class_type
        ("type_name")
        VALUES
        ('лекция')
        RETURNING "type_id" INTO _class_type_id;

    INSERT INTO schedule_item_parity
        (schedule_item_id, day_parity, classroom_id, class_type_id, subject_id)
        VALUES
        (_schedule_item_id, 'ЧС', _class_room_id, _class_type_id, _subject_id);

END $$;

select * from faculty;