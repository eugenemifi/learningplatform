-- USERS
create table users
(
    id    bigserial primary key,
    name  varchar(255) not null,
    email varchar(255) not null unique,
    role  varchar(20)  not null
);

-- PROFILES (1-1 с users)
create table profiles
(
    id         bigserial primary key,
    user_id    bigint not null unique,
    bio        varchar(1000),
    avatar_url varchar(500),
    constraint fk_profiles_user
        foreign key (user_id) references users (id)
);

-- CATEGORIES
create table categories
(
    id   bigserial primary key,
    name varchar(255) not null unique
);

-- TAGS
create table tags
(
    id   bigserial primary key,
    name varchar(255) not null unique
);

-- COURSES
create table courses
(
    id                bigserial primary key,
    title             varchar(255) not null,
    description       varchar(2000),
    category_id       bigint,
    teacher_id        bigint       not null,
    duration_in_hours integer,
    start_date        date,
    constraint fk_courses_category
        foreign key (category_id) references categories (id),
    constraint fk_courses_teacher
        foreign key (teacher_id) references users (id)
);

-- COURSE_TAG (Many-to-Many)
create table course_tag
(
    course_id bigint not null,
    tag_id    bigint not null,
    primary key (course_id, tag_id),
    constraint fk_course_tag_course foreign key (course_id) references courses (id) on delete cascade,
    constraint fk_course_tag_tag foreign key (tag_id) references tags (id) on delete cascade
);

-- MODULES
create table modules
(
    id          bigserial primary key,
    course_id   bigint       not null,
    title       varchar(255) not null,
    order_index integer      not null,
    constraint fk_modules_course foreign key (course_id) references courses (id) on delete cascade
);

-- LESSONS
create table lessons
(
    id        bigserial primary key,
    module_id bigint       not null,
    title     varchar(255) not null,
    content   varchar(8000),
    video_url varchar(500),
    constraint fk_lessons_module foreign key (module_id) references modules (id) on delete cascade
);

-- ASSIGNMENTS
create table assignments
(
    id          bigserial primary key,
    lesson_id   bigint       not null,
    title       varchar(255) not null,
    description varchar(4000),
    due_date    timestamp,
    max_score   integer,
    constraint fk_assignments_lesson foreign key (lesson_id) references lessons (id) on delete cascade
);

-- ENROLLMENTS (Many-to-Many User <-> Course)
create table enrollments
(
    id          bigserial primary key,
    student_id  bigint      not null,
    course_id   bigint      not null,
    enrolled_at timestamp   not null,
    status      varchar(20) not null,
    constraint fk_enrollments_student foreign key (student_id) references users (id),
    constraint fk_enrollments_course foreign key (course_id) references courses (id),
    constraint uk_enrollment_user_course unique (student_id, course_id)
);

-- SUBMISSIONS (решения заданий)
create table submissions
(
    id            bigserial primary key,
    assignment_id bigint    not null,
    student_id    bigint    not null,
    submitted_at  timestamp not null,
    content       varchar(8000),
    score         integer,
    feedback      varchar(2000),
    constraint fk_submissions_assignment foreign key (assignment_id) references assignments (id) on delete cascade,
    constraint fk_submissions_student foreign key (student_id) references users (id),
    constraint uk_submission_student_assignment unique (student_id, assignment_id)
);

-- QUIZZES
create table quizzes
(
    id                 bigserial primary key,
    module_id          bigint unique,
    title              varchar(255) not null,
    time_limit_minutes integer,
    constraint fk_quizzes_module foreign key (module_id) references modules (id) on delete cascade
);

-- QUESTIONS
create table questions
(
    id      bigserial primary key,
    quiz_id bigint        not null,
    text    varchar(2000) not null,
    type    varchar(20)   not null,
    constraint fk_questions_quiz foreign key (quiz_id) references quizzes (id) on delete cascade
);

-- ANSWER_OPTIONS
create table answer_options
(
    id          bigserial primary key,
    question_id bigint        not null,
    text        varchar(1000) not null,
    correct     boolean       not null,
    constraint fk_answer_options_question foreign key (question_id) references questions (id) on delete cascade
);

-- QUIZ_SUBMISSIONS
create table quiz_submissions
(
    id         bigserial primary key,
    quiz_id    bigint    not null,
    student_id bigint    not null,
    score      integer   not null,
    taken_at   timestamp not null,
    constraint fk_quiz_submissions_quiz foreign key (quiz_id) references quizzes (id),
    constraint fk_quiz_submissions_student foreign key (student_id) references users (id),
    constraint uk_quiz_submission_student_quiz unique (student_id, quiz_id)
);

-- COURSE_REVIEWS
create table course_reviews
(
    id         bigserial primary key,
    course_id  bigint    not null,
    student_id bigint    not null,
    rating     integer   not null,
    comment    varchar(2000),
    created_at timestamp not null,
    constraint fk_course_reviews_course foreign key (course_id) references courses (id) on delete cascade,
    constraint fk_course_reviews_student foreign key (student_id) references users (id),
    constraint uk_course_review_student_course unique (student_id, course_id)
);