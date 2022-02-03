USE chessdb;

DROP TABLE IF EXISTS nodeTbl;
CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey LONG,
    parentVisits DOUBLE, # N
    childVisits DOUBLE,  # n
    nodeValue DOUBLE	 # v
);

DROP TABLE IF EXISTS parentChildTbl;
CREATE TABLE IF NOT EXISTS parentChildTbl (
	parentKey LONG,
    childKey LONG,
    move VARCHAR(8)
);

DROP TABLE IF EXISTS initialisingZobristValuesTbl;
CREATE TABLE IF NOT EXISTS initialisingZobristValuesTbl (
	zobristKey LONG
);

DESC nodeTbl;
SELECT * FROM parentChildTbl;
SELECT * FROM nodeTbl;