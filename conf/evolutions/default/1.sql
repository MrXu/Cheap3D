# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table obj_file (
  id                        bigint not null,
  file_name                 varchar(255),
  path_name                 varchar(255),
  due_date                  timestamp,
  constraint pk_obj_file primary key (id))
;

create table stl_file (
  id                        bigint not null,
  objfile_id                bigint,
  path_name                 varchar(255),
  file_name                 varchar(255),
  added_date                timestamp,
  constraint pk_stl_file primary key (id))
;

create sequence obj_file_seq;

create sequence stl_file_seq;

alter table stl_file add constraint fk_stl_file_objfile_1 foreign key (objfile_id) references obj_file (id) on delete restrict on update restrict;
create index ix_stl_file_objfile_1 on stl_file (objfile_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists obj_file;

drop table if exists stl_file;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists obj_file_seq;

drop sequence if exists stl_file_seq;

