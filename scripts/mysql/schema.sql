CREATE TABLE `students`
(
    `id`           int(11)      DEFAULT NULL,
    `name`         varchar(64)  DEFAULT NULL,
    `score`        int(11)      DEFAULT NULL COMMENT 'math score',
    `teacher_note` varchar(256) DEFAULT NULL
) COMMENT ='student score table';


CREATE TABLE `parents`
(
    `id`            int(11)     DEFAULT NULL,
    `student_name`  varchar(64) DEFAULT NULL,
    `parent_name`   varchar(64) DEFAULT NULL,
    `parent_mobile` varchar(16) DEFAULT NULL
);




