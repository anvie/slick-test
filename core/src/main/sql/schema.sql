
create table "ADMIN" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL);
create table "OPERATOR" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL,"ABILITIES" VARCHAR NOT NULL);
create table "INVESTOR" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL,"ROLE" INTEGER NOT NULL);
create table "BUSINESS" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL,
  "FUND" DOUBLE NOT NULL, "DIVIDE_INVEST" DOUBLE NOT NULL, STATE INT NOT NULL);
create table "BUSINESS_GROUP" ("ID" BIGINT NOT NULL AUTO_INCREMENT,"NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL);
create table "BUSINESS_GROUP_LINK" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "BUS_GROUP_ID" BIGINT NOT NULL, "BUS_ID" BIGINT NOT NULL);
create table "BUSINESS_PROFIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "BUS_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "TS" TIMESTAMP NOT NULL, "MUTATOR_ID" BIGINT NOT NULL, "MUTATOR_ROLE" INTEGER NOT NULL, "INFO" VARCHAR NOT NULL);

-- digunakan untuk logging siapa saja yang me-mutasi pada object BUSINESS termasuk penambahan dan pengurangan BUSINESS_PROFIT
--create table "BUSINESS_MUTATE_JOURNAL" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "BUS_ID" BIGINT NOT NULL, "MUTATOR_ID" BIGINT NOT NULL, "MUTATOR_ROLE" INTEGER NOT NULL, "INFO" VARCHAR NOT NULL);

create table "INVEST" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "BUS_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "BUS_KIND" INTEGER NOT NULL);
create table "CREDIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "REF" VARCHAR, "TS" TIMESTAMP NOT NULL);
create table "DEBIT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL, "REF" VARCHAR);
create table "INVESTOR_BALANCE" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "AMOUNT" DOUBLE NOT NULL);

-- project sudah digantikan dengan BUSINESS `state`
-- create table "PROJECT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL, "DONE_PERCENT" DOUBLE NOT NULL);

create table "PROJECT_WATCHER" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "BUS_ID" BIGINT NOT NULL);
