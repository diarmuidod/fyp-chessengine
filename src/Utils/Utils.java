package Utils;

public class Utils {
    public static int getFile(int index) {
        return index % 8;
    }

    public static int getRank(int index) {
        return index / 8;
    }

    public static char getFileChar(int index) {
        return (char) ((index % 8) + 97);
    }

    public static char getRankChar(int index) {
        return (char) ((index / 8) + 49);
    }

    public static int posToIndex(int x, int y, boolean boardFlipped) {
        return boardFlipped ? y * 8 + x : Math.abs(y - 7) * 8 + x;
    }

    public static String moveFromIndex(int index) {
        char number = getRankChar(index);
        char letter = getFileChar(index);

        return letter + String.valueOf(number);
    }
}
