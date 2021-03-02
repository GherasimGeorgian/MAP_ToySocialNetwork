create table users
(
    id        bigserial not null
        constraint users_pk
            primary key,
    firstname varchar   not null,
    lastname  varchar   not null
);

alter table users
    owner to postgres;

create table prietenie
(
    id_left        bigint not null,
    id_right       bigint not null,
    data_prietenie date   not null
);

alter table prietenie
    owner to postgres;

create table message
(
    id        bigserial not null
        constraint message_pk
            primary key,
    from_user bigint    not null,
    to_user   varchar   not null,
    message   varchar   not null,
    date      timestamp not null,
    reply     bigint
);

alter table message
    owner to postgres;

create table invitations
(
    id          bigserial not null
        constraint invitations_pk
            primary key,
    frominvite  bigint    not null,
    toinvite    bigint    not null,
    date_invite timestamp not null,
    status      varchar   not null
);

alter table invitations
    owner to postgres;

create table eveniment
(
    idevent    bigint    not null
        constraint eveniment_pk
            primary key,
    event_name varchar   not null,
    date_event timestamp not null
);

alter table eveniment
    owner to postgres;

create unique index eveniment_idevent_uindex
    on eveniment (idevent);

create table abonatievenimente
(
    id_abonare    bigint    not null
        constraint abonatievenimente_pk
            primary key,
    id_utilizator bigint    not null,
    id_eveniment  bigint    not null,
    data_abonare  timestamp not null
);

alter table abonatievenimente
    owner to postgres;

create unique index abonatievenimente_id_abonare_uindex
    on abonatievenimente (id_abonare);

create table account
(
    id_utilizator       bigint    not null
        constraint account_pk
            primary key,
    date_create_account timestamp not null,
    email               varchar   not null,
    parola              varchar   not null,
    type_account        varchar   not null,
    photo_url           varchar
);

alter table account
    owner to postgres;

create unique index account_id_utilizator_uindex
    on account (id_utilizator);

