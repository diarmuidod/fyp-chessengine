CREATE SCHEMA IF NOT EXISTS chessdb;
USE chessdb;

DROP TABLE IF EXISTS parentChildTbl;
DROP TABLE IF EXISTS nodeTbl;
DROP TABLE IF EXISTS initialisingZobristValuesTbl;

CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey BIGINT UNIQUE,
    visits BIGINT UNSIGNED,  # n
    wValue BIGINT UNSIGNED,	# wV
    bValue BIGINT UNSIGNED	# bV
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

/*
SELECT * FROM nodeTbl ORDER BY visits DESC;
SELECT * FROM parentChildTbl;
SELECT * FROM initialisingzobristvaluestbl;

SELECT * FROM nodeTbl WHERE zobristKey = -1451015966002190617;

# select all children of root node, plus the move to reach them
SELECT n.*, p.move 
FROM nodeTbl AS n JOIN parentChildTbl AS p
ON n.zobristKey = p.childKey
WHERE p.parentKey = -1451015966002190617
#ORDER BY n.visits DESC;
#ORDER BY n.wValue DESC;
ORDER BY (n.wValue/n.visits) DESC;

SELECT COUNT(*) FROM nodeTbl;
SELECT COUNT(*) FROM parentChildTbl;

SELECT * FROM nodeTbl WHERE visits = 0;
*/