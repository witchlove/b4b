# b4b schema
 
# --- !Ups

CREATE SEQUENCE salesperson_id_seq;
CREATE SEQUENCE product_id_seq;
CREATE SEQUENCE order_id_seq;
CREATE SEQUENCE orderItem_id_seq;

CREATE TABLE salespersons (
	id integer NOT NULL DEFAULT nextval('salesperson_id_seq'),
	name varchar(150),
	firstName varchar(30),
	mobile varchar(25)
);

CREATE TABLE products (
	id integer NOT NULL DEFAULT nextval('product_id_seq'),
	productCode varchar(150),
	productName varchar(30),
	productDescription varchar(255),
	productPrice bigint(20)
);

CREATE TABLE orders (
	id integer NOT NULL DEFAULT nextval('order_id_seq'),
	orderCode varchar(150),
	salesPersonId integer
);

CREATE TABLE orderItems (
	id integer NOT NULL DEFAULT nextval('orderItem_id_seq'),
	orderId integer NOT NULL ,
	productId integer NOT NULL,
	quantity number
);
 
# --- !Downs

DROP TABLE salespersons;
DROP SEQUENCE salesperson_id_seq;

DROP TABLE products;
DROP SEQUENCE product_id_seq;

DROP TABLE orders;
DROP SEQUENCE order_id_seq;

DROP TABLE orderItems;
DROP SEQUENCE orderItem_id_seq;