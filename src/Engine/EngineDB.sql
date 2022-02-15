USE chessdb;

DROP TABLE IF EXISTS nodeTbl;
CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey BIGINT UNIQUE,
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
    CONSTRAINT parentChild_PK PRIMARY KEY (parentKey, childKey),
    CONSTRAINT parentKey_FK FOREIGN KEY (parentKey) REFERENCES nodeTbl(zobristKey),
    CONSTRAINT childKey_FK FOREIGN KEY (childKey) REFERENCES nodeTbl(zobristKey),
    CONSTRAINT uniquePairs_UQ UNIQUE (parentKey, childKey)
);

DROP TABLE IF EXISTS initialisingZobristValuesTbl;
CREATE TABLE IF NOT EXISTS initialisingZobristValuesTbl (
	zobristKey BIGINT UNIQUE
);

SELECT * FROM nodeTbl;
SELECT * FROM parentChildTbl;

SELECT * FROM nodeTbl WHERE zobristKey IN (SELECT childKey FROM parentChildTbl WHERE parentKey = -1451015966002190617); # select all children of root node

SELECT COUNT(*) FROM nodeTbl;
SELECT COUNT(*) FROM parentChildTbl;

SELECT * FROM nodeTbl WHERE zobristKey NOT IN (SELECT childKey FROM parentChildTbl); # Should always be empty bar root node


