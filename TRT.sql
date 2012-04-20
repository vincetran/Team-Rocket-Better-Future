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
drop table test;

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

insert into closingprice values('MM', 120.0, sysdate);
insert into closingprice values('RE', 190.0, sysdate);

create table customer(
login varchar2(10) not null,
name varchar2(20) not null,
email varchar2(20) not null,
address varchar2(30) not null,
password varchar2(10) not null,
balance float,
constraint pk_cus primary key (login));

insert into customer values('vince', 'Vincent Tran', 'hello@vincetran.me', '339 Lawn Street', 'lol', 1200);
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

create table trxlog_aux(
login varchar2(10),
amount float,
alloc_no int,
t_date date
);

create table test(
symbol varchar2(10),
num_shares int,
price float, 
amount float
);

insert into allocation values(0, 'vince', sysdate);
insert into prefers values(0, 'MM', .3);
insert into prefers values(0, 'RE', .7);

/*
* This trigger fires after each insert into trxlog_aux
* It will calculate all of the arithmetic involved 
* for each of the buy transactions and then insert into
* the original trxlog. It will also update the owns
* table.
*/

create or replace trigger calc_shares
after insert on trxlog_aux
for each row
declare
	cursor cur_preferred is
		select symbol, percentage
		from prefers
		where allocation_no=:new.alloc_no;
	cursor cur_closing_price is
		select symbol, price from closingprice
			order by p_date DESC;
	pre_symbol varchar2(5);
	pre_percent float; 
	curr_closing_price float;
	amount_to_invest float;
	amount_for_symbol float;
	num_shares int;
	amount_remainder float;
begin
	FOR preferred in cur_preferred
	LOOP
		pre_symbol := preferred.symbol;
		
		FOR closing in cur_closing_price
		LOOP
			IF closing.symbol = pre_symbol THEN
				curr_closing_price := closing.price;
				EXIT;
			END IF;
		END LOOP;

		pre_percent := preferred.percentage;
		amount_to_invest := :new.amount;
		amount_for_symbol := pre_percent * amount_to_invest;
		num_shares := floor(amount_for_symbol / curr_closing_price);

		insert into trxlog values(0, :new.login, pre_symbol, sysdate, 'buy', num_shares, curr_closing_price, num_shares*curr_closing_price);

		amount_remainder := amount_for_symbol - (num_shares*curr_closing_price);

	END LOOP;
end;
/


/* 
* The two triggers will fire whenever there is a 
* buy or sell transaction
*/

create or replace trigger update_balance_sell
after insert on trxlog
for each row
when(new.action = 'sell')
begin
	update customer
		set balance = balance + :new.amount
		where login = :new.login;
	update owns
		set shares = shares - :new.num_shares
		where login = :new.login and
		symbol = :new.symbol;
end;
/

create or replace trigger update_balance_buy
after insert on trxlog
for each row
when(new.action = 'buy')
declare
	sym_exists int;
	prev_shares int;
	neg_balance EXCEPTION;
	PRAGMA EXCEPTION_INIT( neg_balance, -1378 );
	user_balance float;
begin
	select balance into user_balance from customer
		where login=:new.login;
	IF (user_balance-:new.amount) >= 0 THEN
		update customer
		set balance = balance - :new.amount
		where login = :new.login;
	ELSE
		raise_application_error(-1378, 'Negative Balance Achieved');
	END IF;

	select count(*) into sym_exists from owns
			where login = :new.login and symbol = :new.symbol;
	IF sym_exists >= 1 THEN
		select shares into prev_shares from owns
			where login = :new.login and symbol = :new.symbol;
		update owns set shares = prev_shares+:new.num_shares 
			where login = :new.login and symbol = :new.symbol;
	ELSE
		insert into owns values(:new.login, :new.symbol, :new.num_shares );
	END IF;

	EXCEPTION
		WHEN neg_balance THEN
			dbms_output.put_line( sqlerrm );
end;
/
show errors;

/*
* Whenever there is a deposit transaction into trxlog
* the trigger will fire and add the data needed
* for the calc_shares trigger. i.e. login name
* the amount deposited and the user's latest allocation id
*/
create or replace trigger deposit_action
after insert on trxlog
declare 
	cursor cur_last_trx is
		select login, amount, action, t_date
		from trxlog
		order by trans_id desc;
	login_name varchar2(10);
	deposit_amount int;
	action varchar2(10);
	trx_date date;
begin
	open cur_last_trx;
	fetch cur_last_trx into login_name, deposit_amount, action, trx_date;

	IF action = 'deposit' THEN
		insert into trxlog_aux
			values(login_name, deposit_amount, (select allocation_no
					from allocation
					where p_date = (select max(p_date)
						from allocation
						where login = login_name
						group by login)), trx_date
				);
	END if;

	close cur_last_trx;
end;
/

commit;