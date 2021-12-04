package tk.sciwhiz12.aoc2021;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

public class Day4 {
    public static void main(String[] args) {
        final List<String> lines = Helpers.readLines("day4.txt");

        // First line the numbers to be called, separated by commas, then a blank link

        final String calledNumbersStr = lines.remove(0);
        final List<Integer> calledNumbers = Arrays.stream(calledNumbersStr.split(","))
            .map(Integer::parseInt)
            .toList();

        final List<BingoBoard> boards = new ArrayList<>();

        lines.remove(0); // Remove blank line separator
        do {
            BingoBoard.Builder builder = new BingoBoard.Builder();
            String line;
            while (!lines.isEmpty() && !(line = lines.remove(0)).isBlank()) {
                Arrays.stream(line.split(" "))
                    .map(String::trim)
                    .filter(Predicate.not(String::isBlank))
                    .map(Integer::parseInt)
                    .forEach(builder::add);
                builder.finishRow();
            }
            boards.add(builder.build());

        } while (!lines.isEmpty());

        final SolvedBoard firstSolvedBoard = findFirstSolved(boards, calledNumbers);

        if (firstSolvedBoard == null) {
            throw new IllegalStateException("No solving board found");
        }

        final int firstUnmarkedNumbersSum = firstSolvedBoard.board().getMarkedMap().entrySet().stream()
            .filter(entry -> !entry.getValue())
            .mapToInt(Map.Entry::getKey)
            .sum();
        System.out.println(firstSolvedBoard);
        System.out.printf("board #%s; sum: %s, called: %s, score: %s%n",
            boards.indexOf(firstSolvedBoard.board()), firstUnmarkedNumbersSum,
            firstSolvedBoard.calledNumber(), firstSolvedBoard.calledNumber() * firstUnmarkedNumbersSum);

        // part 2
        boards.forEach(BingoBoard::clearAllCells);
        final SolvedBoard lastSolvedBoard = findLastSolved(boards, calledNumbers);

        if (lastSolvedBoard == null) {
            throw new IllegalStateException("No solving board found");
        }

        final int lastUnmarkedNumbersSum = lastSolvedBoard.board().getMarkedMap().entrySet().stream()
            .filter(entry -> !entry.getValue())
            .mapToInt(Map.Entry::getKey)
            .sum();
        System.out.println(lastSolvedBoard);
        System.out.printf("board #%s; sum: %s, called: %s, score: %s%n",
            boards.indexOf(lastSolvedBoard.board()), lastUnmarkedNumbersSum,
            lastSolvedBoard.calledNumber(), lastSolvedBoard.calledNumber() * lastUnmarkedNumbersSum);
    }

    private static record SolvedBoard(BingoBoard board, int calledNumber) {
    }

    @Nullable
    private static SolvedBoard findFirstSolved(List<BingoBoard> boards, List<Integer> calledNumbers) {
        for (Integer calledNumber : calledNumbers) {
//            System.out.println("calling number " + calledNumber);
            BingoBoard solvedBoard = boards.stream()
                .filter(board -> board.markValue(calledNumber, true))
                .findFirst()
                .orElse(null);
//            boards.forEach(System.out::println);

            if (solvedBoard != null) {
                return new SolvedBoard(solvedBoard, calledNumber);
            }
        }
        return null;
    }

    @Nullable
    private static SolvedBoard findLastSolved(List<BingoBoard> boards, List<Integer> calledNumbers) {
        List<BingoBoard> currentBoards = new ArrayList<>(boards);
        for (Integer calledNumber : calledNumbers) {
            List<BingoBoard> solvedBoards = currentBoards.stream()
                .filter(board -> board.markValue(calledNumber, true))
                .toList();
            currentBoards.removeIf(solvedBoards::contains);

            if (currentBoards.isEmpty()) {
                // This batch of solved boards is the last
                final BingoBoard lastBoard = solvedBoards.get(solvedBoards.size() - 1);
                return new SolvedBoard(lastBoard, calledNumber);
            }
        }
        return null;
    }

    private static class BingoBoard {
        private final int dimensionLength;
        private final Table<Integer, Integer, Integer> cells;
        private final BitSet markedCells;

        private BingoBoard(int dimensionLength, Table<Integer, Integer, Integer> cells) {
            this.dimensionLength = dimensionLength;
            this.cells = ImmutableTable.copyOf(cells);
            this.markedCells = new BitSet(dimensionLength * dimensionLength);
        }

