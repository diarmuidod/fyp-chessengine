USE chessdb;

DROP TABLE IF EXISTS parentChildTbl;
DROP TABLE IF EXISTS nodeTbl;
DROP TABLE IF EXISTS initialisingZobristValuesTbl;

CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey BIGINT UNIQUE,
    visits BIGINT UNSIGNED,  # n
    wValue BIGINT UNSIGNED,	# wV
    bValue BIGINT UNSIGNED,	# bV
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

SELECT * FROM nodeTbl ORDER BY visits DESC;
SELECT * FROM parentChildTbl;

# select all children of root node, plus the move to reach them
SELECT n.*, p.move 
FROM nodeTbl AS n JOIN parentChildTbl AS p
ON n.zobristKey = p.childKey
WHERE p.parentKey = -1451015966002190617 #AND n.wValue < n.bValue
ORDER BY n.visits DESC;
#ORDER BY n.wValue DESC;
#ORDER BY (n.wValue/n.bValue) ASC;

SELECT COUNT(*) FROM nodeTbl;
SELECT COUNT(*) FROM parentChildTbl;

# All nodes without parents, should always be empty bar root node
SELECT * FROM nodeTbl WHERE zobristKey NOT IN (SELECT childKey FROM parentChildTbl);


