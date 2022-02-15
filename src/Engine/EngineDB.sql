USE chessdb;

DROP TABLE IF EXISTS parentChildTbl;
DROP TABLE IF EXISTS nodeTbl;
DROP TABLE IF EXISTS initialisingZobristValuesTbl;

CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey BIGINT UNIQUE,
    parentVisits DOUBLE, # N
    childVisits DOUBLE,  # n
    nodeValue DOUBLE,	 # v
    CONSTRAINT node_PK PRIMARY KEY (zobristKey)
);

CREATE TABLE IF NOT EXISTS parentChildTbl (
	parentKey BIGINT,
    childKey BIGINT,
    move VARCHAR(8),
    CONSTRAINT uniquePairs_UQ UNIQUE (parentKey, childKey)
);

CREATE TABLE IF NOT EXISTS initialisingZobristValuesTbl (
	zobristKey BIGINT UNIQUE
);

SELECT * FROM nodeTbl;
SELECT * FROM parentChildTbl;

SELECT * FROM nodeTbl WHERE zobristKey IN (SELECT childKey FROM parentChildTbl WHERE parentKey = -1451015966002190617); # select all children of root node

SELECT COUNT(*) FROM nodeTbl;
SELECT COUNT(*) FROM parentChildTbl;

SELECT * FROM nodeTbl WHERE zobristKey NOT IN (SELECT childKey FROM parentChildTbl); # Should always be empty bar root node


