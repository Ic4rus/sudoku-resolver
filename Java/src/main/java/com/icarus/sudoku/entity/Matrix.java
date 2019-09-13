package com.icarus.sudoku.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Matrix {

    private int matrixSize;

    private int blockSize;

    private HashSet<Integer> allValues;

    private Cell[][] cells;

    private List<Cell> noneValueCellList, twoNoteValuesCellList;

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
            } else {
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

    /**
     * Tim cac diem ma co cung 2 value
     */
    public void step2() {
        twoNoteValuesCellList = new ArrayList<>();
        for (Cell noneValueCell : noneValueCellList) {
            if (noneValueCell.has2ValuesInNote()) {
                twoNoteValuesCellList.add(noneValueCell);
            }
        }
    }


    public void resolve() {
        note();
        while (noneValueCellList.size() > 0) {
            step1();
            show();
        }
    }













    private static int[][] matrixValue = {
            {0, 0, 0, 0, 0, 5, 4, 0, 9},
            {4, 5, 1, 0, 0, 2, 3, 0, 0},
            {9, 8, 2, 0, 0, 0, 5, 6, 1},
            {6, 0, 7, 0, 0, 0, 9, 8, 0},
            {0, 0, 3, 4, 6, 0, 0, 0, 0},
            {5, 0, 0, 2, 8, 7, 0, 1, 0},
            {0, 4, 0, 0, 7, 0, 0, 9, 6},
            {3, 0, 0, 0, 0, 0, 7, 0, 0},
            {0, 0, 5, 9, 4, 6, 8, 0, 2},
    };

    public static void main(String[] args) {
        Matrix matrix = new Matrix(matrixValue);
        matrix.show();
        matrix.resolve();
    }

}
