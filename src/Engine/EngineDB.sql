USE chessdb;

CREATE TABLE IF NOT EXISTS nodeTbl (
	zobristKey LONG,
    move CHAR(8),
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
