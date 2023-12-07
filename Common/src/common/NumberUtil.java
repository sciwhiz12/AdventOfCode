package common;

public class NumberUtil {
    public static int getDigit(long num, int pos) {
        for (int i = 0; i < pos; i++) {
            num = num / 10;
        }
        return (int) num % 10;
    }
}
