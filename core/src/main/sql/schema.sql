
create table "ADMIN" ("ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "NAME" VARCHAR NOT NULL,
  "PASSHASH" VARCHAR NOT NULL);
create table "OPERATOR" ("ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "NAME" VARCHAR NOT NULL,
  "ABILITIES" VARCHAR NOT NULL,
  "PASSHASH" VARCHAR NOT NULL);
create table "INVESTOR" ("ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "NAME" VARCHAR NOT NULL,
  "ROLE" INTEGER NOT NULL,
  "PASSHASH" VARCHAR NOT NULL,
  "STATUS" INTEGER NOT NULL DEFAULT 1);

-- BUSINESS ------------------

create table "BUSINESS" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "NAME" VARCHAR NOT NULL,
  "DESC" VARCHAR NOT NULL,
  "FUND" DOUBLE NOT NULL,
  "SHARE" DOUBLE NOT NULL,
  "STATE" INT NOT NULL,
  "SHARE_TIME" INT NOT NULL,
  "SHARE_PERIOD" INT NOT NULL,
  "SAVING" DOUBLE NOT NULL, -- balance
  "CREATED_AT" DATETIME NOT NULL
);
create table "BUSINESS_GROUP" ("ID" BIGINT NOT NULL AUTO_INCREMENT,
  "NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL);
create table "BUSINESS_GROUP_LINK" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT,
  "BUS_GROUP_ID" BIGINT NOT NULL,
  "BUS_ID" BIGINT NOT NULL);
create table "BUSINESS_PROFIT" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "BUS_ID" BIGINT NOT NULL,
  "OMZET" DOUBLE NOT NULL,
  "PROFIT" DOUBLE NOT NULL,
  "TS" TIMESTAMP NOT NULL,
  "MUTATOR_ID" BIGINT NOT NULL,
  "MUTATOR_ROLE" INTEGER NOT NULL,
  "INFO" VARCHAR NOT NULL,
  "SHARED" BOOL NOT NULL DEFAULT FALSE,
  "SHARED_AT" TIMESTAMP NOT NULL);
CREATE TABLE "PROFIT_SHARE_JOURNAL" (
  "BUS_ID" BIGINT NOT NULL,
  "BUS_PROF_ID" BIGINT NOT NULL,
  "INV_ID" BIGINT NOT NULL,
  "AMOUNT" DOUBLE NOT NULL,
  "SHARE_METHOD" INT NOT NULL DEFAULT 2, -- 2=MANUAL
  "INITIATOR" VARCHAR(100) DEFAULT '',
  "TS" TIMESTAMP NOT NULL
);
-- CREATE TABLE "BUSINESS_BALANCE" (
--   "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
--   "BUS_ID" BIGINT NOT NULL,
--   "BALANCE" DOUBLE NOT NULL
-- );
CREATE TABLE "BUSINESS_ACCOUNT_MUTATION" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "BUS_ID" BIGINT NOT NULL,
  "KIND" INTEGER NOT NULL, -- mutation kind
  "AMOUNT" DOUBLE NOT NULL,
  "INFO" VARCHAR(500) NOT NULL,
  "INITIATOR" VARCHAR(100) NOT NULL,
  "TS" TIMESTAMP NOT NULL
);

-- digunakan untuk logging siapa saja yang me-mutasi pada object BUSINESS termasuk penambahan dan pengurangan BUSINESS_PROFIT
--create table "BUSINESS_MUTATE_JOURNAL" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "BUS_ID" BIGINT NOT NULL, "MUTATOR_ID" BIGINT NOT NULL, "MUTATOR_ROLE" INTEGER NOT NULL, "INFO" VARCHAR NOT NULL);

create table "INVEST" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "INV_ID" BIGINT NOT NULL,
  "BUS_ID" BIGINT NOT NULL,
  "AMOUNT" DOUBLE NOT NULL,
  "BUS_KIND" INTEGER NOT NULL,
  "TS" TIMESTAMP NOT NULL);
create table "MUTATION" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "INV_ID" BIGINT NOT NULL,
  "KIND" INT NOT NULL, -- mutation kind
  "AMOUNT" DOUBLE NOT NULL,
  "REF" VARCHAR,
  "INITIATOR" VARCHAR(100) DEFAULT '',
  "TS" TIMESTAMP NOT NULL);
-- create table "DEBIT" (
--   "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
--   "INV_ID" BIGINT NOT NULL,
--   "AMOUNT" DOUBLE NOT NULL,
--   "REF" VARCHAR);
create table "INVESTOR_BALANCE" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "INV_ID" BIGINT NOT NULL,
  "AMOUNT" DOUBLE NOT NULL);

-- project sudah digantikan dengan BUSINESS `state`
-- create table "PROJECT" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL, "DONE_PERCENT" DOUBLE NOT NULL);

create table "PROJECT_WATCHER" ("ID" BIGINT NOT NULL AUTO_INCREMENT, "INV_ID" BIGINT NOT NULL, "BUS_ID" BIGINT NOT NULL);



----------- API ------------------------


create table "API_CLIENT" ("ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "NAME" VARCHAR NOT NULL, "DESC" VARCHAR NOT NULL,
  "CREATOR_ID" BIGINT NOT NULL, "CREATOR_ROLE" INTEGER NOT NULL,
  "KEY" VARCHAR NOT NULL, "SUSPENDED" BOOL NOT NULL);
create table "API_CLIENT_ACCESS" ("ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "API_CLIENT_ID" BIGINT NOT NULL,
  "GRANT" VARCHAR NOT NULL, "TARGET" VARCHAR NOT NULL);


--------- PROJECT ----------------------


CREATE TABLE "PROJECT_REPORT" (
  "ID" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "BUS_ID" BIGINT NOT NULL,
  "INFO" VARCHAR NOT NULL,
  "PERCENTAGE" DOUBLE NOT NULL,
  "INITIATOR" VARCHAR NOT NULL,
  "TS" TIMESTAMP NOT NULL
)
