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

    public List<Cell> findRelatedCell(Cell sourceCell) {
        List<Cell> relatedCellList = new ArrayList<>();
        Cell targetCell;
        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column++) {
                targetCell = cells[row][column];
                if (sourceCell.relateToCell(targetCell)) {
                    relatedCellList.add(targetCell);
                }
            }
        }
        return relatedCellList;
    }

    public void note() {
        List<Cell> relatedCellList;
        for (Cell noneValueCell : noneValueCellList) {
            relatedCellList = findRelatedCell(noneValueCell);
            for (Cell relatedCell : relatedCellList) {
                noneValueCell.filterByRelatedCell(relatedCell);
            }
        }
    }

    public void step1() {

        Iterator<Cell> noneValueCellIterator = noneValueCellList.iterator();
        Cell noneValueCell;
        boolean selfFillingSuccess;
        List<Cell> relatedCellList;
        while (noneValueCellIterator.hasNext()) {
            noneValueCell = noneValueCellIterator.next();
            selfFillingSuccess = noneValueCell.selfFilling();
            if (selfFillingSuccess) {
                relatedCellList = findRelatedCell(noneValueCell);
                for (Cell relatedCell : relatedCellList) {
                    relatedCell.filterByRelatedCell(noneValueCell);
                }
                noneValueCellIterator.remove();
            } // The only cell has this value
            else {
                relatedCellList = findRelatedCell(noneValueCell);
                HashSet<Integer> noteValues = noneValueCell.getNoteValues();
                boolean isExists;
                for (Integer noteValue : noteValues) {
                    isExists = false;
                    for (Cell relatedCell : relatedCellList) {
                        if (relatedCell.containValueInNote(noteValue)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        noneValueCell.fillValue(noteValue);
                        for (Cell relatedCell : relatedCellList) {
                            relatedCell.filterByRelatedCell(noneValueCell);
                        }
                        noneValueCellIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    public void step2() {
        int size = noneValueCellList.size();
        Cell cell1, cell2;
        for (int i = 0; i < size; i++) {
            cell1 = noneValueCellList.get(i);
            if (cell1.hasNumberValuesInNote(2)) {
                for (int j = i + 1; j < size; j++) {
                    cell2 = noneValueCellList.get(j);
                    if (cell1.hasSameNoteValuesWith(cell2) && cell1.relateToCell(cell2)) {
                        updateNoteValuesInStep2(cell1, cell2);
                    }
                }
            }
        }
    }

    public void updateNoteValuesInStep2(Cell cell1, Cell cell2) {
        for (Cell cell : noneValueCellList) {
            if (cell.inSameRow(cell1) && cell.inSameRow(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameColumn(cell1) && cell.inSameColumn(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameZone(cell1) && cell.inSameZone(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
        }
    }

    public void step3() {
        int size = noneValueCellList.size();
        Cell cell1, cell2, cell3;
        for (int i = 0; i < size; i++) {
            cell1 = noneValueCellList.get(i);
            if (cell1.hasNumberValuesInNote(3)) {
                for (int j = i + 1; j < size; j++) {
                    cell2 = noneValueCellList.get(j);
                    if (cell1.hasSameNoteValuesWith(cell2) && cell1.relateToCell(cell2)) {
                        for (int k = j + 1; k < size; k++) {
                            cell3 = noneValueCellList.get(k);
                            if (cell2.hasSameNoteValuesWith(cell3) && cell2.relateToCell(cell3)) {
                                updateNoteValuesInStep3(cell1, cell2, cell3);
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateNoteValuesInStep3(Cell cell1, Cell cell2, Cell cell3) {
        for (Cell cell : noneValueCellList) {
            if (cell.inSameRow(cell1) && cell.inSameRow(cell2) && cell.inSameRow(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameColumn(cell1) && cell.inSameColumn(cell2) && cell.inSameRow(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameZone(cell1) && cell.inSameZone(cell2) && cell.inSameZone(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
        }
    }

    /**
     * Remove note values of other cells in zone if it have 3 cells on the same row or column that have note values
     * that other cells do not have on the row or column
     */
    public void step4() {
        // Row
        Cell cell, rightCell1, rightCell2;
        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column = column + blockSize) {
                cell = cells[row][column];
                // The cell does not fill
                if (cell.isNotFilled()) {
                    rightCell1 = cells[row][column + 1];
                    if (rightCell1.isNotFilled()) {
                        rightCell2 = cells[row][column + 2];
                        if (rightCell2.isNotFilled()) {
                            for (int i = 0; i < matrixSize; i++) {

                            }
                        }
                    }
                }
            }
        }

    }

    public void resolve() {
        note();
        int previousSize = noneValueCellList.size();
        while (noneValueCellList.size() > 0) {
            step1();
            step2();
            show();
            if (noneValueCellList.size() == previousSize) {
                step3();
            } else {
                previousSize = noneValueCellList.size();
            }
        }
    }

    private static int[][] matrixValue = {
            {0, 0, 0, 8, 7, 1, 0, 2, 0},
            {0, 0, 0, 3, 0, 9, 8, 0, 0},
            {0, 9, 8, 2, 0, 0, 0, 0, 0},
            {7, 8, 0, 9, 0, 0, 3, 0, 0},
            {0, 0, 0, 0, 0, 7, 0, 0, 0},
            {0, 5, 0, 0, 0, 3, 0, 0, 0},
            {9, 7, 4, 1, 3, 2, 6, 8, 5},
            {0, 0, 0, 0, 0, 0, 0, 0, 9},
            {2, 0, 0, 0, 9, 0, 0, 0, 4},
    };

    public static void main(String[] args) {
        Matrix matrix = new Matrix(matrixValue);
        matrix.resolve();
    }

}
