--
-- PostgreSQL database dump
--

-- Dumped from database version 10.8 (Ubuntu 10.8-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 11.1 (Ubuntu 11.1-3.pgdg18.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE bmstu_schedule;
--
-- Name: bmstu_schedule; Type: DATABASE; Schema: -; Owner: admin
--

CREATE DATABASE bmstu_schedule WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';

ALTER DATABASE bmstu_schedule OWNER TO admin;

\connect bmstu_schedule

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

CREATE TABLE "subject"
(
  id           SERIAL PRIMARY KEY,
  subject_name TEXT NOT NULL
);

CREATE TABLE class_type
(
  id          SERIAL PRIMARY KEY,
  "type_name" TEXT NOT NULL,
  UNIQUE ("type_name")
);

CREATE TABLE classroom
(
  id          SERIAL PRIMARY KEY,
  room_number VARCHAR(10) NOT NULL,
  capacity    INTEGER CHECK (capacity >= 0),
  UNIQUE (room_number)
);

CREATE TABLE tutor
(
  id             SERIAL PRIMARY KEY,
  email          TEXT,
  first_name     TEXT NOT NULL,
  middle_name    TEXT NOT NULL,
  last_name      TEXT NOT NULL,
  science_degree TEXT
);

CREATE TABLE day_of_week
(
  id         SERIAL PRIMARY KEY,
  short_form CHAR(3),
  title      CHAR(12) NOT NULL,
  UNIQUE (title),
  UNIQUE (short_form)
);

CREATE TABLE class_time
(
  id          SERIAL PRIMARY KEY,
  no_of_class INTEGER NOT NULL,
  starts_at   time    NOT NULL,
  ends_at     time    NOT NULL,
  UNIQUE (starts_at, ends_at),
  CHECK ( no_of_class >=0 ),
  CHECK (starts_at < ends_at)
);


CREATE TABLE term
(
  id      SERIAL PRIMARY KEY,
  term_no INTEGER NOT NULL CHECK (term_no > 0)
);

CREATE TABLE edu_degree
(
  id                        SERIAL PRIMARY KEY,
  degree_name               TEXT    NOT NULL,
  min_number_of_study_years INTEGER NOT NULL CHECK (min_number_of_study_years > 0),
  UNIQUE (degree_name)
);

CREATE TABLE faculty
(
  id             SERIAL PRIMARY KEY,
  faculty_cipher CHAR(8),
  title          TEXT NOT NULL,
  UNIQUE (faculty_cipher)
);

CREATE TABLE department
(
  id                SERIAL PRIMARY KEY,
  faculty_id        INTEGER REFERENCES faculty (id),
  department_number INTEGER NOT NULL CHECK (department_number > 0),
  title             TEXT    NULL
);

CREATE TABLE speciality
(
  id        SERIAL PRIMARY KEY,
  code      CHAR(8) CHECK (char_length(code) = 8 AND code LIKE '%.%.%'),
  degree_id INTEGER REFERENCES edu_degree (id),
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


CREATE TABLE department_specialization
(
  id                SERIAL PRIMARY KEY,
  department_id     INTEGER REFERENCES department (id),
  specialization_id INTEGER REFERENCES specialization (id)
);

CREATE TABLE department_subject
(
  id            SERIAL PRIMARY KEY,
  department_id INTEGER REFERENCES department (id),
  subject_id    INTEGER REFERENCES subject (id),
  UNIQUE (department_id, subject_id)
);

CREATE TABLE tutor_subject
(
  id                    SERIAL PRIMARY KEY,
  tutor_id              INTEGER REFERENCES tutor (id),
  department_subject_id INTEGER REFERENCES department_subject (id),
  class_type_id         INTEGER REFERENCES class_type (id),
  UNIQUE (tutor_id, department_subject_id, class_type_id)
);

CREATE TABLE study_plan
(
  id                           SERIAL PRIMARY KEY,
  department_specialization_id INTEGER REFERENCES department_specialization (id),
  start_year                   INTEGER NOT NULL CHECK (start_year > 1900 AND start_year < 2100),
  UNIQUE (department_specialization_id, start_year)
);


CREATE TABLE study_group
(
  id             SERIAL PRIMARY KEY,
  study_plan_id    INTEGER REFERENCES study_plan (id),
  term_id        INTEGER REFERENCES term (id),
  group_number   INTEGER NOT NULL CHECK (group_number > 0),
  students_count INTEGER,
  UNIQUE (study_plan_id, term_id, group_number)
);

CREATE TABLE schedule_day
(
  id       SERIAL PRIMARY KEY,
  week_id  INTEGER REFERENCES day_of_week (id),
  group_id INTEGER REFERENCES study_group (id),
  UNIQUE (week_id, group_id)
);

CREATE TABLE schedule_item
(
  id            SERIAL PRIMARY KEY,
  day_id        INTEGER REFERENCES schedule_day (id),
  class_time_id INTEGER REFERENCES class_time (id),
  UNIQUE (day_id, class_time_id)
);

CREATE TABLE schedule_item_parity
(
  id                  SERIAL PRIMARY KEY,
  schedule_item_id    INTEGER REFERENCES schedule_item (id),
  day_parity          CHAR(5)
    CHECK (day_parity = 'ЧС' OR day_parity = 'ЗН' OR day_parity = 'ЧС/ЗН')
    DEFAULT 'ЧС/ЗН',
  classroom_id        INTEGER REFERENCES classroom (id),
  class_type_id       INTEGER REFERENCES class_type (id),
  tutor_subject_id INTEGER REFERENCES tutor_subject (id),

  CONSTRAINT unq_cr UNIQUE (schedule_item_id, day_parity, classroom_id),
  CONSTRAINT unq_subj UNIQUE (schedule_item_id, day_parity, tutor_subject_id),
  CONSTRAINT unq_cl_type UNIQUE (schedule_item_id, day_parity, class_type_id),
  CONSTRAINT unq_parity UNIQUE (schedule_item_id, day_parity)
);


CREATE TABLE study_plan_item
(
  id                    SERIAL PRIMARY KEY,
  study_plan_id         INTEGER REFERENCES study_plan (id),
  department_subject_id INTEGER REFERENCES department_subject (id)
);

CREATE TABLE study_plan_item_cell
(
  id                 SERIAL PRIMARY KEY,
  study_plan_item_id INTEGER REFERENCES study_plan_item (id),
  term_id            INTEGER REFERENCES term (id),
  UNIQUE (study_plan_item_id, term_id)
);

CREATE TABLE hours_per_class
(
  id                      SERIAL PRIMARY KEY,
  study_plan_item_cell_id INTEGER REFERENCES study_plan_item_cell (id),
  class_type_id           INTEGER REFERENCES class_type (id),
  no_of_hours             INTEGER NOT NULL CHECK (no_of_hours > 0),
  UNIQUE (study_plan_item_cell_id, class_type_id)
);