        private int cellIndex(int x, int y) {
            return (x * dimensionLength) + y;
        }

        public boolean isCellMarked(int x, int y) {
            return markedCells.get(cellIndex(x, y));
        }

        public void markCell(int x, int y) {
            markedCells.set(cellIndex(x, y));
        }

        public Map<Integer, Boolean> getMarkedMap() {
            final HashMap<Integer, Boolean> map = new HashMap<>();
            for (Table.Cell<Integer, Integer, Integer> cell : cells.cellSet()) {
                map.put(cell.getValue(), isCellMarked(Objects.requireNonNull(cell.getRowKey()), Objects.requireNonNull(cell.getColumnKey())));
            }
            return map;
        }

        public boolean hasSolution() {
//            System.out.println(markedCells);
            for (int i = 0; i < dimensionLength; i++) {
                if (checkRow(i)) {
//                    System.out.println("row " + i);
                    return true;
                }
                if (checkColumn(i)) {
//                    System.out.println("column " + i);
                    return true;
                }
            }
            return false;
        }

        private boolean checkRow(int x) {
//            System.out.printf("checking row %s: %s,%s (%s) to %s,%s (%s) = %s%n",
//                x, x, 0, cellIndex(x, 0), x, dimensionLength - 1, cellIndex(x, dimensionLength), markedCells.get(cellIndex(x, 0), cellIndex(x, dimensionLength - 1)));
            return markedCells.get(cellIndex(x, 0), cellIndex(x, dimensionLength)).cardinality() == dimensionLength;
        }

        private boolean checkColumn(int y) {
            for (int row = 0; row < dimensionLength; row++) {
                if (!markedCells.get(cellIndex(row, y))) {
                    return false;
                }
            }
            return true;
        }

        public boolean markValue(int value, boolean checkSolution) {
            if (!cells.containsValue(value)) return false;

            for (Table.Cell<Integer, Integer, Integer> cell : cells.cellSet()) {
                if (Objects.requireNonNull(cell.getValue()) == value) {
//                    System.out.printf("marking cell with value %s at %s:%s%n", value, cell.getRowKey(), cell.getColumnKey());
                    markCell(Objects.requireNonNull(cell.getRowKey()), Objects.requireNonNull(cell.getColumnKey()));
                    return checkSolution && hasSolution();
                }
            }

            return false;
        }

        public void clearAllCells() {
            markedCells.clear();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Table.Cell<Integer, Integer, Integer> cell : cells.cellSet()) {
                if (Objects.requireNonNull(cell.getColumnKey()) == 0) {
                    builder.append("\n");
                }
                builder.append(isCellMarked(Objects.requireNonNull(cell.getRowKey()), Objects.requireNonNull(cell.getColumnKey())) ? "*" : " ");
                String num = String.valueOf(cell.getValue());
                builder.append(num).append(" ".repeat(3 - num.length()));
            }
            return builder.toString();
        }

        static class Builder {
            private final Table<Integer, Integer, Integer> cells = TreeBasedTable.create();
            private int firstRowCellCount = -1;
            private int currentRowCellCount = 0;
            private int rowCounter = 0;

            public Builder() {
            }

            @SuppressWarnings("UnusedReturnValue")
            public Builder add(Integer cell) {
                cells.put(rowCounter, currentRowCellCount, cell);
                currentRowCellCount++;
                return this;
            }

            @SuppressWarnings("UnusedReturnValue")
            public Builder finishRow() {
                if (firstRowCellCount == -1) {
                    firstRowCellCount = currentRowCellCount;
                    checkArgument(currentRowCellCount != 0, "Cannot have empty row");
                } else {
                    checkArgument(currentRowCellCount == firstRowCellCount,
                        "%s cells in row #%s do not match expected width of %s", currentRowCellCount, rowCounter, firstRowCellCount);
                    checkArgument(currentRowCellCount >= rowCounter,
                        "Board height cannot exceed board width of %s", firstRowCellCount);
                }
                currentRowCellCount = 0;
                rowCounter++;
                return this;
            }

            public BingoBoard build() {
                checkArgument(currentRowCellCount == 0, "Call nextRow() before building");
                checkArgument(firstRowCellCount == rowCounter,
                    "Board height of %s does not equal board width of %s", rowCounter, firstRowCellCount);
                return new BingoBoard(rowCounter, cells);
            }
        }
    }
}
