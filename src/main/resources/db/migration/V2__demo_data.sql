insert into categories (name)
values ('Programming')
    on conflict (name) do nothing;

insert into tags (name)
values ('Java'), ('Hibernate'), ('Beginner')
    on conflict (name) do nothing;

insert into users (name, email, role)
values
    ('Teacher One', 'teacher1@example.com', 'TEACHER'),
    ('Student One', 'student1@example.com', 'STUDENT'),
    ('Student Two', 'student2@example.com', 'STUDENT')
    on conflict (email) do nothing;

-- возьмём id-шники через подзапросы
insert into courses (title, description, category_id, teacher_id, duration_in_hours, start_date)
select
    'Основы Hibernate',
    'Учебный курс по ORM и Hibernate',
    c.id,
    u.id,
    20,
    current_date
from categories c, users u
where c.name = 'Programming'
  and u.email = 'teacher1@example.com'
    limit 1;

-- модуль и урок
insert into modules (course_id, title, order_index)
select id, 'Введение в ORM', 1 from courses where title = 'Основы Hibernate' limit 1;

insert into lessons (module_id, title, content)
select m.id,
       'Что такое ORM',
       'Текст урока про ORM / Hibernate'
from modules m
         join courses c on c.id = m.course_id
where c.title = 'Основы Hibernate'
  and m.title = 'Введение в ORM'
    limit 1;

-- простое задание
insert into assignments (lesson_id, title, description, due_date, max_score)
select l.id,
       'Домашнее задание 1',
       'Сделайте простой CRUD с Hibernate',
       current_timestamp + interval '7 days',
    100
from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
where c.title = 'Основы Hibernate'
  and l.title = 'Что такое ORM'
    limit 1;

-- записываем студентов на курс
insert into enrollments (student_id, course_id, enrolled_at, status)
select u.id,
       c.id,
       current_timestamp,
       'ACTIVE'
from users u, courses c
where u.email in ('student1@example.com', 'student2@example.com')
  and c.title = 'Основы Hibernate';

-- простой квиз
insert into quizzes (module_id, title, time_limit_minutes)
select m.id, 'Тест по введению в ORM', 15
from modules m
         join courses c on c.id = m.course_id
where c.title = 'Основы Hibernate'
  and m.title = 'Введение в ORM'
    limit 1;

-- вопрос и варианты
insert into questions (quiz_id, text, type)
select q.id, 'Что делает ORM?', 'SINGLE_CHOICE'
from quizzes q
         join modules m on m.id = q.module_id
         join courses c on c.id = m.course_id
where c.title = 'Основы Hibernate'
    limit 1;

insert into answer_options (question_id, text, correct)
select q.id, 'Маппит объекты на таблицы БД', true
from questions q
         join quizzes z on z.id = q.quiz_id
    limit 1;

insert into answer_options (question_id, text, correct)
select q.id, 'Заменяет СУБД', false
from questions q
         join quizzes z on z.id = q.quiz_id
    limit 1;
