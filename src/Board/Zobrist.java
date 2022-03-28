package Board;

import Utils.Utils;

import java.io.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class Zobrist {
    static long[][][] board = new long[2][6][64]; //colour, pieceType, squareIndex
    static long[] enPassant = new long[8]; //file of en passant
    static long[] castling = new long[4]; //castling rights
    static long blackMove; //black to move

    public static void writeRandomNumbers() { //write to file for reuse
        Random random = new Random();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ZobristKeys.txt"));

            int keys = 64 * 6 * 2 + 8 + 4 + 1;
            for (int i = 0; i < keys; i++) {
                writer.write(random.nextLong() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRandomNumbersToDB() {
        Connection conn;
        Statement stmt;

        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/chessdb", "root", "");
            stmt = conn.createStatement();

            String sql;

            Scanner scanner = new Scanner(new File("ZobristKeys.txt"));

            LinkedList<Long> keys = new LinkedList<>();

            while (scanner.hasNextLine()) {
                keys.add(Long.parseLong(scanner.nextLine()));
            }

            for(Long key : keys) {
                sql = "INSERT IGNORE INTO initialisingZobristValuesTbl VALUES (" + key + ")";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            conn.close();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readRandomNumbers() { //read random numbers into arrays
        Scanner scanner;
        try {
            scanner = new Scanner(new File("ZobristKeys.txt"));

            LinkedList<Long> keys = new LinkedList<>();

            while (scanner.hasNextLine()) {
                keys.add(Long.parseLong(scanner.nextLine()));
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    for (int k = 0; k < 64; k++) {
                        board[i][j][k] = keys.pop();
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                enPassant[i] = keys.pop();
            }

            for (int i = 0; i < 4; i++) {
                castling[i] = keys.pop();
            }

            blackMove = keys.pop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readRandomNumbersFromDB() {
        Connection conn;
        Statement stmt;
        ResultSet rs;

        try {
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/chessdb", "root", "");
            stmt = conn.createStatement();

            String sql = "SELECT * FROM initialisingZobristValuesTbl";
            rs = stmt.executeQuery(sql);

            if(!rs.next()) writeRandomNumbersToDB();

            sql = "SELECT * FROM initialisingZobristValuesTbl";
            rs = stmt.executeQuery(sql);

            LinkedList<Long> keys = new LinkedList<>();

            while(rs.next()) {
                keys.add(rs.getLong(1));
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 6; j++) {
                    for (int k = 0; k < 64; k++) {
                        board[i][j][k] = keys.pop();
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                enPassant[i] = keys.pop();
            }

            for (int i = 0; i < 4; i++) {
                castling[i] = keys.pop();
            }

            blackMove = keys.pop();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long getZobristKey(Board b) {
        long zobristKey = 0;

        for (int i = 0; i < 64; i++) {
            if (b.allPieces.get(i)) {
                if (b.whitePieces.get(i)) {
                    if (b.pawnPieces.get(i)) zobristKey ^= board[0][0][i];
                    else if (b.knightPieces.get(i)) zobristKey ^= board[0][1][i];
                    else if (b.bishopPieces.get(i)) zobristKey ^= board[0][2][i];
                    else if (b.rookPieces.get(i)) zobristKey ^= board[0][3][i];
                    else if (b.queenPieces.get(i)) zobristKey ^= board[0][4][i];
                    else if (b.kingPieces.get(i)) zobristKey ^= board[0][5][i];
                } else {
                    if (b.pawnPieces.get(i)) zobristKey ^= board[1][0][i];
                    else if (b.knightPieces.get(i)) zobristKey ^= board[1][1][i];
                    else if (b.bishopPieces.get(i)) zobristKey ^= board[1][2][i];
                    else if (b.rookPieces.get(i)) zobristKey ^= board[1][3][i];
                    else if (b.queenPieces.get(i)) zobristKey ^= board[1][4][i];
                    else if (b.kingPieces.get(i)) zobristKey ^= board[1][5][i];
                }
            }
        }

        if (b.enPassantSquare != -1) zobristKey ^= enPassant[Utils.getFile(b.enPassantSquare)];

        if (b.whiteKingSide) zobristKey ^= castling[0];
        if (b.whiteQueenSide) zobristKey ^= castling[1];
        if (b.blackKingSide) zobristKey ^= castling[2];
        if (b.blackQueenSide) zobristKey ^= castling[3];

        if (!b.whiteToMove) zobristKey ^= blackMove;

        return zobristKey;
    }
}
