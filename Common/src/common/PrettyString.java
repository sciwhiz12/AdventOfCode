package common;

public class PrettyString {
    public static String indexedTable(long[] table, int index) {
        StringBuilder builder = new StringBuilder("[");
        StringBuilder pointerString = new StringBuilder("[");
        int pointLen = -1, half = 1;
        for (int i = 0; i < table.length; i++) {
            if (i != 0) {
                builder.append(", ");
                if (pointLen == -1) half += 2;
            }
            String intVal = Long.toString(table[i]);
            builder.append(intVal);

            if (i == index) pointLen = intVal.length();
            if (pointLen == -1) half += intVal.length();
        }
        if (pointLen != -1) {
            int leftHalf = (int) Math.floor(pointLen / 2) - 1;
            pointerString.append(" ".repeat(half + leftHalf));
            pointerString.append('↑');
        }
        pointerString.append(" ".repeat(builder.length() - pointerString.length()));
        builder.append("]");
        pointerString.append("]");
        return builder.append('\n').append(pointerString).toString();
    }
}
