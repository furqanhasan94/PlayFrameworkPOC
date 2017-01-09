# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table customer (
  id                            number(19) not null,
  customer_name                 varchar2(255),
  constraint pk_customer primary key (id)
);
create sequence customer_seq;

create table person (
  id                            number(19) not null,
  name                          varchar2(255),
  constraint pk_person primary key (id)
);
create sequence person_seq;

create table product (
  id                            number(19) not null,
  prod_name                     varchar2(255),
  constraint pk_product primary key (id)
);
create sequence product_seq;


# --- !Downs

drop table customer cascade constraints purge;
drop sequence customer_seq;

drop table person cascade constraints purge;
drop sequence person_seq;

drop table product cascade constraints purge;
drop sequence product_seq;

