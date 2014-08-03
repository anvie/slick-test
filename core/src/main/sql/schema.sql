
create table "USER" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL,"ROLE" INTEGER NOT NULL);
create table "BUSINESS" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL, "DIVIDE_SYS" DOUBLE NOT NULL, "DIVIDE_INVEST" DOUBLE NOT NULL);
create table "BUSINESS_PROFIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "BUS_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "TS" TIMESTAMP);
create table "INVEST" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "USER_ID" BIGINT NOT NULL, "BUS_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL);
create table "CREDIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "USER_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "REF" VARCHAR);
create table "DEBIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "USER_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "REF" VARCHAR);
create table "USER_BALANCE" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "USER_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL);
