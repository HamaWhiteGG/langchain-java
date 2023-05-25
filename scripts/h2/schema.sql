CREATE TABLE students
(
    id           int,
    name         varchar(64),
    score        int,
    teacher_note varchar(256)
);

CREATE TABLE parents
(
    id            int,
    student_name  varchar(64),
    parent_name   varchar(64),
    parent_mobile varchar(16)
);