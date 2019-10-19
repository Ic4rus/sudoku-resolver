package com.icarus.sudoku.entity;

import java.util.*;

public class Matrix {

    private int matrixSize;

    private int blockSize;

    private HashSet<Integer> allValues;

    private Cell[][] cells;

    private List<Cell> noneValueCellList;

    public Matrix(int[][] values) {
        matrixSize = 9;
        blockSize = (int) Math.sqrt(matrixSize);
        allValues = new HashSet<>();
        for (int i = 0; i < matrixSize; i++) {
            allValues.add(i + 1);
        }
        cells = new Cell[matrixSize][matrixSize];
        noneValueCellList = new ArrayList<>();
        Cell cell;
        int id = 0, row, column, zone, value;
        for (row = 0; row < matrixSize; row++) {
            for (column = 0; column < matrixSize; column++) {
                zone = (row / blockSize) * blockSize + (column / blockSize);
                value = values[row][column];
                if (value == 0) {
                    cell = new Cell(id++, row, column, zone, values[row][column], new HashSet<>(allValues));
                    noneValueCellList.add(cell);
                } else {
                    cell = new Cell(id++, row, column, zone, values[row][column], null);
                }
                cells[row][column] = cell;
            }
        }
    }

    public void show() {
        StringBuilder border = new StringBuilder();
        StringBuilder divider = new StringBuilder();
        for (int i = 0; i < (matrixSize * 3 + blockSize + 1); i++) {
            border.append("-");
            divider.append(i % (blockSize * 3 + 1) == 0 ? "|" : "-");
        }
        System.out.println(border);
        for (int i = 0; i < matrixSize; i++) {
            System.out.print("|");
            for (int j = 0; j < matrixSize; j++) {
                System.out.print(" " + cells[i][j].getValue() + " ");
                if ((j % blockSize) == (blockSize - 1)) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (((i % blockSize) == (blockSize - 1)) && ((i / blockSize) < (blockSize - 1))) {
                System.out.println(divider);
            }
        }
        System.out.println(border);
        for (Cell cell : noneValueCellList) {
            System.out.print(cell.getRow() + "-" + cell.getColumn() + ": ");
            for (int v : cell.getNoteValues()) {
                System.out.print(v + ", ");
            }
            System.out.println();
        }
    }

    public void note() {
        for (Cell cell : noneValueCellList) {
            findInvertRelevantCells(cell);
        }
    }

    /**
     * Find relevant cells which has value is invert of source cell
     *
     * @param sourceCell
     */
    public void findInvertRelevantCells(Cell sourceCell) {

        Relation valueRelation = sourceCell.isNoneValue() ? Relation.VALUE : Relation.NONE_VALUE;
        List<Cell> relevantCellList = findCells(sourceCell, valueRelation, Relation.NOT_ME, Relation.RELEVANT);
        relevantCellList.forEach(cell -> {
            if (valueRelation == Relation.NONE_VALUE) {
                cell.filterByRelevantCell(sourceCell);
            } else {
                sourceCell.filterByRelevantCell(cell);
            }
        });
    }

    /**
     * Find value for cell have only one note value or have unique note value in row/column/zone
     *
     */
    public void step1() {

        Iterator<Cell> noneValueCellIterator = noneValueCellList.iterator();
        Cell cell;
        boolean selfFillingSuccess;
        while (noneValueCellIterator.hasNext()) {
            cell = noneValueCellIterator.next();
            // Auto fill if there is only one note value
            selfFillingSuccess = cell.selfFilling();
            if (selfFillingSuccess) {
                findInvertRelevantCells(cell);
                noneValueCellIterator.remove();
            } // The cell have many note values
            else {
                int uniqueNoteValue = findUniqueNoteValueOfCell(cell);
                if (uniqueNoteValue != 0) {
                    cell.fillValue(uniqueNoteValue);
                    findInvertRelevantCells(cell);
                    noneValueCellIterator.remove();
                }
            }
        }
    }

    /**
     * The only cell have this note value in row/column/zone
     *
     * @return
     */
    public int findUniqueNoteValueOfCell(Cell sourceCell) {

        // The list of cell in zone
        List<Cell> otherCellsInRow = findCells(sourceCell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_ROW);
        List<Cell> otherCellsInColumn = findCells(sourceCell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_COLUMN);
        List<Cell> otherCellsInZone = findCells(sourceCell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_ZONE);
        HashSet<Integer> noteValues = sourceCell.getNoteValues();
        for (Integer noteValue : noteValues) {
            if (!existsNoteValueInCells(noteValue, otherCellsInRow)) {
                return noteValue;
            }
            if (!existsNoteValueInCells(noteValue, otherCellsInColumn)) {
                return noteValue;
            }
            if (!existsNoteValueInCells(noteValue, otherCellsInZone)) {
                return noteValue;
            }
        }
        return 0;
    }

    /**
     * Check a note value if exists in note values of the list of cell
     *
     * @param noteValue
     * @param cellList
     * @return
     */
    public boolean existsNoteValueInCells(int noteValue, List<Cell> cellList) {
        if (cellList == null || cellList.isEmpty()) {
            return false;
        }
        for (Cell cell : cellList) {
            if (cell.containValueInNote(noteValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find equal cells in row/column/zone
     *
     */
    public void step2() {
        HashSet<Integer> noteValues;
        List<Cell> otherCellsInRow, otherCellsInColumn, otherCellsInZone;
        // Loop each none value cell
        for (Cell cell : noneValueCellList) {
            noteValues = cell.getNoteValues();
            // Skip cell if it has only one note value
            if (noteValues.size() == 1) {
                continue;
            }
            // Find other cells on the same row
            otherCellsInRow = findCells(cell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_ROW);
            findEqualCells(cell, otherCellsInRow);

            // Find other cells on the same column
            otherCellsInColumn = findCells(cell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_COLUMN);
            findEqualCells(cell, otherCellsInColumn);

            // Find other cell in same zone
            otherCellsInZone = findCells(cell, Relation.NONE_VALUE, Relation.NOT_ME, Relation.SAME_ZONE);
            findEqualCells(cell, otherCellsInZone);
        }

        noneValueCellList.forEach(cell -> cell.removeNoteValues());
    }

    /**
     * Find equal cells in given the list of cell.
     * If the total number of equal cells (including source cell) equal number of note values,
     * other cells can not contains this note values.
     *
     * @param sourceCell     the source cell
     * @param targetCellList the other cells in same row/column/zone
     */
    public void findEqualCells(Cell sourceCell, List<Cell> targetCellList) {
        HashSet<Integer> noteValues = sourceCell.getNoteValues();
        if (targetCellList.size() <= noteValues.size()) {
            return;
        }
        int count = 1;
        // Find equal cell
        for (Cell targetCell : targetCellList) {
            if (sourceCell.hasSameNoteValuesWith(targetCell)) {
                targetCell.setChecked(true);
                count++;
            }
        }
        if (noteValues.size() == count) {
            for (Cell targetCell : targetCellList) {
                if (targetCell.isChecked()) {
                    targetCell.setChecked(false);
                } else {
                    targetCell.addRemovalNoteValues(noteValues);
                }
            }
        }
    }

    /**
     * Remove note values of other cells in zone if it have cells on the same row or column that have note values
     * that other cells do not have on the row or column
     */
    public void step3() {
        noneValueCellList.forEach(cell -> {
            updateNoteValuesInStep3(cell, Relation.SAME_ROW, Relation.OTHER_ROW);
            updateNoteValuesInStep3(cell, Relation.SAME_COLUMN, Relation.OTHER_COLUMN);
        });
    }

    /**
     * Update note value for other cells in zone or cells on the same row/column out zone
     *
     */
    public void updateNoteValuesInStep3(Cell sourceCell, Relation sameLine, Relation otherLine) {

        Set<Integer> noteValuesIsNotDuplicate = new HashSet<>();
        Set<Integer> noteValuesOnlyInZone = new HashSet<>();

        // Get all note values of cells on the same row/column in zone
        Set<Integer> noteValuesInZone = new HashSet<>();
        List<Cell> cellsInZone = findCells(sourceCell, Relation.NONE_VALUE, Relation.SAME_ZONE, sameLine);
        cellsInZone.forEach(cell -> noteValuesInZone.addAll(cell.getNoteValues()));

        // Get all note values of cells on the other row/column in zone
        Set<Integer> otherNoteValuesInZone = new HashSet<>();
        List<Cell> otherCellsInZone = findCells(sourceCell, Relation.NONE_VALUE, Relation.SAME_ZONE, otherLine);
        otherCellsInZone.forEach(cell -> otherNoteValuesInZone.addAll(cell.getNoteValues()));

        // Get all note values of cells on the same row/column out zone
        Set<Integer> noteValuesOutZone = new HashSet<>();
        List<Cell> cellsOutZone = findCells(sourceCell, Relation.NONE_VALUE, Relation.OTHER_ZONE, sameLine);
        cellsOutZone.forEach(cell -> noteValuesOutZone.addAll(cell.getNoteValues()));

        // Note values not exists in cells on the same row/column out zone
        noteValuesInZone.stream()
                .filter(noteValue -> !noteValuesOutZone.contains(noteValue))
                .forEach(noteValue -> noteValuesIsNotDuplicate.add(noteValue));
        // Remove note values in other cells in zone
        otherCellsInZone.forEach(cell -> cell.removeNoteValues(noteValuesIsNotDuplicate));

        // Note values not exists in cells on the other row/column in zone
        noteValuesInZone.stream()
                .filter(noteValue -> !otherNoteValuesInZone.contains(noteValue))
                .forEach(noteValue -> noteValuesOnlyInZone.add(noteValue));
        // Remove note values in cells out zone
        cellsOutZone.forEach(cell -> cell.removeNoteValues(noteValuesOnlyInZone));
    }

    /**
     * Find none value cells by condition
     *
     * @param sourceCell the cell to start find
     * @param relations  the list of relation
     * @return
     */
    public List<Cell> findCells(Cell sourceCell, Relation... relations) {
        List<Cell> cellList = new ArrayList<>();
        Cell cell;
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                cell = cells[row][column];
                if (sourceCell.inRelationship(cell, relations)) {
                    cellList.add(cell);
                }
            }
        }
        return cellList;
    }

    public void resolve() {
        note();
        while (noneValueCellList.size() > 0) {
            step1();
            step2();
            step3();
            show();
        }
        show();
    }

    private static int[][] matrixValue = {
            {0, 0, 0, 8, 7, 1, 9, 2, 0},
            {0, 2, 7, 3, 0, 9, 8, 0, 0},
            {0, 9, 8, 2, 0, 0, 0, 0, 0},
            {7, 8, 0, 9, 0, 0, 3, 0, 0},
            {0, 0, 0, 0, 0, 7, 0, 0, 0},
            {0, 5, 0, 0, 0, 3, 0, 0, 0},
            {9, 7, 4, 1, 3, 2, 6, 8, 5},
            {8, 0, 0, 0, 0, 0, 2, 0, 9},
            {2, 0, 0, 0, 9, 8, 0, 0, 4},
    };

    public static void main(String[] args) {
        Matrix matrix = new Matrix(matrixValue);
        matrix.resolve();
    }
}
