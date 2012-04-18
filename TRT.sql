drop table mutualfund cascade constraints;
drop table closingprice cascade constraints;
drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table allocation cascade constraints;
drop table prefers cascade constraints;
drop table trxlog cascade constraints;
drop table owns cascade constraints;
drop table mutualdate cascade constraints;
drop table trxlog_aux;

create table mutualfund(
symbol varchar2(20) not null,
name varchar2(30),
description varchar2(100),
category varchar2(10),
c_date date,
constraint pk_mf primary key (symbol));

insert into mutualfund values('MM', 'money-market', 'money market, conservative', 'fixed', CURRENT_DATE);
insert into mutualfund values('RE', 'real-estate', 'real estate', 'fixed', CURRENT_DATE);
insert into mutualfund values('LTB', 'long-term-bonds', 'long term bonds', 'bonds', CURRENT_DATE);
insert into mutualfund values('GS', 'general-stocks', 'general stocks', 'stocks', CURRENT_DATE);
insert into mutualfund values('BBS', 'balance-bonds-stocks', 'balance bonds and stocks', 'mixed', CURRENT_DATE);

create table closingprice(
symbol varchar2(20) not null,
price float,
p_date date,
constraint pk_cp primary key (symbol, p_date),
constraint fk_cp_mf foreign key (symbol) references mutualfund(symbol));

insert into closingprice values('MM', 10.0, sysdate);

create table customer(
login varchar2(10) not null,
name varchar2(20) not null,
email varchar2(20) not null,
address varchar2(30) not null,
password varchar2(10) not null,
balance float,
constraint pk_cus primary key (login));

insert into customer values('vince', 'Vincent Tran', 'hello@vincetran.me', '339 Lawn Street', 'lol', 1000);
insert into customer values('nee', 'Nee Taylor', 'net9@pitt.edu', 'Herp Derp Street', 'lol', 0);


create table administrator(
login varchar2(10) not null,
name varchar2(20),
email varchar2(20),
address varchar2(30),
password varchar2(10),
constraint pk_admin primary key (login));

insert into administrator values('admin', 'Administrator', 'admin@teamrocket.com', '', 'root');

create table allocation(
allocation_no int not null,
login varchar2(10) not null,
p_date date,
constraint pk_alloc primary key (allocation_no),
constraint fk_alloc_cus foreign key (login) references customer(login));

drop sequence allocation_seq;
create sequence allocation_seq minvalue 0 start with 0 increment by 1;

create or replace trigger allocation_autoinc
before insert on allocation
for each row
begin
    select allocation_seq.nextval into :new.allocation_no from dual;
end;
/

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

drop sequence trxlog_seq;
create sequence trxlog_seq minvalue 0 start with 0 increment by 1;

create or replace trigger trxlog_autoinc
before insert on trxlog
for each row
begin
    select trxlog_seq.nextval into :new.trans_id from dual;
end;
/


create table owns(
login varchar2(10) not null,
symbol varchar2(20),
shares int,
constraint pk_owns primary key(login, symbol),
constraint fk_owns_cust foreign key(login) references customer(login),
constraint fk_owns_mf foreign key(symbol) references mutualfund(symbol));

insert into owns values('vince', 'MM', 10);

create table mutualdate(
c_date date not null,
constraint pk_md primary key(c_date));


create or replace trigger update_balance_buy
after insert on trxlog
for each row
when(new.action = 'buy')
begin
	update customer
	set balance = balance - :new.amount
	where login = :new.login;

	insert into owns values(:new.login, :new.symbol, :new.num_shares);
	exception 
	when dup_val_on_index then
		update owns
		set shares = :new.num_shares 
		where login = :new.login AND symbol = :new.symbol;
end;
/


create table trxlog_aux(
login varchar2(10),
amount float,
alloc_no int
);

insert into allocation values(0, 'vince', sysdate);
insert into prefers values(0, 'MM', .3);
insert into prefers values(0, 'RE', .7);

create or replace trigger calc_shares
after insert on trxlog_aux
begin
	insert into trxlog
		values(5, 'vince', 'MM', sysdate, 'buy', 100, 100, 100);
end;
/

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

create or replace trigger deposit_action
after insert on trxlog
declare 
	cursor cur_last_trx is
		select login, amount, action
		from trxlog
		order by trans_id desc;
	login_name varchar2(10);
	deposit_amount int;
	action varchar2(10);
begin
	open cur_last_trx;
	fetch cur_last_trx into login_name, deposit_amount, action;

	IF action = 'deposit' THEN
		insert into trxlog_aux
			values(login_name, deposit_amount, (select allocation_no
					from allocation
					where p_date = (select max(p_date)
						from allocation
						where login = login_name
						group by login))
				);
	END if;

	close cur_last_trx;
end;
/

commit;