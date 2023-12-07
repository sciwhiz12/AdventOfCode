import common.Timings;

public class Day4 {
    static final String input = "372037-905157";

    public static void main(String[] args) {

        int count = 0;

        Timings.start();

        String[] split = input.split("-");
        int lowerBound = Integer.parseInt(split[0]);
        int higherBound = Integer.parseInt(split[1]);

        for (int i = lowerBound; i < higherBound; i++) {
            if (isValidPassword(i)) { count++; }
        }

        Timings.stop();

        System.out.println(count);

    }

    public static boolean isValidPassword(int number) {
        String str = Integer.toString(number);
        if (str.length() != 6) { return false; }
        char prevChar = ' ', prevHigh = ' ';
        boolean hasDouble = false;
        for (char c : str.toCharArray()) {
            if (c < prevHigh) {
                return false;
            }
            if (prevChar == c) {
                hasDouble = true;
            }
            prevChar = prevHigh = c;
        }
        return hasDouble;
    }
}
