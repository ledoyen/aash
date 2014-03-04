CREATE TABLE IF NOT EXISTS USERS(ID INT PRIMARY KEY, CIVILITY_ID INT, REGION_ID INT, BIRTH_DATE DATE, CREATION_DATE DATE);
CREATE TABLE IF NOT EXISTS SCORES(ID INT PRIMARY KEY, USER_ID INT, IS_HISTO BOOLEAN, SCORE DOUBLE, NAME VARCHAR(255));
CREATE TABLE IF NOT EXISTS CIVILITY(ID INT PRIMARY KEY, NAME VARCHAR(255));
CREATE TABLE IF NOT EXISTS REGION(ID INT PRIMARY KEY, CODE VARCHAR(255), CATEGORY VARCHAR(255));