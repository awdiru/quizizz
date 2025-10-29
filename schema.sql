DROP TABLE public.answers;
DROP TABLE public.questions;
DROP TABLE public.tests;
DROP TABLE public.users;
DROP TABLE public.directories;

CREATE TABLE public.directories
(
    id     int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name" varchar                           NOT NULL,
    parent_id  int8 NULL,
    CONSTRAINT directories_pk PRIMARY KEY (id),
    CONSTRAINT directories_unique UNIQUE (name, parent),
    CONSTRAINT directories_directories_fk FOREIGN KEY (parent) REFERENCES public.directories (id) ON DELETE CASCADE
);

CREATE TABLE public.users
(
    id                   int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    login                varchar                           NOT NULL,
    "name"               varchar                           NOT NULL,
    "password"           varchar                           NOT NULL,
    working_directory_id varchar                           NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_unique UNIQUE (login),
    CONSTRAINT users_directories_fk FOREIGN KEY (id) REFERENCES public.directories (id)
);

CREATE TABLE public.tests
(
    id           int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    author_id    int8                              NOT NULL,
    directory_id int8                              NOT NULL,
    CONSTRAINT tests_pk PRIMARY KEY (id),
    CONSTRAINT tests_directories_fk FOREIGN KEY (directory_id) REFERENCES public.directories (id),
    CONSTRAINT tests_users_fk FOREIGN KEY (id) REFERENCES public.users (id)
);

CREATE TABLE public.questions
(
    id       int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    question varchar                           NOT NULL,
    test_id  int8                              NOT NULL,
    CONSTRAINT questions_pk PRIMARY KEY (id),
    CONSTRAINT questions_tests_fk FOREIGN KEY (id) REFERENCES public.tests (id)
);

CREATE TABLE public.answers
(
    id          int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    answer      varchar                           NOT NULL,
    question_id int8                              NOT NULL,
    "number"    int8                              NOT NULL,
    CONSTRAINT answers_pk PRIMARY KEY (id),
    CONSTRAINT answers_questions_fk FOREIGN KEY (question_id) REFERENCES public.questions (id)
);