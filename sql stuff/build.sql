-- to execute: source D:/Daten/DFKI/Workspace/Project Lightning (Desktop)/sql stuff/build.sql;

-- delete database
DROP DATABASE IF EXISTS feedback;

-- create new database
CREATE DATABASE feedback;
USE feedback;

-- delete user
DROP USER 'participant'@'localhost';

-- create user
CREATE USER 'participant'@'localhost' IDENTIFIED BY 'pwd'; 
GRANT USAGE ON *.* TO 'participant'@'localhost' IDENTIFIED BY 'pwd'; 
GRANT INSERT ON feedback.* TO 'participant'@'localhost';

-- create table 
CREATE TABLE statistics (
	id INT NOT NULL AUTO_INCREMENT, 
	datas TEXT NOT NULL,
	PRIMARY KEY (id)
);
	