-- try to drop database
DROP DATABASE testDB;

-- try to drop user
DROP USER test@10.0.2.2;

-- create database testDB
CREATE DATABASE testDB;

-- switch to testDB
USE testDB;

-- create user
CREATE USER test@10.0.2.2 IDENTIFIED BY 'test';

-- create table
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  login VARCHAR(20) UNIQUE,
  password VARCHAR(20),
  name VARCHAR(20)
);

-- grant privileges to test user
GRANT INSERT, SELECT, UPDATE, DELETE ON testDB.users TO test@10.0.2.2;