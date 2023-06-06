CREATE TABLE students
(
    id           int,
    name         varchar(64),
    score        int,
    teacher_note varchar(256)
);

COMMENT ON COLUMN students.score IS 'math score';
COMMENT ON TABLE students IS 'student score table';

CREATE TABLE parents
(
    id            int,
    student_name  varchar(64),
    parent_name   varchar(64),
    parent_mobile varchar(16)
);