CREATE DATABASE IF NOT EXISTS fypChessEngineDB;
USE fypChessEngineDB;

CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey LONG PRIMARY KEY,
    move CHAR(8),
    fen CHAR(90),
    parentVisits DOUBLE, # N
    childVisits DOUBLE,  # n
    nodeValue DOUBLE	 # v
);

CREATE TABLE IF NOT EXISTS parentChildTbl (
	parentKey LONG,
    childKey LONG
);

CREATE TABLE IF NOT EXISTS initialisingZobristValuesTbl (
	zobristKey LONG
);