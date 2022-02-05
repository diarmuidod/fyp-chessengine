USE chessdb;

DROP TABLE IF EXISTS nodeTbl;
CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey BIGINT,
    parentVisits DOUBLE, # N
    childVisits DOUBLE,  # n
    nodeValue DOUBLE,	 # v
    CONSTRAINT node_PK PRIMARY KEY (zobristKey)
);

DROP TABLE IF EXISTS parentChildTbl;
CREATE TABLE IF NOT EXISTS parentChildTbl (
	parentKey BIGINT,
    childKey BIGINT,
    move VARCHAR(8),
    CONSTRAINT parentChild_PK PRIMARY KEY (parentKey, childKey)
);

DROP TABLE IF EXISTS initialisingZobristValuesTbl;
CREATE TABLE IF NOT EXISTS initialisingZobristValuesTbl (
	zobristKey BIGINT UNIQUE
);

SELECT * FROM nodeTbl;
SELECT * FROM parentChildTbl;

SELECT COUNT(*) FROM nodeTbl;
SELECT COUNT(*) FROM parentChildTbl;


