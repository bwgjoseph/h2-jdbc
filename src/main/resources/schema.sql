create table person
(
   id integer not null,
   name varchar(255) not null,
   dob date not null,
   da int not null,
   primary key(id)
);

create table mapper
(
   id integer not null,
   table_name varchar(255) not null,
   date_col varchar(255) not null,
   acc_col varchar(255) not null,
   output varchar(255) null,
   primary key(id)
);