# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table client (
  client_id                 bigint not null,
  constraint pk_client primary key (client_id))
;

create table matching_task (
  id                        bigint not null,
  name                      varchar(255),
  source_ontology           varchar(255),
  target_ontology           varchar(255),
  constraint pk_matching_task primary key (id))
;

create sequence client_seq;

create sequence matching_task_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists client;

drop table if exists matching_task;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists client_seq;

drop sequence if exists matching_task_seq;

