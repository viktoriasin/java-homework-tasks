-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
--/*
create table client
(
    id  bigserial not null primary key,
    name varchar(50)
);

--*/

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
--create sequence client_SEQ start with 1 increment by 1;
--
--create table client
--(
--    id   bigint not null primary key,
--    name varchar(50),
--    address_id bigint
--);

create table address
(
    id   bigserial not null primary key,
    street varchar(50),
    client_id bigint not null references client (id)
);

create table phone
(
    id  bigserial not null primary key,
    client_id   bigint not null references client (id),
    number varchar(50)
);
