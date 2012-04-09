drop table mutualfund cascade constraints;
drop table closingprice cascade constraints;
drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table allocation cascade constraints;
drop table prefers cascade constraints;
drop table trxlog cascade constraints;
drop table owns cascade constraints;
drop table mutualdate cascade constraints;

create table mutualfund(
symbol varchar2(20) not null,
name varchar2(30),
description varchar2(100),
category varchar2(10),
c_date date,
constraint pk_mf primary key (symbol));

create table closingprice(
symbol varchar2(20) not null,
price float,
p_date date,
constraint pk_cp primary key (symbol, p_date),
constraint fk_cp_mf foreign key (symbol) references mutualfund(symbol));

create table customer(
login varchar2(10) not null,
name varchar2(20) not null,
email varchar2(20) not null,
address varchar2(30) not null,
password varchar2(10) not null,
balance float,
constraint pk_cus primary key (login));

create table administrator(
login varchar2(10) not null,
name varchar2(20),
email varchar2(20),
address varchar2(30),
password varchar2(10),
constraint pk_admin primary key (login));

create table allocation(
allocation_no int not null,
login varchar2(10) not null,
p_date date,
constraint pk_alloc primary key (allocation_no),
constraint fk_alloc_cus foreign key (login) references customer(login));

create table prefers(
allocation_no int not null,
symbol varchar2(20),
percentage float,
constraint pk_pref primary key(allocation_no, symbol),
constraint fk_pref_alloc foreign key(allocation_no) references allocation(allocation_no),
constraint fk_pref_mf foreign key(symbol) references mutualfund(symbol));

create table trxlog(
trans_id int not null,
login varchar2(10),
symbol varchar2(20),
t_date date,
action varchar2(10),
num_shares int,
price float,
amount float,
constraint pk_trx primary key(trans_id),
constraint fk_trx_cust foreign key(login) references customer(login),
constraint fk_trx_mf foreign key(symbol) references mutualfund(symbol));

create table owns(
login varchar2(10) not null,
symbol varchar2(20),
shares int,
constraint pk_owns primary key(login, symbol),
constraint fk_owns_cust foreign key(login) references customer(login),
constraint fk_owns_mf foreign key(symbol) references mutualfund(symbol));

create table mutualdate(
c_date date not null,
constraint pk_md primary key(c_date));

create or replace trigger update_balance
after insert on trxlog
for each row
when(new.action = 'sell')
begin
	update customer
	set balance = balance + :new.amount
	where login = :new.login;
end;
/


commit;