create table client
(
    id   bigserial not null primary key,
    name varchar(50),
    age smallint
);
create table manager
(
    id   bigserial not null primary key,
    label varchar(50),
    param1 varchar(50)
);
