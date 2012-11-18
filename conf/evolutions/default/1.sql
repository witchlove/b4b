# b4b schema
 
# --- !Ups

CREATE SEQUENCE order_id_seq;
CREATE TABLE orders (
    id integer NOT NULL DEFAULT nextval('order_id_seq'),
    description varchar(255)
);
 
# --- !Downs
 
DROP TABLE orders;
DROP SEQUENCE order_id_seq;