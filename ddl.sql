create table customer
(
    user_type         tinyint      null,
    verify            bit          not null,
    created_at        datetime(6)  null,
    id                bigint auto_increment
        primary key,
    last_modified_at  datetime(6)  null,
    verify_expired_at datetime(6)  null,
    email             varchar(255) null,
    name              varchar(255) null,
    password          varchar(255) null,
    phone             varchar(255) null,
    register_number   varchar(255) null,
    verification_code varchar(255) null
);

create table partner
(
    user_type         tinyint      null,
    verify            bit          not null,
    created_at        datetime(6)  null,
    last_modified_at  datetime(6)  null,
    partner_id        bigint auto_increment
        primary key,
    verify_expired_at datetime(6)  null,
    email             varchar(255) null,
    name              varchar(255) null,
    password          varchar(255) null,
    phone             varchar(255) null,
    register_number   varchar(255) null,
    verification_code varchar(255) null
);

create table reservation
(
    confirm          bit          not null,
    created_at       datetime(6)  null,
    customer_id      bigint       null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6)  null,
    store_id         bigint       null,
    waiting_number   bigint       null,
    customer_name    varchar(255) null,
    store_name       varchar(255) null
);

create table review
(
    id          bigint auto_increment
        primary key,
    content     varchar(255) null,
    customer_id bigint       null,
    rating      int          not null,
    store_id    bigint       null,
    title       varchar(255) null
);

create table revinfo
(
    rev      int auto_increment
        primary key,
    revtstmp bigint null
);

create table store
(
    latitude         double       not null,
    longitude        double       not null,
    created_at       datetime(6)  null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6)  null,
    partner_id       bigint       null,
    name             varchar(100) not null,
    description      text         null,
    constraint UKp2fvdil1a1m84jcur6s8hj2r7
        unique (partner_id, name)
);

create table store_detail
(
    close_time       datetime(6)  null,
    created_at       datetime(6)  null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6)  null,
    open_time        datetime(6)  null,
    address          varchar(255) null,
    description      varchar(255) null,
    tel              varchar(255) null
);


