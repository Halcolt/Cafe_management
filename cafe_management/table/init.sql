drop database coffeeshop_management;
create database coffeeshop_management;
use coffeeshop_management;

CREATE TABLE login (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  AccountName VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  Passwords VARCHAR(255) NOT NULL,
  permission VARCHAR(255) NOT NULL,
  tel VARCHAR(255),
  email VARCHAR(255),
  identity VARCHAR(255),
  usual_schedule VARCHAR(255),
  hour_payment int NOT NULL,
  INDEX (username) -- Adding index to the username column
);

create table measurement (
	ID INT PRIMARY KEY AUTO_INCREMENT,
	unit varchar(10),
    index (unit)
);

create table stock (
	ID INT PRIMARY KEY AUTO_INCREMENT,
	ingredient varchar(255),
	unit varchar(10),
    amount int,
    index (ingredient,amount,unit)
);

create table stock_change(
	username VARCHAR(255) NOT NULL,
	changedate Date,
    changetime time,
    ingredient varchar(255),
    unit varchar(10),
    old_amount int,
    new_amount int,
    price int,
    index(ingredient,username),
    FOREIGN KEY (ingredient) REFERENCES stock(ingredient),
    FOREIGN KEY (username) REFERENCES login(username),
    FOREIGN KEY (unit) REFERENCES measurement(unit)
);


CREATE TABLE menu (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  item VARCHAR(255) NOT NULL,
  price float,
  index(item)
);

CREATE TABLE Employ_working_hour (
  username VARCHAR(255) NOT NULL,
  working_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  hour_working int NOT NULL,
  PRIMARY KEY (username, working_date),
  FOREIGN KEY (username) REFERENCES login(username)
);

CREATE TABLE orders (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  orderdate Date,
  ordertime time,
  item VARCHAR(255) NOT NULL,
  amount INT NOT NULL,
  total_price float,
  index (username,item),
  FOREIGN KEY (username) REFERENCES login(username),
  FOREIGN KEY (item) REFERENCES menu(item)
)

drop table stock
drop table measurement
drop table stock_change

CREATE TABLE imports (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  Username VARCHAR(255) NOT NULL,
  working_place VARCHAR(255) NULL,
  changetime DATE NOT NULL,
  ingredient varchar(255),
  amount int,
  unit varchar(10),
  total_price float,
  FOREIGN KEY (ingredient) REFERENCES stock(ingredient)
);






select * from login
select * from measurement
select * from imports


