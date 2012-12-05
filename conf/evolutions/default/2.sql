# --- !Ups
insert into users values ('bert.huygens@gmail.com','bert','bert',true);
insert into users values ('greet.huygens@accenture.com','greet','greet',true);
insert into users values ('stefanie.hennebel@gmail.com','stefanie','stefanie',false);

insert into salespersons (name,firstName,mobile) values ('Hennebel','Stefanie','0494121212');
insert into salespersons (name,firstName,mobile) values ('Huygens','Greet','0494121212');
insert into salespersons (name,firstName,mobile) values ('dambrosio','Fredrico','0494121212');
insert into salespersons (name,firstName,mobile) values ('Huygens','Bert','0494121212');
insert into salespersons (name,firstName,mobile) values ('Ceuppens','Christiane','0494121212');

insert into products (productCode,productName,productDescription,productPrice) values ('455-DI','Biberon wide neck','Biberon wide neck  240 ml',2);
insert into products (productCode,productName,productDescription,productPrice) values ('555-DI','Biberon wide neck 120 ml','Biberon wide neck 120 ml',2);
insert into products (productCode,productName,productDescription,productPrice) values ('056-GB','Biberon  standard 60 ml','Biberon  standard 60 ml',2);
insert into products (productCode,productName,productDescription,productPrice) values ('261-INTL','Biberon standard VETRO 240 ml','Biberon standard VETRO 240 ml',3);
insert into products (productCode,productName,productDescription,productPrice) values ('161-INTL','Biberon standard VETRO 120 ml','Biberon standard VETRO 120 ml',3);
insert into products (productCode,productName,productDescription,productPrice) values ('352-DI','Tettarella WN L1 (2p) - 0-3m','Tettarella WN L1 (2p) - 0-3m',2);
insert into products (productCode,productName,productDescription,productPrice) values ('372-DI','Tettarella WN L2 (2p) - 3-6m','Tettarella WN L2 (2p) - 3-6m',2);

# --- !Downs